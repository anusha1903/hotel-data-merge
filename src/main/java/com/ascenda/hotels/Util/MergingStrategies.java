package com.ascenda.hotels.Util;

import java.util.*;
import java.util.stream.Collectors;

public class MergingStrategies {

    private static final Set<String> roomHotelAmenities = Set.of("aircon", "tv", "coffee machine", "kettle",
            "hair dryer", "iron", "bathtub", "minibar",
            "room service", "desk", "tub");
    private static final List<String> sentenceWeightagewords = Arrays.asList("bed", "king", "queen", "view", "bathroom", "balcony", "ocean",
            "suite", "spacious", "room", "wifi", "kitchen", "ac", "air conditioning", "privacy", "garden", "sanctuary", "station", "villa",
            "service", "restaurant", "pool", "parking", "fridge", "microwave", "sundeck", "verandahs", "complimentary", "spa", "activity",
            "government", "business", "airport", "continental"
    );
    private static final Set<String> genericWords = Set.of("pool", "bed", "room", "tub");

    public static String getStringWithMaxLength(String str1, String str2) {
        return str1.length() > str2.length() ? str1 : str2;
    }

    public static Map<String,List<String>> cleanAmenities(List<String> genAmenities, List<String> roomAmenities) {
        List<String> cleanGeneralAmenities = new ArrayList<>();
        List<String> cleanRoomAmenities = new ArrayList<>();

        List<String> generalAmenities = new ArrayList<>(genAmenities);

        Map<String,List<String>> cleanAmenities = new HashMap<>();
//        System.out.println("roomAmenities : "+Arrays.toString(roomAmenities.toArray()));
//        System.out.println("genAmenities : "+Arrays.toString(genAmenities.toArray()));

        for (String genAmenity : generalAmenities) {
            // checking if a specific amenity exists in genAmenities list
             Set<String> newAmenities = genAmenities.stream()
                    .filter(s -> s.equalsIgnoreCase(genAmenity) || s.contains(genAmenity) || genAmenity.contains(s))
                    .collect(Collectors.toSet());
//            System.out.println("newAmenities : "+Arrays.toString(newAmenities.toArray()));

            genAmenities.removeAll(newAmenities);
//            System.out.println("after genAmenities remove : "+Arrays.toString(genAmenities.toArray()));
             // say list has pool and outdoor pool, the size of newAmenities is 2. Here we are removing generic word pool
            // for this we are checking the size
             if(genericWords.contains(genAmenity) && newAmenities.size() > 1){
                 newAmenities.remove(genAmenity);
             }

//            System.out.println("after generic : "+Arrays.toString(newAmenities.toArray()));
//            System.out.println("after roomHotelAmenities : "+Arrays.toString(roomAmenities.toArray()));

             if(roomHotelAmenities.containsAll(newAmenities)){
                 cleanRoomAmenities.addAll(newAmenities);
                 roomAmenities.removeAll(newAmenities);
             } else {
                 cleanGeneralAmenities.addAll(newAmenities);
             }
        }

//        System.out.println("after remove roomAmenities : "+Arrays.toString(roomAmenities.toArray()));
//        System.out.println("after remove cleanRoomAmenities : "+Arrays.toString(cleanRoomAmenities.toArray()));
//        System.out.println("after remove cleanGeneralAmenities : "+Arrays.toString(cleanGeneralAmenities.toArray()));

        cleanRoomAmenities.addAll(roomAmenities);
        cleanGeneralAmenities = getNormalizedList(cleanGeneralAmenities);
        cleanRoomAmenities = getNormalizedList(cleanRoomAmenities);
        cleanAmenities.put("general", cleanGeneralAmenities);
        cleanAmenities.put("room", cleanRoomAmenities);

        return cleanAmenities;
    }

    public static List<String> getNormalizedList(List<String> listToNormalize){
        return listToNormalize.stream()
                .filter( firstStr -> listToNormalize.stream()
                        .noneMatch(secStr -> !firstStr.equalsIgnoreCase(secStr) && secStr.replaceAll("\\s+", "").equalsIgnoreCase(firstStr.replaceAll("\\s+", "")) && secStr.length() > firstStr.length()))
                .collect(Collectors.toList());
    }

    public static boolean isRoomAmenity(String facility) {
        return roomHotelAmenities.contains(facility);
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
