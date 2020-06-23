package com.mvucevski.wbs.musicinfospring;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MusicinfospringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicinfospringApplication.class, args);
    }

}
