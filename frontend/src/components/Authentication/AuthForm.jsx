/**
 * AuthForm component for reusable email and password input forms.
 * Supports dynamic fields with password toggle functionality.
 */

import { useState } from 'react';
import PasswordToggle from '../../components/PasswordToggle';

export default function AuthForm({ fields = [], onSubmit, buttonLabel = 'Submit', className = '', disabled = false }) {
  // Initialize form data and password visibility
  const initialFormData = fields.reduce((acc, field) => ({ ...acc, [field.id]: '' }), {});
  const initialShowPassword = fields.reduce((acc, field) => ({ ...acc, [field.id]: false }), {});
  const [formData, setFormData] = useState(initialFormData);
  const [showPassword, setShowPassword] = useState(initialShowPassword);

  // Handlers
  const handleInputChange = (event) => {
    const { id, value } = event.target;
    setFormData((prev) => ({ ...prev, [id]: value }));
  };

  const handleFormSubmit = (event) => {
    event.preventDefault();
    onSubmit(event, formData);
  };

  // Layout
  return (
    <form onSubmit={handleFormSubmit} className={`flex flex-col gap-4 ${className}`}>
      {/* Form Fields */}
      {fields.map((field) => (
        <div
          key={field.id}
          className="flex flex-col px-3 py-2 text-left bg-gray-100 rounded-xl mb-5 3xl:mb-6"
        >
          <div className="relative">
            <label
              htmlFor={field.id}
              className="text-lg 3xl:text-xl text-gray-700 font-body font-bold"
            >
              {field.label}
              {field.required && <span className="text-secondary_red ml-1">*</span>}
            </label>
            <input
              id={field.id}
              name={field.id}
              type={field.type === 'password' && !showPassword[field.id] ? 'password' : 'text'}
              placeholder={field.placeholder}
              className="w-full text-left bg-gray-100 text-md 3xl:text-lg rounded-sm focus:outline-none focus:ring-2 focus:ring-primary_blue pr-10"
              value={formData[field.id]}
              onChange={handleInputChange}
              required={field.required}
              aria-required={field.required}
              disabled={disabled}
            />
            {field.type === 'password' && (
              <PasswordToggle
                name={field.id}
                showPassword={showPassword}
                setShowPassword={setShowPassword}
              />
            )}
          </div>
        </div>
      ))}
      {/* Submit Button */}
      <button
        type="submit"
        className="bg-primary_blue text-white text-xl rounded-xl font-header font-bold mt-7 shadow-bb-general w-full px-6 py-2 hover:bg-btn_hover transition-colors"
        aria-label={buttonLabel}
        disabled={disabled}
      >
        {buttonLabel}
      </button>
    </form>
  );
}