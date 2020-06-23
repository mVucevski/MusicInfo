package com.mvucevski.musicinfo_app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Award {
    public String awardTitle;
    public String awardFor;
    public String date;
    public String year;

    public Award(String awardTitle, String awardFor, String date) {
        this.awardTitle = awardTitle;
        this.awardFor = awardFor;
        this.date = date;
        if(!date.isEmpty())
            year = date.substring(0,4);
    }

//    private LocalDateTime parseDate(String dateString){
//        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        return LocalDateTime.from(f.parse(dateString));
//    }

//    public int getYear(){
//        return date.getYear();
//    }

    @Override
    public String toString() {
        return awardTitle + " - " + awardFor + " - " + date.toString();
    }
}
