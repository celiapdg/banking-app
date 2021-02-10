package com.ironhack.bankapp.utils;

import java.time.LocalDate;
import java.time.Period;

public class TimeCalc {

    public static int calculateYears(LocalDate date) {
        if (date != null) {
            return Period.between(date, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    public static int calculateMonths(LocalDate date) {
        if (date != null) {
            return Period.between(date, LocalDate.now()).getMonths();
        } else {
            return 0;
        }
    }
}
