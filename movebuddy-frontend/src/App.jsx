import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';

// Components
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import LandingPage from './pages/LandingPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RiderDashboard from './pages/RiderDashboard';
import DriverDashboard from './pages/DriverDashboard';
import AdminDashboard from './pages/AdminDashboard';
import BookRidePage from './pages/BookRidePage';
import RideTrackingPage from './pages/RideTrackingPage';
import ProfilePage from './pages/ProfilePage';
import RideHistoryPage from './pages/RideHistoryPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Toaster position="top-right" />
        <Navbar />
        <main className="page">
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Rider Routes */}
            <Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
              <Route path="/rider-dashboard" element={<RiderDashboard />} />
              <Route path="/book-ride" element={<BookRidePage />} />
              <Route path="/track-ride/:id" element={<RideTrackingPage />} />
              <Route path="/rider-history" element={<RideHistoryPage userType="CUSTOMER" />} />
            </Route>

            {/* Driver Routes */}
            <Route element={<ProtectedRoute allowedRoles={['DRIVER']} />}>
              <Route path="/driver-dashboard" element={<DriverDashboard />} />
              <Route path="/driver-history" element={<RideHistoryPage userType="DRIVER" />} />
            </Route>

            {/* Admin Routes */}
            <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
              <Route path="/admin-dashboard" element={<AdminDashboard />} />
            </Route>

            {/* Shared Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/profile" element={<ProfilePage />} />
            </Route>

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </Router>
    </AuthProvider>
  );
}

export default App;
