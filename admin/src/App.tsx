import React from 'react';
import { ThemeProvider } from './context/ThemeContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';
import { DashboardOverview } from './pages/DashboardOverview';
import { UserManagement } from './pages/UserManagement';
import { CommunityHub } from './pages/CommunityHub';
import { CustomerService } from './pages/CustomerService';
import { SystemLogs } from './pages/SystemLogs';
import { Login } from './pages/Login';

const MainContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Login />;
  }

  return (
    <Layout>
      {(activeTab, setActiveTab) => {
        switch (activeTab) {
          case 'overview':
            return <DashboardOverview onNavigate={setActiveTab} />;
          case 'users':
            return <UserManagement />;
          case 'farmers':
            return <UserManagement initialTab="directory" />;
          case 'community':
            return <CommunityHub />;
          case 'feedback':
          case 'service':
            return <CustomerService />;
          case 'logs':
            return <SystemLogs />;
          default:
            return <DashboardOverview onNavigate={setActiveTab} />;
        }
      }}
    </Layout>
  );
};

export const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <MainContent />
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;
