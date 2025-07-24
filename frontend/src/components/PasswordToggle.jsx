/**
 * PasswordToggle component for toggling password visibility.
 * Displays an eye icon to show/hide password input.
 */

export default function PasswordToggle({ showPassword, setShowPassword, name }) {
  // Toggle password visibility for specified input
  const togglePasswordVisibility = () => {
    setShowPassword((prev) => ({ ...prev, [name]: !prev[name] }));
  };

  // Layout
  return (
    <button
      type="button"
      onClick={togglePasswordVisibility}
      className="absolute right-3 top-[2.2rem] text-gray-400"
      aria-label={showPassword[name] ? 'Hide password' : 'Show password'}
    >
      <img
        src={showPassword[name] ? '/assets/eye-open.png' : '/assets/eye-closed.png'}
        alt={showPassword[name] ? 'Hide password' : 'Show password'}
        className="w-6 h-6 object-contain"
      />
    </button>
  );
}