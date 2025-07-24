/**
 * Signup component for user registration.
 * Renders a form for email and password input with success/error notifications.
 */

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthForm from '../components/Authentication/AuthForm';
import Notification from '../components/Notification';
import LoadingSpinner from '../components/LoadingSpinner';
import { useSignupUser } from '../api/AuthHooks';
import { ROUTES } from '../constants/AppConstants';

export default function Signup() {
  const navigate = useNavigate();
  const [notificationState, setNotificationState] = useState({
    isOpen: false,
    type: 'info',
    message: '',
  });
  const signupUser = useSignupUser();

  const signupFields = [
    { id: 'email', label: 'Email', type: 'email', placeholder: 'example.email@gmail.com', required: true },
    { id: 'password', label: 'Password', type: 'password', placeholder: 'Enter at least 8+ characters', required: true },
    {
      id: 'confirmPassword',
      label: 'Confirm Password',
      type: 'password',
      placeholder: 'Enter password again',
      required: true,
    },
  ];

  // Handlers
  const handleSubmit = (event, formData) => {
    event.preventDefault();

    // Validate empty fields
    if (!formData.email || !formData.password || !formData.confirmPassword) {
      setNotificationState({
        isOpen: true,
        type: 'error',
        message: 'All fields are required',
      });
      return;
    }

    // Validate password confirmation
    if (formData.password !== formData.confirmPassword) {
      setNotificationState({
        isOpen: true,
        type: 'error',
        message: 'Passwords do not match',
      });
      return;
    }

    // Submit signup mutation
    signupUser.mutate(
      {
        email: formData.email,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
      },
      {
        onSuccess: (data) => {
          setNotificationState({
            isOpen: true,
            type: 'success',
            message: data.message || 'User registered successfully',
          });
          setTimeout(() => navigate(ROUTES.ONBOARDING), 1500);
        },
        onError: (error) => {
          setNotificationState({
            isOpen: true,
            type: 'error',
            message:
              error.response?.data?.message ||
              error.response?.data?.errors?.map((err) => err.defaultMessage || err.message).join('; ') ||
              error.message ||
              'An error occurred during signup',
          });
        },
      }
    );
  };

  // Layout
  return (
    <div className="min-h-screen flex flex-row">
      {/* Notification */}
      {notificationState.isOpen && (
        <Notification
          type={notificationState.type}
          message={notificationState.message}
          isOpen={notificationState.isOpen}
          onClose={() => setNotificationState((prev) => ({ ...prev, isOpen: false }))}
          duration={5000}
        />
      )}
      {/* Form Section */}
      <div className="w-1/2 min-h-screen flex flex-col">
        <div className="py-5 px-10 absolute">
          <img src="/assets/bb-logo-text.png" alt="Budget Buddy Logo" width="160" />
        </div>
        <div className="flex-1 flex items-center justify-center px-12">
          {signupUser.isPending ? (
            <LoadingSpinner message="Registering user..." />
          ) : (
            <div className="text-center w-1/2">
              <h1 className="font-header text-gray-800 text-5xl 3xl:text-6xl font-semibold">Sign Up</h1>
              <AuthForm
                fields={signupFields}
                onSubmit={handleSubmit}
                buttonLabel="Sign Up"
                className="w-full mt-9 3xl:mt-14"
                disabled={signupUser.isPending}
              />
              <p className="mt-5 font-body text-md">
                Already have an account?{' '}
                <Link to={ROUTES.LOGIN} className="text-primary_blue underline text-lg font-medium">
                  Login
                </Link>
              </p>
            </div>
          )}
        </div>
      </div>
      {/* Background Image Section */}
      <div className="w-1/2 min-h-screen bg-[url('/assets/signup-bg.png')] bg-cover bg-no-repeat order-2"></div>
    </div>
  );
}