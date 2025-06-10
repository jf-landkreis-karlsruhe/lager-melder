FROM mcr.microsoft.com/playwright:v1.52.0-jammy

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json (if it exists)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy project files
COPY . .

# Create a folder for PDF output
RUN mkdir -p /app/output

# Set environment variables (these can be overridden at runtime)
ENV APP_URL=http://localhost:3000
ENV USERNAME=your_username
ENV PASSWORD=your_password
ENV TARGET_PATH=/dashboard
ENV PDF_OUTPUT_PATH=/app/output/output.pdf
ENV INTERVAL_MINUTES=0

# Command to run the script
CMD ["node", "index.js"]