const savingGoals = [
  { name: "PS5", saved: 261, goal: 500 },
  { name: "Nike Airforces", saved: 35, goal: 140 },
  { name: "Wallet", saved: 12, goal: 50 }
];

export default function SavingGoalsCard() {
  return (
    <div className="bg-white row-span-1 row-start-2 col-span-1 rounded-lg shadow-bb-general px-3 py-2">
      <h2 className="font-header text-md text-gray-800 font-semibold">Saving Goals</h2>
       {savingGoals.map(({ name, saved, goal }, index) => {
        const percentage = Math.min((saved / goal) * 100, 100);
        return (
          <div key={index} className="flex flex-col relative">
            <div className="text-sm text-gray-700 font-medium">{name}</div>

            {/* Dynamic dollar label following progress */}
            <div className="relative h-5 mb-2">
              <div
                className="absolute text-sm font-semibold text-blue-700 transition-all duration-300"
                style={{ left: `calc(${percentage}% - 1.1rem)` }} // shift back to center the text
              >
                ${saved}
              </div>
            </div>

            {/* Progress bar */}
            <div className="w-full h-3 bg-blue-100 rounded-full overflow-hidden relative">
              <div
                className="h-full bg-primary_blue transition-all duration-300"
                style={{ width: `${percentage}%` }}
              />
            </div>

            {/* Min and Max values */}
            <div className="flex justify-between text-xs text-gray-500 font-medium mb-2">
              <span>$0</span>
              <span>${goal}</span>
            </div>
          </div>
        );
      })}
    </div>
  );
}