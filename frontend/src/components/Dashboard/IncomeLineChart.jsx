/**
 * IncomeLineChart component for displaying income trends over time.
 * Renders a line chart comparing this year's and last year's income.
 */

import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';
import { formatMoney } from '../../Utils/helpers';

const DEFAULT_MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
const DEFAULT_DATA = DEFAULT_MONTHS.map((month) => ({ month, thisYear: 0, lastYear: 0 }));

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    const thisYearValue = payload.find((p) => p.dataKey === 'thisYear')?.value ?? 0;
    const lastYearValue = payload.find((p) => p.dataKey === 'lastYear')?.value ?? 0;

    return (
      <div className="bg-white p-4 rounded-lg shadow-bb-general border border-gray-200 text-sm">
        <p className="font-semibold text-gray-800">{label}</p>
        <p className="text-primary_blue">This Year: {formatMoney(thisYearValue)}</p>
        <p className="text-gray-500">Last Year: {formatMoney(lastYearValue)}</p>
      </div>
    );
  }
  return null;
};

export default function IncomeLineChart({ data = { months: [], incomeThisYear: [], incomeLastYear: [] } }) {
  // Validate data structure
  const isDataValid =
    Array.isArray(data.months) &&
    Array.isArray(data.incomeThisYear) &&
    Array.isArray(data.incomeLastYear) &&
    data.months.length === 12 &&
    data.incomeThisYear.length === 12 &&
    data.incomeLastYear.length === 12;

  // Transform data for chart
  const chartData = isDataValid
    ? data.months.map((month, index) => ({
        month,
        thisYear: parseFloat(data.incomeThisYear[index]) || 0,
        lastYear: parseFloat(data.incomeLastYear[index]) || 0,
      }))
    : DEFAULT_DATA;

  // Calculate Y-axis domain for dynamic scaling
  const allValues = chartData.flatMap((d) => [d.thisYear, d.lastYear]).filter((val) => !isNaN(val));
  const yAxisDomain = isDataValid && allValues.length > 0
    ? (() => {
        const minValue = Math.min(...allValues);
        const maxValue = Math.max(...allValues);
        const dataRange = maxValue - minValue;

        // Set tick interval based on data range for clean axis labels
        const tickInterval =
          dataRange < 50 ? 5 :
          dataRange < 200 ? 20 :
          dataRange < 500 ? 50 :
          dataRange < 2000 ? 100 :
          dataRange < 5000 ? 250 :
          dataRange < 10000 ? 500 : 1000;

        // Add 10% padding to max value, round up to nearest tick
        let upperBound = Math.ceil((maxValue + dataRange * 0.1) / tickInterval) * tickInterval;
        if (upperBound === 0 && maxValue >= 0) upperBound = tickInterval; // Ensure positive upper bound

        // Adjust lower bound to zoom in for positive data or include negatives
        const shouldZoomIn = minValue >= 0 && minValue > 500;
        let lowerBound = shouldZoomIn
          ? Math.max(0, Math.floor((minValue - dataRange * 0.1) / tickInterval) * tickInterval)
          : Math.min(0, Math.floor((minValue - dataRange * 0.1) / tickInterval) * tickInterval);

        // Prevent lowerBound >= upperBound
        if (lowerBound >= upperBound) {
          lowerBound = Math.max(0, minValue - tickInterval);
          upperBound = maxValue + tickInterval;
        }

        return [lowerBound, upperBound];
      })()
    : [0, 1000]; // Fallback for invalid data

  // Layout
  return (
    <div className="flex flex-col h-full">
      {/* Header and Legend */}
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-lg font-semibold font-header text-gray-700">Income Over Time</h2>
        <div className="flex space-x-4 text-sm">
          <div className="flex items-center space-x-1 text-primary_blue font-body">
            <span className="w-3 h-3 rounded-full bg-primary_blue inline-block" />
            <span>This Year</span>
          </div>
          <div className="flex items-center space-x-1 text-gray-500">
            <span className="w-3 h-3 rounded-full bg-gray-400 inline-block" />
            <span>Last Year</span>
          </div>
        </div>
      </div>

      {/* Line Chart */}
      <ResponsiveContainer width="100%" height="88%">
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="month" stroke="#6F7787" />
          <YAxis tickFormatter={(value) => `$${value / 1000}k`} stroke="#6F7787" domain={yAxisDomain} />
          <Tooltip content={<CustomTooltip />} />
          <Line
            type="monotone"
            dataKey="thisYear"
            stroke="#0096ff"
            strokeWidth={3}
            dot={{ r: 5 }}
            name="This Year"
          />
          <Line
            type="monotone"
            dataKey="lastYear"
            stroke="#A0AEC0"
            strokeWidth={2}
            dot={{ r: 4 }}
            name="Last Year"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}