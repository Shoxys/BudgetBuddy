/**
 * NetworthPieChart component for visualizing net worth distribution.
 * Renders a pie chart with account type breakdown and legend.
 */

import { PieChart, Pie, Cell, Legend, ResponsiveContainer } from 'recharts';

const ALL_ACCOUNT_TYPES = ['Goal Savings', 'Spending Account', 'Saving Account', 'Investment Account'];
const COLOR_MAP = {
  'Goal Savings': '#64e69c',
  'Spending Account': '#853fff',
  'Saving Account': '#e92c81',
  'Investment Account': '#ffbd51',
};
const EMPTY_COLOR = '#E5E7EB';

export default function NetworthPieChart({ data = { total: 0, breakdownItems: [] } }) {
  // Validate data structure
  const isDataValid = data && typeof data.total === 'number' && Array.isArray(data.breakdownItems) && data.breakdownItems.length > 0;

  // Transform data: include all account types, default to 0 if missing
  const breakdownMap = new Map();
  if (isDataValid) {
    data.breakdownItems.forEach((item) => breakdownMap.set(item.name, item.value ?? 0));
  }
  const chartData = ALL_ACCOUNT_TYPES.map((name) => ({
    name,
    value: breakdownMap.get(name) || 0,
  })).sort((a, b) => b.value - a.value); // Sort by value descending

  // Determine if chart should show "No Data"
  const displayNoData = !isDataValid || chartData.every((item) => item.value === 0);

  // Render legend with percentage for valid data
  const renderLegendText = (value) => {
    if (displayNoData) return <span className="text-gray-400 text-sm">No Data</span>;

    const item = chartData.find((d) => d.name === value);
    const percentage = data.total > 0 ? ((item.value / data.total) * 100).toFixed(0) : 0;

    return (
      <span className="text-gray-600 text-sm p-1">
        {value} <span className="font-semibold text-gray-800">{percentage}%</span>
      </span>
    );
  };

  // Layout
  return (
    <div className="w-full h-[20vh] relative">
      {/* Pie Chart */}
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          {!displayNoData && (
            <Legend
              iconType="circle"
              layout="vertical"
              verticalAlign="middle"
              align="right"
              iconSize={10}
              formatter={renderLegendText}
            />
          )}
          <Pie
            data={displayNoData ? [{ name: 'No Data', value: 1 }] : chartData}
            dataKey="value"
            cx="45%"
            cy="50%"
            innerRadius="60%"
            outerRadius="80%"
            paddingAngle={displayNoData ? 0 : 5}
          >
            {chartData.map((entry) => (
              <Cell
                key={`cell-${entry.name}`}
                fill={displayNoData ? EMPTY_COLOR : COLOR_MAP[entry.name] || EMPTY_COLOR}
              />
            ))}
          </Pie>
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}