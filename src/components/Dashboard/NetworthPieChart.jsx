import { PieChart, Pie, Cell, Legend, ResponsiveContainer  } from "recharts";

const data = [
  { name: "Spending", value: 400 },
  { name: "Savings", value: 300 },
  { name: "Investment", value: 300 },
  { name: "Goal Savings", value: 200 }
];
const COLORS = ["#853fff", "#e92c81", "#ffbd51", "#64e69c"];


const total = data.reduce((acc, item) => acc + item.value, 0);

const renderColorfulLegendText = (value) => {
  const item = data.find(d => d.name === value);
  const percentage = ((item.value / total) * 100).toFixed(0);
  return (
    <span className="font-body text-gray-800 text-sm p-1">
      {value} {percentage}%
    </span>
  );
};

export default function NetworthPieChart() {
  return (
    <PieChart width={300} height={150}>
        <Legend
            height={36}
            iconType="circle"
            layout="horizonal"
            verticalAlign="top"
            align="right"
            iconSize={10}
            padding={5}
            wrapperStyle={{ marginTop: 20}}
            formatter={renderColorfulLegendText}
        />
      <Pie
        data={data}
        cx={58}
        cy={70}
        innerRadius={44}
        outerRadius={63}
        fill="#8884d8"
        paddingAngle={5}
        dataKey="value"
      >
        {data.map((entry, index) => (
          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
        ))}
      </Pie>
    </PieChart>
  );
}
