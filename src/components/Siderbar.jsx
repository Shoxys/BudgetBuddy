import { useState } from "react";
const navItems = [
        { icon: "src/assets/dashboard-icon.png", iconSelected: "src/assets/dashboard-icon-selected.png", label: "Dashboard", href: "#" },
        { icon: "src/assets/transaction-icon.png", iconSelected: "src/assets/transaction-icon-selected.png", label: "Transactions", href: "#" },
        { icon: "src/assets/goal-icon.png", iconSelected: "src/assets/goal-icon-selected.png", label: "Saving Goals", href: "#" },
        { icon: "src/assets/settings-icon.png", iconSelected: "src/assets/settings-icon-selected.png", label: "Settings", href: "#" },
    ];

export default function NavBar() {
    const [expanded, setExpanded] = useState(false);
    const [selectedNav, setSelectedNav] = useState("Dashboard");

    const setNavState = () => {
        setExpanded(prev => !prev);
        console.log(expanded);
    }
    return (
     <nav className={`flex flex-col px-3 py-1 gap-2 bg-white min-h-full shadow-sm ${expanded ? 'w-80' : 'items-center' }`}>
        <div className="flex flex-row">
            {expanded ? <img className="w-28" src="src/assets/bb-logo-text.png" alt="Budget buddy logo with text"/> :
                        <img className="w-10" src="src/assets/bb_logo.png" alt="Budget buddy logo"/>
            }
            <button onClick={setNavState} className={`bg-white px-0.5 py-1 rounded-md border border-gray-400 shadow-sm absolute mt-2 ml-12 ${expanded && 'ml-60 3xl:ml-64'} hover:bg-slate-200 outline-none`}>
                <img className={`${expanded || 'scale-x-[-1]'}`} src="src/assets/arrow.png" alt="arrow head icon" width="17" />
            </button>
        </div>
        <hr className="h-0.5 w-full rounded-sm bg-gray-400 mt-3 mb-2"/>

        {navItems.map(({icon, iconSelected, label, href}) => (
               <a href={href} className={`${selectedNav === label ? 'bg-primary_blue hover:bg-btn_hover' : 'bg-white hover:bg-slate-200'} rounded-md flex flex-row items-center`}>
                <img src={selectedNav === label ? iconSelected : icon} alt={`${label} icon`} />
                {expanded && (
                    <h1 className={`font-body text-md ${selectedNav === label && 'text-white'}`}>
                        {label}
                    </h1>
                )}
            </a>         
        ))}
        <hr className="h-0.5 rounded-sm w-full bg-gray-400 mt-3 mb-2"/>
        <a href="#" className="hover:bg-slate-200 rounded-md flex flex-row items-center">
            <img src="src/assets/logout.png" alt="logout icon" />
            {expanded && 
                <h1 className={`font-body text-xl text-gray-700 font-bold`}>
                    Logout
                </h1>
            }
        </a>
    </nav>
    )
}