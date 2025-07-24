/**
 * InsightsCard component for displaying spending insights.
 * Renders a list of insights or a call-to-action if no data.
 */

import { Link } from 'react-router-dom';
import { ROUTES } from '../../constants/AppConstants';

export default function InsightsCard({ data = [] }) {
  // The specific message from your backend indicating no insights could be generated
  const noDataMessage = 'Not enough transaction data';

  // Check if the data array is empty or contains the noDataMessage
  const hasActualInsights =
    Array.isArray(data) && data.length > 0 && !data[0]?.insight.includes(noDataMessage);

  // Layout
  return (
    <div className="w-full min-w-[23vw] bg-white col-span-1 row-span-1 px-3 pb-4 pt-3 flex flex-col gap-2 rounded-lg shadow-bb-general">
      {/* Header */}
      <h1 className="font-header text-gray-700 font-semibold text-md 3xl:text-xl text-wrap">
        💡 Spending Insight
      </h1>

      {/* Insights */}
      {hasActualInsights ? (
        <ul className="font-body text-sm 3xl:text-lg list-disc pl-5 flex flex-col gap-3">
          {data.map(({ insight }, index) => (
            <li key={index}>{insight}</li>
          ))}
        </ul>
      ) : (
        // This block will now render for both an empty array and the "no data" message
        <div className="flex flex-row w-full py-1 pl-4 pr-2 gap-1 bg-gray-100 rounded-md items-center">
          <span className="font-medium text-lg text-gray-600">Add transactions for insights!</span>
          <Link to={ROUTES.TRANSACTIONS}>
            <img src="/assets/spending-plus.png" alt="Transactions plus icon" className="w-6 h-6" />
          </Link>
        </div>
      )}
    </div>
  );
}