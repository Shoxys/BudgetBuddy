const goalStats = [
    {title: "Completed Goals", subtitle: "Increased by 6 this week", amount: 68, icon: "src/assets/completed-icon.png", color: "bg-[#E8FFF1FF]"},
    {title: "Goals in Progress", subtitle:"Decreased by 5 this week", amount: 17, icon: "src/assets/inprogress-icon.png", color: "bg-[#FFF0CDFF]"},
    {title: "Overdue Goals", subtitle: "Increased by 3 this week", amount: 9, icon: "src/assets/overdue-icon.png", color: "bg-[#FBD8E8FF]"},
    {title: "Total Goals", subtitle: "Completion rate: 80%", amount: 85, icon: "src/assets/total-icon.png", color: "bg-[#F0F8FFFF]"},
]

export default function ValueCard() {
    return(
            <div className="flex flex-row gap-5">
            {goalStats.map(({ title, subtitle, amount, icon, color }) => (
                <div className={`px-6 pt-2 pb-3.5 flex flex-row ${color} gap-3 rounded-lg w-[72] shadow-bb-general`} key={title}>
                    <img className="w-13 h-12" src={icon} alt={`${title} icon`} />
                    <div className="flex flex-col gap-1">
                        <div className="flex flex-row items-center gap-4">
                            <span className="font-body text-[0.9rem] black font-semibold">{title}</span>
                            <span className="text-xl black font-header font-bold">{amount}</span>
                        </div>
                        <span className="font-body text-[0.785rem] black font-normal">{subtitle}</span>
                    </div>
                </div>
            ))}
            </div>
        )
}