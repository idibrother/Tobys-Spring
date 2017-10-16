package com.idibros.study.dto;

import lombok.AllArgsConstructor;

/**
 * Created by dongba on 2017-10-16.
 */
@AllArgsConstructor
public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    public int intValue() {
        return value;
    }

    public static Level valueOf (int value) {
        switch(value) {
            case 1: return BASIC;
            case 2: return SILVER;
            case 3: return GOLD;
            default: throw new AssertionError("Unknown value: " + value);
        }
    }
}
