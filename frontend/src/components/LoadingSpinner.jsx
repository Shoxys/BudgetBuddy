/**
 * Reusable loading spinner component with optional message.
 * Displays a modern animated spinner with a pulse effect.
 */

import { useState, useEffect } from 'react';

export default function LoadingSpinner({ message = 'Loading...' }) {
  // State for pulse animation
  const [pulse, setPulse] = useState(false);

  // Toggle pulse effect
  useEffect(() => {
    const interval = setInterval(() => {
      setPulse((prev) => !prev);
    }, 800);
    return () => clearInterval(interval);
  }, []);

  // Layout
  return (
    <div className="flex flex-col items-center justify-center min-h-[20vh] gap-4">
      {/* Spinner */}
      <div
        className={`animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-primary_blue transition-transform duration-300 ${
          pulse ? 'scale-110' : 'scale-100'
        } shadow-bb-general`}
      ></div>
      {/* Message */}
      {message && (
        <span className="font-body text-lg text-gray-600">{message}</span>
      )}
    </div>
  );
}