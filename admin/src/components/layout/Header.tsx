import React from 'react';
import { Bell, Menu, X, Settings } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface HeaderProps {
  title: string;
  onToggleMobileMenu?: () => void;
  isMobileMenuOpen?: boolean;
}

export const Header: React.FC<HeaderProps> = ({ title, onToggleMobileMenu, isMobileMenuOpen }) => {
  const { user } = useAuth();

  return (
    <header className="h-[72px] px-6 sm:px-8 flex items-center justify-between bg-white border-b border-slate-100">
      <div className="flex items-center gap-3.5">
        {/* Mobile Menu Button */}
        <button
          onClick={onToggleMobileMenu}
          className="lg:hidden p-2 rounded-lg border border-slate-200 bg-white text-slate-600 hover:bg-slate-50 transition cursor-pointer"
          aria-label="Toggle navigation menu"
        >
          {isMobileMenuOpen ? <X className="w-5 h-5 text-emerald-600" /> : <Menu className="w-5 h-5" />}
        </button>

        {/* Page Title */}
        <h1 className="text-xl font-bold text-slate-900 tracking-tight">
          {title}
        </h1>
      </div>

      <div className="flex items-center gap-3">
        {/* Settings Button */}
        <button
          className="p-2 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition h-9 w-9 flex items-center justify-center cursor-pointer"
          title="Settings"
        >
          <Settings className="w-[18px] h-[18px]" />
        </button>

        {/* Notifications with Badge */}
        <button
          className="relative p-2 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition h-9 w-9 flex items-center justify-center cursor-pointer"
          title="Notifications"
        >
          <Bell className="w-[18px] h-[18px]" />
          <span className="absolute -top-0.5 -right-0.5 w-4 h-4 rounded-full bg-rose-500 text-white font-bold text-[9px] flex items-center justify-center shadow-sm">
            2
          </span>
        </button>

        {/* User Profile Pill */}
        <div className="flex items-center gap-2.5 pl-3 ml-1 border-l border-slate-200">
          <div className="w-9 h-9 rounded-full bg-emerald-600 text-white font-bold text-xs flex items-center justify-center shadow-sm">
            {user?.name ? user.name.charAt(0) : 'A'}
          </div>
          <div className="hidden sm:block text-left">
            <p className="text-[13px] font-semibold text-slate-800 leading-tight">
              {user?.name || 'Stephen Austin'}
            </p>
            <p className="text-[11px] text-slate-400 font-medium leading-tight">
              Chief Manager
            </p>
          </div>
        </div>
      </div>
    </header>
  );
};
