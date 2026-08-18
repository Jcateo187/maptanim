import React, { createContext, useContext, useEffect } from 'react';

interface ThemeContextType {
  isDarkMode: boolean;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType>({
  isDarkMode: false,
  toggleTheme: () => {},
});

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  useEffect(() => {
    // Static white mode across entire admin system
    document.documentElement.classList.remove('dark');
    localStorage.removeItem('maptanim_admin_theme');
  }, []);

  return (
    <ThemeContext.Provider value={{ isDarkMode: false, toggleTheme: () => {} }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => useContext(ThemeContext);
