import axiosInstance from "./axios"

// PERSONALIZED
export async function getPersonalizedRecommendations() {
  const res = await axiosInstance.get("/api/v1/recommendations/personalized")
  return res?.data?.result ?? res?.data
}

// TRENDING
export async function getTrending() {
  const res = await axiosInstance.get("/api/v1/recommendations/trending")
  return res?.data?.result ?? res?.data
}

// POPULAR
export async function getPopular() {
  const res = await axiosInstance.get("/api/v1/recommendations/popular")
  return res?.data?.result ?? res?.data
}