import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authAPI } from '../services/api';
import toast from 'react-hot-toast';

const LoginPage = () => {
  const [formData, setFormData] = useState({ mobileNo: '', password: '' });
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await authAPI.login(formData);
      const { token, role, mobileNo } = response.data.data;
      
      login({ role, mobileNo }, token);
      
      toast.success('Welcome back!');
      
      // Redirect based on role
      if (role === 'DRIVER') navigate('/driver-dashboard');
      else if (role === 'ADMIN') navigate('/admin-dashboard');
      else navigate('/rider-dashboard');
      
    } catch (error) {
      toast.error(error.response?.data?.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-logo">
          <div className="auth-logo-icon">M</div>
          <h2 className="auth-title">Welcome Back</h2>
          <p className="auth-subtitle">Login to your MoveBuddy account</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Mobile Number</label>
            <input
              type="number"
              name="mobileNo"
              className="form-input"
              placeholder="e.g. 9876543210"
              required
              value={formData.mobileNo}
              onChange={handleChange}
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              name="password"
              className="form-input"
              placeholder="••••••••"
              required
              value={formData.password}
              onChange={handleChange}
            />
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
            {loading ? <div className="spinner spinner-sm"></div> : 'Login'}
          </button>
        </form>

        <div className="text-center mt-6">
          <p className="text-secondary" style={{ fontSize: '0.9rem' }}>
            New to MoveBuddy? <Link to="/register">Create an account</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
