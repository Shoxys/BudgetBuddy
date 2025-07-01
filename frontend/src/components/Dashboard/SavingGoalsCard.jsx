import { Link } from "react-router-dom";
export default function SavingGoalsCard({ data }) {
  const isValid = Array.isArray(data) && data.length > 0;

  return (
    <div className="w-full min-w-[23vw] bg-white row-span-1 row-start-2 col-span-1 rounded-lg shadow-bb-general px-3 py-2">
      <h2 className="font-header text-md 3xl:text-xl text-gray-800 font-semibold">Saving Goals</h2>
      {isValid
        /* Saving goals data display */
        ? (data.map(({ title, contributed, target }, index) => {
        const percentage = Math.min((contributed / target) * 100, 100);
        return (
          <div key={index} className="flex flex-col relative">
            <div className="text-sm 3xl:text-lg text-gray-700 font-medium">{title}</div>

            {/* Dynamic dollar label following progress */}
            <div className="relative h-5 mb-2">
              <div
                className="absolute text-sm 3xl:text-lg font-semibold text-blue-700 transition-all duration-300"
                style={{ left: `calc(${percentage}% - 1.1rem)` }} // shift back to center the text
              >
                ${contributed}
              </div>
            </div>

            {/* Progress bar */}
            <div className="w-full h-[1.8vh] bg-blue-100 rounded-full overflow-hidden relative">
              <div
                className="h-full bg-primary_blue transition-all duration-300"
                style={{ width: `${percentage}%` }}
              />
            </div>

            {/* Min and Max values */}
            <div className="flex justify-between text-xs 3xl:text-lg text-gray-500 font-medium mb-[1.2vh]">
              <span>$0</span>
              <span>${target}</span>
            </div>
          </div>
        );
      }))
      /* Default message if invalid data */
      : <div className="flex flex-row gap-2 items-center justify-center mt-3 text-xl font-body px-3 text-gray-700 bg-neutral-100 rounded-md text-center">
          <span>Set Saving Goals!</span>
          <Link to="/saving-goals">
            <img src="src/assets/goal-plus.png" alt="Set goals button icon" />
          </Link>
        </div>
    }
       
    </div>
  );
}