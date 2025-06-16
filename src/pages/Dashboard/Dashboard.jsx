import Sidebar from "../../components/Siderbar"
export default function Dashboard () {
   
   return (
      <div className="relative min-h-screen min-w-screen bg-bb_slate pl-16">
        <Sidebar selectedNav="Dashboard"/>
        <div className="flex flex-col px-5 pb-4 w-full h-full gap-3">
            <h2 className={`text-2xl font-header font-bold ml-4 mt-3`}>
                <span className="text-primary_blue">Budget</span>
                <span className="text-secondary_red">Buddy</span>
                <span> Dashboard</span>
            </h2>
            <div className="w-full h-full border border-black py-3 px-12">

            </div>    
        </div>
      </div>
      
   )
}