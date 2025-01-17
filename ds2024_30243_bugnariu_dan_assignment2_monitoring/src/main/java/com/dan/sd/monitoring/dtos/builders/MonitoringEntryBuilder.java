package com.dan.sd.monitoring.dtos.builders;

import com.dan.sd.monitoring.dtos.MonitoringEntryDTO;
import com.dan.sd.monitoring.entities.MonitoringEntry;

public class MonitoringEntryBuilder {
    public static MonitoringEntryDTO toMonitoringEntryDTO(MonitoringEntry monitoringEntry) {
        return new MonitoringEntryDTO(monitoringEntry.getMeasurementValue(), monitoringEntry.getTimestamp());
    }
}
