import axiosInstance from "./axios"

export async function getMyProfile() {
  const response = await axiosInstance.get("/api/v1/users/me")
  return response?.data?.result ?? response?.data
}