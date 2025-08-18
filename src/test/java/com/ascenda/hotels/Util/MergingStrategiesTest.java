package com.ascenda.hotels.Util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MergingStrategiesTest {

    @Test
    public void cleanAmenitiesTest() {
        List<String> genAmenities = new ArrayList<>(List.of("pool","tub", "outdoor pool", "indoor pool"));
        List <String> roomAmenities = new ArrayList<>(List.of("tv","coffee machine", "kettle", "hair dryer", "iron"));
        MergingStrategies.cleanAmenities(genAmenities, roomAmenities);


    }

}