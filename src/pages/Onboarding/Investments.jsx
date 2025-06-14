export default function Investments() {
    return(
        <>
         <div className="flex gap-4 mt-8">
                <button className="rounded-2xl relative overflow-hidden outline-6 outline-primary_blue focus:shadow-custom-blue">
                <img class="rounded-2xl" src="src/assets/investments.png" alt="Investment options you may have"/>
            </button>
            <button className="rounded-2xl relative outline-6 outline-primary_blue focus:shadow-custom-blue">
                <img className="rounded-2xl" src="src/assets/savings.png" alt="Saving options you may have"/>
            </button>
        </div>
        </>
    )
}