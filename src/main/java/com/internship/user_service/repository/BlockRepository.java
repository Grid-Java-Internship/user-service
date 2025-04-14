package com.internship.user_service.repository;

import com.internship.user_service.model.Block;
import com.internship.user_service.model.BlockId;
import com.internship.user_service.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, BlockId> {
    Page<Block> findByBlockingUser(User user, Pageable pageable);
}
