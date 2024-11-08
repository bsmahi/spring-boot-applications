package com.bsmlabs.resilience4j;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BenchProfiles {

    private String recruiterName;

    private String consultantName;

    private String allocatedStatus;

    private String status;

    private Integer turboCheck;

    private String priority;

    private String technology;

    private String organization;

    private String experience;

    private String location;

    private String relocation;

    private String modeOfStaying;

    private String newOrExisting;

    private String sourcedBy;

    private String visaStatus;

    private String marketingVisaStatus;

    private String contactNumber;

    private String emailId;

    private LocalDate originalDob;

    private LocalDate marketingDob;

    private String whatsappNumber;

    private LocalDate marketingStartDate;

    private LocalDate marketingEndDate;

    private String comments;

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;
}
