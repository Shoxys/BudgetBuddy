/**
 * Notification component for displaying success, error, or info messages.
 * Auto-closes after a specified duration.
 */

import { useEffect } from 'react';
import { XMarkIcon, CheckCircleIcon, ExclamationTriangleIcon, InformationCircleIcon } from '@heroicons/react/24/outline';

export default function Notification({ type = 'info', message, onClose, isOpen = false, duration = 3000 }) {
  // Auto-close notification
  useEffect(() => {
    if (isOpen) {
      const timer = setTimeout(onClose, duration);
      return () => clearTimeout(timer);
    }
  }, [isOpen, onClose, duration]);

  if (!isOpen) return null;

  // Define styles and icons based on notification type
  const typeStyles = {
    success: {
      bg: 'bg-green-50',
      border: 'border-green-400',
      text: 'text-green-800',
      iconColor: 'text-green-500',
      icon: CheckCircleIcon,
    },
    error: {
      bg: 'bg-red-50',
      border: 'border-red-400',
      text: 'text-red-800',
      iconColor: 'text-red-500',
      icon: ExclamationTriangleIcon,
    },
    info: {
      bg: 'bg-blue-50',
      border: 'border-blue-400',
      text: 'text-blue-800',
      iconColor: 'text-blue-500',
      icon: InformationCircleIcon,
    },
  };

  const { bg, border, text, iconColor, icon: Icon } = typeStyles[type] || typeStyles.info;

  // Layout
  return (
    <div
      className={`fixed top-4 left-1/2 transform -translate-x-1/2 z-[9999] w-full max-w-sm p-4 rounded-lg shadow-bb-general border-l-4 ${bg} ${border} animate-slide-down`}
      role="alert"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-start">
          <Icon className={`h-6 w-6 mr-3 flex-shrink-0 ${iconColor}`} aria-hidden="true" />
          <p className={`text-sm font-medium ${text}`}>{message}</p>
        </div>
        <button
          onClick={onClose}
          className="ml-auto bg-transparent text-gray-500 rounded-lg p-1.5 hover:bg-gray-100 hover:text-gray-900 focus:ring-2 focus:ring-gray-300 h-8 w-8 flex justify-center items-center transition-colors"
          aria-label="Close"
        >
          <XMarkIcon className="h-5 w-5" />
        </button>
      </div>
    </div>
  );
}