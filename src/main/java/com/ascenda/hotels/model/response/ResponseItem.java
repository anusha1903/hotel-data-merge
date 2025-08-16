package com.ascenda.hotels.model.response;

import java.util.List;
import lombok.Data;

@Data
public class ResponseItem{
	private Amenities amenities;
	private Images images;
	private int destinationId;
	private String name;
	private String description;
	private Location location;
	private String id;
	private List<String> bookingConditions;
}