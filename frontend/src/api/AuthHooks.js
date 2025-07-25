/**
 * Custom React Query hooks for authentication operations.
 */
import { useMutation } from '@tanstack/react-query';
import { login, signupUser, logoutUser } from './AuthApi';

/**
 * Log in a user with credentials.
 * @returns {object} React Query mutation object.
 */
export const useLogin = () => {
  return useMutation({
<<<<<<< HEAD
  mutationFn: login,
=======
    mutationFn: login,
    onError: (error) => {
      throw new Error(error.response?.data?.message || 'An unexpected error occurred during login');
    },
>>>>>>> fc742901d7cab31b565d63a7f9cef837dace4bb7
  });
};

/**
 * Sign up a new user.
 * @returns {object} React Query mutation object.
 */
export const useSignupUser = () => {
  return useMutation({
    mutationFn: signupUser,
    onError: (error) => {
      throw new Error(error.response?.data?.message || 'An unexpected error occurred during signup');
    },
  });
};

/**
 * Log out a user.
 * @returns {object} React Query mutation object.
 */
export const useLogoutUser = () => {
  return useMutation({
    mutationFn: logoutUser,
    onError: (error) => {
      throw new Error(error.response?.data?.message || 'An unexpected error occurred during logout');
    },
  });
};