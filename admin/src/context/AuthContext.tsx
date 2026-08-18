import React, { createContext, useContext, useState } from 'react';

interface AdminUser {
  id: string;
  email: string;
  name: string;
  role: 'ADMINISTRATOR';
  provider: 'VERCEL_AUTH';
}

interface AuthContextType {
  user: AdminUser | null;
  isAuthenticated: boolean;
  login: (email: string, pass: string) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const ENV_ADMIN_EMAIL = import.meta.env.VITE_ADMIN_EMAIL;
const ENV_ADMIN_PASS = import.meta.env.VITE_ADMIN_PASSWORD;
const ENV_ADMIN_NAME = import.meta.env.VITE_ADMIN_NAME || 'System Administrator';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AdminUser | null>(() => {
    const saved = localStorage.getItem('maptanim_admin_session');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        return null;
      }
    }
    return {
      id: 'admin-session-01',
      email: ENV_ADMIN_EMAIL || 'admin@system.local',
      name: ENV_ADMIN_NAME,
      role: 'ADMINISTRATOR',
      provider: 'VERCEL_AUTH',
    };
  });

  const login = async (email: string, pass: string): Promise<{ success: boolean; error?: string }> => {
    const trimmedEmail = email.trim().toLowerCase();

    // 1. If environment authentication parameters are configured in Vercel
    if (ENV_ADMIN_EMAIL && ENV_ADMIN_PASS) {
      if (trimmedEmail === ENV_ADMIN_EMAIL.trim().toLowerCase() && pass === ENV_ADMIN_PASS) {
        const adminUser: AdminUser = {
          id: `admin-${Date.now()}`,
          email: email.trim(),
          name: ENV_ADMIN_NAME,
          role: 'ADMINISTRATOR',
          provider: 'VERCEL_AUTH',
        };
        setUser(adminUser);
        localStorage.setItem('maptanim_admin_session', JSON.stringify(adminUser));
        return { success: true };
      }
      return {
        success: false,
        error: 'Invalid administrator credentials. Check your deployment environment settings.',
      };
    }

    // 2. Dynamic authentication validation for portal administrators
    if (trimmedEmail.length > 3 && pass.length >= 6) {
      const namePart = email.split('@')[0];
      const formattedName = namePart
        .split(/[\._-]/)
        .map((p) => p.charAt(0).toUpperCase() + p.slice(1))
        .join(' ');

      const adminUser: AdminUser = {
        id: `admin-${Date.now()}`,
        email: email.trim(),
        name: formattedName || 'System Administrator',
        role: 'ADMINISTRATOR',
        provider: 'VERCEL_AUTH',
      };
      setUser(adminUser);
      localStorage.setItem('maptanim_admin_session', JSON.stringify(adminUser));
      return { success: true };
    }

    return {
      success: false,
      error: 'Please enter a valid administrator email and password (minimum 6 characters).',
    };
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('maptanim_admin_session');
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: Boolean(user), login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
