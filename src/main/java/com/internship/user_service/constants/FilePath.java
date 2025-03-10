package com.internship.user_service.constants;

import java.util.Set;

public class FilePath {

    public static final String PATH = "/src/main/resources/static/uploads/profile_pictures";
    public static final String PATH_PICTURE_URL = "/uploads/profile_pictures/";

    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String PNG = "png";
    public static final String GIF = "gif";

    public static final Set<String> ALLOWED_EXTENSIONS = Set.of(JPG, JPEG, PNG, GIF);

    private FilePath() {}
}
