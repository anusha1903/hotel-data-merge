# hotel-data-merge
This application reads the hotel data from various sources and merge the data for a single hotel-id.
User can also consume hotel data via REST APIs

### Requirements:
To run this app locally, user should have below 
1. postgresql@14
2. redis
3. jdk21, maven3.9

### How to run
1. git clone feature/hotel-data-fetch
2. Make sure postgresql and redis are up & running  
3. Set the dB username and password in the environment
4. Now run HotelsApplication.java
5. Once the app is up & running, 
<br/>
    a. trigger ingestHotelData API to load the data into dB. This would parse, merge the data and store it into dB
   <br/>
    b. trigger getHotelDetails to get data either requesting by id (hotel_id) or by destination_id

### Hotel Schema
````
CREATE TABLE hotels (
id VARCHAR(50) PRIMARY KEY,
destination_id BIGINT NOT NULL,
name VARCHAR(255) NOT NULL,
description TEXT,
lat DECIMAL(10,8),
lng DECIMAL(11,8),
address VARCHAR(500),
city VARCHAR(255),
country VARCHAR(255),
amenities JSONB,            
images JSONB,               
booking_conditions JSONB    
);

CREATE INDEX idx_hotels_destination_id ON hotels(destination_id);
CREATE INDEX idx_hotels_amenities_gin ON hotels USING GIN (amenities jsonb_path_ops);
CREATE INDEX idx_hotels_images_gin ON hotels USING GIN (images jsonb_path_ops);
````