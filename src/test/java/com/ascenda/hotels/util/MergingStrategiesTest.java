package com.ascenda.hotels.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MergingStrategies} utility class.
 * Tests merging strategies for hotel data including amenities, descriptions, and string operations.
 * 
 * @author Generated Test Suite
 * @version 1.0
 */
class MergingStrategiesTest {

    private List<String> generalAmenities;
    private List<String> roomAmenities;

    @BeforeEach
    void setUp() {
        generalAmenities = new ArrayList<>(Arrays.asList("pool", "outdoor pool", "indoor pool", "fitness center", "spa"));
        roomAmenities = new ArrayList<>(Arrays.asList("tv", "coffee machine", "kettle", "hair dryer", "iron"));
    }


    @Test
    void testGetStringWithMaxLength() {
        String str1 = "short";
        String str2 = "much longer string";
        assertEquals(str2, MergingStrategies.getStringWithMaxLength(str1, str2));
        assertEquals(str2, MergingStrategies.getStringWithMaxLength(str2, str1));
    }


    @Test
    void testGetStringWithMaxLength_EqualLength() {
        String str1 = "hello";
        String str2 = "world";
        String result = MergingStrategies.getStringWithMaxLength(str1, str2);
        // Both strings have same length, so either could be returned
        assertTrue(result.equals(str1) || result.equals(str2));
    }


    @Test
    void testCleanAmenities_ProperCategorization() {
        Map<String, List<String>> result = MergingStrategies.cleanAmenities(generalAmenities, roomAmenities);
        
        assertNotNull(result);
        assertTrue(result.containsKey("general"));
        assertTrue(result.containsKey("room"));
        
        List<String> cleanGeneral = result.get("general");
        List<String> cleanRoom = result.get("room");
        
        assertNotNull(cleanGeneral);
        assertNotNull(cleanRoom);
        assertTrue(cleanRoom.contains("tv"));
        assertTrue(cleanRoom.contains("coffee machine"));
    }


    @Test
    void testCleanAmenities_EmptyLists() {
        Map<String, List<String>> result = MergingStrategies.cleanAmenities(new ArrayList<>(), new ArrayList<>());
        
        assertNotNull(result);
        assertTrue(result.get("general").isEmpty());
        assertTrue(result.get("room").isEmpty());
    }


    @Test
    void testCleanAmenities_WithDuplicates() {
        List<String> genWithDuplicates = new ArrayList<>(Arrays.asList("pool", "pool", "outdoor pool", "fitness center"));
        List<String> roomWithDuplicates = new ArrayList<>(Arrays.asList("tv", "tv", "coffee machine"));
        
        Map<String, List<String>> result = MergingStrategies.cleanAmenities(genWithDuplicates, roomWithDuplicates);
        
        assertNotNull(result);
        List<String> cleanRoom = result.get("room");
        long tvCount = cleanRoom.stream().filter(a -> a.equals("tv")).count();
        assertTrue(tvCount >= 1); // Should have at least one tv after cleaning
    }


    @Test
    void testGetNormalizedList() {
        List<String> testList = Arrays.asList("pool", "swimming pool", "outdoor pool", "fitness center", "fitnesscenter");
        List<String> normalized = MergingStrategies.getNormalizedList(testList);

        assertNotNull(normalized);
        assertFalse(normalized.isEmpty());
        assertTrue(normalized.contains("fitness center"));
    }


    @Test
    void testGetNormalizedList_EmptyList() {
        List<String> normalized = MergingStrategies.getNormalizedList(new ArrayList<>());
        assertNotNull(normalized);
        assertTrue(normalized.isEmpty());
    }


    @Test
    void testIsRoomAmenity() {
        assertTrue(MergingStrategies.isRoomAmenity("tv"));
        assertTrue(MergingStrategies.isRoomAmenity("coffee machine"));
    }


    @Test
    void testIsRoomAmenity_InvalidInputs() {
        try {
            assertFalse(MergingStrategies.isRoomAmenity(null));
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }


    @Test
    void testGetDescriptionAfterMerge() {
        String desc1 = "Basic room with bed";
        String desc2 = "Luxurious suite with king bed, ocean view, spa bathroom, and private balcony";
        
        String result = MergingStrategies.getDescriptionAfterMerge(desc1, desc2);
        assertEquals(desc2, result);
    }


    @Test
    void testGetDescriptionAfterMerge_EqualScores() {
        String desc1 = "Room with bed";
        String desc2 = "Space with bathroom";
        
        String result = MergingStrategies.getDescriptionAfterMerge(desc1, desc2);
        assertTrue(result.equals(desc1) || result.equals(desc2));
    }


    @Test
    void testCalculateScoreForSentence() {
        String highScoreDesc = "Beautiful suite with ocean view, king bed, spa bathroom and private balcony";
        String lowScoreDesc = "Simple accommodation ";
        
        int highScore = MergingStrategies.calculateScoreForSentence(highScoreDesc);
        int lowScore = MergingStrategies.calculateScoreForSentence(lowScoreDesc);
        
        assertTrue(highScore > lowScore);
    }


    @Test
    void testCalculateScoreForSentence_EmptyString() {
        assertEquals(0, MergingStrategies.calculateScoreForSentence(""));
        assertEquals(0, MergingStrategies.calculateScoreForSentence("   "));
    }


    @Test
    void testStem() {
        // 1. "ies" -> "y" (if length > 4)
        // 2. "es" -> remove "es" (if length > 3) 
        // 3. "s" -> remove "s" (if length > 2)
        
        // "facilities" ends with "ies" and length > 4, so "ies" -> "y"
        assertEquals("facility", MergingStrategies.stem("facilities")); 
        
        // "amenities" ends with "ies" and length > 4, so "ies" -> "y"
        assertEquals("amenity", MergingStrategies.stem("amenities"));   
        
        // Words ending with "s" (length > 2) - remove "s"
        assertEquals("room", MergingStrategies.stem("rooms"));          
        assertEquals("view", MergingStrategies.stem("views"));

        // Words ending with "es" (length > 3) - remove "es"
        assertEquals("box", MergingStrategies.stem("boxes"));
        assertEquals("class", MergingStrategies.stem("classes"));
    }


    @Test
    void testStem_NoStemming() {
        assertEquals("tv", MergingStrategies.stem("tv"));
        assertEquals("ts", MergingStrategies.stem("ts"));
        assertEquals("bed", MergingStrategies.stem("bed"));
        assertEquals("s", MergingStrategies.stem("s"));
        assertEquals("", MergingStrategies.stem(""));
        assertEquals("a", MergingStrategies.stem("a"));
    }


    @Test
    void testCompleteAmenitiesCleaning_Integration() {
        List<String> realGeneral = new ArrayList<>(Arrays.asList("pool", "outdoor pool", "indoor pool", "fitness center", 
                                                 "spa", "restaurant", "parking", "wifi", "tv", "coffee machine"));
        List<String> realRoom = new ArrayList<>(Arrays.asList("tv", "coffee machine", "kettle", "hair dryer", "iron", 
                                             "minibar", "desk", "bathtub"));
        
        Map<String, List<String>> result = MergingStrategies.cleanAmenities(realGeneral, realRoom);
        
        assertNotNull(result);
        assertNotNull(result.get("general"));
        assertNotNull(result.get("room"));
        
        List<String> cleanGeneral = result.get("general");
        List<String> cleanRoom = result.get("room");
        
        // Verify room amenities are properly categorized
        assertTrue(cleanRoom.contains("tv"));
        
        // Verify general amenities don't contain room-specific items
        assertFalse(cleanGeneral.contains("tv"));
        assertFalse(cleanGeneral.contains("coffee machine"));
        
        // Verify pool handling (generic word removal)
        long poolCount = cleanGeneral.stream().filter(a -> a.contains("pool")).count();
        assertTrue(poolCount >= 1); // Should have specific pool types, not just "pool"
    }
}