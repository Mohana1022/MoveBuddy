import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { customerAPI, bookingAPI } from '../services/api';
import toast from 'react-hot-toast';

const BookRidePage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [destination, setDestination] = useState('');
  const [loading, setLoading] = useState(false);
  const [bookingLoading, setBookingLoading] = useState(false);
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState(null);
  const [showVehicles, setShowVehicles] = useState(false);

  const [rideDist, setRideDist] = useState(0);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!destination) return;
    
    setLoading(true);
    try {
      const response = await customerAPI.getAvailableVehicles(user.mobileNo, destination);
      if (response.data.statuscode === 200) {
        const rideData = response.data.data;
        setVehicles(rideData.availableVehicles || []);
        setRideDist(rideData.distance || 0);
        setShowVehicles(true);
        if (!rideData.availableVehicles || rideData.availableVehicles.length === 0) {
          toast.error("No vehicles found in your area.");
        }
      } else {
        toast.error(response.data.message);
      }
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to search for vehicles.");
    } finally {
      setLoading(false);
    }
  };

  const handleBook = async () => {
    if (!selectedVehicle) {
      toast.error("Please select a vehicle first.");
      return;
    }

    setBookingLoading(true);
    try {
      const bookingData = {
        vehicleid: selectedVehicle.v.id,
        sourceLoc: "Current Location", 
        destinationLoc: destination,
        distanceTravelled: Math.round(rideDist),
        fare: Math.round(selectedVehicle.fare),
        estimatedTime: Math.round(selectedVehicle.estimatedTime)
      };

      const response = await bookingAPI.bookVehicle(user.mobileNo, bookingData);
      
      if (response.data.statuscode === 200) {
        toast.success("Ride booked successfully!");
        navigate(`/track-ride/${response.data.data.id}`);
      } else {
        toast.error(response.data.message || "Booking failed.");
      }
    } catch (err) {
      console.error("Booking Error:", err);
      toast.error(err.response?.data?.message || err.message || "Booking failed.");
    } finally {
      setBookingLoading(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: '800px' }}>
      <div className="page-header">
        <h1 className="page-title">Book a Ride</h1>
        <p className="page-subtitle">Enter your destination to see available vehicles</p>
      </div>

      <div className="card mb-6">
        <form onSubmit={handleSearch}>
          <div className="form-group">
            <label className="form-label">Drop Location</label>
            <div className="flex gap-2">
              <input
                type="text"
                className="form-input"
                placeholder="Where to?"
                value={destination}
                onChange={(e) => setDestination(e.target.value)}
                required
                disabled={loading || showVehicles}
              />
              {showVehicles ? (
                <button 
                  type="button" 
                  className="btn btn-secondary" 
                  onClick={() => { setShowVehicles(false); setSelectedVehicle(null); }}
                >
                  Change
                </button>
              ) : (
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? <div className="spinner spinner-sm"></div> : 'Find Rides'}
                </button>
              )}
            </div>
          </div>
        </form>
      </div>

      {showVehicles && (
        <div className="animate-slideUp">
          <h3 className="mb-4">Available Options</h3>
          <div className="vehicle-grid">
            {vehicles.map((v) => (
              <div 
                key={v.v.id} 
                className={`vehicle-card ${selectedVehicle?.v.id === v.v.id ? 'selected' : ''}`}
                onClick={() => setSelectedVehicle(v)}
              >
                <div className="flex justify-between items-start">
                  <div>
                    <div className="vehicle-type-icon">
                      {v.v.type === 'Bike' ? '🏍️' : v.v.type === 'Auto' ? '🛺' : '🚗'}
                    </div>
                    <div className="vehicle-name">{v.v.name}</div>
                    <div className="vehicle-info">{v.v.model} • {v.v.vehicleNo}</div>
                  </div>
                  <div className="text-right">
                    <div className="vehicle-fare">₹{v.fare}</div>
                    <div className="vehicle-est">{v.estimatedTime} min away</div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6">
            <button 
              className="btn btn-primary btn-block btn-lg" 
              disabled={!selectedVehicle || bookingLoading}
              onClick={handleBook}
            >
              {bookingLoading ? <div className="spinner spinner-sm"></div> : 'Confirm Booking'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default BookRidePage;
