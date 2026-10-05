import React, { createContext, useContext, useEffect, useState } from 'react';
import { supabase, isSupabaseConfigured } from '../services/supabase';

export interface AdminUser {
  id: string;
  email: string;
  name: string;
  role: 'SUPER_ADMIN' | 'ADMINISTRATOR';
  provider: 'SUPABASE_AUTH' | 'LOCAL_DEV';
}

interface AuthContextType {
  user: AdminUser | null;
  isAuthenticated: boolean;
  login: (email: string, pass: string, remember?: boolean) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
  isConfigured: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AdminUser | null>(() => {
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

  const isConfigured = isSupabaseConfigured;

  // Sync session with Supabase Auth state listener
  useEffect(() => {
    if (!isSupabaseConfigured) return;

    // Check active session on mount
    supabase.auth.getSession().then(async ({ data: { session } }) => {
      if (session?.user) {
        // Fetch role from public.users table
        const { data: dbUser } = await supabase
          .from('users')
          .select('id, email, role, status')
          .eq('id', session.user.id)
          .single();

        if (dbUser && ['ADMIN', 'SUPER_ADMIN'].includes(dbUser.role) && dbUser.status !== 'SUSPENDED') {
          const adminUser: AdminUser = {
            id: session.user.id,
            email: session.user.email || dbUser.email,
            name: session.user.user_metadata?.full_name || (session.user.email ? session.user.email.split('@')[0] : 'Admin'),
            role: dbUser.role === 'SUPER_ADMIN' ? 'SUPER_ADMIN' : 'ADMINISTRATOR',
            provider: 'SUPABASE_AUTH',
          };
          setUser(adminUser);
        } else if (!dbUser) {
          // If public.users record isn't loaded or role is not admin, clear session
          setUser(null);
          localStorage.removeItem('maptanim_admin_session');
          sessionStorage.removeItem('maptanim_admin_session');
        }
      }
    });

    const { data: authListener } = supabase.auth.onAuthStateChange(async (event, session) => {
      if (event === 'SIGNED_OUT' || !session) {
        setUser(null);
        localStorage.removeItem('maptanim_admin_session');
        sessionStorage.removeItem('maptanim_admin_session');
      }
    });

    return () => {
      authListener?.subscription?.unsubscribe();
    };
  }, []);

  const login = async (email: string, pass: string, remember: boolean = true): Promise<{ success: boolean; error?: string }> => {
    const trimmedEmail = email.trim().toLowerCase();

    if (!isSupabaseConfigured) {
      return {
        success: false,
        error: 'Supabase endpoint is not configured. Verify VITE_SUPABASE_URL and VITE_SUPABASE_ANON_KEY.',
      };
    }

    try {
      // 1. Authenticate with Supabase Auth
      const { data, error } = await supabase.auth.signInWithPassword({
        email: trimmedEmail,
        password: pass.trim(),
      });

      if (error || !data.user) {
        return {
          success: false,
          error: error?.message || 'Invalid administrator email or password.',
        };
      }

      // 2. Verify administrative privileges from public.users
      const { data: dbUser, error: roleError } = await supabase
        .from('users')
        .select('id, email, role, status')
        .eq('id', data.user.id)
        .single();

      if (roleError || !dbUser) {
        await supabase.auth.signOut();
        return {
          success: false,
          error: 'Access denied: No administrative permissions found for this account.',
        };
      }

      if (dbUser.status === 'SUSPENDED') {
        await supabase.auth.signOut();
        return {
          success: false,
          error: 'Account suspended. Please contact MapTanim system security.',
        };
      }

      if (!['ADMIN', 'SUPER_ADMIN'].includes(dbUser.role)) {
        await supabase.auth.signOut();
        return {
          success: false,
          error: 'Access denied: Requires Administrator or Super Admin role.',
        };
      }

      const adminUser: AdminUser = {
        id: data.user.id,
        email: data.user.email || trimmedEmail,
        name: data.user.user_metadata?.full_name || trimmedEmail.split('@')[0],
        role: dbUser.role === 'SUPER_ADMIN' ? 'SUPER_ADMIN' : 'ADMINISTRATOR',
        provider: 'SUPABASE_AUTH',
      };

      setUser(adminUser);
      const sessionStr = JSON.stringify(adminUser);
      if (remember) {
        localStorage.setItem('maptanim_admin_session', sessionStr);
      } else {
        sessionStorage.setItem('maptanim_admin_session', sessionStr);
      }

      return { success: true };
    } catch (err: any) {
      return {
        success: false,
        error: err?.message || 'An unexpected error occurred during authentication.',
      };
    }
  };

  const logout = async () => {
    setUser(null);
    localStorage.removeItem('maptanim_admin_session');
    sessionStorage.removeItem('maptanim_admin_session');
    if (isSupabaseConfigured) {
      try {
        await supabase.auth.signOut();
      } catch (_) {}
    }
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

