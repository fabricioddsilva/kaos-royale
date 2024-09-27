package com.kaos.mongoroyale.resources.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class URL {

    public static String decodeParam(String text){
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e){
            return "";
        }
    }

    public static Instant convertDate(String textDate, Instant defaultValue){
        try {
            return Instant.parse(textDate);
        } catch (DateTimeParseException e){
            return defaultValue;
        }

    }
}
