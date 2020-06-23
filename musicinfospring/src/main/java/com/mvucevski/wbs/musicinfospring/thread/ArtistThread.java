package com.mvucevski.wbs.musicinfospring.thread;

import com.mvucevski.wbs.musicinfospring.model.Artist;

import java.util.concurrent.Callable;

public class ArtistThread implements Callable<Boolean> {
    String taskName;
    Callable<Object> query;

    public ArtistThread(String taskName, Callable<Object> query){
        this.taskName=taskName;
        this.query = query;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("Started taskName: "+taskName);
        query.call();
        System.out.println("Completed taskName: "+taskName);

        return true;
    }
}
