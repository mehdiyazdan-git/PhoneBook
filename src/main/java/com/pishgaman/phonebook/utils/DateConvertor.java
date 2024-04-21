package com.pishgaman.phonebook.utils;

import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateConvertor {
    public String convertGregorianToJalali(LocalDateTime localDateTime) {

        // Create a DateTimeFormatter for formatting the time part.
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Use DateConverter to convert Gregorian date to Jalali.
        DateConverter dateConverter = new DateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(
                localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth()
        );

        // Manually format the Jalali date to ensure two-digit month and day.
        String formattedJalaliDate = String.format("%d/%02d/%02d",
                jalaliDate.getYear(), jalaliDate.getMonthPersian().getValue(), jalaliDate.getDay());

        // Format the time part using the created formatter.
        String formattedTime = localDateTime.format(timeFormatter);

        // Combine formatted Jalali date and formatted time into one string.
        return formattedJalaliDate + " - " + formattedTime;
    }
}
