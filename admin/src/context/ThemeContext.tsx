import React, { createContext, useContext, useEffect } from 'react';

interface ThemeContextType {
  isDarkMode: boolean;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType>({
  isDarkMode: true,
  toggleTheme: () => {},
});

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  useEffect(() => {
    // Permanent mobile dark theme
    document.documentElement.classList.add('dark');
    document.documentElement.classList.remove('light');
    document.documentElement.style.backgroundColor = '#0b151a';
    document.documentElement.style.colorScheme = 'dark';
    try {
      localStorage.removeItem('theme');
      localStorage.setItem('maptanim_admin_theme', 'dark');
    } catch {
      // ignore storage access errors
    }
  }, []);

  return (
    <ThemeContext.Provider value={{ isDarkMode: true, toggleTheme: () => {} }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => useContext(ThemeContext);
