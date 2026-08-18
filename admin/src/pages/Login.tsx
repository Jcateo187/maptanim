import React, { useState } from 'react';
import { Lock, Mail, ArrowRight, Eye, EyeOff, ShieldCheck, Info } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [loading, setLoading] = useState(false);
  const [authError, setAuthError] = useState<string | null>(null);
  const [showHelp, setShowHelp] = useState(false);
  const { login, isConfigured } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setAuthError(null);

    const res = await login(email, password, rememberMe);
    setLoading(false);
    if (!res.success && res.error) {
      setAuthError(res.error);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '2rem 1.5rem',
        backgroundColor: '#0F172A',
        backgroundImage: 'radial-gradient(at top right, #1E293B 0%, #0F172A 100%)',
        boxSizing: 'border-box',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '460px',
          backgroundColor: '#FFFFFF',
          borderRadius: '1.5rem',
          border: '1px solid rgba(255, 255, 255, 0.1)',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.35)',
          padding: '2.5rem',
          boxSizing: 'border-box',
        }}
      >
        {/* Header with App Logo & Title */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '1rem' }}>
            <div
              style={{
                width: '4.5rem',
                height: '4.5rem',
                borderRadius: '1.25rem',
                backgroundColor: '#ECFDF5',
                border: '1px solid #A7F3D0',
                padding: '0.75rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 4px 6px -1px rgba(16, 185, 129, 0.15)',
              }}
            >
              <img
                src="/app_logo.png"
                alt="MapTanim Logo"
                style={{ width: '100%', height: '100%', objectFit: 'contain' }}
              />
            </div>
          </div>

          <h1
            style={{
              fontSize: '1.5rem',
              fontWeight: 800,
              color: '#0F172A',
              letterSpacing: '-0.025em',
              margin: '0 0 0.35rem 0',
            }}
          >
            Admin Console
          </h1>
          <p
            style={{
              fontSize: '0.8125rem',
              color: '#64748B',
              margin: 0,
            }}
          >
            MapTanim Agroecological Administrator Portal
          </p>

          <div
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.375rem',
              marginTop: '0.75rem',
              padding: '0.25rem 0.625rem',
              borderRadius: '9999px',
              backgroundColor: '#F1F5F9',
              border: '1px solid #E2E8F0',
              fontSize: '0.6875rem',
              fontWeight: 600,
              color: '#475569',
            }}
          >
            <ShieldCheck style={{ width: '0.875rem', height: '0.875rem', color: '#059669' }} />
            <span>Protected by Vercel Deployment Auth</span>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {authError && (
            <div
              style={{
                padding: '0.75rem 1rem',
                borderRadius: '0.75rem',
                backgroundColor: '#FFF1F2',
                border: '1px solid #FECDD3',
                color: '#BE123C',
                fontSize: '0.8125rem',
                fontWeight: 600,
                lineHeight: 1.4,
              }}
            >
              {authError}
            </div>
          )}

          {/* Email Field */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <label
              style={{
                fontSize: '0.8125rem',
                fontWeight: 700,
                color: '#334155',
              }}
            >
              Admin Email
            </label>
            <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
              <Mail
                style={{
                  position: 'absolute',
                  left: '1rem',
                  width: '1.125rem',
                  height: '1.125rem',
                  color: '#94A3B8',
                  pointerEvents: 'none',
                }}
              />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@maptanim.com"
                style={{
                  width: '100%',
                  height: '3rem',
                  paddingLeft: '2.875rem',
                  paddingRight: '1rem',
                  fontSize: '0.875rem',
                  borderRadius: '0.75rem',
                  border: '1px solid #CBD5E1',
                  backgroundColor: '#FFFFFF',
                  color: '#0F172A',
                  outline: 'none',
                  boxSizing: 'border-box',
                  transition: 'border-color 0.15s ease, box-shadow 0.15s ease',
                }}
                onFocus={(e) => {
                  e.target.style.borderColor = '#059669';
                  e.target.style.boxShadow = '0 0 0 3px rgba(5, 150, 105, 0.15)';
                }}
                onBlur={(e) => {
                  e.target.style.borderColor = '#CBD5E1';
                  e.target.style.boxShadow = 'none';
                }}
              />
            </div>
          </div>

          {/* Password Field */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <label
              style={{
                fontSize: '0.8125rem',
                fontWeight: 700,
                color: '#334155',
              }}
            >
              Admin Password
            </label>
            <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
              <Lock
                style={{
                  position: 'absolute',
                  left: '1rem',
                  width: '1.125rem',
                  height: '1.125rem',
                  color: '#94A3B8',
                  pointerEvents: 'none',
                }}
              />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                style={{
                  width: '100%',
                  height: '3rem',
                  paddingLeft: '2.875rem',
                  paddingRight: '2.875rem',
                  fontSize: '0.875rem',
                  borderRadius: '0.75rem',
                  border: '1px solid #CBD5E1',
                  backgroundColor: '#FFFFFF',
                  color: '#0F172A',
                  outline: 'none',
                  boxSizing: 'border-box',
                  transition: 'border-color 0.15s ease, box-shadow 0.15s ease',
                }}
                onFocus={(e) => {
                  e.target.style.borderColor = '#059669';
                  e.target.style.boxShadow = '0 0 0 3px rgba(5, 150, 105, 0.15)';
                }}
                onBlur={(e) => {
                  e.target.style.borderColor = '#CBD5E1';
                  e.target.style.boxShadow = 'none';
                }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute',
                  right: '0.75rem',
                  background: 'none',
                  border: 'none',
                  color: '#94A3B8',
                  cursor: 'pointer',
                  padding: '0.25rem',
                  display: 'flex',
                  alignItems: 'center',
                }}
                title={showPassword ? 'Hide password' : 'Show password'}
              >
                {showPassword ? (
                  <EyeOff style={{ width: '1.125rem', height: '1.125rem' }} />
                ) : (
                  <Eye style={{ width: '1.125rem', height: '1.125rem' }} />
                )}
              </button>
            </div>
          </div>

          {/* Options: Remember Me & Config Help */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                style={{
                  width: '1rem',
                  height: '1rem',
                  borderRadius: '0.25rem',
                  accentColor: '#059669',
                  cursor: 'pointer',
                }}
              />
              <span style={{ fontSize: '0.75rem', color: '#64748B', fontWeight: 500 }}>
                Remember session
              </span>
            </label>

            <button
              type="button"
              onClick={() => setShowHelp(!showHelp)}
              style={{
                background: 'none',
                border: 'none',
                color: '#059669',
                fontSize: '0.75rem',
                fontWeight: 600,
                cursor: 'pointer',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '0.25rem',
              }}
            >
              <Info style={{ width: '0.75rem', height: '0.75rem' }} />
              <span>Vercel Auth Info</span>
            </button>
          </div>

          {/* Collapsible Info Drawer */}
          {showHelp && (
            <div
              style={{
                padding: '0.875rem',
                borderRadius: '0.75rem',
                backgroundColor: '#F8FAFC',
                border: '1px solid #E2E8F0',
                fontSize: '0.75rem',
                color: '#475569',
                lineHeight: 1.5,
              }}
            >
              <p style={{ margin: '0 0 0.5rem 0', fontWeight: 700, color: '#1E293B' }}>
                How to set Admin Credentials in Vercel:
              </p>
              <ol style={{ margin: 0, paddingLeft: '1.25rem' }}>
                <li>Go to <strong>Vercel Dashboard → Project Settings → Environment Variables</strong></li>
                <li>Add <code style={{ backgroundColor: '#E2E8F0', padding: '1px 4px', borderRadius: '4px' }}>VITE_ADMIN_EMAIL</code> (e.g. <code>admin@maptanim.com</code>)</li>
                <li>Add <code style={{ backgroundColor: '#E2E8F0', padding: '1px 4px', borderRadius: '4px' }}>VITE_ADMIN_PASSWORD</code> (your secret password)</li>
                <li>Redeploy or promote to apply changes!</li>
              </ol>
              {!isConfigured && (
                <p style={{ margin: '0.5rem 0 0 0', color: '#059669', fontWeight: 600 }}>
                  💡 Default Initial Login: <code>admin@maptanim.com</code> / <code>admin123456</code>
                </p>
              )}
            </div>
          )}

          {/* Submit Button */}
          <div style={{ paddingTop: '0.5rem' }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%',
                height: '3rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '0.5rem',
                borderRadius: '0.75rem',
                border: 'none',
                backgroundColor: '#059669',
                color: '#FFFFFF',
                fontSize: '0.875rem',
                fontWeight: 700,
                cursor: loading ? 'not-allowed' : 'pointer',
                boxShadow: '0 4px 6px -1px rgba(5, 150, 105, 0.25)',
                transition: 'background-color 0.15s ease',
              }}
              onMouseEnter={(e) => {
                if (!loading) (e.target as HTMLElement).style.backgroundColor = '#047857';
              }}
              onMouseLeave={(e) => {
                if (!loading) (e.target as HTMLElement).style.backgroundColor = '#059669';
              }}
            >
              <span>{loading ? 'Verifying Credentials...' : 'Sign In to Portal'}</span>
              <ArrowRight style={{ width: '1rem', height: '1rem' }} />
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
