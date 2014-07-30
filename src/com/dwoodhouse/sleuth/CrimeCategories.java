package com.dwoodhouse.sleuth;

public class CrimeCategories {

	/*
	 * [
    {
        "url": "all-crime",
        "name": "All crime and ASB"
    },
    {
        "url": "burglary",
        "name": "Burglary"
    },
    {
        "url": "anti-social-behaviour",
        "name": "Anti-social behaviour"
    },
    ...
]
	 */
	String url;
	String name;
	
	public String getId()
	{
		return url;
	}
	
	public String getName()
	{
		return name;
	}
	
	
}
