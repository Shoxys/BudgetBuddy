import { formatMoney } from "../../Utils/helpers"
import { useNavigate } from "react-router-dom";

export default function ValueCard({ accounts, setUpdateActioned }) {
    const valueObjects = [
    {name: "Spending Account", icon: "src/assets/spending-icon.png", navIcon: "src/assets/spending-plus.png", action: "/transactions", editable: false},
    {name: "Savings Account", icon: "src/assets/savings-icon.png", navIcon: "src/assets/savings-plus.png", action: "Savings" ,editable: true},
    {name: "Investments", icon: "src/assets/investments-icon.png", navIcon: "src/assets/investments-plus.png", action: "Investments", editable: true},
    {name: "Goal Savings", icon: "src/assets/saving-goals-icon.png", navIcon: "src/assets/goal-plus.png", action: "/saving-goals", editable: false}
    ]

    const isValid = Array.isArray(accounts) && accounts.length > 0;
    const navigate = useNavigate();

    return (
        <>
        {isValid 
          /* Accounts cards with data */
         ? (accounts.map(({ name, balance }) => {
            const account = valueObjects.find(value => value.name === name);
            if (!account) return null; // Skip unmatched accounts

            return (
                <div className="px-4 pt-3 pb-2 flex flex-row justify-between gap-2 rounded-lg w-72 shadow-bb-general" key={name}>
                    <div>
                        <div className="flex flex-row">
                            <h2 className="font-body text-md text-gray-600 font-normal">{name}</h2>
                            {account.editable && (
                            <button onClick={() => setUpdateActioned(name)}>
                                <img className="w-6" src="src/assets/edit-icon.png" alt="Edit icon" />
                            </button>
                            )}
                        </div>
                        <h1 className="text-2xl text-gray-700 font-header font-bold">
                            {formatMoney(balance)}
                        </h1>
                    </div>
                    <div className="flex items-center">
                        <img className="w-13 h-12" src={account.icon} alt={`${name} icon`} />
                    </div>
                </div>
            )}))
            /* Default Accounts cards empty data */
            : valueObjects.map (({name, action, navIcon, editable }) => (
                <div className="px-4 pt-3 pb-2 flex flex-row justify-between gap-2 rounded-lg w-72 shadow-bb-general" key={name}>
                    <div>
                        <div className="flex flex-row">
                            <h2 className="font-body text-md text-gray-600 font-normal">{name}</h2>
                        </div>
                        <h1 className="text-2xl text-gray-700 font-header font-bold">
                            $0
                        </h1>
                    </div>
                    <div className="flex items-center">
                        <button 
                            onClick= {editable
                                ? () => setUpdateActioned(name)
                                : () => navigate(action)
                            }>
                            <img className="w-13 h-12" src={navIcon} alt={`${name} icon`} />
                        </button>
                    </div>
                </div>
            ))
        }
        </>
        )
}