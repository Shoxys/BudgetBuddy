export default function ImageUpload({ src }) {
    return (
        <div className="flex justify-center h-2/5">
            <div
                className={`${src ? "bg-cover bg-center w-[80%]" : "bg-bb_aqua px-5 py-4"} 
                            flex w-[90%] h-full flex-col justify-center items-center gap-1 
                            rounded-3xl border-dashed border border-primary_blue 
                            font-body text-gray-800 text-center text-sm`}
                style={src ? { backgroundImage: `url('${src}')` } : {}}
            >
                {src
                ? <div className="flex h-[22vh] items-center">
                    <input className="" type="file" id="uploadFile" name="goalImage" accept=".png .jpg" multiple hidden/>
                    <button className="btn-primary text-md rounded-lg shadow-bb-general font-semibold hover-effect mb-2">Upload New</button>
                  </div>
                : <>
                    <img src="/src/assets/upload.PNG" alt="upload icon" width="65" />
                    <h3 className="font-body text-sm">Drag & drop your files here</h3>
                    <p className="text-gray-500 font-body text-xs">Supported format PNG, JPG. Max 10MB each.</p>
                    <h3 className="font-body text-md"> OR</h3>
                    <input className="" type="file" id="uploadFile" name="goalImage" accept=".png .jpg" multiple hidden/>
                    <button className="btn-primary font-normal hover-effect text-[0.8rem]">Browse files</button>
                  </>
                }
                
            </div>
        </div>
    )
}