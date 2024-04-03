package com.elijahwaswa.filetracker.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderEvent {
    private String lrNo;
    private UUID fileTrailId;
    private String assignedToIdNumber;
    private String department;
}