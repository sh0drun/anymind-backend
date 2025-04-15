package com.anymind.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class SalesQueryInput {

    @NotNull(message = "startDateTime can't be null")
    @NotBlank(message = "startDateTime can't be blank")
    private String startDateTime;

    @NotNull(message = "endDateTime can't be null")
    @NotBlank(message = "endDateTime can't be blank")
    private String endDateTime;

    public LocalDateTime getParsedStartDateTime() {
        return LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public LocalDateTime getParsedEndDateTime() {
        return LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
