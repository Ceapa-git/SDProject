package com.dan.sd.monitoring.repositories;

import com.dan.sd.monitoring.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

}
