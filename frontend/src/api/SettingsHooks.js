/**
 * Custom React Query hooks for user settings operations.
 */
import { useQuery, useMutation } from '@tanstack/react-query';
import { updateEmail, updatePassword, deleteAccount, fetchCurrentEmail } from './SettingsApi';

/**
 * Update user email.
 * @returns {object} React Query mutation object.
 */
export const useUpdateEmail = () => {
  return useMutation({
    mutationFn: updateEmail,
    onError: (error) => {
      throw error.response?.data || error;
    },
  });
};

/**
 * Update user password.
 * @returns {object} React Query mutation object.
 */
export const useUpdatePassword = () => {
  return useMutation({
    mutationFn: updatePassword,
    onError: (error) => {
      throw error.response?.data || error;
    },
  });
};

/**
 * Delete user account.
 * @returns {object} React Query mutation object.
 */
export const useDeleteAccount = () => {
  return useMutation({
    mutationFn: deleteAccount,
    onError: (error) => {
      throw error.response?.data || error;
    },
  });
};

/**
 * Fetch current user email.
 * @returns {object} React Query result object.
 */
export const useFetchCurrentEmail = () => {
  return useQuery({
    queryKey: ['currentEmail'],
    queryFn: fetchCurrentEmail,
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });
};