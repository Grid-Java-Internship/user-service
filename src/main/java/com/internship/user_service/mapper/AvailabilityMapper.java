package com.internship.user_service.mapper;

import com.internship.user_service.dto.AvailabilityDTO;
import com.internship.user_service.model.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AvailabilityMapper {

    Availability toEntity(AvailabilityDTO availabilityDTO);
}
