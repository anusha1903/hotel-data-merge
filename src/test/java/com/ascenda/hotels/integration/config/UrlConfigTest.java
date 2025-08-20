package com.ascenda.hotels.integration.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UrlConfig} configuration class.
 * Tests URL configuration management and access methods.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
class UrlConfigTest {

    private UrlConfig urlConfig;

    @BeforeEach
    void setUp() {
        urlConfig = new UrlConfig();
        // Manually set the URL list since @ConfigurationProperties won't work in unit tests
        List<String> testUrls = Arrays.asList(
            "https://mockUrl/suppliers/acme",
            "https://mockUrl/suppliers/patagonia",
            "https://mockUrl/suppliers/paperflies"
        );
        urlConfig.setUrlList(testUrls);
    }

    /**
     * Validates if all urls are fetched properly
     */
    @Test
    void testGetUrlList() {
        List<String> urls = urlConfig.getUrlList();

        assertNotNull(urls);
        assertEquals(3, urls.size());
        
        assertTrue(urls.stream().anyMatch(url -> url.contains("acme")));
    }

}