import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { customerAPI, driverAPI } from '../services/api';
import toast from 'react-hot-toast';

const ProfilePage = () => {
  const { user, isCustomer, isDriver, isAdmin } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    age: '',
    gender: ''
  });
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        let res;
        if (isCustomer) res = await customerAPI.getProfile(user.mobileNo);
        else if (isDriver) res = await driverAPI.getProfile(user.mobileNo);
        
        if (res) {
          const data = res.data.data;
          setProfile(data);
          setFormData({
            name: data.name || '',
            email: data.emailId || data.mailid || '',
            age: data.age || '',
            gender: data.gender || ''
          });
        }
      } catch (err) {
        toast.error("Failed to load profile.");
      } finally {
        setLoading(false);
      }
    };
    if (!isAdmin) fetchProfile();
    else { setProfile({ name: 'System Admin', role: 'ADMIN' }); setLoading(false); }
  }, [user, isCustomer, isDriver, isAdmin]);

  const handleSave = async () => {
    setSaving(true);
    try {
      const payload = {
        ...profile,
        name: formData.name,
        age: formData.age,
        gender: formData.gender,
      };

      if (isCustomer) {
        payload.emailId = formData.email;
        payload.mobileNo = user.mobileNo;
        await customerAPI.updateProfile(payload);
      } else if (isDriver) {
        payload.mailid = formData.email;
        payload.mobileno = user.mobileNo;
        await driverAPI.updateProfile(payload);
      }

      toast.success("Profile updated successfully!");
      setIsEditing(false);
      setProfile(payload);
    } catch (err) {
      toast.error("Update failed.");
    } finally {
      setSaving(false);
    }
  };

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
            {(profile?.name || 'U').charAt(0)}
          </div>
          <h2>{isEditing ? 'Editing Profile' : (profile?.name || 'User')}</h2>
          <div className="badge badge-primary">{user.role}</div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input 
              type="text" className="form-input" 
              value={formData.name} 
              onChange={(e) => setFormData({...formData, name: e.target.value})}
              readOnly={!isEditing} 
              disabled={!isEditing} 
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input 
              type="text" className="form-input" 
              value={formData.email} 
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              readOnly={!isEditing} 
              disabled={!isEditing} 
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Age</label>
              <input 
                type="number" className="form-input" 
                value={formData.age} 
                onChange={(e) => setFormData({...formData, age: e.target.value})}
                readOnly={!isEditing} 
                disabled={!isEditing} 
              />
            </div>
            <div className="form-group">
              <label className="form-label">Gender</label>
              <select 
                className="form-input" 
                value={formData.gender} 
                onChange={(e) => setFormData({...formData, gender: e.target.value})}
                disabled={!isEditing}
              >
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Phone Number (Account ID)</label>
            <input type="text" className="form-input" value={user.mobileNo || ''} readOnly disabled />
          </div>

          {isDriver && !isEditing && (
            <div className="card mt-2" style={{ background: 'var(--surface-2)', border: 'none' }}>
              <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '4px' }}>LICENSE & VEHICLE</p>
              <p>ID: {profile?.licenseNo}</p>
              <p>Vehicle: {profile?.vehicle?.vehicleName} ({profile?.vehicle?.vehicleNo})</p>
            </div>
          )}

          {!isAdmin && (
            <div className="mt-6 flex gap-4">
              {!isEditing ? (
                <button className="btn btn-primary btn-block" onClick={() => setIsEditing(true)}>
                  Edit Profile
                </button>
              ) : (
                <>
                  <button className="btn btn-secondary btn-block" onClick={() => setIsEditing(false)}>
                    Cancel
                  </button>
                  <button className="btn btn-primary btn-block" onClick={handleSave} disabled={saving}>
                    {saving ? <div className="spinner spinner-sm"></div> : 'Save Changes'}
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
