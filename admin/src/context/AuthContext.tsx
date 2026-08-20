import React, { createContext, useContext, useState } from 'react';

export interface AdminUser {
  id: string;
  email: string;
  name: string;
  role: 'SUPER_ADMIN' | 'ADMINISTRATOR';
  provider: 'VERCEL_AUTH';
}

interface AuthContextType {
  user: AdminUser | null;
  isAuthenticated: boolean;
  login: (email: string, pass: string, remember?: boolean) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  isConfigured: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Environment variables configured in Vercel Project Settings
const ENV_ADMIN_EMAIL = import.meta.env.VITE_ADMIN_EMAIL;
const ENV_ADMIN_PASS = import.meta.env.VITE_ADMIN_PASSWORD;
const ENV_ADMIN_NAME = import.meta.env.VITE_ADMIN_NAME || 'System Administrator';
const ENV_ADMIN_ACCOUNTS = import.meta.env.VITE_ADMIN_ACCOUNTS; // Format: email:password:name,email2:pass2:name2 or JSON

// Helper to parse multiple admin accounts if provided
interface ConfiguredAccount {
  email: string;
  pass: string;
  name: string;
  role: 'SUPER_ADMIN' | 'ADMINISTRATOR';
}

const parseConfiguredAccounts = (): ConfiguredAccount[] => {
  const list: ConfiguredAccount[] = [];

  // Primary single account from Vercel env
  if (ENV_ADMIN_EMAIL && ENV_ADMIN_PASS) {
    list.push({
      email: ENV_ADMIN_EMAIL.trim().toLowerCase(),
      pass: ENV_ADMIN_PASS.trim(),
      name: ENV_ADMIN_NAME.trim(),
      role: 'SUPER_ADMIN',
    });
  }

  // Optional multiple accounts from Vercel env (VITE_ADMIN_ACCOUNTS)
  if (ENV_ADMIN_ACCOUNTS) {
    try {
      if (ENV_ADMIN_ACCOUNTS.startsWith('[') || ENV_ADMIN_ACCOUNTS.startsWith('{')) {
        const parsed = JSON.parse(ENV_ADMIN_ACCOUNTS);
        const array = Array.isArray(parsed) ? parsed : [parsed];
        for (const item of array) {
          if (item.email && item.password) {
            list.push({
              email: String(item.email).trim().toLowerCase(),
              pass: String(item.password).trim(),
              name: item.name || 'Administrator',
              role: item.role || 'ADMINISTRATOR',
            });
          }
        }
      } else {
        // Delimited: "email:pass:name,email2:pass2:name2"
        const entries = ENV_ADMIN_ACCOUNTS.split(',');
        for (const entry of entries) {
          const [em, pw, nm] = entry.split(':');
          if (em && pw) {
            list.push({
              email: em.trim().toLowerCase(),
              pass: pw.trim(),
              name: nm ? nm.trim() : 'Administrator',
              role: 'ADMINISTRATOR',
            });
          }
        }
      }
    } catch (err) {
      console.warn('Failed to parse VITE_ADMIN_ACCOUNTS env variable:', err);
    }
  }

  // Safe development/initial fallback if not configured in Vercel yet
  if (list.length === 0) {
    list.push({
      email: 'admin@maptanim.com',
      pass: 'admin123456',
      name: 'System Administrator',
      role: 'SUPER_ADMIN',
    });
  }

  return list;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AdminUser | null>(() => {
    // Only authenticate if user actively has a valid saved session in localStorage or sessionStorage
    const saved = localStorage.getItem('maptanim_admin_session') || sessionStorage.getItem('maptanim_admin_session');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed && parsed.email && parsed.role) {
          return parsed;
        }
      } catch {
        // ignore corrupted storage
      }
    }
    return null;
  });

  const isConfigured = Boolean(ENV_ADMIN_EMAIL && ENV_ADMIN_PASS);

  const login = async (email: string, pass: string, remember: boolean = true): Promise<{ success: boolean; error?: string }> => {
    const trimmedEmail = email.trim().toLowerCase();
    const accounts = parseConfiguredAccounts();

    const matched = accounts.find(
      (acc) => acc.email === trimmedEmail && acc.pass === pass.trim()
    );

    if (matched) {
      const adminUser: AdminUser = {
        id: `admin-${Date.now()}`,
        email: matched.email,
        name: matched.name,
        role: matched.role,
        provider: 'VERCEL_AUTH',
      };

      setUser(adminUser);
      const sessionStr = JSON.stringify(adminUser);
      if (remember) {
        localStorage.setItem('maptanim_admin_session', sessionStr);
      } else {
        sessionStorage.setItem('maptanim_admin_session', sessionStr);
      }
      return { success: true };
    }

    return {
      success: false,
      error: 'Invalid administrator email or password. Verify credentials configured in Vercel Environment Variables.',
    };
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('maptanim_admin_session');
    sessionStorage.removeItem('maptanim_admin_session');
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: Boolean(user), login, logout, isConfigured }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};
