import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'purple' | 'neutral';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'neutral', size = 'sm' }) => {
  const styles: Record<string, string> = {
    success: 'bg-[#E8F5E9] text-[#2E7D32] border-[#A5D6A7]',
    warning: 'bg-[#FEF3C7] text-[#D97706] border-[#FDE68A]',
    danger: 'bg-[#FEE2E2] text-[#DC2626] border-[#FECACA]',
    info: 'bg-[#E0F2FE] text-[#0284C7] border-[#BAE6FD]',
    purple: 'bg-[#F3E8FF] text-[#7E22CE] border-[#E9D5FF]',
    neutral: 'bg-[#F3F4F6] text-[#374151] border-[#E5E7EB]',
  };

  const pad = size === 'sm' ? 'px-2 py-0.5 text-xs' : 'px-3 py-1 text-sm';

  return (
    <span className={`inline-flex items-center font-semibold rounded-full border ${styles[variant]} ${pad}`}>
      {children}
    </span>
  );
};
