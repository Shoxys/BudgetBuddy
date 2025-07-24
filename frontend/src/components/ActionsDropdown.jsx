/**
 * ActionsDropdown component for displaying edit and delete actions.
 * Closes on outside click and supports disabled state.
 */

import { useState, useRef, useEffect } from 'react';

export default function ActionsDropdown({ onEdit, onDelete, children, disabled = false }) {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown on outside click
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Layout
  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
        className="text-2xl text-gray-600 hover:text-black transition-colors"
        disabled={disabled}
      >
        {children}
      </button>
      {isDropdownOpen && (
        <div className="absolute right-0 z-10 mt-2 w-36 bg-white rounded-md shadow-bb-general border">
          <button
            className="flex items-center gap-2 px-3 py-2 w-full hover:bg-gray-100 transition-colors"
            onClick={() => {
              setIsDropdownOpen(false);
              onEdit();
            }}
          >
            <img className="h-8" src="/assets/edit-icon.png" alt="Edit icon" />
            Edit
          </button>
          <button
            className="flex items-center gap-2 px-3 py-2 text-secondary_red hover:bg-gray-100 w-full text-left transition-colors"
            onClick={() => {
              setIsDropdownOpen(false);
              onDelete();
            }}
          >
            <img className="h-8" src="/assets/trash.png" alt="Delete icon" />
            Delete
          </button>
        </div>
      )}
    </div>
  );
}