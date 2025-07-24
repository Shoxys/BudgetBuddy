/**
 * API functions for account-related operations.
 */
import { axiosInstance } from './AxiosInstance';
import { ACCOUNT_API_BASE_URL } from '../constants/ApiConstants';

// Update account balance
export const updateAccountBalance = async (id, name, accountType, balance) => {
  const response = await axiosInstance.post(`${ACCOUNT_API_BASE_URL}/update`, {
    id,
    name,
    accountType,
    balance,
  });
  return response.data;
};

// Fetch account balance
export const getAccountBalance = async (name, accountType) => {
  const response = await axiosInstance.get(`${ACCOUNT_API_BASE_URL}/balance`, {
    params: { name, accountType },
  });
  return response.data;
};