import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // On mount, restore user from localStorage
  useEffect(() => {
    const storedUser = localStorage.getItem('movebuddy_user');
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch {
        localStorage.removeItem('movebuddy_user');
        localStorage.removeItem('movebuddy_token');
      }
    }
    setLoading(false);
  }, []);

  const login = (userData, token) => {
    const userObj = { ...userData, token };
    setUser(userObj);
    localStorage.setItem('movebuddy_user', JSON.stringify(userObj));
    localStorage.setItem('movebuddy_token', token);
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('movebuddy_user');
    localStorage.removeItem('movebuddy_token');
  };

  const isAuthenticated = !!user;
  const isCustomer = user?.role === 'CUSTOMER';
  const isDriver = user?.role === 'DRIVER';
  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      logout,
      isAuthenticated,
      isCustomer,
      isDriver,
      isAdmin,
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};

export default AuthContext;
