from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.responses import JSONResponse, FileResponse
from fastapi.middleware.cors import CORSMiddleware
import os
import shutil
import asyncio

import generator

app = FastAPI(title="Content Wizard - Document Multi-Agent")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def read_root():
    return {"status": "ok", "message": "Content Wizard API is running!"}

@app.post("/generate/all")
async def generate_all_endpoint(file: UploadFile = File(None), text: str = Form(None)):
    if not file and not text:
        raise HTTPException(status_code=400, detail="Provide either a text string or a PDF file.")

    content = ""
    if file:
        temp_path = f"temp_{file.filename}"
        with open(temp_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        try:
             content = generator.extract_text(temp_path)
        finally:
             if os.path.exists(temp_path):
                 os.remove(temp_path)
    else:
        content = text

    if not content.strip():
        raise HTTPException(status_code=400, detail="Could not extract any text.")

    # Using asyncio.gather if we wanted to true parallel, but HF rate limits might trip.
    # We will do them sequentially or provide an option to select which ones.
    # Given the scale, returning text summaries here. 
    # For Audio, we return a script and the user can request the physical mp3 file from another route.
    
    try:
        report     = generator.generate_report(content)
        flashcards = generator.generate_flashcards(content)
        quiz       = generator.generate_quiz(content)
        mindmap    = generator.generate_mindmap(content)
        slide_deck = generator.generate_slides(content)
        table      = generator.generate_datatable(content)
        info       = generator.generate_infographic_layout(content)
        
        # Audio overview script
        audio_script = generator.generate_audio_script(content)
    
        return {
            "status": "success",
            "report": report,
            "flashcards": flashcards,
            "quiz": quiz,
            "mindmap": mindmap,
            "slide_deck": slide_deck,
            "data_table": table,
            "infographic_layout": info,
            "audio_script": audio_script,
        }
    except Exception as e:
         return JSONResponse(status_code=500, content={"error": str(e)})

@app.post("/generate/audio-file")
async def generate_audio_file(script: str = Form(...)):
    # Uses Edge-TTS to render the audio and return the stream.
    output_filename = "overview.mp3"
    import nest_asyncio
    nest_asyncio.apply()
    
    loop = asyncio.get_event_loop()
    loop.run_until_complete(generator.generate_tts(script, output_filename))
    
    return FileResponse(output_filename, media_type="audio/mpeg", filename="audio_overview.mp3")