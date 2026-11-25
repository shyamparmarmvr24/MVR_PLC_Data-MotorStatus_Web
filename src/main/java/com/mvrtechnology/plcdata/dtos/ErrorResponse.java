package com.mvrtechnology.plcdata.dtos;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private boolean success;

    private String field;

    private String message;
}