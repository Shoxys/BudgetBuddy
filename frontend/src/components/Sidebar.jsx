import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import Notification from './Notification';
import LoadingSpinner from './LoadingSpinner';
import LogoutPopup from './LogoutPopup';
import { logoutUser } from '../api/AuthApi';
import { ROUTES } from '../constants/AppConstants';

const navItems = [
    { icon: '/assets/dashboard-icon.png', iconSelected: '/assets/dashboard-icon-selected.png', label: 'Dashboard', link: ROUTES.DASHBOARD, },
    { icon: '/assets/transaction-icon.png', iconSelected: '/assets/transaction-icon-selected.png', label: 'Transactions', link: ROUTES.TRANSACTIONS, },
    { icon: '/assets/goal-icon.png', iconSelected: '/assets/goal-icon-selected.png', label: 'Saving Goals', link: ROUTES.SAVING_GOALS, },
    { icon: '/assets/settings-icon.png', iconSelected: '/assets/settings-icon-selected.png', label: 'Settings', link: ROUTES.SETTINGS, },
];

export default function Sidebar() {
    const location = useLocation();
    const navigate = useNavigate();
    const [isSidebarExpanded, setIsSidebarExpanded] = useState(false);
    const [isLogoutOpen, setIsLogoutOpen] = useState(false);
    const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

    const logoutMutation = useMutation({
        mutationFn: logoutUser,
        onSuccess: () => {
            setNotification({ isOpen: true, type: 'success', message: 'Successfully logged out!', });
            navigate(ROUTES.HOME);
            setIsLogoutOpen(false);
        },
        onError: (error) => {
            setNotification({
                isOpen: true,
                type: 'error',
                message: error.response?.data?.message || 'Logout failed. Please try again.',
            });
            setIsLogoutOpen(false);
        },
    });

    return (
        <>
            <nav
                className={`fixed top-0 left-0 bottom-0 z-10 flex flex-col py-1 gap-2 bg-white shadow-bb-general transition-[width] duration-200 ${
                    isSidebarExpanded ? 'w-80 px-5 opacity-92' : 'w-20 px-2 items-center' 
                }`}
            >
                <div className="flex flex-row relative h-14 items-center">
                    <img
                        className={isSidebarExpanded ? 'h-14' : 'w-12'} 
                        src={isSidebarExpanded ? '/assets/bb-logo-text.png' : '/assets/bb_logo.png'}
                        alt="Budget Buddy logo"
                    />
                    <button
                        onClick={() => setIsSidebarExpanded((prev) => !prev)}
                        className="absolute top-1/2 right-[-1rem] translate-x-1/2 -translate-y-1/2 w-5 h-10 bg-white rounded-md border border-gray-400 shadow-bb-general hover:bg-slate-200 flex items-center justify-center transition-colors"
                        aria-label={isSidebarExpanded ? 'Collapse sidebar' : 'Expand sidebar'}
                    >
                        <img
                            className={isSidebarExpanded ? '' : 'scale-x-[-1]'}
                            src="/assets/arrow.png"
                            alt="Toggle sidebar"
                            width="12"
                        />
                    </button>
                </div>
                <hr className="h-0.5 w-full rounded-sm bg-gray-400 mt-3 mb-2" />

                {navItems.map(({ icon, iconSelected, label, link }, index) => {
                    const isSelected = location.pathname.startsWith(link);
                    return (
                        <Link
                            key={index}
                            to={link}
                            className={`rounded-md h-16 flex flex-row items-center ${
                                isSelected ? 'bg-primary_blue hover:bg-btn_hover' : 'hover:bg-slate-200 transition-colors'
                            } ${isSidebarExpanded ? 'w-full' : 'w-16 justify-center'}`}
                        >
                            <img src={isSelected ? iconSelected : icon} alt={`${label} icon`} className="w-12 h-12" />
                            {isSidebarExpanded && (
                                <h1 className={`font-body text-md ml-2 ${isSelected ? 'text-white' : 'text-gray-700'}`}>
                                    {label}
                                </h1>
                            )}
                        </Link>
                    );
                })}
                <hr className="h-0.5 w-full rounded-sm bg-gray-400 mt-3 mb-2" />
                <button
                    onClick={() => setIsLogoutOpen(true)}
                    className={`rounded-md h-16 flex flex-row items-center bg-transparent hover:bg-slate-200 w-full transition-colors ${!isSidebarExpanded && 'justify-center'}`}
                    disabled={logoutMutation.isPending}
                >
                    <img src="/assets/logout.png" alt="Logout icon" className="w-12 h-12" />
                    {isSidebarExpanded && (
                        <h1 className="font-body text-xl text-gray-700 font-bold ml-2">Logout</h1>
                    )}
                    {logoutMutation.isPending && <LoadingSpinner size="small" />}
                </button>
            </nav>

            <LogoutPopup
                isOpen={isLogoutOpen}
                onClose={() => setIsLogoutOpen(false)}
                onConfirm={() => logoutMutation.mutate()}
                desc="Are you sure you want to log out of your account?"
            />
            {notification.isOpen && (
                <Notification
                    type={notification.type}
                    message={notification.message}
                    isOpen={notification.isOpen}
                    onClose={() => setNotification({ isOpen: false, type: '', message: '' })}
                    duration={3000}
                />
            )}
        </>
    );
}