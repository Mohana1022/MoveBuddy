import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const RegisterPage = () => {
  const query = new URLSearchParams(useLocation().search);
  const initialRole = query.get('role') === 'DRIVER' ? 'DRIVER' : 'CUSTOMER';
  
  const [role, setRole] = useState(initialRole);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Basic shared fields
  const [formData, setFormData] = useState({
    name: '',
    age: '',
    gender: 'Male',
    mobileNo: '',
    email: '',
    password: '',
    latitude: '12.9716', // Default Bangalore
    longitude: '77.5946',
    
    // Driver specific
    licenseNo: '',
    upiID: '',
    vehicleName: '',
    vehicleNo: '',
    vehicleType: 'Bike',
    model: '',
    vehicleCapacity: 1,
    pricePerKM: 15,
    averageSpeed: 40
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (role === 'CUSTOMER') {
        await authAPI.registerCustomer({
          name: formData.name,
          age: parseInt(formData.age),
          gender: formData.gender,
          mobileNo: parseInt(formData.mobileNo),
          emailId: formData.email,
          password: formData.password,
          latitude: formData.latitude,
          longitude: formData.longitude
        });
        toast.success('Rider account created! Please login.');
      } else {
        await authAPI.registerDriver({
          ...formData,
          driverName: formData.name,
          mailId: formData.email,
          age: parseInt(formData.age),
          mobileNo: parseInt(formData.mobileNo),
          licenseNo: parseInt(formData.licenseNo),
          vehicleCapacity: parseInt(formData.vehicleCapacity),
          pricePerKM: parseInt(formData.pricePerKM),
          averageSpeed: parseInt(formData.averageSpeed)
        });
        toast.success('Driver account created! Please login.');
      }
      navigate('/login');
    } catch (error) {
      console.error("Full Registration Error:", error);
      const errorData = error.response?.data;
      
      // Force a popup so the user can see exactly what backend sent
      const errorMsg = errorData?.message || 'Unknown Server Error';
      const detail = errorData?.data ? JSON.stringify(errorData.data) : '';
      alert(`Registration Failed!\n\nReason: ${errorMsg}\nDetails: ${detail}`);
      
      if (errorData?.data && typeof errorData.data === 'object') {
        const firstError = Object.values(errorData.data)[0];
        toast.error(firstError);
      } else {
        toast.error(errorMsg);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page" style={{ alignItems: 'flex-start', paddingTop: '40px' }}>
      <div className="auth-card" style={{ maxWidth: '600px' }}>
        <div className="auth-logo">
          <div className="auth-logo-icon">M</div>
          <h2 className="auth-title">Join MoveBuddy</h2>
          <p className="auth-subtitle">Register to start your journey</p>
        </div>

        <div style={{ display: 'flex', background: 'var(--surface-2)', borderRadius: 'var(--radius)', padding: '4px', marginBottom: '24px' }}>
          <button 
            type="button"
            className={`btn btn-block ${role === 'CUSTOMER' ? 'btn-primary' : ''}`}
            style={{ borderRadius: 'var(--radius-sm)' }}
            onClick={() => setRole('CUSTOMER')}
          >
            I'm a Rider
          </button>
          <button 
            type="button"
            className={`btn btn-block ${role === 'DRIVER' ? 'btn-primary' : ''}`}
            style={{ borderRadius: 'var(--radius-sm)' }}
            onClick={() => setRole('DRIVER')}
          >
            I'm a Driver
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">{role === 'CUSTOMER' ? 'Full Name' : 'Driver Name'}</label>
              <input 
                type="text" 
                name="name" 
                className="form-input" 
                required 
                value={formData.name}
                onChange={handleChange} 
              />
            </div>
            
            <div className="form-group">
              <label className="form-label">Internal Email</label>
              <input 
                type="email" 
                name="email" 
                className="form-input" 
                required 
                value={formData.email}
                onChange={handleChange} 
              />
            </div>

            <div className="form-group">
              <label className="form-label">Mobile Number</label>
              <input type="number" name="mobileNo" className="form-input" required value={formData.mobileNo} onChange={handleChange} />
            </div>

            <div className="form-group">
              <label className="form-label">Age</label>
              <input type="number" name="age" className="form-input" required value={formData.age} onChange={handleChange} />
            </div>

            <div className="form-group">
              <label className="form-label">Gender</label>
              <select name="gender" className="form-select" value={formData.gender} onChange={handleChange}>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <input type="password" name="password" className="form-input" required value={formData.password} onChange={handleChange} />
            </div>
          </div>

          {role === 'DRIVER' && (
            <div style={{ marginTop: '24px', borderTop: '1px solid var(--border)', paddingTop: '24px' }}>
              <h3 className="mb-4" style={{ fontSize: '1rem', color: 'var(--primary)' }}>Vehicle & Professional Details</h3>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label className="form-label">License Number</label>
                  <input type="number" name="licenseNo" className="form-input" required value={formData.licenseNo} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label className="form-label">UPI ID (for payments)</label>
                  <input type="text" name="upiID" className="form-input" required placeholder="name@upi" value={formData.upiID} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label className="form-label">Vehicle Name</label>
                  <input type="text" name="vehicleName" className="form-input" required placeholder="Activa / Swift" value={formData.vehicleName} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label className="form-label">Vehicle Type</label>
                  <select name="vehicleType" className="form-select" value={formData.vehicleType} onChange={handleChange}>
                    <option value="Bike">Bike</option>
                    <option value="Auto">Auto</option>
                    <option value="Cab">Cab</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Vehicle Number</label>
                  <input type="text" name="vehicleNo" className="form-input" required placeholder="KA-01-XX-0000" value={formData.vehicleNo} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label className="form-label">Model/Year</label>
                  <input type="text" name="model" className="form-input" required value={formData.model} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label className="form-label">Price per KM (₹)</label>
                  <input type="number" name="pricePerKM" className="form-input" required onChange={handleChange} value={formData.pricePerKM} />
                </div>
                <div className="form-group">
                  <label className="form-label">Average Speed (km/h)</label>
                  <input type="number" name="averageSpeed" className="form-input" required onChange={handleChange} value={formData.averageSpeed} />
                </div>
              </div>
            </div>
          )}

          <div style={{ marginTop: '24px' }}>
            <p className="text-muted mb-4" style={{ fontSize: '0.8rem' }}>
              By registering, you agree to our Terms of Service and Privacy Policy. Default location will be set via GPS.
            </p>
            <button type="submit" className="btn btn-primary btn-block btn-lg" disabled={loading}>
              {loading ? <div className="spinner spinner-sm"></div> : 'Create Account'}
            </button>
          </div>
        </form>

        <div className="text-center mt-6">
          <p className="text-secondary" style={{ fontSize: '0.9rem' }}>
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
