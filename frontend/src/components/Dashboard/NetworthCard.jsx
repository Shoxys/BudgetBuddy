/**
 * NetworthCard component for displaying net worth distribution.
 * Renders a pie chart via NetworthPieChart.
 */

import NetworthPieChart from './NetworthPieChart';

export default function NetworthCard({ data = { total: 0, breakdownItems: [] } }) {
  // Layout
  return (
    <div className="w-full bg-white col-start-2 col-span-1 row-span-1 rounded-lg shadow-bb-general px-4 pb-4 pt-3">
      {/* Header */}
      <h1 className="font-header font-semibold text-md 3xl:text-xl text-gray-700">Net Worth</h1>
      {/* Pie Chart */}
      <NetworthPieChart data={data} />
    </div>
  );
}