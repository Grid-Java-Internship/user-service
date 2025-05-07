package com.internship.user_service.bean;

import com.google.api.client.util.Value;
import org.springframework.stereotype.Component;

@Component
public class GcsUrlHelper {

    @Value("${gcs.bucket.name}")
    private static String bucketName;

    public static String buildFullGcsUrl(String path) {
        return "https://storage.googleapis.com/" + bucketName + "/" + path;
    }
}
