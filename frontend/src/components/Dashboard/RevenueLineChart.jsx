import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';

const data = [
  { month: 'Jan', thisYear: 3200, lastYear: 3000 },
  { month: 'Feb', thisYear: 3400, lastYear: 3100 },
  { month: 'Mar', thisYear: 3600, lastYear: 3300 },
  { month: 'Apr', thisYear: 3500, lastYear: 3400 },
  { month: 'May', thisYear: 3700, lastYear: 3600 },
  { month: 'Jun', thisYear: 3900, lastYear: 3800 },
  { month: 'Jul', thisYear: 4000, lastYear: 4000 },
  { month: 'Aug', thisYear: 4200, lastYear: 4100 },
  { month: 'Sep', thisYear: 4300, lastYear: 4200 },
  { month: 'Oct', thisYear: 4500, lastYear: 4300 },
  { month: 'Nov', thisYear: 4700, lastYear: 4400 },
  { month: 'Dec', thisYear: 4800, lastYear: 4600 },
];

// Custom Tooltip
const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200 text-sm">
        <p className="font-semibold text-gray-800">{label}</p>
        <p className="text-blue-600">This Year: ${payload[0].value.toLocaleString()}</p>
        <p className="text-gray-500">Last Year: ${payload[1].value.toLocaleString()}</p>
      </div>
    );
  }

  return null;
};

const RevenueChart = () => {
  return (
    <>
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-lg font-semibold text-gray-700">Revenue</h2>
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

      <ResponsiveContainer width="100%" height={220}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} />
          <XAxis 
            dataKey="month" stroke="#6F7787FF"  
            />
          <YAxis 
            tickFormatter={(value) => `$${value / 1000}k`} 
            stroke="#6F7787FF" 
            domain={[
               (dataMin) => (Math.floor(dataMin / 200) * 200) - 500,
               (dataMax) => Math.ceil(dataMax * 1.1 / 500) * 500 
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

export default RevenueChart;
