import { useEffect, useState } from "react"
import { getMyProfile } from "../../api/users.api"
import { getPersonalizedRecommendations } from "../../api/recommendations.api"

function ProfilePage() {
  const [profile, setProfile] = useState(null)
  const [recommendBooks, setRecommendBooks] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      setLoading(true)

      // gọi song song 2 API
      const [profileData, recommendData] = await Promise.all([
        getMyProfile(),
        getPersonalizedRecommendations()
      ])

      setProfile(profileData?.result || profileData)
      setRecommendBooks(recommendData?.result || recommendData || [])
    } catch (err) {
      console.error("Profile error:", err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div className="p-6 text-center text-slate-500">
        Loading profile...
      </div>
    )
  }

  if (!profile) {
    return (
      <div className="p-6 text-center text-red-500">
        Cannot load profile
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-slate-100 p-6 space-y-6">

      {/* PROFILE INFO */}
      <div className="bg-white p-6 rounded-xl shadow">
        <h1 className="text-2xl font-bold mb-4">My Profile</h1>

        <div className="space-y-2">
          <p><strong>Name:</strong> {profile.name}</p>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Role:</strong> {profile.role}</p>
          <p><strong>Status:</strong> {profile.active ? "Active" : "Inactive"}</p>
        </div>
      </div>

      {/* RECOMMEND BOOKS */}
      <div className="bg-white p-6 rounded-xl shadow">
        <h2 className="text-xl font-semibold mb-4">
          Recommended Books
        </h2>

        {recommendBooks.length === 0 ? (
          <p className="text-slate-500">No recommendations</p>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {recommendBooks.map((b) => (
              <div
                key={b.id}
                className="border rounded-lg overflow-hidden hover:shadow cursor-pointer"
              >
                {b.thumbnailUrl ? (
                  <img
                    src={b.thumbnailUrl}
                    alt={b.title}
                    className="h-40 w-full object-cover"
                  />
                ) : (
                  <div className="h-40 bg-slate-200 flex items-center justify-center">
                    No Image
                  </div>
                )}

                <div className="p-2">
                  <p className="text-sm font-medium line-clamp-2">
                    {b.title}
                  </p>
                  <p className="text-xs text-slate-500">
                    {b.categoryName}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

    </div>
  )
}

export default ProfilePage