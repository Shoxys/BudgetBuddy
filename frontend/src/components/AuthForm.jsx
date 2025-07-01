import Button from '../components/Button';
import { useState } from 'react'

const AuthForm = ({ fields, onSubmit, buttonLabel, className = '' }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit();
  };

  // Object to track state per field
  const [showPassword, setShowPassword] = useState({}); 

  const togglePasswordVisibility = (fieldId) => {
    setShowPassword((prev) => ({
      ...prev,
      [fieldId]: !prev[fieldId],
    }));
  };

 return (
    <form onSubmit={handleSubmit} className={`${className}`}>
      {fields.map((field, index) => (
        <div key={index} className="flex flex-col px-3 py-2 text-left bg-bb_neutral rounded-xl mb-5 3xl:mb-6">
          <label htmlFor={field.id} className="text-lg 3xl:text-xl text-gray-700 font-body font-bold">
            {field.label}
          </label>
          <div className="relative">
            <input
              id={field.id}
              type={field.type === 'password' && !showPassword[field.id] ? 'password' : 'text'}
              placeholder={field.placeholder}
              className="rounded-sm w-full text-left bg-bb_neutral text-md 3xl:text-lg focus:outline-none focus:ring-primary_blue pr-10"
            />
            {field.type === 'password' && (
              <button
                type="button"
                onClick={() => togglePasswordVisibility(field.id)}
                className="absolute right-3 top-1/2 transform -translate-y-1/2 w-6 h-6 hover:opacity-80"
              >
                <img
                  src={showPassword[field.id] ? '/src/assets/eye-open.png' : '/src/assets/eye-closed.png'}
                  alt={showPassword[field.id] ? 'Hide password' : 'Show password'}
                  className="w-full h-full object-contain"
                />
              </button>
            )}
          </div>
        </div>
      ))}
      <Button 
        label={buttonLabel}
        size="text-xl"
        padding="px-6 py-2"
        className="mt-7 shadow-custom-blue w-full"
        onClick={onSubmit}
      />
    </form>
  )
}

export default AuthForm;