import axiosInstance from "./axios"

export async function getPersonalizedRecommendations() {
  const res = await axiosInstance.get("/api/v1/recommendations/personalized")
  return res?.data?.result ?? res?.data
}