import React, { useState } from 'react';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

interface LayoutProps {
  children: (activeTab: string, setActiveTab: (tab: string) => void) => React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [activeTab, setActiveTab] = useState<string>('overview');
  const [mobileMenuOpen, setMobileMenuOpen] = useState<boolean>(false);

  return (
    <div className="h-screen w-full bg-[#F7FAF7] text-[#111813] flex flex-row overflow-hidden">
      {/* 1. Slim Icon Sidebar - Pinned to Left Edge & Viewport Height */}
      <Sidebar
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        isOpen={mobileMenuOpen}
        onClose={() => setMobileMenuOpen(false)}
      />

      {/* 2. Full-Screen Main Content Area - Header Fixed, Main Viewport Scrolls */}
      <div className="flex-1 flex flex-col min-w-0 h-screen bg-[#F7FAF7] overflow-hidden">
        {/* Top Header */}
        <Header
          onToggleMobileMenu={() => setMobileMenuOpen(!mobileMenuOpen)}
          isMobileMenuOpen={mobileMenuOpen}
        />

        {/* Dashboard Main Viewport with Generous Vertical & Horizontal Padding */}
        <main className="flex-1 px-5 sm:px-7 lg:px-8 py-6 sm:py-7 w-full overflow-y-auto">
          {children(activeTab, setActiveTab)}
        </main>
      </div>
    </div>
  );
};
