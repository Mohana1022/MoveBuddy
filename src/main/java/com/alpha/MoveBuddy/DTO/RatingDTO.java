package com.alpha.MoveBuddy.DTO;

import jakarta.validation.constraints.*;

public class RatingDTO {

    @NotNull(message = "Booking ID is required")
    private Integer bookingId;

    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating must be at most 5 stars")
    private int stars;

    @Size(max = 500, message = "Comment must not exceed 500 characters")
    private String comment;

    public RatingDTO() {}

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
