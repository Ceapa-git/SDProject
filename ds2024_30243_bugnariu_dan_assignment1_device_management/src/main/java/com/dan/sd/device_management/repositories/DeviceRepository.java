package com.dan.sd.device_management.repositories;

import com.dan.sd.device_management.entities.Device;
import com.dan.sd.device_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    List<Device> findByUserId(User userId);
}
