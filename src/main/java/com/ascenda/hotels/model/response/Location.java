package com.ascenda.hotels.model.response;

import lombok.Data;

@Data
public class Location{
	private String country;
	private String address;
	private Object lng;
	private String city;
	private Object lat;
}