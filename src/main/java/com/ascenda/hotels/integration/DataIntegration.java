package com.ascenda.hotels.integration;

import com.ascenda.hotels.integration.config.UrlConfig;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataIntegration {

    private final UrlConfig urlConfig;

    public DataIntegration(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }

    //Reading data from upstreams/sources

    public void processUrls(){

        List<String> urls = urlConfig.getUrlList();

    }


}
