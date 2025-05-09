package com.internship.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {

    public byte[] image;

    public MediaType mediaType;
}
