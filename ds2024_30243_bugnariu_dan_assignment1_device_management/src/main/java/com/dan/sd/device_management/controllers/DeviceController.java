package com.dan.sd.device_management.controllers;

import com.dan.sd.device_management.dtos.DeviceDTO;
import com.dan.sd.device_management.dtos.DeviceDetailsDTO;
import com.dan.sd.device_management.dtos.DeviceUpdateDTO;
import com.dan.sd.device_management.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/devices")
public class DeviceController {
    private final DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @PostMapping()
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody DeviceDetailsDTO deviceDetailsDTO) {
        return ResponseEntity.ok(deviceService.insert(deviceDetailsDTO));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable UUID id, @RequestBody DeviceUpdateDTO deviceUpdateDTO) {
        return ResponseEntity.ok(deviceService.update(id, deviceUpdateDTO));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<List<DeviceDTO>> getDeviceByUserId(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findByUserId(id));
    }
}
