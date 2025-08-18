package com.ascenda.hotels.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AmenitiesItem{

    @JsonProperty("link")
    @JsonAlias({"url"})
	private String link;
	private String description;
}