export default function Investments() {
    return(
        <>
         <div className="flex justify-center flex-row gap-4 3xl:gap-10 mt-8 3xl:mt-20">
                <button className="rounded-2xl relative overflow-hidden outline-6 outline-primary_blue focus:shadow-custom-blue">
                <img className="rounded-2xl 3xl:w-96" src="src/assets/investments.png" alt="Investment options you may have"/>
            </button>
            <button className="rounded-2xl relative outline-6 outline-primary_blue focus:shadow-custom-blue">
                <img className="rounded-2xl 3xl:w-96" src="src/assets/savings.png" alt="Saving options you may have"/>
            </button>
        </div>
        </>
    )
}