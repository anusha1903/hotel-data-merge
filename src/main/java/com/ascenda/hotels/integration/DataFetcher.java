package com.ascenda.hotels.integration;

import com.ascenda.hotels.integration.config.UrlConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(DataFetcher.class);

    private final UrlConfig urlConfig;

    public DataFetcher(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }


    public Map<String, JsonNode> getHotelDataFromsource() {
        logger.info("Starting URL processing - reading configured URLs");
        List<String> urls = urlConfig.getUrlList();
        logger.info("Fetching hotel data from {} configured URLs", urls.size());
        Map<String, JsonNode> dataMap = getHotelData(urls);
        return dataMap;
    }

    public  Map<String, JsonNode> getHotelData(List<String> urls){
        Map<String, JsonNode> dataMap = new HashMap<>();
        List<CompletableFuture<Void>> futures = urls.stream()
                .map(url ->
                        CompletableFuture.runAsync(() -> {
                            try (InputStream in = new URL(url).openStream()) {
                                JsonNode currentUpstreamNode = new ObjectMapper().readTree(in);
                                String utlKey = getKey(url);
                                logger.debug("Processing data source: {}", utlKey);
                                dataMap.put(utlKey, currentUpstreamNode);
                                logger.info("Successfully fetched data from: {}", url);
                            } catch (Exception e) {
                                logger.error("Failed to fetch data from URL: {}", url, e);
                            }
                        })).toList();


        // Wait for all to complete (blocking)
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

//        dataMap.forEach((key, value) -> {
//            System.out.println("Key: " + key);
//            System.out.println("Value: " + value.toPrettyString());
//        });
        return dataMap;
    }

    //TODO - change to enum
    private String getKey(String url) {
        String key = null;
        if(url.contains("acme")){
            key = "acme";
        } else if(url.contains("patagonia")){
            key = "patagonia";
        } else if(url.contains("paperflies")){
            key = "paperflies";
        }

        return key;
    }
}
