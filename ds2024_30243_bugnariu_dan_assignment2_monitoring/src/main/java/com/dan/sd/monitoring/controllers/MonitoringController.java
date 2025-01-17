package com.dan.sd.monitoring.controllers;

import com.dan.sd.monitoring.dtos.MonitoringEntryDTO;
import com.dan.sd.monitoring.services.MonitoringEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/monitoring")
public class MonitoringController {
    private final MonitoringEntryService monitoringEntryService;

    @Autowired
    public MonitoringController(MonitoringEntryService monitoringEntryService) {
        this.monitoringEntryService = monitoringEntryService;
    }

    @GetMapping("/{deviceId}/{timestamp}")
    public ResponseEntity<List<MonitoringEntryDTO>> getMonitoringEntries(@PathVariable UUID deviceId, @PathVariable Long timestamp) {
        return ResponseEntity.ok(monitoringEntryService.findByDeviceId(deviceId, timestamp));
    }
}
