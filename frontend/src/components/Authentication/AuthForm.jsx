import PasswordToggle from "../PasswordToggle"
import { useState } from 'react'

export default function AuthForm ({ fields, onSubmit, buttonLabel, className = '' }) {
  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit();
  };

  // Object to track state per field
  const [showPassword, setShowPassword] = useState({}); 

 return (
    <form onSubmit={handleSubmit} className={`${className}`}>
      {fields.map((field, index) => (
        <div key={index} className="flex flex-col px-3 py-2 text-left bg-bb_neutral rounded-xl mb-5 3xl:mb-6">
          <div className="relative">
            <label htmlFor={field.id} className="text-lg 3xl:text-xl text-gray-700 font-body font-bold">
              {field.label}
            </label>
            <input
              id={field.id}
              type={field.type === 'password' && !showPassword[field.id] ? 'password' : 'text'}
              placeholder={field.placeholder}
              className="rounded-sm w-full text-left bg-bb_neutral text-md 3xl:text-lg focus:outline-none focus:ring-primary_blue pr-10"
            />
            {field.type === 'password' && (
              <PasswordToggle name={field.id} showPassword={showPassword} setShowPassword={setShowPassword}/>
            )}
          </div>
        </div>
      ))}
      <button 
        className="btn-primary text-xl rounded-xl font-header font-bold mt-7 shadow-custom-blue w-full px-6 py-2"
      >
          {buttonLabel}
      </button>

    </form>
  )
}

