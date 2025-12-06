//package com.alpha.MoveBuddy.entity;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//
//@Entity
//public class Booking {
//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private int id;
//    @ManyToOne
//	private Customer customer;
//    @ManyToOne
//	private Driver driver;
//	private String sourceLoc;
//	private String destinationLoc;
//	private int distanceTravelled;
//	private int fare;
//	private int estimatedTime;
//	private int bookingDate;
//	
//	private Payment payment;
//	public Customer getCustomer() {
//		return customer;
//	}
//	public void setCustomer(Customer customer) {
//		this.customer = customer;
//	}
//	public Driver getDriver() {
//		return driver;
//	}
//	public void setDriver(Driver driver) {
//		this.driver = driver;
//	}
//	public String getSourceLoc() {
//		return sourceLoc;
//	}
//	public void setSourceLoc(String sourceLoc) {
//		this.sourceLoc = sourceLoc;
//	}
//	public String getDestinationLoc() {
//		return destinationLoc;
//	}
//	public void setDestinationLoc(String destinationLoc) {
//		this.destinationLoc = destinationLoc;
//	}
//	public int getDistanceTravelled() {
//		return distanceTravelled;
//	}
//	public void setDistanceTravelled(int distanceTravelled) {
//		this.distanceTravelled = distanceTravelled;
//	}
//	public int getFare() {
//		return fare;
//	}
//	public void setFare(int fare) {
//		this.fare = fare;
//	}
//	public int getEstimatedTime() {
//		return estimatedTime;
//	}
//	public void setEstimatedTime(int estimatedTime) {
//		this.estimatedTime = estimatedTime;
//	}
//	public int getBookingDate() {
//		return bookingDate;
//	}
//	public void setBookingDate(int bookingDate) {
//		this.bookingDate = bookingDate;
//	}
//	public Payment getPayment() {
//		return payment;
//	}
//	public void setPayment(Payment payment) {
//		this.payment = payment;
//	}
//	public Booking(Customer customer, Driver driver, String sourceLoc, String destinationLoc, int distanceTravelled,
//			int fare, int estimatedTime, int bookingDate, Payment payment) {
//		super();
//		this.customer = customer;
//		this.driver = driver;
//		this.sourceLoc = sourceLoc;
//		this.destinationLoc = destinationLoc;
//		this.distanceTravelled = distanceTravelled;
//		this.fare = fare;
//		this.estimatedTime = estimatedTime;
//		this.bookingDate = bookingDate;
//		this.payment = payment;
//	}
//	public Booking() {
//		super();
//	}
//	@Override
//	public String toString() {
//		return "Booking [id=" + id + ", customer=" + customer + ", driver=" + driver + ", sourceLoc=" + sourceLoc
//				+ ", destinationLoc=" + destinationLoc + ", distanceTravelled=" + distanceTravelled + ", fare=" + fare
//				+ ", estimatedTime=" + estimatedTime + ", bookingDate=" + bookingDate + ", payment=" + payment + "]";
//	}
//	
//
//}
