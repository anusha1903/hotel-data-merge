package com.ascenda.hotels.repository.entity;

import com.ascenda.hotels.model.response.Images;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "hotels")
@Data
public class Hotel {

    @Id
    private String id;

    @Column(name = "destination_id", nullable = false)
    private Long destinationId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double lat;
    private Double lng;

    private String address;
    private String city;
    private String country;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<String>> amenities;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Images images;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<String> bookingConditions;

    // Getters and setters
}
