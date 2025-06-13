const { chromium } = require('playwright');
const dotenv = require('dotenv');
const path = require('path');
const yargs = require('yargs/yargs');
const { hideBin } = require('yargs/helpers');

// Load environment variables
dotenv.config();

const argv = yargs(hideBin(process.argv))
  .option('url', {
    alias: 'u',
    type: 'string',
    description: 'URL of the Vue application',
    default: process.env.APP_URL || 'http://localhost:3000'
  })
  .option('username', {
    alias: 'n',
    type: 'string',
    description: 'Username for login',
    default: process.env.USERNAME
  })
  .option('password', {
    alias: 'p',
    type: 'string',
    description: 'Password for login',
    default: process.env.PASSWORD
  })
  .option('targetPath', {
    alias: 't',
    type: 'string',
    description: 'Path to navigate to after login',
    default: process.env.TARGET_PATH || '/dashboard'
  })
  .option('output', {
    alias: 'o',
    type: 'string',
    description: 'Path where to save the PDF',
    default: process.env.PDF_OUTPUT_PATH || './output.pdf'
  })
  .option('interval', {
    alias: 'i',
    type: 'number',
    description: 'Time interval between runs in minutes (0 for single run)',
    default: process.env.INTERVAL_MINUTES || 0
  })
  .help()
  .argv;

async function generatePdf() {
  // Validate required arguments
  if (!argv.username || !argv.password) {
    console.error('Username and password are required!');
    process.exit(1);
  }

  console.log(`Starting browser and navigating to ${argv.url}...`);
  
  const browser = await chromium.launch({ 
    headless: true // Change to false if you want to see the browser while running
  });
  
  const context = await browser.newContext();
  const page = await context.newPage();

  try {
    // Navigate to the application
    await page.goto(argv.url);

    // Login - Adjust these selectors as needed for your specific Vue app
    await page.fill('input[id="username"]', argv.username);
    await page.fill('input[id="password"]', argv.password);
    await page.click('button[type="submit"]');

    // Wait for navigation after login - adjust timing or selector as needed
    await page.waitForSelector('nav#nav', { timeout: 5000 });

    // Navigate to the target path
    const targetUrl = new URL(argv.targetPath, argv.url).toString();
    await page.goto(targetUrl);
    await page.waitForLoadState('networkidle');

    // Create output directory if it doesn't exist
    const outputDir = path.dirname(argv.output);
    require('fs').mkdirSync(outputDir, { recursive: true });

    const outputFilePath = argv.output.replace(".pdf", "_" + new Date().toISOString().replace(/[:.]/g, '-') + ".pdf");

    // Generate PDF
    await page.pdf({
      path: outputFilePath,
      format: 'A4',
      printBackground: true,
      margin: {
        top: '20px',
        right: '20px',
        bottom: '20px',
        left: '20px'
      }
    });
    console.log(`PDF saved successfully to ${outputFilePath}`);

    return true;
  } catch (error) {
    console.error('An error occurred:', error);
    return false;
  } finally {
    // Close browser
    await browser.close();
  }
}

async function run() {
  const intervalMinutes = Number(argv.interval);

  // If interval is set, run periodically
  if (intervalMinutes > 0) {
    console.log(`Starting periodic execution every ${intervalMinutes} minutes...`);
    
    while (true) {
      await generatePdf();
      await new Promise(resolve => setTimeout(resolve, intervalMinutes * 60 * 1000));
    }
  } else {
    // Single run
    await generatePdf();
  }
}

run();
