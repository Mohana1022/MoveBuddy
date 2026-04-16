import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { customerAPI, driverAPI } from '../services/api';
import toast from 'react-hot-toast';

const ProfilePage = () => {
  const { user, isCustomer, isDriver, isAdmin } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        let res;
        if (isCustomer) res = await customerAPI.getProfile(user.mobileNo);
        else if (isDriver) res = await driverAPI.getProfile(user.mobileNo);
        
        if (res) setProfile(res.data.data);
      } catch (err) {
        toast.error("Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };
    if (!isAdmin) fetchProfile();
    else { setProfile({ name: 'System Admin', role: 'ADMIN' }); setLoading(false); }
  }, [user, isCustomer, isDriver, isAdmin]);

  if (loading) return <div className="loading-wrap"><div className="spinner"></div></div>;

  return (
    <div className="container" style={{ maxWidth: '600px' }}>
      <div className="page-header text-center">
        <h1 className="page-title">Profile Settings</h1>
      </div>

      <div className="card">
        <div className="text-center mb-6">
          <div style={{ 
            width: '100px', height: '100px', borderRadius: '50%', background: 'var(--gradient-primary)', 
            margin: '0 auto 16px', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '3rem' 
          }}>
            {(profile?.name || profile?.driverName || 'U').charAt(0)}
          </div>
          <h2>{profile?.name || profile?.driverName}</h2>
          <div className="badge badge-primary">{user.role}</div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="form-group">
            <label className="form-label">Phone Number</label>
            <input type="text" className="form-input" value={user.mobileNo || ''} readOnly disabled />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input type="text" className="form-input" value={profile?.emailId || profile?.mailId || ''} readOnly disabled />
          </div>

          {isDriver && (
            <>
              <div className="form-group">
                <label className="form-label">License Number</label>
                <input type="text" className="form-input" value={profile?.licenseNo || ''} readOnly disabled />
              </div>
              <div className="form-group">
                <label className="form-label">Vehicle Details</label>
                <input type="text" className="form-input" value={`${profile?.vehicle?.vehicleName} - ${profile?.vehicle?.vehicleNo}`} readOnly disabled />
              </div>
            </>
          )}

          {isCustomer && (
            <div className="form-group">
              <label className="form-label">Home Location (Last Seen)</label>
              <input type="text" className="form-input" value={profile?.currentLoc || 'Not Set'} readOnly disabled />
            </div>
          )}

          <div className="alert alert-info" style={{ marginTop: '20px' }}>
            Profile editing is currently managed by Admin. Please contact support to update your details.
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
