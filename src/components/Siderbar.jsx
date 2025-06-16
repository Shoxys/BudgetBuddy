import { useState } from "react";
const navItems = [
        { icon: "src/assets/dashboard-icon.png", iconSelected: "src/assets/dashboard-icon-selected.png", label: "Dashboard", href: "#" },
        { icon: "src/assets/transaction-icon.png", iconSelected: "src/assets/transaction-icon-selected.png", label: "Transactions", href: "#" },
        { icon: "src/assets/goal-icon.png", iconSelected: "src/assets/goal-icon-selected.png", label: "Saving Goals", href: "#" },
        { icon: "src/assets/settings-icon.png", iconSelected: "src/assets/settings-icon-selected.png", label: "Settings", href: "#" },
    ];

export default function Sidebar({selectedNav}) {
    const [expanded, setExpanded] = useState(false);
    const setNavState = () => {
        setExpanded(prev => !prev);
        console.log(expanded);
    }
    return (
     <nav className={`fixed top-0 left-0 bottom-0 z-50 flex flex-col px-3 py-1 gap-2 bg-white shadow-sm transition-[width] duration-200 ${expanded ? 'w-80 px-5' : 'w-16 px-2 items-center'}`}>
        <div className="flex flex-row">
            {expanded ? <img className="h-14" src="src/assets/bb-logo-text.png" alt="Budget buddy logo with text"/> :
                        <img className="w-10" src="src/assets/bb_logo.png" alt="Budget buddy logo"/>
            }
            <button
                onClick={setNavState}
                className={`absolute top-2 right-[-14px] w-6 h-10 opacity-100 bg-white rounded-md border border-gray-400 shadow-sm hover:bg-slate-200 outline-none flex items-center justify-center`}
                >
                <img
                    className={`${expanded ? '' : 'scale-x-[-1]'}`}
                    src="src/assets/arrow.png"
                    alt="Toggle sidebar"
                />
            </button>
        </div>
        <hr className="h-0.5 w-full rounded-sm bg-gray-400 mt-3 mb-2"/>

        {navItems.map(({icon, iconSelected, label, href}) => (
               <a href={href} className={`${selectedNav === label ? 'bg-primary_blue hover:bg-btn_hover' : 'bg-white hover:bg-slate-200'} 
                                        rounded-md flex flex-row items-center 
                                        ${expanded ? 'w-full' : 'w-14'}`}>
                <img src={selectedNav == label ? iconSelected : icon} alt={`${label} icon`}/>
                {expanded && (
                    <h1 className={`font-body text-md ${selectedNav === label && 'text-white'}`}>
                        {label}
                    </h1>
                )}
            </a>         
        ))}
        <hr className="h-0.5 rounded-sm w-full bg-gray-400 mt-3 mb-2"/>
        <a href="#" className="hover:bg-slate-200 rounded-md flex flex-row items-center w-14">
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