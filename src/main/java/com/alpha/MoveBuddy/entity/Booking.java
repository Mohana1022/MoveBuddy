package com.alpha.MoveBuddy.entity;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private String sourceLoc;
    private String destinationLoc;
    private int distanceTravelled;
    private int fare;
    private int estimatedTime;

    @Column(updatable = false)
    private Date bookingDate;

    private String bookingStatus = "PENDING";
    private String paymentStatus = "NOT PAID";

    // OTP FIELD
    @Column(length = 4)
    private String otp;

    @PrePersist
    protected void onCreate() {
        this.bookingDate = new Date(System.currentTimeMillis());
    }

    // getters & setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public String getSourceLoc() { return sourceLoc; }
    public void setSourceLoc(String sourceLoc) { this.sourceLoc = sourceLoc; }

    public String getDestinationLoc() { return destinationLoc; }
    public void setDestinationLoc(String destinationLoc) { this.destinationLoc = destinationLoc; }

    public int getDistanceTravelled() { return distanceTravelled; }
    public void setDistanceTravelled(int distanceTravelled) { this.distanceTravelled = distanceTravelled; }

    public int getFare() { return fare; }
    public void setFare(int fare) { this.fare = fare; }

    public int getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(int estimatedTime) { this.estimatedTime = estimatedTime; }

    public Date getBookingDate() { return bookingDate; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOtp() { return otp; }
    
    public void setOtp(String otp) { this.otp = otp; }

	public Booking(Customer customer, Vehicle vehicle, String sourceLoc, String destinationLoc, int distanceTravelled,
			int fare, int estimatedTime, Date bookingDate, String bookingStatus, String paymentStatus, String otp) {
		super();
		this.customer = customer;
		this.vehicle = vehicle;
		this.sourceLoc = sourceLoc;
		this.destinationLoc = destinationLoc;
		this.distanceTravelled = distanceTravelled;
		this.fare = fare;
		this.estimatedTime = estimatedTime;
		this.bookingDate = bookingDate;
		this.bookingStatus = bookingStatus;
		this.paymentStatus = paymentStatus;
		this.otp = otp;
	}

	public Booking() {
		super();
	}

	@Override
	public String toString() {
		return "Booking [id=" + id + ", customer=" + customer + ", vehicle=" + vehicle + ", sourceLoc=" + sourceLoc
				+ ", destinationLoc=" + destinationLoc + ", distanceTravelled=" + distanceTravelled + ", fare=" + fare
				+ ", estimatedTime=" + estimatedTime + ", bookingDate=" + bookingDate + ", bookingStatus="
				+ bookingStatus + ", paymentStatus=" + paymentStatus + ", otp=" + otp + "]";
	}
    
    
    
}
