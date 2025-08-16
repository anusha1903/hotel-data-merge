package com.ascenda.hotels.model;

import lombok.Data;

import java.util.List;

@Data
public class Amenities {
    List<String> general;
    List<String> room;
}
