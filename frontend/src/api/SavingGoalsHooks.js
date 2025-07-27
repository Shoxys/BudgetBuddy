/**
 * Custom React Query hooks for savings goal operations.
 */
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { API_BASE_URL } from '../constants/ApiConstants';
import { DEFAULT_IMAGE } from '../constants/SavingGoalConstants';
import {
  fetchGoalStats,
  fetchPendingGoals,
  fetchCompletedGoals,
  saveGoal,
  getGoalById,
  deleteGoal,
  contributeToGoal,
} from './SavingGoalsApi';

/**
 * Fetch savings goal statistics.
 * @returns {object} React Query result object.
 */
export const useGoalStats = () => {
  return useQuery({
    queryKey: ['goalStats'],
    queryFn: fetchGoalStats,
    retry: 1,
    staleTime: 5 * 60 * 1000,
    refetchOnWindowFocus: true,
    refetchOnMount: true,
  });
};

/**
 * Fetch pendiang savings goals.
 * @returns {object} React Query result object.
 */
export const usePendingGoals = () => {
  return useQuery({
    queryKey: ['pendingGoals'],
    queryFn: fetchPendingGoals,
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Fetch completed savings goals.
 * @returns {object} React Query result object.
 */
export const useCompletedGoals = () => {
  return useQuery({
    queryKey: ['completedGoals'],
    queryFn: fetchCompletedGoals,
    retry: 1,
    staleTime: 5 * 60 * 1000,
  });
};

/**
 * Save a savings goal (create or update).
 * @returns {object} React Query mutation object.
 */
export const useSaveGoal = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: saveGoal,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingGoals'] });
      queryClient.invalidateQueries({ queryKey: ['completedGoals'] });
      queryClient.invalidateQueries({ queryKey: ['goalStats'] });
      queryClient.invalidateQueries({ queryKey: ['goal'] });
    },
  });
};

/**
 * Fetch a savings goal by ID.
 * @param {string} id - The ID of the savings goal.
 * @returns {object} React Query result object.
 */
export const useGetGoalById = (id) => {
  return useQuery({
    queryKey: ['goal', id],
    queryFn: async () => {
      if (!id) return null;
      const data = await getGoalById(id);
      const normalizedImageRef = data.imageRef && typeof data.imageRef === 'string' && data.imageRef.trim() !== ''
        ? data.imageRef.startsWith('http')
          ? data.imageRef
          : `${API_BASE_URL}/${data.imageRef.replace(/^\/?/, '')}` === DEFAULT_IMAGE
            ? DEFAULT_IMAGE
            : `${API_BASE_URL}/${data.imageRef.replace(/^\/?/, '')}`
        : DEFAULT_IMAGE;
      return { ...data, imageRef: normalizedImageRef };
    },
    enabled: !!id,
    retry: 1,
  });
};

/**
 * Delete a savings goal.
 * @returns {object} React Query mutation object.
 */
export const useDeleteGoal = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteGoal,
    onMutate: async (goalId) => {
      await queryClient.cancelQueries({ queryKey: ['pendingGoals'] });
      await queryClient.cancelQueries({ queryKey: ['completedGoals'] });
      const previousPendingGoals = queryClient.getQueryData(['pendingGoals']);
      const previousCompletedGoals = queryClient.getQueryData(['completedGoals']);
      queryClient.setQueryData(['pendingGoals'], (old) =>
        old ? old.filter((goal) => goal.id !== goalId) : old
      );
      queryClient.setQueryData(['completedGoals'], (old) =>
        old ? old.filter((goal) => goal.id !== goalId) : old
      );
      return { previousPendingGoals, previousCompletedGoals };
    },
    onError: (_err, _goalId, context) => {
      queryClient.setQueryData(['pendingGoals'], context.previousPendingGoals);
      queryClient.setQueryData(['completedGoals'], context.previousCompletedGoals);
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingGoals'] });
      queryClient.invalidateQueries({ queryKey: ['completedGoals'] });
      queryClient.invalidateQueries({ queryKey: ['goalStats'] });
    },
  });
};

/**
 * Contribute to a savings goal.
 * @returns {object} React Query mutation object.
 */
export const useContributeToGoal = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, amount }) => {
      if (!amount || isNaN(amount) || amount <= 0) {
        throw new Error('Invalid contribution amount');
      }
      return contributeToGoal({ id, amount });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['pendingGoals'] });
      queryClient.invalidateQueries({ queryKey: ['completedGoals'] });
      queryClient.invalidateQueries({ queryKey: ['goalStats'] });
    },
  });
};