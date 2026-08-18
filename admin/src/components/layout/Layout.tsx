import React, { useState } from 'react';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { LayoutDashboard, Users, Sprout, Compass, MessageSquare } from 'lucide-react';

interface LayoutProps {
  children: (activeTab: string) => React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [mobileMenuOpen, setMobileMenuOpen] = useState<boolean>(false);

  const titleMap: Record<string, string> = {
    overview: 'Dashboard',
    farmers: 'Farmers Management',
    crops: 'Crops Library',
    dss: 'DSS Matrix',
    community: 'Community Hub',
    feedback: 'Farmer Support',
    logs: 'System Logs',
  };

  const bottomNavItems = [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'farmers', label: 'Farmers', icon: Users },
    { id: 'crops', label: 'Crops', icon: Sprout },
    { id: 'dss', label: 'DSS Rules', icon: Compass },
    { id: 'community', label: 'Community', icon: MessageSquare },
  ];

  return (
    <div className="min-h-screen flex bg-[#F4F6F9] text-slate-900 relative">
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        isOpen={mobileMenuOpen}
        onClose={() => setMobileMenuOpen(false)}
      />
      
      <div className="flex-1 flex flex-col min-w-0 pb-16 lg:pb-0">
        <Header
          title={titleMap[activeTab] || 'Dashboard'}
          onToggleMobileMenu={() => setMobileMenuOpen(!mobileMenuOpen)}
          isMobileMenuOpen={mobileMenuOpen}
        />
        {/* Main Content Canvas */}
        <main className="flex-1 p-6 sm:p-8 lg:p-10 overflow-y-auto w-full max-w-[1600px] mx-auto box-border">
          {children(activeTab)}
        </main>
      </div>

      {/* Mobile Bottom Quick Navigation Bar */}
      <nav className="fixed bottom-0 left-0 right-0 z-30 lg:hidden bg-white border-t border-slate-200 px-2 py-2 flex items-center justify-around shadow-lg">
        {bottomNavItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`flex flex-col items-center gap-0.5 px-3 py-1 rounded-xl transition-all ${
                isActive
                  ? 'text-emerald-600 font-bold'
                  : 'text-slate-400 hover:text-slate-700'
              }`}
            >
              <Icon className={`w-5 h-5 ${isActive ? 'text-emerald-600' : 'text-slate-400'}`} />
              <span className="text-[10px] tracking-tight">{item.label}</span>
            </button>
          );
        })}
      </nav>
    </div>
  );
};
