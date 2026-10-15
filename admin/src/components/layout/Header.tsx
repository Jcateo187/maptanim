import React, { useState, useRef, useEffect } from 'react';
import {
  Search,
  SlidersHorizontal,
  Bell,
  ChevronDown,
  Menu,
  X,
  LogOut,
  User,
  ShieldCheck,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface HeaderProps {
  title?: string;
  onToggleMobileMenu?: () => void;
  isMobileMenuOpen?: boolean;
}

export const Header: React.FC<HeaderProps> = ({
  onToggleMobileMenu,
  isMobileMenuOpen,
}) => {
  const { user, logout } = useAuth();
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setProfileDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const displayName = user?.name || 'Robert M.';

  return (
    <header className="w-full px-5 sm:px-7 lg:px-8 py-4 sm:py-5 border-b border-[#E5E7EB] flex items-center justify-between gap-4 select-none shrink-0 bg-white shadow-2xs">
      {/* Left: Mobile Toggle & Search Bar */}
      <div className="flex items-center gap-3 flex-1 max-w-lg">
        <button
          onClick={onToggleMobileMenu}
          className="md:hidden p-2 rounded-xl border border-[#E5E7EB] bg-[#F9FAFB] text-[#111813] hover:bg-[#F3F4F6] transition cursor-pointer"
          aria-label="Toggle menu"
        >
          {isMobileMenuOpen ? (
            <X className="w-5 h-5 text-[#2E7D32]" />
          ) : (
            <Menu className="w-5 h-5 text-[#111813]" />
          )}
        </button>

        {/* Pill Search Input */}
        <div className="relative w-full">
          <Search className="w-4 h-4 text-[#9CA3AF] absolute left-4 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search farmers, tickets, posts..."
            className="w-full h-11 pl-11 pr-11 text-xs bg-[#F9FAFB] border border-[#E5E7EB] text-[#111813] placeholder-[#9CA3AF] rounded-full outline-none focus:border-[#2E7D32] focus:ring-1 focus:ring-[#2E7D32] transition-all"
          />
          <button
            type="button"
            className="absolute right-4 top-1/2 -translate-y-1/2 text-[#9CA3AF] hover:text-[#111813] transition-colors"
            title="Filter options"
          >
            <SlidersHorizontal className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* Right: Notifications & User Profile */}
      <div className="flex items-center gap-4 sm:gap-6">
        {/* Notification Bell with Dot */}
        <button
          className="relative p-2.5 text-[#4B5563] hover:text-[#111813] hover:bg-[#F3F4F6] rounded-full transition-colors cursor-pointer"
          aria-label="Notifications"
        >
          <Bell className="w-5 h-5" />
          <span className="absolute top-2 right-2 w-2 h-2 rounded-full bg-[#2E7D32] ring-2 ring-white" />
        </button>

        {/* User Profile */}
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
            className="flex items-center gap-3 p-1 rounded-full hover:bg-[#F3F4F6] transition-colors cursor-pointer"
          >
            {/* Avatar */}
            <div className="w-10 h-10 rounded-full overflow-hidden border-2 border-[#2E7D32] bg-[#E8F5E9] flex items-center justify-center shrink-0 shadow-xs text-[#2E7D32] font-bold">
              <span className="text-xs font-bold uppercase">
                {displayName.charAt(0)}
              </span>
            </div>

            {/* Name & Chevron */}
            <div className="hidden sm:flex items-center gap-2">
              <span className="text-xs font-bold text-[#111813]">
                {displayName}
              </span>
              <ChevronDown
                className={`w-3.5 h-3.5 text-[#6B7280] transition-transform duration-200 ${
                  profileDropdownOpen ? 'rotate-180' : ''
                }`}
              />
            </div>
          </button>

          {/* Profile Dropdown Menu */}
          {profileDropdownOpen && (
            <div className="absolute right-0 mt-2 w-52 bg-white border border-[#E5E7EB] rounded-2xl shadow-xl p-2 z-50 animate-fadeIn">
              <div className="px-3 py-2 border-b border-[#E5E7EB]">
                <p className="text-xs font-bold text-[#111813] truncate">{displayName}</p>
                <p className="text-[11px] text-[#2E7D32] font-semibold flex items-center gap-1 mt-0.5">
                  <ShieldCheck className="w-3.5 h-3.5" />
                  <span>Admin Session</span>
                </p>
              </div>

              <div className="py-1">
                <button
                  onClick={() => {
                    setProfileDropdownOpen(false);
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-xs font-semibold text-[#374151] hover:text-[#111813] hover:bg-[#F3F4F6] rounded-xl transition cursor-pointer"
                >
                  <User className="w-4 h-4 text-[#6B7280]" />
                  <span>Profile Account</span>
                </button>

                <button
                  onClick={() => {
                    setProfileDropdownOpen(false);
                    logout();
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-xs font-semibold text-[#DC2626] hover:bg-red-50 rounded-xl transition cursor-pointer"
                >
                  <LogOut className="w-4 h-4 text-[#DC2626]" />
                  <span>Sign Out</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
