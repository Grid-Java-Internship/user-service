package com.internship.user_service.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum Status {
    ACTIVE(1),
    SUSPENDED(2),
    BANNED(3);

    private final Integer code;

    public static Status fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(status -> Objects.equals(status.code, code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation status code: " + code));
    }

}
