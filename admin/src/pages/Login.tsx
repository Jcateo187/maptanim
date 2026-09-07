import React, { useState } from 'react';
import { Lock, Mail, ArrowRight, Eye, EyeOff, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(true);
  const [loading, setLoading] = useState(false);
  const [authError, setAuthError] = useState<string | null>(null);
  const { login } = useAuth();

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
        backgroundColor: '#112230',
        backgroundImage: 'radial-gradient(at top right, #183145 0%, #112230 100%)',
        boxSizing: 'border-box',
        fontFamily: "'Outfit', 'Inter', system-ui, sans-serif",
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '460px',
          backgroundColor: '#2B3136',
          borderRadius: '1.25rem',
          border: '1px solid #38434D',
          boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5)',
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
                backgroundColor: '#183145',
                border: '1px solid #38434D',
                padding: '0.75rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.25)',
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
              color: '#F4F4F4',
              letterSpacing: '-0.025em',
              margin: '0 0 0.35rem 0',
            }}
          >
            Admin Console
          </h1>
          <p
            style={{
              fontSize: '0.8125rem',
              color: '#C7D0D8',
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
              backgroundColor: '#183145',
              border: '1px solid #38434D',
              fontSize: '0.6875rem',
              fontWeight: 600,
              color: '#C7D0D8',
            }}
          >
            <ShieldCheck style={{ width: '0.875rem', height: '0.875rem', color: '#4CAF50' }} />
            <span>MapTanim Administrator Portal</span>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          {authError && (
            <div
              style={{
                padding: '0.75rem 1rem',
                borderRadius: '0.75rem',
                backgroundColor: 'rgba(231, 111, 81, 0.15)',
                border: '1px solid rgba(231, 111, 81, 0.4)',
                color: '#FF8A65',
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
                color: '#F4F4F4',
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
                  color: '#8A9BA8',
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
                  border: '1px solid #38434D',
                  backgroundColor: '#1D2429',
                  color: '#F4F4F4',
                  outline: 'none',
                  boxSizing: 'border-box',
                  transition: 'border-color 0.15s ease, box-shadow 0.15s ease',
                }}
                onFocus={(e) => {
                  e.target.style.borderColor = '#4CAF50';
                  e.target.style.boxShadow = '0 0 0 3px rgba(76, 175, 80, 0.25)';
                }}
                onBlur={(e) => {
                  e.target.style.borderColor = '#38434D';
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
                color: '#F4F4F4',
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
                  color: '#8A9BA8',
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
                  border: '1px solid #38434D',
                  backgroundColor: '#1D2429',
                  color: '#F4F4F4',
                  outline: 'none',
                  boxSizing: 'border-box',
                  transition: 'border-color 0.15s ease, box-shadow 0.15s ease',
                }}
                onFocus={(e) => {
                  e.target.style.borderColor = '#4CAF50';
                  e.target.style.boxShadow = '0 0 0 3px rgba(76, 175, 80, 0.25)';
                }}
                onBlur={(e) => {
                  e.target.style.borderColor = '#38434D';
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
                  color: '#8A9BA8',
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

          {/* Options: Remember Me */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-start' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={(e) => setRememberMe(e.target.checked)}
                style={{
                  width: '1rem',
                  height: '1rem',
                  borderRadius: '0.25rem',
                  accentColor: '#4CAF50',
                  cursor: 'pointer',
                }}
              />
              <span style={{ fontSize: '0.75rem', color: '#C7D0D8', fontWeight: 500 }}>
                Remember session
              </span>
            </label>
          </div>

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
                backgroundColor: '#4CAF50',
                color: '#FFFFFF',
                fontSize: '0.875rem',
                fontWeight: 700,
                cursor: loading ? 'not-allowed' : 'pointer',
                boxShadow: '0 4px 6px -1px rgba(76, 175, 80, 0.3)',
                transition: 'background-color 0.15s ease',
              }}
              onMouseEnter={(e) => {
                if (!loading) (e.target as HTMLElement).style.backgroundColor = '#388E3C';
              }}
              onMouseLeave={(e) => {
                if (!loading) (e.target as HTMLElement).style.backgroundColor = '#4CAF50';
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
