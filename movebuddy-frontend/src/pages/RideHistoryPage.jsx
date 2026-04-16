import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { customerAPI, driverAPI } from '../services/api';
import toast from 'react-hot-toast';

const RideHistoryPage = ({ userType }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        let res;
        if (userType === 'CUSTOMER') {
          res = await customerAPI.getBookingHistory(user.mobileNo);
        } else {
          res = await driverAPI.getBookingHistory(user.mobileNo);
        }
        setHistory(res.data.data.history || []);
      } catch (err) {
        toast.error("Failed to load history.");
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [user.mobileNo, userType]);

  if (loading) return <div className="loading-wrap"><div className="spinner"></div></div>;

  return (
    <div className="container">
      <div className="page-header">
        <h1 className="page-title">Ride History</h1>
        <p className="page-subtitle">Viewing all past and present activity</p>
      </div>

      <div className="card">
        {history.length === 0 ? (
          <div className="text-center py-10">
            <p className="text-secondary">No rides found in your history.</p>
          </div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Date/Time</th>
                  <th>Route</th>
                  <th>Distance</th>
                  <th>Fare</th>
                  <th>Status</th>
                  <th>{userType === 'CUSTOMER' ? 'Driver' : 'Customer'}</th>
                </tr>
              </thead>
              <tbody>
                {history.map((ride, idx) => (
                  <tr key={ride.id || idx}>
                    <td>
                      {ride.bookingDate ? new Date(ride.bookingDate).toLocaleDateString() : 'N/A'}
                    </td>
                    <td>
                      <div style={{ fontSize: '0.85rem' }}>
                        <span style={{ color: 'var(--success)' }}>●</span> {ride.fromLoc || ride.sourceLoc}<br/>
                        <span style={{ color: 'var(--error)' }}>●</span> {ride.toLoc || ride.destinationLoc}
                      </div>
                    </td>
                    <td>{ride.distance !== undefined ? ride.distance : ride.distanceTravelled} km</td>
                    <td><strong>₹{ride.fare}</strong></td>
                    <td>
                      <span className={`badge ${ride.status === 'COMPLETED' ? 'badge-success' : 'badge-info'}`}>
                        {ride.status}
                      </span>
                    </td>
                    <td>
                      {userType === 'CUSTOMER' ? (
                        <>
                          {ride.driverName || 'N/A'}<br/>
                          {ride.status === 'COMPLETED' && (
                            <button 
                              className="btn btn-primary btn-sm mt-1" 
                              style={{ padding: '2px 8px', fontSize: '0.75rem' }}
                              onClick={() => navigate(`/track-ride/${ride.id}`)}
                            >
                              Rate Ride
                            </button>
                          )}
                        </>
                      ) : (
                        ride.customerName || 'N/A'
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default RideHistoryPage;
