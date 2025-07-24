/**
 * ValueCard component for displaying account balances.
 * Renders cards for each account type with edit/navigation actions.
 */

import { useNavigate } from 'react-router-dom';
import { formatMoney } from '../../Utils/helpers';
import { AccountTypes } from '../../constants/AccountConstants';
import { ROUTES } from '../../constants/AppConstants';

export default function ValueCard({ accounts = [], setUpdateActioned }) {
  const navigate = useNavigate();

  // Define account templates
  const accountTemplates = [
    {
      type: AccountTypes.SPENDING,
      name: 'Spending Account',
      icon: '/assets/spending-icon.png',
      navIcon: '/assets/spending-plus.png',
      action: ROUTES.TRANSACTIONS,
      editable: false,
    },
    {
      type: AccountTypes.SAVINGS,
      name: 'Saving Account',
      icon: '/assets/savings-icon.png',
      navIcon: '/assets/savings-plus.png',
      action: ROUTES.SAVINGS,
      editable: true,
    },
    {
      type: AccountTypes.INVESTMENTS,
      name: 'Investment Account',
      icon: '/assets/investments-icon.png',
      navIcon: '/assets/investments-plus.png',
      action: ROUTES.INVESTMENTS,
      editable: true,
    },
    {
      type: AccountTypes.GOALSAVINGS,
      name: 'Goal Savings',
      icon: '/assets/saving-goals-icon.png',
      navIcon: '/assets/goal-plus.png',
      action: ROUTES.SAVING_GOALS,
      editable: false,
    },
  ];

  // Generate cards: include matching accounts and default templates
  const cardData = accountTemplates.reduce((cards, template) => {
    const matchingAccounts = accounts.filter((acc) => acc.type === template.type);

    // Add default card for template if no matching accounts or default name missing
    const hasDefaultName = matchingAccounts.some((acc) => acc.name === template.name);
    cards.push({
      type: template.type,
      name: template.name,
      balance: hasDefaultName ? matchingAccounts.find((acc) => acc.name === template.name)?.balance || 0 : 0,
      id: hasDefaultName ? matchingAccounts.find((acc) => acc.name === template.name)?.id : undefined,
      icon: hasDefaultName ? template.icon : template.navIcon,
      action: template.action,
      editable: template.editable,
      hasData: hasDefaultName,
    });

    // Add non-default named accounts
    matchingAccounts.forEach((account) => {
      if (account.name !== template.name) {
        cards.push({
          type: account.type,
          name: account.name,
          balance: account.balance,
          id: account.id,
          icon: template.icon,
          action: template.action,
          editable: template.editable,
          hasData: true,
        });
      }
    });

    return cards;
  }, []);

  // Layout
  return (
    <>
      {cardData.map((card, index) => (
        <div
          className="p-4 flex flex-row justify-between items-center gap-4 rounded-xl w-72 shadow-bb-general bg-white transition-all hover:shadow-lg hover:-translate-y-1"
          key={`${card.type}-${card.name}-${index}`}
        >
          {/* Account Info */}
          <div>
            <div className="flex flex-row items-center gap-2">
              <h2 className="text-md text-gray-600 font-normal font-body">{card.name}</h2>
              {card.editable && card.hasData && (
                <button
                  onClick={() => setUpdateActioned(card.type, card.name, card.id)}
                  className="text-gray-400 hover:text-blue-600 transition-colors"
                >
                  <img className="w-6 h-6" src="/assets/edit-icon.png" alt="Edit icon" />
                </button>
              )}
            </div>
            <h1 className="text-2xl text-gray-800 font-bold font-header">{formatMoney(card.balance)}</h1>
          </div>
          {/* Action Button */}
          <div className="flex items-center">
            <button
              className="w-12 h-12"
              onClick={() => (card.editable && !card.hasData ? setUpdateActioned(card.type, card.name, undefined) : navigate(card.action))}
            >
              <img src={card.icon} alt={`${card.name} icon`} className="w-12 h-12" />
            </button>
          </div>
        </div>
      ))}
    </>
  );
}