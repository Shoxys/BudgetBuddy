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

// Custom Tooltip
const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200 text-sm">
        <p className="font-semibold text-gray-800">{label}</p>
        <p className="text-blue-600">This Year: {formatMoney(payload[0].value)}</p>
        <p className="text-gray-500">Last Year: {formatMoney(payload[1].value)}</p>
      </div>
    );
  }
  return null;
};

const DEFAULT_RANGE = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

export default function IncomeLineChart ({data}) {
  const isValid = Array.isArray(data) && data.length > 0;
  const fallback = DEFAULT_RANGE.map((month) => ({month, thisYear: 0, lastYear: 0}));

  data = isValid ? data : fallback;

  return (
    <>
     { /* Title */}
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-lg font-semibold text-gray-700">Income Over Time</h2>
        { /* Legend */}
        <div className="flex space-x-4 text-sm">
          <div className="flex items-center space-x-1 text-primary_blue font-body">
            <span className="w-3 h-3 rounded-full bg-primary_blue inline-block"></span>
            <span>This Year</span>
          </div>
          <div className="flex items-center space-x-1 text-gray-500">
            <span className="w-3 h-3 rounded-full bg-gray-400 inline-block"></span>
            <span>Last Year</span>
          </div>
        </div>
      </div>

      { /* Income Over Time Line Chart */}
      <ResponsiveContainer width="100%" height="88%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis 
            dataKey="month" stroke="#6F7787FF"  
            />
          <YAxis 
            tickFormatter={(value) => `$${value / 1000}k`} 
            stroke="#6F7787FF" 
            domain={[
               isValid
                ? [0, 1000] // default fallback
                : [
                    (dataMin) => Math.floor(dataMin / 200) * 200 - 500,
                    (dataMax) => Math.ceil(dataMax * 1.1 / 500) * 500
                  ]
            ]}   
            />
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
    </>
  );
};


