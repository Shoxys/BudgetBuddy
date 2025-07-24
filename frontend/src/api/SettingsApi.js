/**
 * API functions for user settings operations.
 */
import { axiosInstance } from './AxiosInstance';
import { SETTINGS_API_BASE_URL } from '../constants/ApiConstants';

// Update user email
export const updateEmail = async (emailData) => {
  const response = await axiosInstance.put(`${SETTINGS_API_BASE_URL}/update-email`, emailData);
  return response.data;
};

// Update user password
export const updatePassword = async (passwordData) => {
  const response = await axiosInstance.put(`${SETTINGS_API_BASE_URL}/change-password`, passwordData);
  return response.data;
};

// Delete user account
export const deleteAccount = async () => {
  const response = await axiosInstance.delete(`${SETTINGS_API_BASE_URL}/delete-account`);
  return response.data;
};

// Fetch current user email
export const fetchCurrentEmail = async () => {
  const response = await axiosInstance.get(`${SETTINGS_API_BASE_URL}/current-email`);
  return response.data;
};