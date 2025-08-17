package com.ascenda.hotels.model.response;

import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Data
public class ResponseItem{
	private Amenities amenities;
	private Images images;
	private Integer destinationId;
	private String name;
	private String description;
	private Location location;
	private String id;
	private List<String> bookingConditions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ResponseItem that = (ResponseItem) o;

        return new EqualsBuilder().append(destinationId, that.destinationId).append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(destinationId).append(id).toHashCode();
    }
}