package com.fintech.wallet.dto;

import java.time.LocalDate;

public record CompleteProfileRequest(String firstName, String lastName, LocalDate dateOfBirth) {

}
