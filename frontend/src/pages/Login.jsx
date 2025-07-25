/**
 * Login page component for user authentication.
 */
import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import AuthForm from '../components/Authentication/AuthForm';
import { useLogin } from '../api/AuthHooks';
import Notification from '../components/Notification';
import { ROUTES } from '../constants/AppConstants';

/**
 * Login page with form and notification handling.
 * @returns {JSX.Element} Login component.
 */
export default function Login() {
  const [notification, setNotification] = useState({
    isOpen: false,
    type: 'info',
    message: '',
  });
  const navigate = useNavigate();
  const { mutate: login, isLoading } = useLogin();

  const loginFields = [
    { id: 'email', label: 'Email', type: 'email', placeholder: 'example.email@gmail.com', required: true },
    { id: 'password', label: 'Password', type: 'password', placeholder: 'Enter at least 8+ characters', required: true },
  ];

  // Handlers
  const handleSubmit = (event, formData) => {
    event.preventDefault();
    login(formData, {
      onSuccess: (data) => {
        setNotification({
          isOpen: true,
          type: 'success',
          message: data.message || 'Login successful',
        });
        setTimeout(() => {
          navigate(ROUTES.DASHBOARD);
        }, 1500);
      },
      onError: (error) => {
        setNotification({
          isOpen: true,
          type: 'error',
          message: error.response?.data?.message || 'An error occurred during login',
        });
      },
    });
  };

  const closeNotification = () => {
    setNotification((prev) => ({ ...prev, isOpen: false }));
  };

  // Main layout
  return (
    <div className="min-h-screen flex flex-row">
      {/* Form section */}
      <div className="w-1/2 min-h-screen flex flex-col">
        {/* Logo */}
        <div className="p-6 absolute">
          <img src="/assets/bb-logo-text.png" alt="Budget Buddy Logo with text" width="180" />
        </div>
        {/* Login form */}
        <div className="flex-1 flex items-center justify-center px-12">
          <div className="text-center w-1/2">
            <h1 className="font-header text-gray-800 text-5xl 3xl:text-6xl font-medium">Login</h1>
            <AuthForm
              fields={loginFields}
              onSubmit={handleSubmit}
              buttonLabel={isLoading ? 'Logging in...' : 'Login'}
              className="w-full mt-9"
            />
            <p className="mt-5 font-body text-md">
              <span>Don't have an account? </span>
              <Link to="/signup" className="text-primary_blue underline text-lg font-medium">
                Sign Up
              </Link>
            </p>
          </div>
        </div>
      </div>
      {/* Background image */}
      <div className="w-1/2 min-h-screen bg-[url('/assets/login-bg.png')] bg-cover bg-no-repeat order-2"></div>
      {/* Notification */}
      <Notification
        type={notification.type}
        message={notification.message}
        isOpen={notification.isOpen}
        onClose={closeNotification}
        duration={5000}
      />
    </div>
  );
}