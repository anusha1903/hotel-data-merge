package com.ascenda.hotels.model.response;

import java.util.List;
import lombok.Data;

@Data
public class Amenities{
	private List<String> general;
	private List<String> room;
}