package com.mvucevski.wbs.musicinfospring.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Award {
    public String awardTitle;
    public String awardFor;
    //public LocalDateTime date;
    public String date;
    public int year;

    public Award(String awardTitle, String awardFor, String date) {
        this.awardTitle = awardTitle;
        this.awardFor = awardFor;
        LocalDateTime tmp = parseDate(date);
        year = tmp.getYear();
        this.date = tmp.toString();
    }

    private LocalDateTime parseDate(String dateString){
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.from(f.parse(dateString));
    }

    public int getYear(){
        return year;
        //return date.getYear();
    }

    @Override
    public String toString() {
        return "Award{" +
                "awardTitle='" + awardTitle + '\'' +
                ", awardFor='" + awardFor + '\'' +
                ", year='" + getYear() + '\'' +
                '}';
    }
}
