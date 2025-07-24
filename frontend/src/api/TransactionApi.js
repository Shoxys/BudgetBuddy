/**
 * API functions for transaction-related operations.
 */
import { axiosInstance } from './AxiosInstance';
import { TRANSACTION_API_BASE_URL } from '../constants/ApiConstants';

// Fetch transactions with pagination and sorting
export const fetchTransactions = async (page = 0, size = 20, sort = 'date,desc') => {
  const response = await axiosInstance.get(`${TRANSACTION_API_BASE_URL}/paginated`, {
    params: { page, size, sort },
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
  return {
    content: response.data._embedded?.transactionList || response.data.content || [],
    totalPages: response.data.page?.totalPages || 0,
    totalElements: response.data.page?.totalElements || 0,
  };
};

// Add a new transaction
export const addTransaction = async (transactionData) => {
  const { data } = await axiosInstance.post(TRANSACTION_API_BASE_URL, transactionData);
  return data;
};

// Update an existing transaction
export const updateTransaction = async (payload) => {
  const { id, ...updateData } = payload;
  
  const { data } = await axiosInstance.put(`${TRANSACTION_API_BASE_URL}/${id}`, updateData);
  return data;
};

// Delete a single transaction
export const deleteTransaction = async (transactionId) => {
  await axiosInstance.delete(`${TRANSACTION_API_BASE_URL}/${transactionId}`);
};

// Delete multiple transactions
export const deleteMultipleTransactions = async (transactionIds) => {
  await axiosInstance.delete(`${TRANSACTION_API_BASE_URL}/delete-selected`, {
    data: transactionIds,
  });
};

// Import transactions from CSV files
export const importTransactions = async (files) => {
  const formData = new FormData();
  files.forEach((file) => formData.append('files', file));
  const { data } = await axiosInstance.post(`${TRANSACTION_API_BASE_URL}/upload`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return data;
};