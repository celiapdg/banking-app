package com.ironhack.bankapp.utils;

import java.time.LocalDate;
import java.time.Period;

public class TimeCalc {

    /** Calculate years between a date and now **/
    public static int calculateYears(LocalDate date) {
        if (date != null) {
            return Period.between(date, LocalDate.now()).getYears();
        } else {
            return 0;
        }
    }

    /** Calculate months between a date and now **/
    public static int calculateMonths(LocalDate date) {
        if (date != null) {
            Integer years = Period.between(date, LocalDate.now()).getYears();
            Integer months = years*12 + Period.between(date, LocalDate.now()).getMonths();
            return months;
        } else {
            return 0;
        }
    }
}
