const GoalTypes = {
  COMPLETED: 'COMPLETED',
  INPROGRESS: 'INPROGRESS',
  OVERDUE: 'OVERDUE',
  TOTAL: 'TOTAL'
}

// constants/static metadata
const goalMeta = {
  'COMPLETED': { title: "Completed Goals", icon: "src/assets/completed-icon.png", color: "bg-[#E8FFF1FF]" },
  'INPROGRESS': { title: "Goals in Progress", icon: "src/assets/inprogress-icon.png", color: "bg-[#FFF0CDFF]" },
  'OVERDUE': { title: "Overdue Goals", icon: "src/assets/overdue-icon.png", color: "bg-[#FBD8E8FF]" },
  'TOTAL': { title: "Total Goals", icon: "src/assets/total-icon.png", color: "bg-[#F0F8FFFF]" },
};

// Mock dynamic stats only
const goalData = [
  { insight: "Increased by 7 this week", type: GoalTypes.COMPLETED, amount: 68 },
  { insight: "Decreased by 5 this week", type: GoalTypes.INPROGRESS, amount: 17 },
  { insight: "Increased by 3 this week", type: GoalTypes.OVERDUE, amount: 9 },
  { insight: "Completion rate: 80%", type: GoalTypes.TOTAL, amount: 85 },
];
export default function ValueCard() {
    return(
            <div className="flex flex-row gap-5">
            {goalData.map(({ insight, type, amount }) => {
                const { title, icon, color } = goalMeta[type];

                return (
                    <div key={title} className={`px-6 pt-2 pb-3.5 flex flex-row ${color} gap-3 rounded-lg w-[19vw] shadow-bb-general`}>
                    <img className="w-[2.8vw] h-[2.8vw]" src={icon} alt={`${title} icon`} />
                    <div className="flex flex-col gap-1">
                        <div className="flex flex-row items-center gap-4">
                        <span className="font-body text-[1vw] font-semibold">{title}</span>
                        <span className="text-[1.3vw] font-header font-bold">{amount}</span>
                        </div>
                        <span className="font-body text-[0.8vw] font-normal">{insight}</span>
                    </div>
                    </div>
                );
                })}
            </div>
        )
}