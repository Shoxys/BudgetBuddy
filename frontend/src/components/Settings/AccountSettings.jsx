import { useState } from "react";
import DeletionPopup from "../DeletionPopup"
import PasswordToggle from "../PasswordToggle";

const data = {
    currentEmail: 'jackadoy157@gmail.com',
    newEmail: '',
    confirmPass: '',
    currentPass: '',
    newPass: '',
    newConfirmPass: '',
  }

export default function AccountSettings() {
  const passToggle = {
    confirmPass: false, 
    currentPass: false,
    newPass: false,
    newConfirmPass: false,
  };

  const [showPassword, setShowPassword] = useState(passToggle);
  const [deletePopup, setDeletePopup] = useState(false);
  const [FormData, setFormData] = useState(data)

  const handleInput = (event) => {
    const {name, value} = event.target;
    setFormData((prev) => ({...prev, [name] : value}))
  }
  return (
    <div className="space-y-6 font-body text-sm text-gray-800">
      {/* Change Email */}
      <div className="flex flex-row items-start gap-x-6">
          <h2 className="text-lg font-header font-semibold min-w-[10rem] pt-2">Change Email</h2>
          <div className="bg-white p-6 rounded-lg border space-y-4 max-w-[35rem] w-full">
            {/* Current Email input */}
            <div>
              <label className="input-label">Current Email</label>
              <input name="currentEmail" type="email" value={FormData.currentEmail} defaultValue="Current Email" className="input" disabled />
            </div>
            {/* New Email input */}
            <div>
              <label className="input-label">New Email</label>
              <input name="newEmail" type="email" value={FormData.newEmail} onChange={handleInput} placeholder="Enter new email" className="input" />
            </div>
            {/* Confirm Password input */}
            <div className="relative">
              <label className="input-label">Confirm password</label>
              <input name="confirmPass" type={showPassword["confirmPass"] ? "text" : "password"} value={FormData.password1} placeholder="Confirm password" className="input pr-10" />
              <PasswordToggle name={"confirmPass"} showPassword={showPassword} setShowPassword={setShowPassword}/>
            </div>
            <button className="btn-primary mt-2">Change email</button>
        </div>
      </div>

      {/* Line break */}
      <hr className="mr-10" />

      {/* Change Password */}
      <div className="flex flex-row items-start gap-x-6">
        <h2 className="text-lg font-header font-semibold min-w-[10rem] pt-2">Change Password</h2>
        <div className="bg-white p-6 rounded-lg border space-y-4 max-w-[35rem] w-full">
          {/* Current Password input */}
          <div className="relative">
            <label className="input-label">Current password</label>
            <input name="currentPass" type={showPassword["currentPass"] ? "text" : "password"} placeholder="Enter current password" className="input pr-10" />
            <PasswordToggle name={"currentPass"} showPassword={showPassword} setShowPassword={setShowPassword}/>
          </div>
          {/* New Password input */}
          <div className="relative">
            <label className="input-label">New password</label>
            <input name="newPass" type={showPassword["newPass"] ? "text" : "password"} placeholder="Enter new password" className="input" />
            <PasswordToggle name={"newPass"} showPassword={showPassword} setShowPassword={setShowPassword}/>
          </div>
          {/* Confirm new Password input */}
          <div className="relative">
            <label className="input-label">Confirm new password</label>
            <input name="confirmNewPass" type={showPassword["confirmNewPass"] ? "text" : "password"} placeholder="Confirm new password" className="input" />
            <PasswordToggle name={"confirmNewPass"} showPassword={showPassword} setShowPassword={setShowPassword}/>
          </div>

          <button className="btn-primary mt-2">Change password</button>
        </div>
      </div>

      {/* Delete Account */}
      <div className="pt-6 border-t mr-10">
        <h2 className="text-lg font-header font-semibold mb-4">Delete Account</h2>
        <div className="bg-white p-6 rounded-lg border flex items-center justify-between">
          <p className="text-gray-600 text-sm font-body">You cannot retrieve your account after it has been deleted</p>
          <button className="btn-danger" onClick={() => setDeletePopup(true)}>Delete account</button>
        </div>
      </div>
      {/* Confirm account deletion popup modal */}
      {deletePopup &&
        <DeletionPopup 
          isOpen={deletePopup} 
          onClose={() => setDeletePopup(false)} 
          desc="Are you sure you want to delete your account"
        />
      }
    </div>
  );
}
