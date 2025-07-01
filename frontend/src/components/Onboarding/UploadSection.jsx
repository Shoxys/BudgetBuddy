export default function UploadSection() {
    return (
        <div className="flex justify-center h-2/5">
            <div className="flex w-4/5 h-full flex-col justify-center items-center gap-2 bg-bb_aqua rounded-3xl border-dashed border 
                            border-primary_blue font-body text-gray-800 text-center text-sm">
                <img src="/src/assets/upload.PNG" alt="upload icon" width="110" />
                <h3>Drag & drop your files here</h3>
                <p className="text-gray-500">Supported format CSV. Max 10MB each.</p>
                <h3>OR</h3>
                <input className="" type="file" id="uploadFile" name="transaction" accept=".csv" multiple hidden/>
                <button className="btn-primary font-normal text-base hover-effect">Browse files</button>
            </div>
        </div>
    )
}