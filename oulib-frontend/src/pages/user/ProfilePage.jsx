import { useEffect, useState } from "react"
import { getMyProfile, updateProfile, changePassword } from "../../api/users.api"
import { getPersonalizedRecommendations } from "../../api/recommendations.api"
import { toast } from 'sonner'
import { useNavigate } from "react-router-dom"

function ProfilePage() {
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [recommendBooks, setRecommendBooks] = useState([])

  const [loading, setLoading] = useState(true)
  const [tab, setTab] = useState("info")
  const [isEditingProfile, setIsEditingProfile] = useState(false)

  const [fullName, setFullName] = useState("")

  const [oldPassword, setOldPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [confirmNewPassword, setConfirmNewPassword] = useState("")
  const [showOldPassword, setShowOldPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)

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

  const handleUpdateProfile = async () => {
    try {
      const formData = new FormData()
      const profilePayload = {
        fullName: fullName.trim(),
      }

      formData.append(
        "data",
        new Blob([JSON.stringify(profilePayload)], {
          type: "application/json",
        }),
      )

      await updateProfile(formData)

      toast.success("Cập nhật thành công")
      setIsEditingProfile(false)
      fetchData()
    } catch (err) {
      console.error(err)
      toast.error("Lỗi cập nhật")
    }
  }

  const isPasswordConfirmationValid = () => {
    return newPassword === confirmNewPassword
  }

  const handleChangePassword = async () => {
    if (!isPasswordConfirmationValid()) {
      toast.error("Mật khẩu mới và xác nhận mật khẩu mới không khớp")
      return
    }

    try {
      await changePassword({
        oldPassword,
        newPassword,
      })

      toast.success("Đổi mật khẩu thành công")
      setOldPassword("")
      setNewPassword("")
      setConfirmNewPassword("")
    } catch (err) {
      console.error(err)
      toast.error("Sai mật khẩu hoặc lỗi")
    }
  }

  if (loading) {
    return <div className="p-6 text-center">Loading...</div>
  }

  if (!profile) {
    return <div className="p-6 text-center text-red-500">Cannot load profile</div>
  }

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <div className="max-w-6xl mx-auto flex gap-6">

        {/* Sidebar */}
        <div className="w-64 bg-white rounded-xl shadow-sm p-4 space-y-2">
          <button
            onClick={() => setTab("info")}
            className={`w-full text-left px-4 py-2 rounded-lg flex items-center gap-2 ${tab === "info"
              ? "text-blue-600 font-medium bg-blue-50"
              : "text-gray-600 hover:bg-gray-100"
              }`}
          >
            Hồ sơ
          </button>

          <button
            onClick={() => setTab("password")}
            className={`w-full text-left px-4 py-2 rounded-lg flex items-center gap-2 ${tab === "password"
              ? "text-blue-600 font-medium bg-blue-50"
              : "text-gray-600 hover:bg-gray-100"
              }`}
          >
            Bảo mật
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 space-y-6">

          {/* TAB INFO */}
          {tab === "info" && (
            <div className="bg-white rounded-xl shadow-sm p-8 space-y-8 max-w-4xl">
              {/* Header */}
              <div className="space-y-1">
                <h2 className="text-xl font-semibold text-slate-900">Thông tin cá nhân</h2>
                <p className="text-sm text-gray-500">
                  Thông tin này sẽ được hiển thị công khai.
                </p>
              </div>

              {/* Form Fields */}
              <div className="divide-y divide-gray-100 border-t border-b border-gray-100">
                <div className="grid grid-cols-[200px,1fr] items-center py-5">
                  <span className="text-sm font-medium text-gray-600">ID người dùng</span>
                  <input
                    value={profile.id || ""}
                    readOnly
                    className="w-full bg-transparent px-0 py-2 text-sm text-gray-900 font-medium outline-none"
                  />
                </div>
                {/* Họ và tên */}
                <div className="grid grid-cols-[200px,1fr] items-center py-5">
                  <span className="text-sm font-medium text-gray-600">Họ và tên</span>
                  <input
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    readOnly={!isEditingProfile}
                    className={`w-full text-sm transition-all duration-200 outline-none ${isEditingProfile
                      ? "rounded-lg border border-gray-300 px-3 py-2 bg-white focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500"
                      : "border-transparent bg-transparent px-0 py-2 text-gray-900 font-medium"
                      }`}
                  />
                </div>

                {/* Địa chỉ email */}
                <div className="grid grid-cols-[200px,1fr] items-center py-5">
                  <span className="text-sm font-medium text-gray-600">Địa chỉ email</span>
                  <input
                    value={profile.email || ""}
                    readOnly
                    className="w-full bg-transparent px-0 py-2 text-sm text-gray-900 font-medium outline-none"
                  />
                </div>
              </div>

              {/* Action Button */}
              <div className="flex justify-end pt-4">
                <button
                  onClick={() => {
                    if (!isEditingProfile) {
                      setIsEditingProfile(true);
                      return;
                    }
                    handleUpdateProfile();
                  }}
                  className="rounded-md bg-[#1a1c1e] px-8 py-2.5 text-xs font-bold text-white transition hover:bg-black tracking-wider uppercase"
                >
                  {isEditingProfile ? "LƯU THAY ĐỔI" : "CHỈNH SỬA"}
                </button>
              </div>
            </div>
          )}

          {/* TAB PASSWORD */}
          {tab === "password" && (
            <div className="space-y-6">

              <div className="bg-white rounded-xl shadow-sm p-6 space-y-6">
                <div>
                  <h2 className="text-lg font-semibold">Đổi mật khẩu</h2>
                  <p className="text-sm text-gray-500">
                    Đảm bảo tài khoản của bạn đang sử dụng mật khẩu dài, ngẫu nhiên để giữ an toàn.
                  </p>
                </div>

                <div className="space-y-4 max-w-xl">
                  <div>
                    <label className="text-sm text-gray-600">Mật khẩu hiện tại</label>
                    <div className="mt-1 flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-2">
                      <input
                        type={showOldPassword ? "text" : "password"}
                        value={oldPassword}
                        onChange={(e) => setOldPassword(e.target.value)}
                        className="w-full border-0 bg-transparent px-1 py-2 text-sm outline-none"
                      />
                      <button
                        type="button"
                        onClick={() => setShowOldPassword((prev) => !prev)}
                        className="rounded px-2 py-1 text-xs font-medium text-gray-600 transition hover:bg-gray-100"
                      >
                        {showOldPassword ? "Ẩn" : "Hiện"}
                      </button>
                    </div>
                  </div>

                  <div>
                    <label className="text-sm text-gray-600">Mật khẩu mới</label>
                    <div className="mt-1 flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-2">
                      <input
                        type={showNewPassword ? "text" : "password"}
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        className="w-full border-0 bg-transparent px-1 py-2 text-sm outline-none"
                      />
                      <button
                        type="button"
                        onClick={() => setShowNewPassword((prev) => !prev)}
                        className="rounded px-2 py-1 text-xs font-medium text-gray-600 transition hover:bg-gray-100"
                      >
                        {showNewPassword ? "Ẩn" : "Hiện"}
                      </button>
                    </div>
                  </div>

                  <div>
                    <label className="text-sm text-gray-600">Xác nhận mật khẩu mới</label>
                    <div className="mt-1 flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-2">
                      <input
                        type={showConfirmPassword ? "text" : "password"}
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                        className="w-full border-0 bg-transparent px-1 py-2 text-sm outline-none"
                      />
                      <button
                        type="button"
                        onClick={() => setShowConfirmPassword((prev) => !prev)}
                        className="rounded px-2 py-1 text-xs font-medium text-gray-600 transition hover:bg-gray-100"
                      >
                        {showConfirmPassword ? "Ẩn" : "Hiện"}
                      </button>
                    </div>
                  </div>
                </div>

                <div className="flex justify-end gap-3">
                  <button
                    onClick={() => {
                      setOldPassword("")
                      setNewPassword("")
                      setConfirmNewPassword("")
                    }}
                    className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50"
                  >
                    HỦY
                  </button>

                  <button
                    onClick={handleChangePassword}
                    className="rounded-lg bg-gray-800 px-4 py-2 text-sm font-medium text-white transition hover:bg-gray-700"
                  >
                    LƯU
                  </button>
                </div>
              </div>

            </div>
          )}

          {/* RECOMMEND */}
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h2 className="text-lg font-semibold mb-4">Recommended Books</h2>

            {recommendBooks.length === 0 ? (
              <p className="text-gray-500 text-sm">No recommendations</p>
            ) : (
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {recommendBooks.map((b) => (
                  <div key={b.id}
                    onClick={() => navigate(`/books/${b.id}`)}
                    className="border border-gray-300 rounded-lg overflow-hidden">
                    {b.thumbnailUrl ? (
                      <img src={b.thumbnailUrl} className="h-40 w-full object-cover" />
                    ) : (
                      <div className="h-40 flex items-center justify-center bg-gray-100">
                        No Image
                      </div>
                    )}

                    <div className="p-3">
                      <p className="text-sm font-semibold line-clamp-2">{b.title}</p>
                      <p className="text-xs text-gray-500 mt-1">{b.categoryName}</p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      </div>
    </div>
  )
}

export default ProfilePage