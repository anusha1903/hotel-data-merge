package com.ascenda.hotels.Util;

import java.util.Arrays;
import java.util.List;

public class MergingStrategies {

    private static final List<String> roomAmenities = List.of("aircon", "tv", "coffee machine", "kettle", "hair dryer", "iron", "bathtub", "minibar",
            "room service", "desk");
    private static final List<String> sentenceWeightagewords = Arrays.asList("bed", "king", "queen", "view", "bathroom", "balcony", "ocean",
            "suite", "spacious", "room", "wifi", "kitchen", "ac", "air conditioning", "privacy", "garden", "sanctuary", "station", "villa",
            "service", "restaurant", "pool", "parking", "fridge", "microwave", "sundeck", "verandahs", "complimentary", "spa", "activity"
    );

    public static String getStringWithMaxLength(String str1, String str2) {
        return str1.length() > str2.length() ? str1 : str2;
    }

    public static boolean isRoomAmenity(String facility) {
        return roomAmenities.contains(facility);
    }

    public static String getDescriptionAfterMerge(String desc1, String desc2) {
        int score1 = 0, score2 = 0;
        score1 = calculateScoreForSentence(desc1);
        score2 = calculateScoreForSentence(desc2);
        return score1 > score2 ? desc1 : desc2;
    }

    public static int calculateScoreForSentence(String description){
        int score = 0;
        String[] descInLowerCase = description.toLowerCase().split("\\W+");
        for(String word : descInLowerCase){
            String stemmedWord = stem(word).trim();
            if(sentenceWeightagewords.contains(stemmedWord) || sentenceWeightagewords.contains(word)){
                score++;
            }
        }
        return score;
    }

    public static String stem(String word) {
        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        } else if (word.endsWith("es") && word.length() > 3) {
            return word.substring(0, word.length() - 2);
        } else if (word.endsWith("s") && word.length() > 2) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
}
