package com.anymind.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SalesQueryInput {

    private String startDateTime;
    private String endDateTime;

    public LocalDateTime getParsedStartDateTime() {
        return LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public LocalDateTime getParsedEndDateTime() {
        return LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
