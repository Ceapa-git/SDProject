package com.dan.sd.monitoring.repositories;

import com.dan.sd.monitoring.entities.Device;
import com.dan.sd.monitoring.entities.MonitoringEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MonitoringEntryRepository extends JpaRepository<MonitoringEntry, UUID> {
    List<MonitoringEntry> findAllByDeviceIdAndTimestampAfter(Device device, Long timestamp);
    List<MonitoringEntry> findAllByDeviceIdAndTimestampBetween(Device device, Long start, Long end);
}
