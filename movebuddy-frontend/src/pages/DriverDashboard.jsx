import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { driverAPI } from '../services/api';
import toast from 'react-hot-toast';

const DriverDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [incomingRides, setIncomingRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isOnline, setIsOnline] = useState(false);
  const [activeRide, setActiveRide] = useState(null);
  const [otpInput, setOtpInput] = useState('');
  
  const [stats, setStats] = useState({ earnings: 0, rides: 0 });
  const [history, setHistory] = useState([]);

  const fetchData = async () => {
    try {
      const profileRes = await driverAPI.getProfile(user.mobileNo);
      const data = profileRes.data.data;
      setProfile(data);
      setIsOnline(data.status === 'Available');
      
      // Fetch stats from history
      const historyRes = await driverAPI.getBookingHistory(user.mobileNo);
      if (historyRes.data.statuscode === 200) {
        const histData = historyRes.data.data;
        const histList = histData.history || [];
        setHistory(histList);
        setStats({
          earnings: histData.totalAmount || 0,
          rides: histList.filter(r => r.status === 'COMPLETED').length || 0
        });
      }

      const ridesRes = await driverAPI.getIncomingRides(user.mobileNo);
      const rides = ridesRes.data.data;
      const ongoing = rides.find(r => r.bookingStatus === 'IN_PROGRESS' || (r.bookingStatus === 'booked' && r.vehicle?.id === data.vehicle?.id));
      
      if (ongoing) {
        setActiveRide(ongoing);
      } else {
        setIncomingRides(rides.filter(r => r.bookingStatus === 'booked'));
      }
    } catch (err) {
      console.error("Driver data error", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, [user.mobileNo]);

  const handleToggleOnline = async () => {
    try {
      await driverAPI.toggleAvailability(user.mobileNo);
      setIsOnline(!isOnline);
      toast.success(!isOnline ? "You are now Online!" : "You are now Offline");
    } catch (err) {
      console.error("Toggle Error:", err);
      toast.error(err.response?.data?.message || "Failed to toggle status.");
    }
  };

  const handleStartRide = async (bookingId) => {
    if (!otpInput) {
      toast.error("Please enter the OTP provided by customer.");
      return;
    }
    
    try {
      await driverAPI.validateOtp(bookingId, otpInput);
      toast.success("OTP Verified! Drive safe.");
      setOtpInput('');
      fetchData();
    } catch (err) {
      toast.error("Invalid OTP. Please check with customer.");
    }
  };

  const handleCompleteRide = async (bookingId) => {
    try {
      await driverAPI.completeRide(bookingId, "CASH");
      toast.success("Ride Completed! Payment processed.");
      setActiveRide(null);
      fetchData();
    } catch (err) {
      toast.error("Failed to complete ride.");
    }
  };

  if (loading) return <div className="loading-wrap"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="page-header flex justify-between items-center">
        <div>
          <h1 className="page-title">Driver Console</h1>
          <p className="page-subtitle">{profile?.name} • {profile?.vehicle?.name} ({profile?.vehicle?.vehicleNo})</p>
        </div>
        
        <div className="availability-toggle">
          <span style={{ fontSize: '0.9rem', fontWeight: '600' }}>
            {isOnline ? 'ONLINE' : 'OFFLINE'}
          </span>
          <label className="toggle-switch">
            <input type="checkbox" checked={isOnline} onChange={handleToggleOnline} />
            <span className="toggle-slider"></span>
          </label>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-label">Total Earnings</span>
          <span className="stat-value">₹{stats.earnings}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Rides Done</span>
          <span className="stat-value">{stats.rides}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Rating</span>
          <span className="stat-value">5.0 ⭐</span>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '24px' }}>
        {activeRide ? (
          <div className="card" style={{ borderColor: 'var(--primary)', borderWidth: '2px' }}>
            <div className="badge badge-primary mb-4">Current Active Ride</div>
            <div className="flex justify-between items-start">
              <div>
                <h2>{activeRide.customer?.name}</h2>
                <p className="text-secondary">{activeRide.customer?.mobileNo}</p>
                <div className="mt-4">
                  <p><strong>From:</strong> {activeRide.sourceLoc}</p>
                  <p><strong>To:</strong> {activeRide.destinationLoc}</p>
                </div>
              </div>
              <div className="text-right">
                <p className="text-secondary">Expected Fare</p>
                <h2 style={{ color: 'var(--primary)' }}>₹{activeRide.fare}</h2>
              </div>
            </div>

            <div className="mt-6 border-top pt-6">
              {activeRide.bookingStatus === 'booked' ? (
                <div className="flex gap-2">
                  <input 
                    type="text" 
                    className="form-input" 
                    placeholder="Enter Customer OTP" 
                    style={{ maxWidth: '200px' }}
                    value={otpInput}
                    onChange={(e) => setOtpInput(e.target.value)}
                  />
                  <button className="btn btn-primary" onClick={() => handleStartRide(activeRide.id)}>
                    Verify & Start Ride
                  </button>
                </div>
              ) : (
                <button className="btn btn-success btn-block" onClick={() => handleCompleteRide(activeRide.id)}>
                  Finish Ride & Collect Cash
                </button>
              )}
            </div>
          </div>
        ) : (
          <div className="card">
            <h3 className="mb-4">New Ride Requests ({incomingRides.length})</h3>
            {!isOnline ? (
              <div className="text-center py-6">
                <p className="text-secondary">Go Online to see ride requests in {profile?.vehicle?.currentCity}</p>
              </div>
            ) : incomingRides.length === 0 ? (
              <div className="text-center py-6">
                <div className="spinner spinner-sm mx-auto mb-4"></div>
                <p className="text-secondary">Searching for rides in {profile?.vehicle?.currentCity}...</p>
              </div>
            ) : (
              <div className="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>Customer</th>
                      <th>Route</th>
                      <th>Distance</th>
                      <th>Fare</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {incomingRides.map(ride => (
                      <tr key={ride.id} className="animate-fadeIn">
                        <td>{ride.customer?.name}</td>
                        <td>
                          <div style={{ fontSize: '0.8rem' }}>
                            <span style={{ color: 'var(--success)' }}>●</span> {ride.sourceLoc.substring(0, 20)}...<br/>
                            <span style={{ color: 'var(--error)' }}>●</span> {ride.destinationLoc.substring(0, 20)}...
                          </div>
                        </td>
                        <td>{ride.distanceTravelled} km</td>
                        <td><strong>₹{ride.fare}</strong></td>
                        <td>
                          <button className="btn btn-primary btn-sm" onClick={() => setActiveRide(ride)}>
                            Accept Ride
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </div>
      {/* Recent Activity Section */}
      <section className="mt-10">
        <div className="flex justify-between items-center mb-4">
          <h2 className="section-title" style={{ fontSize: '1.25rem' }}>Recent Rides</h2>
          <button className="btn-link" onClick={() => navigate('/history')}>View All</button>
        </div>

        <div className="card">
          {history.length === 0 ? (
            <div className="text-center py-6">
              <p className="text-secondary">No ride history available.</p>
            </div>
          ) : (
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Customer</th>
                    <th>Route</th>
                    <th>Fare</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {history.slice(0, 5).map((ride, idx) => (
                    <tr key={ride.id || idx}>
                      <td>{ride.customerName || 'Customer'}</td>
                      <td>
                        <div style={{ fontSize: '0.8rem' }}>
                          {ride.fromLoc} → {ride.toLoc}
                        </div>
                      </td>
                      <td>₹{ride.fare}</td>
                      <td>
                        <span className={`badge ${ride.status === 'COMPLETED' ? 'badge-success' : 'badge-info'}`}>
                          {ride.status}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </section>
    </div>
  );
};

export default DriverDashboard;
