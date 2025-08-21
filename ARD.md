# Architecture Design Record

| Architecture decision |                                                                                                                                                                                                                                                                |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `WHAT`                | Read hotel data from the source and rebuild the a map with <hotel_id, <hotel_data_source, hotel_data_json_node>> format.<br/>Now for each hotelId, sequentially, read source_data_node, set it to final response </br> object using various merging strategies |
| `WHY`                 | The Hotel Json structure is different for different sources. To avoid multiple operations for backtrack for hotel_id, this approach is followed                                                                                                                |


# Future Enhancement
1. Say ResponseItem is the final POJO.
2. Data from each upstream is read asynchronously and store it in maps
3. Now after recreating the map based on the hotel_id (existing way),
        for each hotel_id, the processing of source data mapping would be done in parallel.
4. For this, each upstream would be mapped with each ResponseItem in parallel say,
   if If i have 2 upstream objects for a specific hotelId, 2 resp. objects are created in parallel
5. Each attribute of these (n) objects are merged in parallel