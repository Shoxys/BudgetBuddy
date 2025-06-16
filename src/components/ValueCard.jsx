const valueObjects = [
    {title: "Spending Account", amount: "$1,850", icon: "src/assets/spending-icon.png", editable: false},
    {title: "Savings Account", amount:"$2,000", icon: "src/assets/savings-icon.png", editable: true},
    {title: "Investments", amount: "$1,232", icon: "src/assets/investments-icon.png", editable: true},
    {title: "Goal Savings", amount: "$325", icon: "src/assets/saving-goals-icon.png", editable: false}
]

export default function ValueCard() {
    return(
            <>
            {valueObjects.map(({ title, amount, icon, editable }) => (
                <div className="px-4 pt-3 pb-4 flex flex-row justify-between gap-2 rounded-lg w-60 shadow-bb-general" key={title}>
                    <div>
                        <div className="flex flex-row">
                            <h2 className="font-body text-md text-gray-600 font-normal">{title}</h2>
                            {editable && 
                                <button>
                                  <img className="w-6" src="src/assets/edit-icon.png" alt="Edit icon" />      
                                </button>
                            }
                        </div>
                        <h1 className="text-2xl text-gray-700 font-header font-bold">{amount}</h1>
                    </div>
                        <div className="flex items-center">
                        <img className="w-13 h-12" src={icon} alt={`${title} icon`} />
                    </div>
                </div>
            ))}
            </>
        )
}