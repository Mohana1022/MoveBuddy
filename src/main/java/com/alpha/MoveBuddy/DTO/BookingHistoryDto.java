package com.alpha.MoveBuddy.DTO;


import java.util.List;

public class BookingHistoryDto {
	
	private List<RideDetailsDTO> history;
	private double TotalAmount;
	@Override
	public String toString() {
		return "BookingHistoryDto [history=" + history + ", TotalAmount=" + TotalAmount + "]";
	}
	public List<RideDetailsDTO> getHistory() {
		return history;
	}
	public void setHistory(List<RideDetailsDTO> history) {
		this.history = history;
	}
	public double getTotalAmount() {
		return TotalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		TotalAmount = totalAmount;
	}
	public BookingHistoryDto(List<RideDetailsDTO> history, double totalAmount) {
		super();
		this.history = history;
		TotalAmount = totalAmount;
	}
	public BookingHistoryDto() {
		super();
	}
	
}
