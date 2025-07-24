/**
 * API functions for file upload operations.
 */
import { axiosInstance } from './AxiosInstance';
import { UPLOAD_API_BASE_URL } from '../constants/ApiConstants';

// Upload an image file
export const uploadImage = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  const response = await axiosInstance.post(`${UPLOAD_API_BASE_URL}/image`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
};