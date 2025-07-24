/**
 * API functions for savings goal operations.
 */
import { axiosInstance } from './AxiosInstance';
import { SAVING_GOALS_API_BASE_URL } from '../constants/ApiConstants';

// Fetch savings goal stats
export const fetchGoalStats = async () => {
  const response = await axiosInstance.get(`${SAVING_GOALS_API_BASE_URL}/stats`);
  return response.data.goalStats;
};

// Fetch pending savings goals
export const fetchPendingGoals = async () => {
  const response = await axiosInstance.get(`${SAVING_GOALS_API_BASE_URL}/pending`);
  return response.data;
};

// Fetch completed savings goals
export const fetchCompletedGoals = async () => {
  const response = await axiosInstance.get(`${SAVING_GOALS_API_BASE_URL}/completed`);
  return response.data;
};

// Save a savings goal (create or update)
export const saveGoal = async (goal) => {
  const payload = {
    title: goal.title,
    target: goal.target,
    contributed: goal.contributed || '0.00',
    date: goal.date,
    imageRef: goal.imageRef || null,
  };
  const response = await (goal.id
    ? axiosInstance.put(`${SAVING_GOALS_API_BASE_URL}/${goal.id}/update`, payload)
    : axiosInstance.post(SAVING_GOALS_API_BASE_URL, payload));
  return response.data;
};

// Fetch a savings goal by ID
export const getGoalById = async (id) => {
  const response = await axiosInstance.get(`${SAVING_GOALS_API_BASE_URL}/${id}`);
  return response.data;
};

// Delete a savings goal
export const deleteGoal = async (goalId) => {
  await axiosInstance.delete(`${SAVING_GOALS_API_BASE_URL}/${goalId}/delete`);
};

// Contribute to a savings goal
export const contributeToGoal = async ({ id, amount }) => {
  const response = await axiosInstance.put(`${SAVING_GOALS_API_BASE_URL}/${id}/contribute`, {
    contribution: Number(amount.toFixed(2)),
  });
  return response.data;
};