package com.dwoodhouse.streetcrimes;

public class StreetCrimeData {
/*
 * Example JSON response for reference
 * {
        "category": "anti-social-behaviour",
        "location_type": "Force",
        "location": {
            "latitude": "52.635767",
            "street": {
                "id": 883291,
                "name": "On or near High Street"
            },
            "longitude": "-1.135267"
        },
        "context": "",
        "outcome_status": null,
        "persistent_id": "",
        "id": 20605774,
        "location_subtype": "",
        "month": "2013-01"
    }
 */
	
	// Gson creates objects out of JSON arrays!  Plug in required variable names here and Gson will automatically create the object for us
    
	private String category;
	private LocationData location;
	
	class LocationData
	{
		public double latitude;
		public double longitude;
		public Street street;
	}
	
	class Street
	{
		public int id;
		public String name;
	}

	public String getCategory() {
		return category;
	}

	public LocationData getLocation() {
		return location;
	}
}
