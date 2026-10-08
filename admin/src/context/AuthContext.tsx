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

        if (dbUser && ['ADMINISTRATOR', 'ADMIN', 'SUPER_ADMIN'].includes(dbUser.role) && dbUser.status !== 'SUSPENDED') {
          const adminUser: AdminUser = {
            id: session.user.id,
            email: session.user.email || dbUser.email,
            name: session.user.user_metadata?.full_name || (session.user.email ? session.user.email.split('@')[0] : 'Admin'),
            role: dbUser.role === 'SUPER_ADMIN' ? 'SUPER_ADMIN' : 'ADMINISTRATOR',
            provider: 'SUPABASE_AUTH',
          };
          setUser(adminUser);
        } else {
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

  const login = async (email: string, pass: string, remember: boolean = false): Promise<{ success: boolean; error?: string }> => {
    const trimmedEmail = email.trim().toLowerCase();
    const envEmail = (import.meta.env.VITE_ADMIN_EMAIL || 'admin@maptanim.com').trim().toLowerCase();
    const envPass = (import.meta.env.VITE_ADMIN_PASSWORD || 'admin123456').trim();
    const envName = import.meta.env.VITE_ADMIN_NAME || 'System Administrator';

    // 1. Try Supabase Auth first if configured
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase.auth.signInWithPassword({
          email: trimmedEmail,
          password: pass.trim(),
        });

        if (!error && data?.user) {
          // Verify administrative privileges from public.users
          const { data: dbUser, error: roleError } = await supabase
            .from('users')
            .select('id, email, role, status')
            .eq('id', data.user.id)
            .single();

          if (!roleError && dbUser && ['ADMINISTRATOR', 'ADMIN', 'SUPER_ADMIN'].includes(dbUser.role) && dbUser.status !== 'SUSPENDED') {
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
          }
        }
      } catch (err: any) {
        console.warn('Supabase Auth sign-in attempt warning:', err);
      }
    }

    // 2. Local development & environment credentials fallback
    if (trimmedEmail === envEmail && pass.trim() === envPass) {
      const adminUser: AdminUser = {
        id: 'admin_local_dev',
        email: envEmail,
        name: envName,
        role: 'SUPER_ADMIN',
        provider: 'LOCAL_DEV',
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
      error: 'Invalid administrator email or password.',
    };
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

