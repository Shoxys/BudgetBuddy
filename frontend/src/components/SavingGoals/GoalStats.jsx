/**
 * GoalStats component for displaying savings goal statistics.
 * Renders cards with goal counts and insights by type.
 */

import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import { GoalTypes, GoalMeta } from '../../constants/SavingGoalConstants';
import { useGoalStats } from '../../api/SavingGoalsHooks';

export default function GoalStats() {
  const { data: goalData, isLoading, error } = useGoalStats();

  // Layout
  return (
    <div className="flex flex-row gap-5">
      {/* Notification */}
      {error && (
        <Notification
          isOpen={!!error}
          type="error"
          message={`Failed to load goal stats: ${error.response?.data || error.message}`}
          onClose={() => {}}
        />
      )}
      {/* Loading */}
      {isLoading && (
        <div className="flex justify-center py-4">
          <LoadingSpinner />
        </div>
      )}
      {/* Stats */}
      {!isLoading && !error && goalData && (
        <>
          {Object.values(GoalTypes).map((goalType) => {
            // Map goal data to metadata
            const goal = goalData.find((item) => item.goalType === goalType) || {
              insight: 'No data available',
              amount: 0,
              goalType,
            };
            const { title, icon, color } = GoalMeta[goalType];

            return (
              <div
                key={goalType}
                className={`px-6 pt-2 pb-3.5 flex flex-row ${color} gap-3 rounded-lg w-[19vw] shadow-bb-general`}
              >
                <img className="w-16 h-16" src={icon} alt={`${title} icon`} />
                <div className="flex flex-col gap-1">
                  <div className="flex flex-row items-center gap-4">
                    <span className="font-body text-md font-semibold">{title}</span>
                    <span className="text-2xl font-header font-bold">{goal.amount}</span>
                  </div>
                  <span className="font-body text-sm font-normal">{goal.insight}</span>
                </div>
              </div>
            );
          })}
        </>
      )}
    </div>
  );
}