/**
 * SavingGoals component for displaying and managing saving goals.
 * Renders goal statistics, pending and completed goals, and a modal for adding new goals.
 */

// Component Imports
import Sidebar from '../components/Sidebar';
import GoalStats from '../components/SavingGoals/GoalStats';
import GoalCard from '../components/SavingGoals/GoalCard';
import GoalModal from '../components/SavingGoals/GoalModal';
import Notification from '../components/Notification';
import LoadingSpinner from '../components/LoadingSpinner';

// Hook Imports
import { useState, useEffect } from 'react';
import { usePendingGoals, useCompletedGoals } from '../api/SavingGoalsHooks';

// Constant Imports
import { GoalMeta } from '../constants/SavingGoalConstants';
import { ROUTES } from '../constants/AppConstants';

export default function SavingGoals() {
  // State for modal and notifications
  const [isGoalModalOpen, setIsGoalModalOpen] = useState(false);
  const [notificationState, setNotificationState] = useState({
    isOpen: false,
    type: 'info',
    message: '',
  });

  // Fetch goal data
  const {
    data: pendingGoals = [],
    isLoading: isLoadingPending,
    error: errorPending,
  } = usePendingGoals();
  const {
    data: completedGoals = [],
    isLoading: isLoadingCompleted,
    error: errorCompleted,
  } = useCompletedGoals();

  // Handle errors
  useEffect(() => {
    if (errorPending || errorCompleted) {
      setNotificationState({
        isOpen: true,
        type: 'error',
        message: errorPending?.message || errorCompleted?.message || 'Failed to load goals',
      });
    }
  }, [errorPending, errorCompleted]);

  // Handlers
  const openGoalModal = () => setIsGoalModalOpen(true);
  const closeGoalModal = () => setIsGoalModalOpen(false);
  const closeNotification = () => setNotificationState((prev) => ({ ...prev, isOpen: false }));

  // Derived state
  const hasPendingGoals = pendingGoals.length > 0;
  const hasCompletedGoals = completedGoals.length > 0;
  const isLoading = isLoadingPending || isLoadingCompleted;

  // Layout
  return (
    <div className="relative min-h-screen bg-bb_slate pl-20">
      {/* Notification */}
      <Notification
        type={notificationState.type}
        message={notificationState.message}
        isOpen={notificationState.isOpen}
        onClose={closeNotification}
        duration={5000}
      />

      {/* Sidebar */}
      <Sidebar selectedNav={ROUTES.DASHBOARD} />

      {/* Main Content */}
      <div className="flex flex-col w-full pb-4">
        {/* Header */}
        <div className="flex flex-col bg-white py-1 mt-3 px-10">
          <div className="flex flex-row items-center gap-2">
            <img className="w-10.5" src={GoalMeta.TOTAL.icon} alt="Goals icon" />
            <h2 className="text-2xl font-header font-bold">Saving Goals</h2>
          </div>
        </div>

        {/* Pending Goals Section */}
        <div className="flex flex-col gap-5 bg-white py-4 px-6 ml-8 mr-8 mt-2 text-black rounded-sm shadow-bb-general">
          <GoalStats />
          <div className="flex flex-col gap-4 min-h-[40vh]">
            <div className="flex flex-row gap-6 items-center">
              <h1 className="font-header text-2xl font-bold text-gray-800">My Goals</h1>
              <button
                onClick={openGoalModal}
                className="btn-primary font-header font-semibold text-md rounded-md px-4 py-2 hover:bg-btn_hover"
              >
                + New Goal
              </button>
            </div>
            {isLoading ? (
              <LoadingSpinner message="Loading goals..." />
            ) : hasPendingGoals ? (
              <GoalCard data={pendingGoals} />
            ) : (
              <span className="font-body text-gray-800 text-md">Add new goals to get started!</span>
            )}
          </div>
        </div>

        {/* Completed Goals Section */}
        <div className="flex flex-col min-h-[40vh] gap-4 py-4 px-4 ml-8 mr-8 text-black rounded-sm shadow-bb-general" style={{ backgroundColor: GoalMeta.COMPLETED.color }}>
          <h1 className="font-header text-xl font-bold text-gray-800">Completed</h1>
          {isLoading ? (
            <LoadingSpinner message="Loading completed goals..." />
          ) : hasCompletedGoals ? (
            <GoalCard data={completedGoals} completed={true} />
          ) : (
            <span className="font-body text-gray-800 text-md">Add new goals to get started!</span>
          )}
        </div>

        {/* Goal Modal */}
        <GoalModal isOpen={isGoalModalOpen} onClose={closeGoalModal} />
      </div>
    </div>
  );
}