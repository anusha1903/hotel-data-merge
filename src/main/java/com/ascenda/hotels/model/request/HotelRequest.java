package com.ascenda.hotels.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequest {

    @Schema(
            description = "Hotel ID",
            example = "f8c9",
            defaultValue = ""
    )
    @Size(max = 50, message = "Hotel ID must not exceed 50 characters")
    private String id;

    @Schema(
            description = "Destination ID",
            example = "1122",
            defaultValue = ""
    )
    @Positive(message = "Destination ID must be positive when provided")
    private Long destinationId;

    // getters and setters
}
