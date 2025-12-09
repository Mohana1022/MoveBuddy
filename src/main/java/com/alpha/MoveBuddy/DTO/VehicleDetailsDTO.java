package com.alpha.MoveBuddy.DTO;

import org.springframework.beans.factory.annotation.Autowired;

import com.alpha.MoveBuddy.entity.Vehicle;

public class VehicleDetailsDTO {
	@Autowired
	private Vehicle v;
	private int fare;
	private int estimatedTime;
	
	public Vehicle getV() {
		return v;
	}
	public void setV(Vehicle v) {
		this.v = v;
	}
	public int getFare() {
		return fare;
	}
	public void setFare(int fare) {
		this.fare = fare;
	}
	public int getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimatedTime(int estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	public VehicleDetailsDTO(Vehicle v, int fare, int estimatedTime) {
		super();
		this.v = v;
		this.fare = fare;
		this.estimatedTime = estimatedTime;
	}
	public VehicleDetailsDTO() {
		super();
	}
	@Override
	public String toString() {
		return "VehicleDetailsDTO [v=" + v + ", fare=" + fare + ", estimatedTime=" + estimatedTime + "]";
	}
	
	
	
	
	

}
