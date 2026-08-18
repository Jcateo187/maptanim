import React from 'react';
import {
  LayoutDashboard,
  Users,
  Sprout,
  Compass,
  MessageSquare,
  LifeBuoy,
  FileText,
  LogOut,
  ChevronRight,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  isOpen?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ activeTab, setActiveTab, isOpen, onClose }) => {
  const { user, logout } = useAuth();

  const menuItems = [
    { id: 'overview', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'farmers', label: 'Farmers', icon: Users },
    { id: 'crops', label: 'Crops Library', icon: Sprout },
    { id: 'dss', label: 'DSS Matrix', icon: Compass },
    { id: 'community', label: 'Community Hub', icon: MessageSquare },
    { id: 'feedback', label: 'Farmer Support', icon: LifeBuoy },
    { id: 'logs', label: 'System Logs', icon: FileText },
  ];

  const handleSelectTab = (tabId: string) => {
    setActiveTab(tabId);
    if (onClose) onClose();
  };

  return (
    <>
      {/* Mobile Backdrop */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 z-40 bg-slate-900/60 backdrop-blur-xs lg:hidden animate-fadeIn"
          aria-hidden="true"
        />
      )}

      <aside
        className={`w-[240px] bg-[#1E2638] text-slate-300 flex flex-col min-h-screen select-none transition-transform duration-300 ease-in-out fixed inset-y-0 left-0 z-50 lg:static lg:z-auto shrink-0 shadow-2xl lg:shadow-none ${
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        {/* Brand Header */}
        <div className="h-[72px] px-5 flex items-center justify-between border-b border-white/5">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-emerald-500 flex items-center justify-center p-1.5 shadow-md shadow-emerald-950/40 shrink-0">
              <img src="/app_logo.png" alt="MapTanim" className="w-full h-full object-contain brightness-0 invert" />
            </div>
            <div>
              <h2 className="font-extrabold text-[15px] text-white tracking-tight flex items-center gap-1.5">
                MapTanim
              </h2>
              <p className="text-[10px] text-slate-500 font-medium tracking-wide uppercase">Agroecological DSS</p>
            </div>
          </div>

          {onClose && (
            <button
              onClick={onClose}
              className="lg:hidden p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition"
              aria-label="Close menu"
            >
              ✕
            </button>
          )}
        </div>

        {/* Navigation List */}
        <nav className="flex-1 px-3 py-5 space-y-0.5 overflow-y-auto">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => handleSelectTab(item.id)}
                className={`w-full flex items-center justify-between px-3.5 py-2.5 rounded-lg text-[13px] font-medium transition-all cursor-pointer group ${
                  isActive
                    ? 'bg-white/10 text-white'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
                }`}
              >
                <div className="flex items-center gap-3">
                  <Icon className={`w-[18px] h-[18px] shrink-0 ${isActive ? 'text-emerald-400' : 'text-slate-500 group-hover:text-slate-300'}`} />
                  <span>{item.label}</span>
                </div>

                {isActive && <ChevronRight className="w-3.5 h-3.5 text-slate-500" />}
              </button>
            );
          })}
        </nav>

        {/* User Account Profile */}
        <div className="p-3 mx-3 mb-3 rounded-xl bg-white/5 border border-white/5 text-xs text-slate-300 flex items-center justify-between">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-emerald-500 to-teal-400 text-white font-bold text-xs flex items-center justify-center shrink-0">
              {user?.name ? user.name.charAt(0) : 'A'}
            </div>
            <div className="min-w-0">
              <p className="font-semibold text-white text-[12px] truncate">
                {user?.name || 'Administrator'}
              </p>
              <p className="text-[10px] text-slate-500 truncate">
                {user?.email || 'admin@maptanim.ph'}
              </p>
            </div>
          </div>

          <button
            onClick={logout}
            className="p-1.5 rounded-lg text-slate-500 hover:text-rose-400 hover:bg-rose-950/40 transition cursor-pointer"
            title="Log out"
          >
            <LogOut className="w-3.5 h-3.5" />
          </button>
        </div>
      </aside>
    </>
  );
};
