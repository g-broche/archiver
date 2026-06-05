package com.gbroche.archiver.utils;

import java.util.Date;

public class Logger {
    public static void log(String message){
        Date date = new Date();
        System.out.println(date + " > " + message);
    }
    public static void err(String message){
        Date date = new Date();
        System.err.println(date + " > " + message);
    }
}
