package com.internship.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.internship.user_service.model.FavoriteId;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FavoriteResponse {
    private FavoriteId id;
}
