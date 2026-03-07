package com.ou.oulib.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    SYSADMIN("SYSADMIN"),
    LIBRARIAN("LIBRARIAN"),
    USER("USER");

    private final String value;
}
