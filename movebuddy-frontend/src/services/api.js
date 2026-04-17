import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090';

// Create Axios instance
const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor – attach JWT token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('movebuddy_token');
    if (token) {
      // Token is stored as "Bearer xxxx"
      config.headers.Authorization = token.startsWith('Bearer ')
        ? token
        : `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor – handle 401 globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('movebuddy_user');
      localStorage.removeItem('movebuddy_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ============================
// AUTH ENDPOINTS
// ============================
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  registerCustomer: (data) => api.post('/auth/register/customer', data),
  registerDriver: (data) => api.post('/auth/register/driver', data),
};

// ============================
// CUSTOMER ENDPOINTS
// ============================
export const customerAPI = {
  getProfile: (mobileNo) => api.get(`/customer/findcustomer?mobileNo=${mobileNo}`),
  getAvailableVehicles: (mobileNo, destination) =>
    api.get(`/customer/seeavailableVehicles?mobileNo=${mobileNo}&destination=${destination}`),
  getBookingHistory: (mobileNo) =>
    api.get(`/customer/seecustomerbookinghistory?mobileNo=${mobileNo}`),
  cancelBooking: (bookingId, customerId) =>
    api.put(`/customer/customerCancellation?bookingid=${bookingId}&customerid=${customerId}`),
  submitRating: (customerMobile, data) =>
    api.post(`/customer/rating/submit?customerMobile=${customerMobile}`, data),
  getDriverRating: (driverId) =>
    api.get(`/customer/rating/driver/${driverId}`),
  updateProfile: (data) => api.put('/customer/update', data),
};

// ============================
// BOOKING ENDPOINTS
// ============================
export const bookingAPI = {
  bookVehicle: (mobileNo, data) =>
    api.post(`/booking/book?mobileNo=${mobileNo}`, data),
  getOtp: (customerId, bookingId) =>
    api.get(`/booking/otp?customerId=${customerId}&bookingId=${bookingId}`),
  getActiveBooking: (mobileNo) =>
    api.get(`/booking/active?mobileNo=${mobileNo}`),
  getBookingById: (id) =>
    api.get(`/booking/${id}`),
};

// ============================
// DRIVER ENDPOINTS
// ============================
export const driverAPI = {
  getProfile: (mobileNo) => api.get(`/driver/profile/${mobileNo}`),
  toggleAvailability: (mobileNo) =>
    api.put(`/driver/toggle-availability/${mobileNo}`),
  getIncomingRides: (mobileNo) =>
    api.get(`/driver/incoming-rides/${mobileNo}`),
  validateOtp: (bookingId, otp) =>
    api.post(`/driver/validate-otp?bookingId=${bookingId}&otp=${otp}`),
  completeRide: (bookingId, paymentType) =>
    api.put(`/driver/complete-ride?bookingId=${bookingId}&paymentType=${paymentType}`),
  getBookingHistory: (mobileNo) =>
    api.get(`/driver/booking-history/${mobileNo}`),
  updateLocation: (mobileNo, latitude, longitude) =>
    api.put(`/driver/update-location?mobileNo=${mobileNo}&latitude=${latitude}&longitude=${longitude}`),
  updateProfile: (data) => api.put('/driver/update', data),
};

// ============================
// ADMIN ENDPOINTS
// ============================
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getAllUsers: () => api.get('/admin/users'),
  getAllCustomers: () => api.get('/admin/customers'),
  getAllDrivers: () => api.get('/admin/drivers'),
  getAllRides: () => api.get('/admin/rides'),
  suspendDriver: (mobileNo) => api.put(`/admin/suspend/driver/${mobileNo}`),
  restoreDriver: (mobileNo) => api.put(`/admin/restore/driver/${mobileNo}`),
  deleteCustomer: (mobileNo) => api.delete(`/admin/customer/${mobileNo}`),
};

export default api;
