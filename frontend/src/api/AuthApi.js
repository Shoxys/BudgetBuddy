/**
 * API functions for authentication operations.
 */
import { axiosInstance } from './AxiosInstance';
import { AUTH_API_BASE_URL } from '../constants/ApiConstants';

// Log in a user
export const login = async (credentials) => {
  const response = await axiosInstance.post(`${AUTH_API_BASE_URL}/login`, credentials);
  return response.data;
};

// Sign up a new user
export const signupUser = async (formData) => {
  const response = await axiosInstance.post(`${AUTH_API_BASE_URL}/signup`, formData);
  return response.data;
};

// Log out a user
export const logoutUser = async () => {
  const response = await axiosInstance.post(`${AUTH_API_BASE_URL}/logout`, {}, { withCredentials: true });
  return response.data;
};