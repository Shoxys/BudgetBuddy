import { PieChart, Pie, Cell, Legend, ResponsiveContainer  } from "recharts";

const COLORS = ["#853fff", "#e92c81", "#ffbd51", "#64e69c"];
const EMPTY_COLOR = "#D3D3D3";

export default function NetworthPieChart({ data }) {
  const isValid = data && data.total && Array.isArray(data.breakdown) && data.breakdown.length > 0;

  // If data is valid then transform data for pie chart else use default data values
  const breakdown = isValid
    ? data.breakdown.map((d) => ({ ...d, value: d.value ?? 0 }))
    : [{ name: "No Data", value: 1 }]; 

  // Renders legend for piechart
  const renderColorfulLegendText = (value) => {
    // Ensures data is valid
    if (!isValid) {
      return <span className="text-gray-400 text-sm">No Data</span>;
    }

    const item = breakdown.find(d => d.name === value);
    if (!item) return value;

    const percentage = ((item.value / data.total) * 100).toFixed(0);
    return (
      <span className="font-body text-gray-800 text-sm 3xl:text-lg p-1">
        {value} {percentage}%
      </span>
    );
  };

  return (
    <div className="w-full h-[20vh]">
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Legend
            iconType="circle"
            layout="vertical"
            verticalAlign="middle"
            align="right"
            iconSize={10}
            formatter={renderColorfulLegendText}
          />
          <Pie
            data={breakdown}
            dataKey="value"
            cx="35%"
            cy="50%"
            innerRadius="55%"
            outerRadius="75%"
            paddingAngle={isValid ? 5 : 0}
          >
            {breakdown.map((entry, index) => (
              <Cell
                key={`cell-${index}`}
                fill={isValid ? COLORS[index % COLORS.length] : EMPTY_COLOR}
              />
            ))}
          </Pie>
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
