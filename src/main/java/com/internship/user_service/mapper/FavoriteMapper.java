package com.internship.user_service.mapper;

import com.internship.user_service.dto.FavoriteResponse;
import com.internship.user_service.model.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FavoriteMapper {
    FavoriteResponse toResponse(Favorite favorite);
}
