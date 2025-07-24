/**
 * SavingGoalsCard component for displaying saving goal progress.
 * Renders individual goal items or a call-to-action if no goals.
 */

import { Link } from 'react-router-dom';
import { ROUTES } from '../../constants/AppConstants';

function SavingGoalItem({ title, contributed, target }) {
  const percentage = Math.min((contributed / target) * 100, 100);
  const isCompleted = percentage === 100;

  const progressBarColor = isCompleted ? 'bg-green-500' : 'bg-primary_blue';
  const amountTextColor = isCompleted ? 'text-green-700' : 'text-blue-700';

  const contributedLabelStyle = {
    left: percentage === 0 ? '0%' : percentage === 100 ? 'auto' : `${percentage}%`,
    right: percentage === 100 ? '0%' : 'auto',
    transform: percentage === 0 || percentage === 100 ? 'translateX(0)' : 'translateX(-50%)',
  };

  return (
    <div className="flex flex-col mb-4 last:mb-0 border-b border-gray-200 last:border-b-0 pb-3 last:pb-0">
      {/* Goal Header */}
      <div className="flex items-center gap-2 mb-1">
        <span className="text-gray-600 text-lg 3xl:text-2xl">🎯</span>
        <div className="text-base 3xl:text-xl text-gray-800 font-semibold">{title}</div>
        {isCompleted && (
          <span className="ml-auto text-green-600 text-sm 3xl:text-lg font-bold">Completed! 🎉</span>
        )}
      </div>

      {/* Contributed Amount */}
      <div className="relative h-6 overflow-hidden">
        <div
          className={`absolute top-0 whitespace-nowrap transition-all duration-300 ${amountTextColor} text-sm 3xl:text-lg font-bold`}
          style={contributedLabelStyle}
        >
          ${contributed.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}
        </div>
      </div>

      {/* Progress Bar */}
      <div className="w-full h-3 3xl:h-4 bg-gray-200 rounded-full relative mt-1">
        <div
          className={`h-full ${progressBarColor} rounded-full transition-all duration-300`}
          style={{ width: `${percentage}%` }}
        />
      </div>

      {/* Min/Max Values */}
      <div className="flex justify-between text-xs 3xl:text-sm text-gray-500 font-medium mt-1">
        <span>$0</span>
        <span>${target.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}</span>
      </div>
    </div>
  );
}

export default function SavingGoalsCard({ data = [] }) {
  const isDataValid = Array.isArray(data) && data.length > 0;

  // Layout
  return (
    <div className="w-full min-w-[23vw] bg-white row-span-1 row-start-2 col-span-1 rounded-lg shadow-bb-general px-4 py-3">
      {/* Header */}
      <h2 className="font-header text-lg 3xl:text-2xl text-gray-800 font-bold mb-4">Saving Goals</h2>

      {/* Goals */}
      {isDataValid ? (
        data.map((goal, index) => <SavingGoalItem key={index} {...goal} />)
      ) : (
        <div className="flex flex-col gap-3 items-center justify-center mt-3 text-lg 3xl:text-xl font-body text-gray-700 bg-neutral-50 p-4 rounded-md text-center border border-dashed border-gray-300">
          <span>No saving goals set yet.</span>
          <Link
            to={ROUTES.SAVING_GOALS}
            className="flex items-center gap-2 px-4 py-2 bg-primary_blue text-white rounded-md hover:bg-blue-600 transition-colors"
          >
            Set Your First Goal!
            <img src="/assets/goal-plus.png" alt="Set goals button icon" className="w-6 h-6" />
          </Link>
        </div>
      )}
    </div>
  );
}