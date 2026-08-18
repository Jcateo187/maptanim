import React from 'react';
import { ThemeProvider } from './context/ThemeContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Layout } from './components/layout/Layout';
import { DashboardOverview } from './pages/DashboardOverview';
import { FarmerManagement } from './pages/FarmerManagement';
import { CropLibrary } from './pages/CropLibrary';
import { DSSRuleEditor } from './pages/DSSRuleEditor';
import { CommunityHub } from './pages/CommunityHub';
import { FeedbackManagement } from './pages/FeedbackManagement';
import { SystemLogs } from './pages/SystemLogs';
import { Login } from './pages/Login';

const MainContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Login />;
  }

  return (
    <Layout>
      {(activeTab) => {
        switch (activeTab) {
          case 'overview':
            return <DashboardOverview />;
          case 'farmers':
            return <FarmerManagement />;
          case 'crops':
            return <CropLibrary />;
          case 'dss':
            return <DSSRuleEditor />;
          case 'community':
            return <CommunityHub />;
          case 'feedback':
            return <FeedbackManagement />;
          case 'logs':
            return <SystemLogs />;
          default:
            return <DashboardOverview />;
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
