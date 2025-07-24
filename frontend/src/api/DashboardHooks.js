/**
 * Custom React Query hooks for dashboard-related operations.
 */
import { useQuery } from '@tanstack/react-query';
import {
  fetchTotalBalance,
  fetchAccountsSummary,
  fetchNetworth,
  fetchSpendingInsights,
  fetchSavingGoals,
  fetchIncomeExpenseSummary,
  fetchIncomeTrend,
  fetchExpenseAnalysis,
  fetchRecentTransactions,
} from './DashboardApi';

/**
 * Fetch total balance for the dashboard.
 * @returns {object} React Query result object.
 */
export const useTotalBalance = () => {
  return useQuery({
    queryKey: ['dashboardTotalBalance'],
    queryFn: fetchTotalBalance,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch accounts summary for the dashboard.
 * @returns {object} React Query result object.
 */
export const useAccountsSummary = () => {
  return useQuery({
    queryKey: ['dashboardAccountsSummary'],
    queryFn: fetchAccountsSummary,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch net worth for the dashboard.
 * @returns {object} React Query result object.
 */
export const useNetworth = () => {
  return useQuery({
    queryKey: ['dashboardNetworth'],
    queryFn: fetchNetworth,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch spending insights for the dashboard.
 * @returns {object} React Query result object.
 */
export const useSpendingInsights = () => {
  return useQuery({
    queryKey: ['dashboardSpendingInsights'],
    queryFn: fetchSpendingInsights,
    staleTime: 60 * 60 * 1000,
    refetchOnWindowFocus: false,
  });
};

/**
 * Fetch saving goals for the dashboard.
 * @returns {object} React Query result object.
 */
export const useSavingGoals = () => {
  return useQuery({
    queryKey: ['dashboardSavingGoals'],
    queryFn: fetchSavingGoals,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch income and expense summary for the dashboard.
 * @returns {object} React Query result object.
 */
export const useIncomeExpenseSummary = () => {
  return useQuery({
    queryKey: ['dashboardIncomeExpenseSummary'],
    queryFn: fetchIncomeExpenseSummary,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch income trend for the dashboard.
 * @returns {object} React Query result object.
 */
export const useIncomeTrend = () => {
  return useQuery({
    queryKey: ['dashboardIncomeTrend'],
    queryFn: fetchIncomeTrend,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch expense analysis for the dashboard.
 * @returns {object} React Query result object.
 */
export const useExpenseAnalysis = () => {
  return useQuery({
    queryKey: ['dashboardExpenseAnalysis'],
    queryFn: fetchExpenseAnalysis,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
  });
};

/**
 * Fetch recent transactions for the dashboard.
 * @returns {object} React Query result object.
 */
export const useRecentTransactions = () => {
  return useQuery({
    queryKey: ['dashboardRecentTransactions'],
    queryFn: fetchRecentTransactions,
    staleTime: 60 * 1000,
    refetchOnWindowFocus: true,
  });
};