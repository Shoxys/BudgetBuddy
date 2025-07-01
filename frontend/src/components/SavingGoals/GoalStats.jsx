// constants/static metadata
const goalMeta = {
  "Completed Goals": { icon: "src/assets/completed-icon.png", color: "bg-[#E8FFF1FF]" },
  "Goals in Progress": { icon: "src/assets/inprogress-icon.png", color: "bg-[#FFF0CDFF]" },
  "Overdue Goals": { icon: "src/assets/overdue-icon.png", color: "bg-[#FBD8E8FF]" },
  "Total Goals": { icon: "src/assets/total-icon.png", color: "bg-[#F0F8FFFF]" },
};

// Mock dynamic stats only
const goalData = [
  { title: "Completed Goals", subtitle: "Increased by 7 this week", amount: 68 },
  { title: "Goals in Progress", subtitle: "Decreased by 5 this week", amount: 17 },
  { title: "Overdue Goals", subtitle: "Increased by 3 this week", amount: 9 },
  { title: "Total Goals", subtitle: "Completion rate: 80%", amount: 85 },
];
export default function ValueCard() {
    return(
            <div className="flex flex-row gap-5">
            {goalData.map(({ title, subtitle, amount }) => {
                const { icon, color } = goalMeta[title];

                return (
                    <div key={title} className={`px-6 pt-2 pb-3.5 flex flex-row ${color} gap-3 rounded-lg w-[19vw] shadow-bb-general`}>
                    <img className="w-[2.8vw] h-[2.8vw]" src={icon} alt={`${title} icon`} />
                    <div className="flex flex-col gap-1">
                        <div className="flex flex-row items-center gap-4">
                        <span className="font-body text-[1vw] font-semibold">{title}</span>
                        <span className="text-[1.3vw] font-header font-bold">{amount}</span>
                        </div>
                        <span className="font-body text-[0.8vw] font-normal">{subtitle}</span>
                    </div>
                    </div>
                );
                })}
            </div>
        )
}