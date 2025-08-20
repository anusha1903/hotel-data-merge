package com.ascenda.hotels.integration;

import com.ascenda.hotels.util.DataHelper;
import com.ascenda.hotels.util.MergingStrategies;
import com.ascenda.hotels.integration.config.UrlConfig;
import com.ascenda.hotels.model.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DataIntegration {

    private static final Logger logger = LoggerFactory.getLogger(DataIntegration.class);

    private final DataFetcher dataFetcher;
    private final DataParser dataParser;

    public DataIntegration(DataFetcher dataFetcher, DataParser dataParser) {
        this.dataFetcher = dataFetcher;
        this.dataParser = dataParser;
    }

    //Reading data from upstreams/sources
    // Here the aim is not to create the pojo from json. So jsonnode is used instead of jsonobject

    /**
     *
     * @return
     */
    public Response processUrls(){

        Map<String, JsonNode> dataMap = dataFetcher.getHotelDataFromsource();

        // take each upstream data and create a map with hotelId as the key
        //assuming hotelId would be unique

        logger.info("Processing response data for URL sources");

      Response  hotelDataResponse = dataParser.getMergedHotelData(dataMap);

      return hotelDataResponse;


    }


}
