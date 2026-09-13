import React, { useEffect, useRef } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl' | '5xl' | string;
  size?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | '3xl' | '4xl' | '5xl' | string;
  position?: 'center' | 'top';
}

export const Modal: React.FC<ModalProps> = ({
  isOpen,
  onClose,
  title,
  children,
  maxWidth,
  size,
  position = 'top',
}) => {
  const backdropRef = useRef<HTMLDivElement>(null);
  const bodyRef = useRef<HTMLDivElement>(null);

  // Lock body scroll while modal is open so overlay stays locked to active screen
  useEffect(() => {
    if (!isOpen) return;
    const originalOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = originalOverflow;
    };
  }, [isOpen]);

  useEffect(() => {
    if (isOpen) {
      bodyRef.current?.scrollTo({ top: 0, behavior: 'instant' });
      backdropRef.current?.scrollTo({ top: 0, behavior: 'instant' });
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const resolvedWidth = maxWidth || size || 'md';

  const widthClasses: Record<string, string> = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
    '2xl': 'max-w-2xl',
    '3xl': 'max-w-3xl',
    '4xl': 'max-w-4xl',
    '5xl': 'max-w-5xl',
    '6xl': 'max-w-6xl',
    '7xl': 'max-w-7xl',
  };

  const resolvedWidthClass = widthClasses[resolvedWidth] || (resolvedWidth.startsWith('max-w-') ? resolvedWidth : 'max-w-2xl');

  return createPortal(
    <div
      ref={backdropRef}
      className={`fixed inset-0 z-[9999] overflow-y-auto p-3 sm:p-4 bg-[#112230]/85 backdrop-blur-md animate-fadeIn flex justify-center ${
        position === 'center'
          ? 'items-center min-h-screen'
          : 'items-start pt-3 sm:pt-6 md:pt-8 pb-10'
      }`}
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        width: '100vw',
        height: '100vh',
      }}
      onClick={(e) => {
        if (e.target === backdropRef.current) onClose();
      }}
    >
      <div className={`w-[95vw] sm:w-full ${resolvedWidthClass} overflow-hidden shadow-2xl border border-[#38434D] bg-[#2B3136] rounded-2xl text-[#F4F4F4] my-auto`}>
        {/* Modal Header */}
        <div className="flex items-center justify-between px-4 py-3 sm:px-6 sm:py-4 border-b border-[#38434D] bg-[#183145]">
          <h3 className="text-base sm:text-lg font-bold text-[#F4F4F4] truncate pr-2">
            {title}
          </h3>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-[#8A9BA8] hover:text-[#F4F4F4] hover:bg-[#1D2429] transition cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        {/* Modal Body */}
        <div ref={bodyRef} className="p-4 sm:p-6 max-h-[85vh] overflow-y-auto">
          {children}
        </div>
      </div>
    </div>,
    document.body
  );
};
