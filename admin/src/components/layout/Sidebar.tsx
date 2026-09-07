import React from 'react';
import {
  LayoutGrid,
  Wallet,
  PieChart,
  Calendar,
  BarChart2,
  HelpCircle,
  Shield,
  Sprout,
  X,
  Workflow,
} from 'lucide-react';

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
  isOpen?: boolean;
  onClose?: () => void;
}

interface NavButton {
  id: string;
  label: string;
  icon: React.ElementType;
  tab: string;
}

export const Sidebar: React.FC<SidebarProps> = ({
  activeTab,
  setActiveTab,
  isOpen = false,
  onClose,
}) => {
  const mainNavItems: NavButton[] = [
    {
      id: 'overview',
      label: 'Dashboard',
      icon: LayoutGrid,
      tab: 'overview',
    },
    {
      id: 'users',
      label: 'Farmers & Finances',
      icon: Wallet,
      tab: 'users',
    },
    {
      id: 'crops',
      label: 'Crop Breakdown',
      icon: PieChart,
      tab: 'crops',
    },
    {
      id: 'dss',
      label: 'Seasonal Schedules',
      icon: Calendar,
      tab: 'dss',
    },
    {
      id: 'community',
      label: 'Community Analytics',
      icon: BarChart2,
      tab: 'community',
    },
  ];

  const handleNavClick = (tab: string) => {
    setActiveTab(tab);
    if (onClose) onClose();
  };

  return (
    <>
      {/* Mobile Drawer Overlay */}
      {isOpen && (
        <div
          onClick={onClose}
          className="fixed inset-0 z-40 bg-black/60 lg:hidden backdrop-blur-xs"
          aria-hidden="true"
        />
      )}

      {/* Slim Modern Icon Sidebar - Flush to Left Edge, Fixed to Viewport Height */}
      <aside
        className={`w-[78px] h-screen shrink-0 bg-[#111C23] flex flex-col items-center py-6 select-none z-50 border-r border-[#1C2E3A] transition-transform duration-200 ease-in-out fixed inset-y-0 left-0 md:sticky md:top-0 ${
          isOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'
        }`}
      >
        {/* Top Section: Brand Logo Shield & Navigation Stack */}
        <div className="flex flex-col items-center w-full gap-6">
          {/* Brand Logo Shield */}
          <div className="flex flex-col items-center gap-2">
            <div className="w-12 h-12 rounded-2xl flex items-center justify-center relative group cursor-pointer">
              <div className="w-11 h-11 rounded-2xl bg-[#14232C] border border-[#223B49] flex items-center justify-center p-1.5 shadow-[0_0_15px_rgba(76,175,80,0.25)] hover:border-[#4CAF50] transition-colors">
                <img src="/app_logo.png" alt="MapTanim" className="w-full h-full object-contain" />
              </div>
              {/* Tooltip */}
              <span className="absolute left-16 px-2.5 py-1 rounded-lg bg-[#182933] border border-[#223B49] text-xs font-bold text-white whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-50 shadow-lg">
                maptanim admin
              </span>
            </div>

            {onClose && (
              <button
                onClick={onClose}
                className="md:hidden p-1.5 text-[#8A9BA8] hover:text-white transition cursor-pointer"
                aria-label="Close menu"
              >
                <X className="w-5 h-5" />
              </button>
            )}
          </div>

          {/* Vertical Navigation Icons - Positioned at the Top */}
          <nav className="flex flex-col items-center gap-4 w-full">
            {mainNavItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.tab;

              return (
                <div key={item.id} className="relative group">
                  <button
                    onClick={() => handleNavClick(item.tab)}
                    className={`w-11 h-11 rounded-xl flex items-center justify-center transition-all duration-200 cursor-pointer ${
                      isActive
                        ? 'bg-[#4CAF50]/15 text-[#00E676] shadow-[0_0_20px_rgba(0,230,118,0.45)] border border-[#4CAF50]/40'
                        : 'text-[#647888] hover:text-[#C7D0D8] hover:bg-[#162732]'
                    }`}
                    aria-label={item.label}
                  >
                    <Icon className="w-5 h-5 stroke-[2.2]" />
                  </button>

                  {/* Floating Clean Label Tooltip */}
                  <span className="absolute left-14 top-1/2 -translate-y-1/2 px-3 py-1 rounded-lg bg-[#182933] border border-[#223B49] text-xs font-semibold text-[#F4F4F4] whitespace-nowrap opacity-0 group-hover:opacity-100 transition-all pointer-events-none z-50 shadow-xl">
                    {item.label}
                  </span>
                </div>
              );
            })}
          </nav>
        </div>

        {/* Bottom: Flowchart & Help / Settings Icons */}
        <div className="flex flex-col items-center gap-2.5 mt-auto pt-4">
          <div className="relative group">
            <a
              href="/flowchart.html"
              target="_blank"
              rel="noreferrer"
              className="w-11 h-11 rounded-xl flex items-center justify-center text-[#4CAF50] hover:text-[#00E676] hover:bg-[#4CAF50]/15 border border-[#4CAF50]/30 transition-all cursor-pointer shadow-[0_0_10px_rgba(76,175,80,0.15)]"
              aria-label="System Flowchart & Architecture"
            >
              <Workflow className="w-5 h-5 stroke-[2.2]" />
            </a>

            <span className="absolute left-14 top-1/2 -translate-y-1/2 px-3 py-1 rounded-lg bg-[#182933] border border-[#223B49] text-xs font-semibold text-[#F4F4F4] whitespace-nowrap opacity-0 group-hover:opacity-100 transition-all pointer-events-none z-50 shadow-xl">
              System Flowchart & Architecture
            </span>
          </div>

          <div className="relative group">
            <button
              onClick={() => handleNavClick('logs')}
              className={`w-11 h-11 rounded-xl flex items-center justify-center transition-colors cursor-pointer ${
                activeTab === 'logs'
                  ? 'bg-[#4CAF50]/15 text-[#00E676] border border-[#4CAF50]/30 shadow-[0_0_15px_rgba(76,175,80,0.3)]'
                  : 'text-[#647888] hover:text-[#C7D0D8] hover:bg-[#162732]'
              }`}
              aria-label="Settings & Help"
            >
              <HelpCircle className="w-5 h-5 stroke-[2.2]" />
            </button>

            <span className="absolute left-14 bottom-2 px-3 py-1 rounded-lg bg-[#182933] border border-[#223B49] text-xs font-semibold text-[#F4F4F4] whitespace-nowrap opacity-0 group-hover:opacity-100 transition-all pointer-events-none z-50 shadow-xl">
              Audit & System Help
            </span>
          </div>
        </div>
      </aside>
    </>
  );
};
