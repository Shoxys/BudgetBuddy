import { useState } from "react";
import { Link } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

const navItems = [
        { icon: "src/assets/dashboard-icon.png", iconSelected: "src/assets/dashboard-icon-selected.png", label: "Dashboard", link: "/dashboard" },
        { icon: "src/assets/transaction-icon.png", iconSelected: "src/assets/transaction-icon-selected.png", label: "Transactions", link: "/transactions" },
        { icon: "src/assets/goal-icon.png", iconSelected: "src/assets/goal-icon-selected.png", label: "Saving Goals", link: "/saving-goals" },
        { icon: "src/assets/settings-icon.png", iconSelected: "src/assets/settings-icon-selected.png", label: "Settings", link: "/settings" },
    ];

export default function Sidebar() {
    const location = useLocation();
    const [expanded, setExpanded] = useState();
    
    const setNavState = () => {
        setExpanded(prev => !prev);
    }
    return (    
     <nav className={`fixed top-0 left-0 bottom-0 z-10 flex flex-col px-3 py-1 gap-2 bg-white shadow-bb-general transition-[width] duration-200 
                    ${expanded ? 'w-80 px-5 opacity-90' : 'w-16 px-2 items-center'}`}
                    >
        {/* Budget Buddy logo */}
        <div className="flex flex-row">
            {expanded ? <img className="h-14" src="src/assets/bb-logo-text.png" alt="Budget buddy logo with text"/> :
                        <img className="w-10" src="src/assets/bb_logo.png" alt="Budget buddy logo"/>
            }
            {/* Expand/Collapse toggle button */}
            <button
                onClick={setNavState}
                className={`absolute top-2 right-[-14px] w-5 h-10 opacity-100 bg-white rounded-md border border-gray-400 
                            shadow-bb-general hover:bg-slate-200 outline-none flex items-center justify-center hover-effect`}
                >
                <img
                    className={`${expanded ? '' : 'scale-x-[-1]'}`}
                    src="src/assets/arrow.png"
                    alt="Toggle sidebar"
                    width="12"
                />
            </button>
        </div>
        {/* Line Break */}            
        <hr className="h-0.5 w-full rounded-sm bg-gray-400 mt-3 mb-2"/>

        {/* Dynamic nav item generation */}            
        {navItems.map(({icon, iconSelected, label, link}, index) => {
                const currentSelected = location.pathname.startsWith(link)
                return (
                    <Link key={index} 
                        to={link} 
                        className={`rounded-md flex flex-row items-center hover-effect 
                                    ${currentSelected ? 'bg-primary_blue hover:bg-btn_hover' : 'bg-white hover:bg-slate-200'}
                                    ${expanded ? 'w-full' : 'w-14'}`}>
                    <img src={currentSelected ? iconSelected : icon} alt={`${label} icon`}/>
                    {expanded && (
                        <h1 className={`font-body text-md ${currentSelected && 'text-white'}`}>
                            {label}
                        </h1>
                    )}
                    </Link>    
                )     
        })}
        {/* Line Break */}
        <hr className="h-0.5 rounded-sm w-full bg-gray-400 mt-3 mb-2"/>

        {/* Logout Button */}
        <Link  to="/" className="hover:bg-slate-200 rounded-md flex flex-row items-center w-full hover-effect">
            <img src="src/assets/logout.png" alt="logout icon" />
            {expanded && 
                <h1 className={`font-body text-2xl text-gray-700 font-bold`}>
                    Logout
                </h1>
            }
        </Link>
    </nav>
    )
}