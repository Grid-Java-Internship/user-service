package com.internship.user_service.repository;

import com.internship.user_service.model.Favorite;
import com.internship.user_service.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
}
