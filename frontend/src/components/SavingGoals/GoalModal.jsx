/**
 * GoalModal component for creating or editing savings goals.
 * Handles form input, image upload, and async goal saving.
 */

import { useState, useEffect } from 'react';
import { Dialog } from '@headlessui/react';
import Notification from '../../components/Notification';
import LoadingSpinner from '../../components/LoadingSpinner';
import ImageUpload from './ImageUpload';
import { DEFAULT_IMAGE } from '../../constants/SavingGoalConstants';
import { useUploadImage } from '../../api/UploadImageHooks';
import { useSaveGoal, useGetGoalById } from '../../api/SavingGoalsHooks';

export default function GoalModal({ isOpen = false, onClose, data = {} }) {
  const [formData, setFormData] = useState({
    title: '',
    target: '',
    date: '',
    image: DEFAULT_IMAGE,
  });
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(DEFAULT_IMAGE);
  const [notification, setNotification] = useState({ isOpen: false, type: '', message: '' });

  const { data: goalData, isLoading: isFetching } = useGetGoalById(data?.id);
  const { mutateAsync: uploadImage, isLoading: isUploading } = useUploadImage();
  const { mutateAsync: saveGoal, isLoading: isSaving } = useSaveGoal();

  // Handlers
  const handleChange = (field) => (e) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    const url = URL.createObjectURL(file);
    setPreviewUrl(url);
    return () => URL.revokeObjectURL(url);
  };

  const showNotification = (type, message) => setNotification({ isOpen: true, type, message });
  const closeNotification = () => setNotification({ isOpen: false, type: '', message: '' });

  const handleSave = async () => {
    // Validate form fields
    if (!formData.title || !formData.target || !formData.date) {
      showNotification('error', 'Please fill all required fields.');
      return;
    }

    let finalImageRef = formData.image;
    if (selectedFile) {
      try {
        finalImageRef = await uploadImage(selectedFile);
      } catch (error) {
        showNotification('error', `Failed to upload image: ${error.response?.data || error.message}`);
        return;
      }
    }

    const payload = {
      title: formData.title,
      target: parseFloat(formData.target),
      contributed: formData.contributed || 0,
      date: formData.date,
      imageRef: finalImageRef === DEFAULT_IMAGE ? null : finalImageRef,
      ...(data?.id && { id: data.id }),
    };

    try {
      await saveGoal(payload);
      showNotification('success', data?.id ? 'Goal updated successfully!' : 'Goal created successfully!');
      resetForm();
      onClose();
    } catch (error) {
      showNotification('error', `Failed to save goal: ${error.response?.data || error.message}`);
    }
  };
   const resetForm = () => {
    setFormData({ title: '', target: '', date: '', image: DEFAULT_IMAGE });
    setSelectedFile(null);
    setPreviewUrl(null);
  };

  // Sync form with fetched goal data
  useEffect(() => {
    if (data?.id && goalData) {
      const imageUrl = goalData.imageRef || DEFAULT_IMAGE;
      setFormData({
        title: goalData.title || '',
        target: goalData.target || '',
        date: goalData.date || '',
        image: imageUrl,
      });
      setPreviewUrl(imageUrl);
      setSelectedFile(null);
    } else {
      resetForm();
    }
  }, [goalData, data?.id, isOpen]);
  
  // Layout
  return (
    <>
      {/* Notification */}
      {notification.isOpen && (
        <Notification
          isOpen={notification.isOpen}
          type={notification.type}
          message={notification.message}
          onClose={closeNotification}
        />
      )}

      {/* Modal */}
      <Dialog
        open={isOpen}
        onClose={() => {
          resetForm();
          onClose();
        }}
        className="relative z-50"
      >
        <div className="fixed inset-0 bg-gray-600/60" aria-hidden="true" />
        <div className="fixed inset-0 flex items-center justify-center p-4 font-body">
          <Dialog.Panel className="w-full max-w-md rounded-lg bg-white p-6 shadow-bb-general">
            {/* Header */}
            <Dialog.Title className="text-lg font-semibold font-header mb-4">
              {data?.id ? 'Edit Goal' : 'Create New Goal'}
            </Dialog.Title>

            {/* Content */}
            {isFetching || isSaving ? (
              <div className="flex justify-center py-4">
                <LoadingSpinner />
              </div>
            ) : (
              <div className="space-y-4">
                {/* Title */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 font-header">
                    Name
                  </label>
                  <input
                    type="text"
                    placeholder="Enter goal title"
                    value={formData.title}
                    onChange={handleChange('title')}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  />
                </div>
                {/* Target */}
                <div className="relative">
                  <label className="block text-sm font-medium text-gray-700 font-header">
                    Goal Target
                  </label>
                  <span className="absolute ml-3 top-[1.9rem] font-header text-lg">$</span>
                  <input
                    type="number"
                    placeholder="Enter goal target"
                    value={formData.target}
                    onChange={handleChange('target')}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue pl-7"
                    min="0"
                    step="0.01"
                  />
                </div>
                {/* Image */}
                <div className="space-y-2">
                  <label className="font-header text-sm">Goal Image</label>
                  <ImageUpload
                    src={previewUrl}
                    onFileChange={handleFileChange}
                    isLoading={isUploading}
                    isError={notification.isOpen && notification.type === 'error'}
                    error={notification.message}
                  />
                </div>
                {/* Date */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 font-header">
                    Goal End Date
                  </label>
                  <input
                    type="date"
                    value={formData.date}
                    onChange={handleChange('date')}
                    className="mt-1 w-full rounded-md border border-gray-300 p-2 focus:outline-none focus:ring-2 focus:ring-primary_blue"
                  />
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="mt-6 flex justify-end gap-2">
              <button
                onClick={() => {
                  resetForm();
                  onClose();
                }}
                className="rounded-md border border-gray-300 px-4 py-2 text-gray-700 hover:bg-gray-100 transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                className="rounded-md bg-primary_blue px-5 py-2 text-white hover:bg-btn_hover transition-colors"
                disabled={isFetching || isSaving || isUploading}
              >
                {data?.id ? 'Save' : 'Create'}
              </button>
            </div>
          </Dialog.Panel>
        </div>
      </Dialog>
    </>
  );
}