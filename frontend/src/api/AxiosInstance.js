/**
 * Axios instance configuration for API requests.
 */
import axios from 'axios';
import { API_BASE_URL } from '../constants/ApiConstants';

export const axiosInstance = axios.create({
  baseURL: `${API_BASE_URL}/api`,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});