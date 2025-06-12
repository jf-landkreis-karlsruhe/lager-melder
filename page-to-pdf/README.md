# Page to PDF - Vue App Login & PDF Generator

This tool uses Playwright to automatically log into a Vue web application, navigate to a specific page, and save it as a PDF.

## Setup

1. Install dependencies:
```
npm install
```

2. Create your environment file:
```
cp .env.example .env
```

3. Update the `.env` file with your credentials and settings:
```
APP_URL=http://localhost:3000
USERNAME=your_username
PASSWORD=your_password 
TARGET_PATH=/dashboard
PDF_OUTPUT_PATH=./output.pdf
INTERVAL_MINUTES=0
```

## Usage

### Using environment variables

```
npm start
```

### Using command line arguments

```
node index.js --url=http://localhost:3000 --username=user --password=pass --targetPath=/reports --output=./report.pdf
```

### Periodic execution

To run the script periodically:

```
node index.js --interval=30
```

This will run the script every 30 minutes

### Available options

- `--url, -u`: URL of the Vue application (default: from .env or http://localhost:3000)
- `--username, -n`: Username for login (required)
- `--password, -p`: Password for login (required)
- `--targetPath, -t`: Path to navigate to after login (default: from .env or /dashboard)
- `--output, -o`: Path where to save the PDF (default: from .env or ./output.pdf)
- `--interval, -i`: Time interval between runs in minutes, 0 for single run (default: 0)
- `--help`: Show help

## Docker Usage

### Build Docker image

```
docker build -t page-to-pdf .
```

### Run with Docker

```
docker run -e USERNAME=user -e PASSWORD=pass page-to-pdf
```

### Run with volume mount for PDF output

```
docker run -v $(pwd)/output:/app/output -e USERNAME=user -e PASSWORD=pass page-to-pdf
```

### Run with all environment variables

```
docker run \
  -e APP_URL=http://host.docker.internal:3000 \
  -e USERNAME=user \
  -e PASSWORD=pass \
  -e TARGET_PATH=/reports \
  -e PDF_OUTPUT_PATH=/app/output/report.pdf \
  -e INTERVAL_MINUTES=60 \
  -v $(pwd)/output:/app/output \
  page-to-pdf
```

> Note: When running in Docker and accessing a localhost URL on your host machine, use `host.docker.internal` instead of `localhost`.

### Using the pre-built image from GitHub Container Registry

You can also use the pre-built Docker image from GitHub Container Registry:

```
docker pull ghcr.io/jf-landkreis-karlsruhe/page-to-pdf:latest
docker run -v $(pwd)/output:/app/output -e USERNAME=user -e PASSWORD=pass ghcr.io/jf-landkreis-karlsruhe/page-to-pdf:latest
```

## Install playwright
`npx playwright install`
