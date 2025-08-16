package com.ascenda.hotels.model.response;

import java.util.List;
import lombok.Data;

@Data
public class Images{
	private List<AmenitiesItem> amenities;
	private List<RoomsItem> rooms;
	private List<SiteItem> site;
}