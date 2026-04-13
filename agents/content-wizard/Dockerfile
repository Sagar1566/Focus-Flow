# Dockerfile for Hugging Face Spaces Deployment
FROM python:3.10-slim

# Creates a non-root user
RUN useradd -m -u 1000 user
USER user

# Sets home config
ENV HOME=/home/user \
    PATH=/home/user/.local/bin:$PATH

WORKDIR $HOME/app

# Installs and configures python requirements
COPY --chown=user requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copies actual source code
COPY --chown=user . .

# Environment config
ENV PYTHONUNBUFFERED=1

# Expose port and run
EXPOSE 7860
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "7860"]