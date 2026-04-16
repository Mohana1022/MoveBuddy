import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingAPI, customerAPI } from '../services/api';
import toast from 'react-hot-toast';

const RideTrackingPage = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [otp, setOtp] = useState(null);
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [ratingLoading, setRatingLoading] = useState(false);

  const fetchStatus = useCallback(async () => {
    try {
      const response = await bookingAPI.getBookingById(id);
      const myBooking = response.data.data;
      
      if (myBooking) {
        setBooking(myBooking);
        if (myBooking.rideOtp) {
          setOtp(myBooking.rideOtp);
        }
      }
    } catch (err) {
      console.error("Tracking error", err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchStatus();
    const interval = setInterval(fetchStatus, 5000); // Poll every 5s
    return () => clearInterval(interval);
  }, [fetchStatus]);

  const handleCancel = async () => {
    if (!window.confirm("Are you sure you want to cancel this ride?")) return;
    
    try {
      await customerAPI.cancelBooking(booking.id, booking.customer.id);
      toast.success("Ride cancelled.");
      navigate('/rider-dashboard');
    } catch (err) {
      toast.error("Cancellation failed.");
    }
  };

  const handleRatingSubmit = async () => {
    if (rating === 0) {
      toast.error("Please select a star rating.");
      return;
    }

    setRatingLoading(true);
    try {
      await customerAPI.submitRating(user.mobileNo, {
        bookingId: parseInt(id),
        stars: rating,
        comment: comment
      });
      toast.success("Thank you for your feedback!");
      navigate('/rider-dashboard');
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to submit rating.");
    } finally {
      setRatingLoading(false);
    }
  };

  if (loading && !booking) return <div className="loading-wrap"><div className="spinner"></div></div>;
  if (!booking) return <div className="container">Booking not found.</div>;

  const getStatusStep = () => {
    const status = booking.bookingStatus.toLowerCase();
    if (status === 'booked') return 1;
    if (status === 'in_progress') return 2;
    if (status === 'completed') return 3;
    return 0;
  };

  const step = getStatusStep();

  return (
    <div className="container" style={{ maxWidth: '600px' }}>
      <div className="page-header text-center">
        <h1 className="page-title">Ride Tracking</h1>
        <p className="page-subtitle">Ride ID: #{id}</p>
      </div>

      <div className="card mb-6">
        <div className="ride-status-track">
          <div className={`status-step ${step >= 1 ? 'done' : ''} ${step === 1 ? 'active' : ''}`}>
            <div className="status-dot">1</div>
            <span className="status-label">Booked</span>
          </div>
          <div className={`status-step ${step >= 2 ? 'done' : ''} ${step === 2 ? 'active' : ''}`}>
            <div className="status-dot">2</div>
            <span className="status-label">On Trip</span>
          </div>
          <div className={`status-step ${step >= 3 ? 'done' : ''} ${step === 3 ? 'active' : ''}`}>
            <div className="status-dot">3</div>
            <span className="status-label">Arrival</span>
          </div>
        </div>

        {booking.bookingStatus === 'booked' && otp && (
          <div className="otp-box mt-6 animate-slideUp">
            <p className="otp-hint">Share this OTP with driver to start ride</p>
            <div className="otp-value">{otp}</div>
          </div>
        )}

        {booking.bookingStatus === 'IN_PROGRESS' && (
          <div className="alert alert-info mt-6 text-center">
            <div style={{ fontSize: '2rem', marginBottom: '8px' }}>🚕</div>
            <strong>Your ride is in progress!</strong>
            <p className="mt-2">You are heading to {booking.destinationLoc}</p>
          </div>
        )}

        {booking.bookingStatus === 'COMPLETED' && (
          <div className="animate-slideUp mt-6">
            <div className="alert alert-success text-center">
              <strong>Ride Finished!</strong>
              <p>You have arrived at your destination.</p>
            </div>
            
            <div className="card mt-4" style={{ background: 'var(--surface-2)' }}>
              <h4>Rate your Driver</h4>
              <div className="star-rating mt-2 mb-4">
                {[1, 2, 3, 4, 5].map(s => (
                  <span 
                    key={s} 
                    className={`star ${rating >= s ? 'active' : ''}`}
                    onClick={() => setRating(s)}
                  >
                    ★
                  </span>
                ))}
              </div>
              <div className="form-group">
                <textarea 
                  className="form-input" 
                  placeholder="Tell us about your trip..."
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  rows="3"
                ></textarea>
              </div>
              <button 
                className="btn btn-primary btn-block" 
                onClick={handleRatingSubmit}
                disabled={ratingLoading}
              >
                {ratingLoading ? <div className="spinner spinner-sm"></div> : 'Submit Review'}
              </button>
            </div>
          </div>
        )}

        <div className="mt-6 border-top pt-6">
          <div className="flex justify-between items-center mb-4">
            <div>
              <p className="text-secondary" style={{ fontSize: '0.8rem' }}>DRIVER</p>
              <p><strong>{booking.vehicle?.driver?.name || 'Driver'}</strong></p>
            </div>
            <div className="text-right">
              <p className="text-secondary" style={{ fontSize: '0.8rem' }}>VEHICLE</p>
              <p><strong>{booking.vehicle?.vehicleName} ({booking.vehicle?.vehicleNo})</strong></p>
            </div>
          </div>
          
          <div className="flex justify-between items-center">
            <div className="badge badge-secondary">₹{booking.fare} • {booking.paymentStatus}</div>
            
            {booking.bookingStatus === 'booked' && (
              <button className="btn btn-outline btn-sm btn-danger" onClick={handleCancel}>
                Cancel Ride
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RideTrackingPage;
