package com.ascenda.hotels.model.response;

import lombok.Data;

@Data
public class Location{
	private String country;
	private String address;
	private Double lng;
	private String city;
	private Double lat;
}