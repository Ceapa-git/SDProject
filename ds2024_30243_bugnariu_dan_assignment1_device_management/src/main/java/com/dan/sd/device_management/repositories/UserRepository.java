package com.dan.sd.device_management.repositories;

import com.dan.sd.device_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
