package com.internship.user_service.service.impl;

import com.google.cloud.storage.*;
import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.dto.ImageDTO;
import com.internship.user_service.dto.WorkingHoursRequest;
import com.internship.user_service.exception.*;
import com.internship.user_service.mapper.AvailabilityMapper;
import com.internship.user_service.exception.PictureNotFoundException;
import com.internship.user_service.exception.AlreadyExistsException;
import com.internship.user_service.exception.UserNotFoundException;
import com.internship.user_service.mapper.UserMapper;
import com.internship.user_service.model.Availability;
import com.internship.user_service.model.User;
import com.internship.user_service.dto.UserDTO;
import com.internship.user_service.repository.AvailabilityRepository;
import com.internship.user_service.repository.UserRepository;
import com.internship.user_service.dto.UserResponse;
import jakarta.transaction.Transactional;
import com.internship.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.internship.user_service.constants.FilePath.ALLOWED_EXTENSIONS;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Storage storage;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvailabilityRepository availabilityRepository;
    private final AvailabilityMapper availabilityMapper;

    @Value("${gcs.bucket.name}")
    private String bucketName;

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    boolean isValidImageExtension(String fileName) {
        String extension = getFileExtension(fileName);
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @Override
    public UserResponse createUser(UserDTO userDTO) {
        if (userRepository.existsById(userDTO.getId())) {
            log.error("User with id {} already exists.", userDTO.getId());
            throw new AlreadyExistsException("User with id " + userDTO.getId() + " already exists.");
        }
        User user = userRepository.save(userMapper.toUserEntity(userDTO));
        log.info("User with id {} created successfully.", user.getId());
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse addProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userId);
                    return new UserNotFoundException("User not found.");
                });

        if (file == null || file.isEmpty()) {
            log.error("Profile picture is missing!");
            throw new PictureNotFoundException("Profile picture is missing!");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !isValidImageExtension(originalFilename)) {
            log.error("Invalid file type!");
            throw new PictureNotFoundException("Invalid file type!");
        }

        String fileName = "pictureUserId_" + userId + "." + getFileExtension(originalFilename);

        BlobId blobId = BlobId.of(bucketName, fileName);

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(
                blobInfo,
                file.getBytes(),
                Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ)
        );

        user.setProfilePicturePath(fileName);
        User savedUser = userRepository.save(user);
        log.info("Profile picture added for user with id {}.", userId);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public Boolean deleteProfilePicture(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        return deleteProfilePicture(user);
    }

    @Override
    public Boolean deleteProfilePicture(User user) {

        if (!user.getProfilePicturePath().isBlank()) {

            String pictureName = user.getProfilePicturePath().split("/")[4];

            BlobId blobId = BlobId.of(bucketName, pictureName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("Image deleted for user {}.", user.getId());
                user.setProfilePicturePath("");
                userRepository.save(user);
            } else {
                log.error("Image deletion failed for user {}.", user.getId());
            }
            return deleted;
        }

        return false;
    }

    @Override
    public ImageDTO getProfilePicture(Long userId) {
        UserResponse user = getUser(userId);

        if (!user.getProfilePicturePath().isBlank()) {
            log.info("User {} doesn't have a profile picture.", userId);
            throw new UserNotFoundException("User " + userId + " doesn't have profile picture.");
        }

        String pictureName = user.getProfilePicturePath().split("/")[4];

        log.info("Fetching profile picture for user {}.", userId);

        try {
            BlobId blobId = BlobId.of(bucketName, pictureName);
            Blob blob = storage.get(blobId);

            if (blob == null || !blob.exists()) {
                log.error("Picture file {} not found in GCS bucket {} for user {}.", pictureName, bucketName, userId);
                throw new UserNotFoundException("Profile picture " +
                        user.getProfilePicturePath() +
                        " not found in cloud storage."
                );
            }

            ImageDTO imageDTO = ImageDTO.builder().image(blob.getContent()).build();

            if (pictureName.endsWith(".png")) {
                imageDTO.setMediaType(MediaType.IMAGE_PNG);
            } else if (pictureName.endsWith(".jpg") || pictureName.endsWith(".jpeg")) {
                imageDTO.setMediaType(MediaType.IMAGE_JPEG);
            } else {
                imageDTO.setMediaType(MediaType.APPLICATION_OCTET_STREAM);
            }

            return imageDTO;

        } catch (StorageException e) {
            log.error("GCS error while fetching profile picture {} for user {}.", pictureName, userId);
            throw new ServiceUnavailableException("GCS error while fetching profile picture " + pictureName + " for user " + userId);
        }
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userId);
                    return new UserNotFoundException("User not found.");
                });
        UserResponse userResponse = userMapper.toUserResponse(user);
        log.info("Retrieved user with id {}.", userId);
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userRepository
                .findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
        log.info("Retrieved all users. Total count: {}.", users.size());
        return users;
    }

    /**
     * Retrieves all availabilities for the user with the specified {@code userId}.
     * If the list of availabilities is empty, that means that the user is available
     * to it's proposed time.
     *
     * @param userId The id of the user for whom to retrieve the availabilities.
     * @return A list of availabilities for the user with the specified {@code userId}.
     */
    @Transactional
    @Override
    public List<Availability> getAvailabilityForTheUser(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with this id " + userId + "was not found!"));

        return availabilityRepository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public void addAvailabilityToTheUser(AvailabilityDTO availabilityDTO) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(availabilityDTO.getWorkerId()).orElseThrow(() -> new UserNotFoundException("User not found.")));

        if(availabilityDTO.getStartTime().isAfter(availabilityDTO.getEndTime())){
            log.error("Start time must be before end time.");
            throw new InvalidTimeFormatException("Start time must be before end time.");
        }

        if(user.isEmpty()){
            log.error("User with id {} not found.", availabilityDTO.getWorkerId());
        }


        List<Availability> availabilities = availabilityRepository.findAllByUserId(availabilityDTO.getWorkerId());

        for(Availability a : availabilities){
            log.info("TESTING AVA: {}", a.getId());
            log.info("{}",a.getStartTime().isBefore(availabilityDTO.getStartTime()));

            log.info("{}",a.getEndTime().isAfter(availabilityDTO.getEndTime()));

            if(a.getStartTime().isBefore(availabilityDTO.getStartTime())
                    || a.getEndTime().isAfter(availabilityDTO.getEndTime())) { // Add: 8:00 - 12:00,  Existing: 7:00 - 13:00
                log.error("User is already busy in that time.");
                throw new UserUnavailableException("User is already busy in that time.");
            }else if(a.getStartTime().isEqual(availabilityDTO.getStartTime()) ||
            a.getEndTime().isEqual(availabilityDTO.getEndTime())){
                log.error("User is already busy in that time.");
                throw new UserUnavailableException("User is already busy in that time.");
            }
        }

        Availability availability = availabilityMapper.toEntity(availabilityDTO);
        availability.setUser(user.get());
        availabilityRepository.save(availability);
    }

    @Override
    public Boolean undoUserCreation(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found.")
        );

        if (!user.getProfilePicturePath().isBlank()) {
            deleteProfilePicture(user);
        }

        userRepository.delete(user);

        return true;
    }

    @Override
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id {} not found.", userId);
            return new UserNotFoundException("User not found.");
        });
    }

    @Override
    public boolean checkIfPhoneExists(String phoneNumber) {
        return userRepository.checkIfPhoneExists(phoneNumber);
    }


    @Override
    public void updateWorkingHours(WorkingHoursRequest request) {

        Long userId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        User user = userRepository.findById(userId).orElseThrow(() ->
            new UserNotFoundException("User with id " + userId + " was not found.")
        );

        if(startTime.isAfter(endTime)) {
            throw new ConflictException("Start time must be before end time.");
        }

        Duration duration = Duration.between(startTime, endTime);

        if(duration.toMinutes() < 30) {
            throw new ConflictException("Your working hours must be at least 30 minutes.");
        }

        user.setStartTime(startTime);

        user.setEndTime(endTime);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse editUser(UserDTO userDTO) {

        if (userDTO.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository
                .findById(userDTO.getId())
                .orElseThrow(() -> {
                    log.error("User with id {} not found.", userDTO.getId());
                    return new UserNotFoundException("User not found.");
                });

        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setBirthday(userDTO.getBirthday());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setCountry(userDTO.getCountry());
        user.setCity(userDTO.getCity());
        user.setZipCode(userDTO.getZipCode());

        log.info("User with id {} updated successfully.", user.getId());
        return userMapper.toUserResponse(user);
    }



}
