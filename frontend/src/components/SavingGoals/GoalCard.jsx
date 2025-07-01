import { formatMoney } from "../../Utils/helpers"
import FormatRelativeDate from "./formatRelativeDate";

const goals = [
    {title: "Emergency Fund", contributed: 125, goal: 2000, image: "src/assets/placeholder.png", dateEnd: "2025-05-21"},
    {title: "PS5", contributed: 230, goal: 500, image: "src/assets/ps5.png", dateEnd: "2025-06-30"},
    {title: "Nike Air Forces", contributed: 30, goal: 100, image: "src/assets/placeholder.png", dateEnd: "2025-02-30"},
    {title: "Wallet", contributed: 40, goal: 85, image: "src/assets/placeholder.png", dateEnd: "2025-12-30"},
    
]

export default function GoalCard({completed}) {
    return(
            <div className="flex flex-row gap-8">
            {goals.map(({ title, contributed, goal, image, dateEnd}, index) => {
                const percentage = Math.round(contributed / goal * 100);

                return (<div className={` px-4 pt-3 pb-4 flex flex-col gap-1 rounded-lg w-[80vh] shadow-bb-general bg-white`} key={index}>
                    {console.log(new Date(dateEnd) - new Date())}
                    {/* Goal Image */}
                    <div
                        className="relative border border-red w-full h-[18vh] bg-cover bg-center rounded-lg mb-2"
                        style={{ backgroundImage: `url('${image}')` }}
                    >  
                        <div className="rounded-tl-lg rounded-br-lg absolute right-0 bottom-0 bg-white py-0.5 px-2 font-body text-gray-800 text-sm opacity-85">
                            <FormatRelativeDate date={dateEnd}/>
                        </div>
                    </div>
                    {/* Title & goal */}
                    <h1 className="flex flex-row gap-3 font-header text-wrap">
                        <span className="font-bold text-md black">{title}</span>
                        <span className="text-[#03C149FF]">Goal: {formatMoney(goal)}</span>
                    </h1>  
                    <h2 className="flex flex-row items-center gap-3">
                        <span className="text-2xl font-header black font-bold">
                            {formatMoney(contributed)}
                        </span>
                        <span className="text-lg text-gray-400">
                            / {goal}
                        </span>
                    </h2>
                    {/* Progress bar */}
                    <div className="flex flex-row items-center gap-2 mb-1.5">
                        <div className="w-full h-2.5 bg-[#B4DDFFFF] rounded-full overflow-hidden relative">
                            <div
                                className={`h-full ${completed ? "bg-[#03D952FF]" : "bg-[#008CFFFF]"} transition-all duration-300`}
                                style={{ width: `${percentage}%` }}
                            />
                        </div>
                        <span className="font-body text-gray-500">{percentage}%</span>
                    </div>
                    {completed
                    ? <div className="w-full py-1 px-2 rounded-md text-white font-body font-semibold text-center bg-[#03D952FF]">
                        ✓ Completed
                    </div>
                    : <button className="w-full py-1 border border-primary_blue rounded-md text-primary_blue hover:bg-primary_blue hover:opacity-95 hover:text-white outline-none">
                        + Add Contribution
                    </button>
                    }
                </div>);
        })}
            </div>
        )
}