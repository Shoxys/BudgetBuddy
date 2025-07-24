/**
 * AccountSettings component for managing user account settings.
 * Handles email and password updates, and account deletion.
 */

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import DeletionPopup from '../../components/DeletionPopup';
import PasswordToggle from '../../components/PasswordToggle';
import { ROUTES } from '../../constants/AppConstants';
import { updateEmail, updatePassword, deleteAccount, fetchCurrentEmail } from '../../api/SettingsApi';

const initialData = {
  currentEmail: '',
  newEmail: '',
  confirmPass: '',
  currentPass: '',
  newPass: '',
  newConfirmPass: '',
};

export default function AccountSettings() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState(initialData);
  const [showPassword, setShowPassword] = useState({
    confirmPass: false,
    currentPass: false,
    newPass: false,
    newConfirmPass: false,
  });
  const [deletePopup, setDeletePopup] = useState(false);
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  const { data: currentEmail, isLoading: isFetchingEmail } = useQuery({
    queryKey: ['currentEmail'],
    queryFn: fetchCurrentEmail,
    staleTime: 5 * 60 * 1000,
  });

  // Mutations
  const updateEmailMutation = useMutation({
    mutationFn: updateEmail,
    onSuccess: (data) => {
      setNotification({
        isOpen: true,
        type: 'success',
        message: data.message || 'Email updated successfully',
      });
      queryClient.refetchQueries(['currentEmail']);
      setFormData((prev) => ({ ...prev, newEmail: '', confirmPass: '' }));
    },
    onError: (error) => {
      setNotification({
        isOpen: true,
        type: 'error',
        message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      });
    },
  });

  const updatePasswordMutation = useMutation({
    mutationFn: updatePassword,
    onSuccess: (data) => {
      setNotification({
        isOpen: true,
        type: 'success',
        message: data || 'Password updated successfully',
      });
      setFormData((prev) => ({ ...prev, currentPass: '', newPass: '', newConfirmPass: '' }));
    },
    onError: (error) => {
      setNotification({
        isOpen: true,
        type: 'error',
        message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      });
    },
  });

  const deleteAccountMutation = useMutation({
    mutationFn: deleteAccount,
    onSuccess: () => {
      setNotification({
        isOpen: true,
        type: 'success',
        message: 'Account deleted successfully',
      });
      queryClient.clear();
      setDeletePopup(false);
      setTimeout(() => navigate(ROUTES.HOME), 1000);
    },
    onError: (error) => {
      setNotification({
        isOpen: true,
        type: 'error',
        message: error.response?.data?.message || error.message || 'An unexpected error occurred',
      });
    },
  });

  // Handlers
  const handleInput = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleEmailSubmit = (e) => {
    e.preventDefault();
    if (!formData.newEmail || !formData.confirmPass) {
      setNotification({ isOpen: true, type: 'error', message: 'Please fill in all fields' });
      return;
    }
    updateEmailMutation.mutate({
      currentEmail: formData.currentEmail,
      newEmail: formData.newEmail,
      confirmPassword: formData.confirmPass,
    });
  };

  const handlePasswordSubmit = (e) => {
    e.preventDefault();
    if (!formData.currentPass || !formData.newPass || !formData.newConfirmPass) {
      setNotification({ isOpen: true, type: 'error', message: 'Please fill in all fields' });
      return;
    }
    if (formData.newPass !== formData.newConfirmPass) {
      setNotification({ isOpen: true, type: 'error', message: 'Passwords do not match' });
      return;
    }
    updatePasswordMutation.mutate({
      currentPassword: formData.currentPass,
      newPassword: formData.newPass,
      confirmPassword: formData.newConfirmPass,
    });
  };

  // Layout
  return (
    <div className="space-y-6 font-body text-sm text-gray-800 p-6">
      {/* Notification */}
      {notification.isOpen && (
        <Notification
          isOpen={notification.isOpen}
          type={notification.type}
          message={notification.message}
          onClose={() => setNotification({ isOpen: false, type: '', message: '' })}
        />
      )}
      {/* Loading */}
      {isFetchingEmail && (
        <div className="flex justify-center py-4">
          <LoadingSpinner />
        </div>
      )}
      {/* Change Email */}
      {!isFetchingEmail && (
        <div className="flex flex-row items-start gap-x-6">
          <h2 className="text-lg font-header font-semibold min-w-[10rem] pt-2">Change Email</h2>
          <div className="bg-white p-6 rounded-lg border space-y-4 max-w-[35rem] w-full shadow-bb-general">
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">Current Email</label>
              <input
                name="currentEmail"
                type="email"
                value={currentEmail || formData.currentEmail}
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                disabled
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 font-header">New Email</label>
              <input
                name="newEmail"
                type="email"
                value={formData.newEmail}
                onChange={handleInput}
                placeholder="Enter new email"
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
              />
            </div>
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 font-header">Confirm Password</label>
              <input
                name="confirmPass"
                type={showPassword.confirmPass ? 'text' : 'password'}
                value={formData.confirmPass}
                onChange={handleInput}
                placeholder="Confirm password"
                className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pr-10"
              />
              <PasswordToggle
                name="confirmPass"
                showPassword={showPassword}
                setShowPassword={setShowPassword}
              />
            </div>
            <button
              className="btn-primary mt-2 transition-colors"
              onClick={handleEmailSubmit}
              disabled={updateEmailMutation.isPending}
            >
              {updateEmailMutation.isPending ? "Changing Email..." : 'Change Email'}
            </button>
          </div>
        </div>
      )}
      <hr className="mr-10" />
      {/* Change Password */}
      <div className="flex flex-row items-start gap-x-6">
        <h2 className="text-lg font-header font-semibold min-w-[10rem] pt-2">Change Password</h2>
        <div className="bg-white p-6 rounded-lg border space-y-4 max-w-[35rem] w-full shadow-bb-general">
          <div className="relative flex flex-col gap-y-1">
            <label className="block text-sm font-medium text-gray-700 font-header">Current Password</label>
            <input
              name="currentPass"
              type={showPassword.currentPass ? 'text' : 'password'}
              value={formData.currentPass}
              onChange={handleInput}
              placeholder="Enter current password"
              className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pr-10"
            />
            <PasswordToggle
              name="currentPass"
              showPassword={showPassword}
              setShowPassword={setShowPassword}
            />
          </div>
          <div className="relative flex flex-col gap-y-1">
            <label className="block text-sm font-medium text-gray-700 font-header">New Password</label>
            <input
              name="newPass"
              type={showPassword.newPass ? 'text' : 'password'}
              value={formData.newPass}
              onChange={handleInput}
              placeholder="Enter new password"
              className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pr-10"
            />
            <PasswordToggle
              name="newPass"
              showPassword={showPassword}
              setShowPassword={setShowPassword}
            />
          </div>
          <div className="relative flex flex-col gap-y-1">
            <label className="block text-sm font-medium text-gray-700 font-header">Confirm New Password</label>
            <input
              name="newConfirmPass"
              type={showPassword.newConfirmPass ? 'text' : 'password'}
              value={formData.newConfirmPass}
              onChange={handleInput}
              placeholder="Confirm new password"
              className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pr-10"
            />
            <PasswordToggle
              name="newConfirmPass"
              showPassword={showPassword}
              setShowPassword={setShowPassword}
            />
          </div>
          <button
            className="btn-primary mt-2 transition-colors"
            onClick={handlePasswordSubmit}
            disabled={updatePasswordMutation.isPending}
          >
            {updatePasswordMutation.isPending ? "Changing Password..." : 'Change Password'}
          </button>
        </div>
      </div>
      {/* Delete Account */}
      <div className="pt-6 border-t mr-10">
        <h2 className="text-lg font-header font-semibold mb-4">Delete Account</h2>
        <div className="bg-white p-6 rounded-lg border flex items-center justify-between shadow-bb-general">
          <p className="text-gray-600 text-sm font-body">You cannot retrieve your account after it has been deleted</p>
          <button
            className="btn-danger transition-colors"
            onClick={() => setDeletePopup(true)}
            disabled={deleteAccountMutation.isPending}
          >
            {deleteAccountMutation.isPending ? <LoadingSpinner size="small" /> : 'Delete Account'}
          </button>
        </div>
      </div>
      {/* Deletion Popup */}
      {deletePopup && (
        <DeletionPopup
          isOpen={deletePopup}
          onClose={() => setDeletePopup(false)}
          desc="Are you sure you want to delete your account?"
          onConfirm={() => deleteAccountMutation.mutate()}
          isLoading={deleteAccountMutation.isPending}
        />
      )}
    </div>
  );
}