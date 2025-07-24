/**
 * API functions for dashboard-related operations.
 */
import { axiosInstance } from './AxiosInstance';
import { DASHBOARD_API_BASE_URL } from '../constants/ApiConstants';

// Helper function to fetch data with auth header
const fetchData = async (endpoint) => {
  const response = await axiosInstance.get(`${DASHBOARD_API_BASE_URL}${endpoint}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    },
  });
  return response.data;
};

export const fetchTotalBalance = () => fetchData('/total-balance');

export const fetchAccountsSummary = () => fetchData('/accounts-summary');

export const fetchNetworth = () => fetchData('/networth');

export const fetchSpendingInsights = () => fetchData('/spending-insights');

export const fetchSavingGoals = () => fetchData('/saving-goals');

export const fetchIncomeExpenseSummary = () => fetchData('/income-expense-summary');

export const fetchIncomeTrend = () => fetchData('/income-trend');

export const fetchExpenseAnalysis = () => fetchData('/expense-analysis');

export const fetchRecentTransactions = () => fetchData('/recent-transactions');