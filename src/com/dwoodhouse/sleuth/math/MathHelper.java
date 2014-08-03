package com.dwoodhouse.sleuth.math;

import com.google.android.gms.maps.model.LatLng;

public class MathHelper {
	//private static final String TAG = "LatLngHelper";
	private static final int EARTH_RADIUS = 6371; // in km

	/*
	 * Returns the destination point of a distance from a given LatLng
	 */
	public static LatLng findDestinationWithDistance(double d, double bearing, LatLng origin)
	{
		double originLat = Math.toRadians(origin.latitude);
		double originLng = Math.toRadians(origin.longitude);
		double lat, lng;
		double ad = d / EARTH_RADIUS; // Angular distance
		double bearingRad = Math.toRadians(bearing);
		
		lat = Math.asin( Math.sin(originLat) * Math.cos(ad) + Math.cos(originLat) * Math.sin(ad) * Math.cos(bearingRad) );
		
		lng = originLng + Math.atan2( Math.sin(bearingRad) * Math.sin(ad) * Math.cos(originLat),
									  Math.cos(ad) - Math.sin(originLat) * Math.sin(lat) );
		
		return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));		
	}
	
	public static double ToKM(double miles)
	{
		return miles * 0.6;
	}
	
	public static double ToMiles(double km)
	{
		return km * 1.6;
	}
}
