import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { bookingAPI, customerAPI } from '../services/api';
import toast from 'react-hot-toast';

const RiderDashboard = () => {
  const { user } = useAuth();
  const [activeRide, setActiveRide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [profileRes, activeRes] = await Promise.allSettled([
          customerAPI.getProfile(user.mobileNo),
          bookingAPI.getActiveBooking(user.mobileNo)
        ]);

        if (profileRes.status === 'fulfilled') {
          setProfile(profileRes.value.data.data);
        }

        if (activeRes.status === 'fulfilled' && activeRes.value.data.statuscode === 200) {
          setActiveRide(activeRes.value.data.data);
        }
      } catch (err) {
        console.error("Error fetching dashboard data", err);
      } finally {
        setLoading(false);
      }
    };

    if (user?.mobileNo) fetchData();
  }, [user]);

  if (loading) return <div className="loading-wrap"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="page-header">
        <h1 className="page-title">Hello, {profile?.name || 'Rider'}!</h1>
        <p className="page-subtitle">Where are you heading today?</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-label">Current City</span>
          <span className="stat-value" style={{ fontSize: '1.2rem' }}>{profile?.currentLoc || 'Searching...'}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Wallet Balance</span>
          <span className="stat-value" style={{ fontSize: '1.2rem' }}>₹0.00</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Safety Rating</span>
          <span className="stat-value" style={{ fontSize: '1.2rem' }}>5.0 ⭐</span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
        <div className="card">
          <h3 className="mb-4">Quick Actions</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <Link to="/book-ride" className="btn btn-primary" style={{ padding: '24px', flexDirection: 'column' }}>
              <span style={{ fontSize: '1.5rem' }}>🚗</span>
              Book a Ride
            </Link>
            <Link to="/rider-history" className="btn btn-secondary" style={{ padding: '24px', flexDirection: 'column' }}>
              <span style={{ fontSize: '1.5rem' }}>📜</span>
              Ride History
            </Link>
          </div>
        </div>

        <div className="card">
          <h3 className="mb-4">Current Status</h3>
          {activeRide ? (
            <div className="animate-pulse">
              <div className="badge badge-primary mb-2">Ride in Progress</div>
              <p><strong>From:</strong> {activeRide.sourceLoc}</p>
              <p><strong>To:</strong> {activeRide.destinationLoc}</p>
              <div className="mt-4">
                <Link to={`/track-ride/${activeRide.id}`} className="btn btn-primary btn-sm btn-block">
                  View Live Tracking
                </Link>
              </div>
            </div>
          ) : (
            <div className="text-center" style={{ padding: '20px' }}>
              <p className="text-secondary">No active rides at the moment.</p>
              <Link to="/book-ride" className="btn btn-outline btn-sm mt-4">
                Find a Ride
              </Link>
            </div>
          )}
        </div>
      </div>

      {profile?.penality > 0 && (
        <div className="alert alert-warning mt-6">
          <strong>Notice:</strong> You have {profile.penality} cancellation marks on your profile. Frequent cancellations may lead to account suspension.
        </div>
      )}
    </div>
  );
};

export default RiderDashboard;
