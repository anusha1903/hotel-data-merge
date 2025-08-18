package com.ascenda.hotels.model.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SiteItem{
	private String link;

    @JsonProperty("description")
    @JsonAlias({"caption"})
	private String description;
}