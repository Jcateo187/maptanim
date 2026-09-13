const fs = require('fs');
const https = require('https');
const path = require('path');

const configPath = path.join(process.env.USERPROFILE, '.config', 'configstore', 'firebase-tools.json');
const config = JSON.parse(fs.readFileSync(configPath, 'utf8'));

let accessToken = config.tokens.access_token;
const projectId = 'maptanim-77851';

async function fetchGoogleApi(url) {
  return new Promise((resolve, reject) => {
    const req = https.get(url, {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'User-Agent': 'MapTanim-Dev/1.0'
      }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          const parsed = JSON.parse(data);
          resolve({ status: res.statusCode, data: parsed });
        } catch (e) {
          resolve({ status: res.statusCode, data });
        }
      });
    });
    req.on('error', reject);
  });
}

async function main() {
  console.log(`Checking project ${projectId}...`);
  // 1. List GCS buckets
  const bucketsRes = await fetchGoogleApi(`https://storage.googleapis.com/storage/v1/b?project=${projectId}`);
  console.log('Buckets response status:', bucketsRes.status);
  if (bucketsRes.data && bucketsRes.data.items) {
    console.log('Buckets found:');
    bucketsRes.data.items.forEach(b => console.log(` - ${b.name} (created: ${b.timeCreated})`));
  } else {
    console.log('Buckets data:', JSON.stringify(bucketsRes.data));
  }

  // 2. Check Tool Results histories
  const toolResultsRes = await fetchGoogleApi(`https://toolresults.googleapis.com/v1/projects/${projectId}/histories`);
  console.log('\nTool Results Histories status:', toolResultsRes.status);
  if (toolResultsRes.data && toolResultsRes.data.histories) {
    console.log('Histories found:');
    toolResultsRes.data.histories.forEach(h => console.log(` - ID: ${h.historyId}, Name: ${h.name}`));
  } else {
    console.log('Tool Results data:', JSON.stringify(toolResultsRes.data));
  }
}

main().catch(err => console.error(err));
