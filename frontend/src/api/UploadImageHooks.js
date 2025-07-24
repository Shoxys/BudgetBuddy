/**
 * Custom React Query hooks for file upload operations.
 */
import { useMutation } from '@tanstack/react-query';
import { uploadImage } from './UploadImageApi';

/**
 * Upload an image file.
 * @returns {object} React Query mutation object.
 */
export const useUploadImage = () => {
  return useMutation({
    mutationFn: uploadImage,
  });
};