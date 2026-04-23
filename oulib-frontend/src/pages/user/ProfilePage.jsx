import { useEffect, useState } from "react"
import { getMyProfile, updateProfile, changePassword } from "../../api/users.api"
import { getPersonalizedRecommendations } from "../../api/recommendations.api"

function ProfilePage() {
  const [profile, setProfile] = useState(null)
  const [recommendBooks, setRecommendBooks] = useState([])

  const [loading, setLoading] = useState(true)
  const [tab, setTab] = useState("info")

  // update info
  const [fullName, setFullName] = useState("")
  const [avatarFile, setAvatarFile] = useState(null)

  // password
  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      setLoading(true)

      const [profileData, recommendData] = await Promise.all([
        getMyProfile(),
        getPersonalizedRecommendations(),
      ])

      const p = profileData?.result || profileData
      setProfile(p)
      setFullName(p?.fullName || "")

      setRecommendBooks(recommendData?.result || recommendData || [])
    } catch (err) {
      console.error("Profile error:", err)
    } finally {
      setLoading(false)
    }
  }

  // ================= UPDATE PROFILE =================
  const handleUpdateProfile = async () => {
    try {
      const formData = new FormData()

      formData.append("data", JSON.stringify({ fullName }))

      if (avatarFile) {
        formData.append("avatar", avatarFile)
      }

      await updateProfile(formData)

      alert("Cập nhật thành công")
      fetchData()
    } catch (err) {
      console.error(err)
      alert("Lỗi cập nhật")
    }
  }

  // ================= CHANGE PASSWORD =================
  const handleChangePassword = async () => {
    try {
      await changePassword({
        oldPassword,
        newPassword,
      })

      alert("Đổi mật khẩu thành công")
      setOldPassword("")
      setNewPassword("")
    } catch (err) {
      console.error(err)
      alert("Sai mật khẩu hoặc lỗi")
    }
  }

  if (loading) {
    return <div className="p-6 text-center">Loading profile...</div>
  }

  if (!profile) {
    return <div className="p-6 text-red-500">Cannot load profile</div>
  }

  return (
    <div className="min-h-screen bg-slate-100 p-6 space-y-6">

      {/* HEADER */}
      <div className="bg-white p-6 rounded-xl shadow">
        <h1 className="text-2xl font-bold">My Profile</h1>

        {/* TABS */}
        <div className="flex gap-4 mt-4">
          <button
            onClick={() => setTab("info")}
            className={`px-4 py-2 rounded ${
              tab === "info" ? "bg-blue-600 text-white" : "bg-slate-200"
            }`}
          >
            Thông tin
          </button>

          <button
            onClick={() => setTab("password")}
            className={`px-4 py-2 rounded ${
              tab === "password" ? "bg-blue-600 text-white" : "bg-slate-200"
            }`}
          >
            Đổi mật khẩu
          </button>
        </div>
      </div>

      {/* ================= TAB INFO ================= */}
      {tab === "info" && (
        <div className="bg-white p-6 rounded-xl shadow space-y-4">

          <div className="flex items-center gap-4">
            {profile.avatar ? (
              <img
                src={profile.avatar}
                alt="avatar"
                className="w-20 h-20 rounded-full object-cover"
              />
            ) : (
              <div className="w-20 h-20 bg-slate-200 rounded-full flex items-center justify-center">
                No Avatar
              </div>
            )}
          </div>

          <p><strong>Email:</strong> {profile.email}</p>

          <div>
            <label className="block mb-1">Full Name</label>
            <input
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="w-full border px-3 py-2 rounded"
            />
          </div>

          <div>
            <label className="block mb-1">Upload Avatar</label>
            <input
              type="file"
              onChange={(e) => setAvatarFile(e.target.files[0])}
            />
          </div>

          <button
            onClick={handleUpdateProfile}
            className="bg-blue-600 text-white px-5 py-2 rounded"
          >
            Cập nhật
          </button>
        </div>
      )}

      {/* ================= TAB PASSWORD ================= */}
      {tab === "password" && (
        <div className="bg-white p-6 rounded-xl shadow space-y-4">

          <div>
            <label>Mật khẩu cũ</label>
            <input
              type="password"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              className="w-full border px-3 py-2 rounded"
            />
          </div>

          <div>
            <label>Mật khẩu mới</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="w-full border px-3 py-2 rounded"
            />
          </div>

          <button
            onClick={handleChangePassword}
            className="bg-red-600 text-white px-5 py-2 rounded"
          >
            Đổi mật khẩu
          </button>
        </div>
      )}

      {/* ================= RECOMMEND BOOKS ================= */}
      <div className="bg-white p-6 rounded-xl shadow">
        <h2 className="text-xl font-semibold mb-4">
          Recommended Books
        </h2>

        {recommendBooks.length === 0 ? (
          <p>No recommendations</p>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {recommendBooks.map((b) => (
              <div
                key={b.id}
                className="border rounded-lg overflow-hidden hover:shadow"
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
                  <p className="text-sm font-medium">{b.title}</p>
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