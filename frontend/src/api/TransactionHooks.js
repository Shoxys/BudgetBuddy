/**
 * Custom React Query hooks for transaction-related operations.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  fetchTransactions,
  addTransaction,
  updateTransaction,
  deleteTransaction,
  deleteMultipleTransactions,
  importTransactions,
} from './TransactionApi';

/**
 * Helper function to invalidate all relevant queries after a transaction mutation.
 * @param {object} queryClient - The React Query client instance.
 */
const invalidateAllTransactionRelatedQueries = (queryClient) => {
  // Invalidate queries for the main transactions list
  queryClient.invalidateQueries({ queryKey: ['transactions'] });

  // Invalidate all dashboard queries that depend on transaction data
  queryClient.invalidateQueries({ queryKey: ['dashboardTotalBalance'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardAccountsSummary'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardNetworth'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardSpendingInsights'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardIncomeExpenseSummary'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardIncomeTrend'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardExpenseAnalysis'] });
  queryClient.invalidateQueries({ queryKey: ['dashboardRecentTransactions'] });
};


/**
 * Fetch transactions with pagination and sorting.
 * @param {number} page - Current page number (default: 0).
 * @param {number} size - Number of items per page (default: 20).
 * @param {string} sort - Sorting criteria (e.g., 'date,desc').
 * @returns {object} React Query result object.
 */
export const useTransactions = (page = 0, size = 20, sort = 'date,desc') => {
  return useQuery({
    queryKey: ['transactions', { page, size, sort }],
    queryFn: () => fetchTransactions(page, size, sort),
    keepPreviousData: true,
  });
};

/**
 * Add a new transaction.
 * @returns {object} React Query mutation object.
 */
export const useAddTransaction = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: addTransaction,
    onSuccess: () => {
      invalidateAllTransactionRelatedQueries(queryClient);
    },
  });
};

/**
 * Update an existing transaction.
 * @returns {object} React Query mutation object.
 */
export const useUpdateTransaction = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateTransaction,
    onSuccess: () => {
      invalidateAllTransactionRelatedQueries(queryClient);
    },
  });
};

/**
 * Delete a single transaction.
 * @returns {object} React Query mutation object.
 */
export const useDeleteTransaction = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteTransaction,
    onSuccess: () => {
      invalidateAllTransactionRelatedQueries(queryClient);
    },
  });
};

/**
 * Delete multiple transactions.
 * @returns {object} React Query mutation object.
 */
export const useDeleteMultipleTransactions = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteMultipleTransactions,
    onSuccess: () => {
      invalidateAllTransactionRelatedQueries(queryClient);
    },
  });
};

/**
 * Import transactions from CSV files.
 * @returns {object} React Query mutation object.
 */
export const useImportTransactions = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: importTransactions,
    onSuccess: () => {
      invalidateAllTransactionRelatedQueries(queryClient);
    },
  });
};