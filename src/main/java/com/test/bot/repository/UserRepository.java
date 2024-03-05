package com.test.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.bot.models.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Boolean existsBytgId(Long tgId);
}