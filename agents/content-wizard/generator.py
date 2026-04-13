import os
from huggingface_hub import InferenceClient
import edge_tts
from pypdf import PdfReader
import asyncio

def get_llm():
    hf_token = os.getenv("HF_TOKEN")
    if not hf_token:
        raise ValueError("HF_TOKEN missing in environment variables!")
    return InferenceClient(token=hf_token)

def extract_text(pdf_path: str) -> str:
    reader = PdfReader(pdf_path)
    text = ""
    for page in reader.pages:
        t = page.extract_text()
        if t: text += t + "\n"
    return text

def ask_llm(prompt: str, max_tokens=1500) -> str:
    client = get_llm()
    messages = [{"role": "user", "content": prompt}]
    res = client.chat_completion(
        messages=messages,
        model="Qwen/Qwen2.5-72B-Instruct",
        max_tokens=max_tokens,
        temperature=0.3
    )
    return res.choices[0].message.content.strip()

# --- 1. Audio Overview Script (and TTS layer hook) ---
def generate_audio_script(text: str) -> str:
    prompt = f"""You are a professional podcast host. Create a concise, highly engaging, 2-minute audio script summarizing this text. 
Do not include stage directions or speaker tags (like 'Host:'). Just write the words that will be spoken aloud.
Text to summarize: {text[:8000]}"""
    return ask_llm(prompt)

async def generate_tts(text: str, output_file: str):
    communicate = edge_tts.Communicate(text, "en-US-ChristopherNeural")
    await communicate.save(output_file)

# --- 2. Slide Deck ---
def generate_slides(text: str) -> str:
    prompt = f"""Convert the following text into a Slide Deck format. 
Use Markdown format. Each slide should start with '---' followed by '# Slide Title' and bullet points. 
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 3. Mind Map (Mermaid.js) ---
def generate_mindmap(text: str) -> str:
    prompt = f"""Create a Mermaid.js mindmap summarizing the structural concepts of this text.
Use the `mindmap` layout. Respond ONLY with the Mermaid code block, nothing else.
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 4. Report ---
def generate_report(text: str) -> str:
    prompt = f"""Write a comprehensive, professional executive summary report based on the following text. 
Include an Introduction, Key Findings, Detailed Analysis, and Conclusion. Format with Markdown.
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 5. Flashcards (JSON) ---
def generate_flashcards(text: str) -> str:
    prompt = f"""Create 10 educational flashcards from the text. 
Output ONLY a raw JSON array of objects with "front" and "back" keys. Do not use Markdown wrappers.
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 6. Quiz (JSON) ---
def generate_quiz(text: str) -> str:
    prompt = f"""Create a 5-question multiple-choice quiz based on the text. 
Output ONLY a raw JSON array of objects with "question", "options" (array of 4 strings), and "answer" (exact string match from options). No Markdown wrappers.
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 7. Infographic Layout ---
def generate_infographic_layout(text: str) -> str:
    prompt = f"""Design a visual infographic layout based on this text.
Describe the Hero Section, 3-4 visual data points, and a footer conclusion. Keep it highly visual and short. Use Markdown.
Text: {text[:8000]}"""
    return ask_llm(prompt)

# --- 8. Data Table ---
def generate_datatable(text: str) -> str:
    prompt = f"""Extract the most important statistical data, facts, or comparisons from this text and present them in a Markdown table.
Return ONLY the Markdown table.
Text: {text[:8000]}"""
    return ask_llm(prompt)