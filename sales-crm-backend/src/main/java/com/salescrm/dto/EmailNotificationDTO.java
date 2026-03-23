package com.salescrm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationDTO {

    // Who to send to
    private String toEmail;
    private String toName;

    // Lead info
    private String leadName;
    private String leadCompany;
    private String leadStatus;

    // Follow-up details
    private String followUpNotes;
    private String outcome;
    private String nextFollowUpDate;
    private String followUpTimestamp;

    // Employee info
    private String employeeName;
    private String employeeEmail;
    private String employeeInitials;

    // Email meta
    private String subject;
    private boolean success;
    private String message;
}
