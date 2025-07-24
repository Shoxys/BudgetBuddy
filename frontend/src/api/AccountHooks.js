/**
 * Custom React Query hooks for account-related operations.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getAccountBalance, updateAccountBalance } from './AccountApi';

/**
 * Fetch a single account's balance.
 * @param {string} accountName - The name of the account.
 * @param {string} accountType - The type of the account (e.g., AccountTypes.SAVINGS).
 * @param {boolean} enabled - Whether the query should run (default: true).
 * @returns {object} React Query result object.
 */
export const useAccountBalance = (accountName, accountType, enabled = true) => {
  return useQuery({
    queryKey: ['accountBalance', accountName, accountType],
    queryFn: () => getAccountBalance(accountName, accountType),
    enabled: enabled && !!accountName && !!accountType,
    staleTime: Infinity,
  });
};

/**
 * Update an account's balance.
 * @returns {object} React Query mutation object.
 */
export const useUpdateAccountBalance = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, name, accountType, balance }) => {
      if (!accountType || !name || isNaN(balance)) {
        throw new Error('Invalid parameters: accountType, name, and balance are required.');
      }
      return updateAccountBalance(id, name, accountType, balance);
    },
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['accountBalance', variables.name, variables.accountType], exact: true });
      queryClient.invalidateQueries({ queryKey: ['dashboardAccountsSummary'] });
      queryClient.invalidateQueries({ queryKey: ['dashboardTotalBalance'] });
      queryClient.invalidateQueries({ queryKey: ['dashboardNetworth'] });
      queryClient.setQueriesData({ queryKey: ['accountBalance', variables.name, variables.accountType] }, _data);
    },
  });
};