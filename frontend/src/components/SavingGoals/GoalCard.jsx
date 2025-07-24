/**
 * GoalCard component for displaying savings goals.
 * Renders goal details with edit, delete, and contribution actions.
 */

import { useState } from 'react';
import { formatMoney } from '../../Utils/helpers';
import FormatRelativeDate from './FormatRelativeDate';
import ContributionModal from './ContributionModal';
import GoalModal from './GoalModal';
import Notification from '../../components/Notification';
import ActionsDropdown from '../../components/ActionsDropdown';
import DeletionPopup from '../../components/DeletionPopup';
import LoadingSpinner from '../../components/LoadingSpinner';
import { DEFAULT_IMAGE } from '../../constants/SavingGoalConstants';
import { useDeleteGoal, useContributeToGoal } from '../../api/SavingGoalsHooks';

export default function GoalCard({ data = [], completed = false }) {
  const [contributeModal, setContributeModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedGoal, setSelectedGoal] = useState(null);
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  const { mutateAsync: deleteGoal, isLoading: isDeleting } = useDeleteGoal();
  const { mutateAsync: contributeToGoal, isLoading: isContributing } = useContributeToGoal();

  // Handlers
  const getSelected = () => data.find((goal) => goal.id === selectedGoal);
  const showNotification = (type, message) => setNotification({ isOpen: true, type, message });
  const closeNotification = () => setNotification({ isOpen: false, type: '', message: '' });

  // Validate data
  const isDataValid = Array.isArray(data) && data.length > 0;
  if (!isDataValid) {
    return <div className="text-gray-500 font-body">No savings goals found.</div>;
  }

  // Layout
  return (
    <div className="flex flex-wrap gap-8">
      {/* Notification */}
      {notification.isOpen && (
        <Notification
          isOpen={notification.isOpen}
          type={notification.type}
          message={notification.message}
          onClose={closeNotification}
        />
      )}

      {/* Goals */}
      {data.map(({ id, title, contributed, target, imageRef, date }) => {
        // Calculate progress and normalize image URL
        const percentage = Math.round((contributed / target) * 100) || 0;
        const imageUrl = !imageRef || imageRef.includes('default.png')
          ? DEFAULT_IMAGE
          : imageRef.startsWith('http')
            ? imageRef
            : `${import.meta.env.VITE_API_URL}/${imageRef.replace(/^\/?/, '')}`;

        return (
          <div
            key={id}
            className="px-4 pt-3 pb-4 flex flex-col gap-1 rounded-lg lg:w-[19rem] md:w-[13rem] 3xl:w-[25rem] shadow-bb-general bg-white"
          >
            {/* Image and Actions */}
            <div className="relative w-full h-[18vh] rounded-lg mb-2">
              <img
                src={imageUrl}
                alt={title || 'Goal image'}
                className="w-full h-full object-cover rounded-lg border border-gray-200"
                onError={(e) => (e.target.src = DEFAULT_IMAGE)}
              />
              <div className="absolute top-0 right-0 z-10">
                <ActionsDropdown
                  onDelete={() => {
                    setSelectedGoal(id);
                    setShowDeleteModal(true);
                  }}
                  onEdit={() => {
                    setSelectedGoal(id);
                    setShowEditModal(true);
                  }}
                >
                  <div className="rounded-bl-lg rounded-tr-lg bg-white py-0.5 px-1 opacity-85">
                    <img src="/assets/edit-blue.png" alt="Edit icon" className="w-6 h-6" />
                  </div>
                </ActionsDropdown>
              </div>
              <div className="absolute bottom-0 right-0 z-10 bg-white py-0.5 px-2 font-body text-gray-800 text-sm opacity-85 rounded-tl-lg rounded-br-lg">
                <FormatRelativeDate date={date} />
              </div>
            </div>
            {/* Goal Info */}
            <h1 className="flex flex-row gap-3 font-header text-wrap">
              <span className="font-bold text-md text-black">{title || 'Untitled Goal'}</span>
              <span className="text-[#03C149]">Goal: {formatMoney(target || 0)}</span>
            </h1>
            <h2 className="flex flex-row items-center gap-3">
              <span className="text-2xl font-header font-bold text-black">
                {formatMoney(contributed || 0)}
              </span>
              <span className="text-lg text-gray-400">/ {formatMoney(target || 0)}</span>
            </h2>
            {/* Progress Bar */}
            <div className="flex flex-row items-center gap-2 mb-1.5">
              <div className="w-full h-2.5 bg-[#B4DDFF] rounded-full overflow-hidden relative">
                <div
                  className={`h-full ${completed ? 'bg-[#03D952]' : 'bg-[#008CFF]'} transition-all duration-300`}
                  style={{ width: `${percentage}%` }}
                />
              </div>
              <span className="font-body text-gray-500">{percentage}%</span>
            </div>
            {/* Action Button */}
            {completed ? (
              <div className="w-full py-1 px-2 rounded-md text-white font-body font-semibold text-center bg-[#03D952]">
                ✓ Completed
              </div>
            ) : (
              <button
                className="w-full py-1 border border-primary_blue rounded-md text-primary_blue hover:bg-primary_blue hover:text-white transition-colors"
                onClick={() => {
                  setSelectedGoal(id);
                  setContributeModal(true);
                }}
                disabled={isContributing}
              >
                {isContributing ? <LoadingSpinner size="small" /> : '+ Add Contribution'}
              </button>
            )}
          </div>
        );
      })}

      {/* Modals */}
      {contributeModal && (
        <ContributionModal
          isOpen={contributeModal}
          onClose={() => {
            setContributeModal(false);
            setSelectedGoal(null);
          }}
          data={getSelected() || {}}
          onSubmit={(amount, error) => {
            if (error) {
              showNotification('error', error);
              return;
            }
            if (!selectedGoal) {
              showNotification('error', 'No goal selected.');
              setContributeModal(false);
              return;
            }
            contributeToGoal(
              { id: selectedGoal, amount },
              {
                onSuccess: () => {
                  showNotification('success', 'Contribution added successfully!');
                  setContributeModal(false);
                  setSelectedGoal(null);
                },
                onError: (err) => {
                  showNotification('error', err.message || 'Failed to contribute.');
                },
              }
            );
          }}
        />
      )}
      {showDeleteModal && (
        <DeletionPopup
          isOpen={showDeleteModal}
          onConfirm={async () => {
            if (!selectedGoal) {
              showNotification('error', 'No goal selected.');
              setShowDeleteModal(false);
              return;
            }
            await deleteGoal(selectedGoal);
            setShowDeleteModal(false);
            setSelectedGoal(null);
            showNotification('success', 'Goal deleted successfully!');
          }}
          onClose={() => {
            setShowDeleteModal(false);
            setSelectedGoal(null);
          }}
          desc="Are you sure you want to delete this goal?"
          isLoading={isDeleting}
        />
      )}
      {showEditModal && (
        <GoalModal
          isOpen={showEditModal}
          onClose={() => {
            setShowEditModal(false);
            setSelectedGoal(null);
          }}
          data={getSelected() || {}}
        />
      )}
    </div>
  );
}