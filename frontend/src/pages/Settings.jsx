import AccountSettings from "../components/Settings/AccountSettings"
import Sidebar from "../components/Sidebar"

export default function Settings() {
   return (
      <div className="relative min-h-screen min-w-screen bg-white pl-20">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col pb-4 w-full">
            <div className="flex flex-col bg-white py-1 mt-3 px-6">
                <div className="flex flex-row justify-between">
                    <div className="flex flex-row w-full items-center gap-2">
                        <img className="w-10.5" src="src/assets/settings-blue.png" alt="settings icon" />
                        <h2 className="text-2xl font-header font-bold">
                            Settings
                        </h2>
                    </div>
                </div>
            </div>
            <div className="flex flex-row">
                <div className="w-[60%] bg-white py-4 ml-8 mt-2 text-black">
                    <AccountSettings/>
                </div>
                <div className="w-[40%] bg-white mt-2 mr-8">
                    <img className="w-[40rem] rounded-md" src="src/assets/settings-bg.png" alt="settings background image" />
                </div>
            </div>

        </div>
    </div>
   )
}