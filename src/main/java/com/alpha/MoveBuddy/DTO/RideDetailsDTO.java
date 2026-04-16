package com.alpha.MoveBuddy.DTO;
import java.util.Date;

public class RideDetailsDTO {
    private int id;
    private String fromLoc;
    private String toLoc;
    private double distance;
    private int fare;
    private String status;
    private String driverName;
    private String customerName;
    private Date bookingDate;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFromLoc() { return fromLoc; }
    public void setFromLoc(String fromLoc) { this.fromLoc = fromLoc; }
    public String getToLoc() { return toLoc; }
    public void setToLoc(String toLoc) { this.toLoc = toLoc; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public int getFare() { return fare; }
    public void setFare(int fare) { this.fare = fare; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }

    public RideDetailsDTO() {}
}
