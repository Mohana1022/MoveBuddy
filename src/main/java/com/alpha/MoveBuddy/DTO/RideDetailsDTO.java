package com.alpha.MoveBuddy.DTO;

public class RideDetailsDTO {
	
	private String FromLoc;
	private String ToLoc;
	private double Distance;
	private int fare;
	public String getFromLoc() {
		return FromLoc;
	}
	public void setFromLoc(String fromLoc) {
		FromLoc = fromLoc;
	}
	public String getToLoc() {
		return ToLoc;
	}
	public void setToLoc(String toLoc) {
		ToLoc = toLoc;
	}
	public double getDistance() {
		return Distance;
	}
	public void setDistance(double distance) {
		Distance = distance;
	}
	public int getFare() {
		return fare;
	}
	public void setFare(int fare) {
		this.fare = fare;
	}
	@Override
	public String toString() {
		return "RideDetailsDTO [FromLoc=" + FromLoc + ", ToLoc=" + ToLoc + ", Distance=" + Distance + ", fare=" + fare
				+ "]";
	}
	public RideDetailsDTO(String fromLoc, String toLoc, double distance, int fare) {
		super();
		FromLoc = fromLoc;
		ToLoc = toLoc;
		Distance = distance;
		this.fare = fare;
	}
	public RideDetailsDTO() {
		super();
	}
	

}
