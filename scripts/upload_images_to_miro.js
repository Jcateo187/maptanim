/**
 * Upload images to Supabase Storage bucket and return public URLs.
 * Then use those URLs to create image items on Miro board.
 */
const https = require('https');
const fs = require('fs');
const path = require('path');

const MIRO_TOKEN = "eyJtaXJvLm9yaWdpbiI6ImV1MDEifQ_RTwu2aHccMO7R_V5yvhcHL-FjiM";
const BOARD_ID = "uXjVHxtgZgg=";

const BASE_DIR = "C:\\Users\\james cateo\\.gemini\\antigravity-ide\\brain\\f1c602f1-dfea-4de7-8ee0-63e59fb46c2e";

// Image files to upload
const IMAGE_FILES = {
    admin_dashboard: path.join(BASE_DIR, "admin_dashboard_loaded_1789284598454.png"),
    admin_seasonal: path.join(BASE_DIR, "dss_seasonal_schedules_1789284622797.png"),
    admin_croplibrary: path.join(BASE_DIR, "crop_library_catalog_1789284993836.png"),
    admin_community: path.join(BASE_DIR, "community_moderation_reports_1789285070187.png"),
    mobile_monitoring: path.join(BASE_DIR, "mobile_monitoring_1789284938532.jpg"),
    mobile_farm: path.join(BASE_DIR, "mobile_isometric_farm_1789284952660.jpg"),
};

async function main() {
    console.log("🖼️ Checking image files...");
    for (const [key, filepath] of Object.entries(IMAGE_FILES)) {
        if (fs.existsSync(filepath)) {
            const stat = fs.statSync(filepath);
            console.log(`  ✅ ${key}: ${(stat.size / 1024).toFixed(0)}KB`);
        } else {
            console.log(`  ❌ ${key}: MISSING`);
        }
    }

    // Upload images to Miro using createImageFromDevice (multipart upload)
    console.log("\n📤 Uploading images to Miro board as image items...");

    const imagePositions = {
        admin_dashboard:   { x: -1700, y: 8050, label: "Admin Dashboard" },
        admin_seasonal:    { x: -580,  y: 8050, label: "DSS Seasonal Hub" },
        admin_croplibrary: { x: 540,   y: 8050, label: "Crop Library" },
        admin_community:   { x: 1660,  y: 8050, label: "Community Moderation" },
        mobile_monitoring: { x: -580,  y: 8550, label: "Mobile Monitoring" },
        mobile_farm:       { x: 540,   y: 8550, label: "Mobile Isometric Farm" },
    };

    for (const [key, filepath] of Object.entries(IMAGE_FILES)) {
        if (!fs.existsSync(filepath)) continue;
        const pos = imagePositions[key];
        
        try {
            const fileData = fs.readFileSync(filepath);
            const ext = path.extname(filepath).toLowerCase();
            const mime = ext === '.png' ? 'image/png' : 'image/jpeg';
            const filename = path.basename(filepath);
            
            const boundary = '----MiroBoundary' + Date.now();
            
            // Build multipart body
            const parts = [];
            
            // Resource part (file)
            parts.push(`--${boundary}\r\n`);
            parts.push(`Content-Disposition: form-data; name="resource"; filename="${filename}"\r\n`);
            parts.push(`Content-Type: ${mime}\r\n\r\n`);
            
            const headerBuf = Buffer.from(parts.join(''));
            const fileBuf = fileData;
            
            // Data part (JSON metadata)
            const dataJson = JSON.stringify({
                title: pos.label,
                position: { origin: 'center', x: pos.x, y: pos.y },
                geometry: { width: 900 }
            });
            
            const dataPart = Buffer.from(
                `\r\n--${boundary}\r\n` +
                `Content-Disposition: form-data; name="data"\r\n` +
                `Content-Type: application/json\r\n\r\n` +
                dataJson +
                `\r\n--${boundary}--\r\n`
            );
            
            const body = Buffer.concat([headerBuf, fileBuf, dataPart]);
            
            await new Promise((resolve, reject) => {
                const req = https.request({
                    hostname: 'api.miro.com',
                    port: 443,
                    path: `/v2/boards/${BOARD_ID}/images`,
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${MIRO_TOKEN}`,
                        'Content-Type': `multipart/form-data; boundary=${boundary}`,
                        'Content-Length': body.length
                    }
                }, (res) => {
                    let d = '';
                    res.on('data', (c) => d += c);
                    res.on('end', () => {
                        if (res.statusCode >= 200 && res.statusCode < 300) {
                            console.log(`  ✅ ${pos.label} uploaded (${res.statusCode})`);
                            resolve();
                        } else {
                            console.log(`  ⚠️ ${pos.label} status ${res.statusCode}: ${d.substring(0, 200)}`);
                            resolve(); // Don't fail, continue
                        }
                    });
                });
                req.on('error', (e) => { console.log(`  ❌ ${pos.label}: ${e.message}`); resolve(); });
                req.write(body);
                req.end();
            });
            
            // Rate limit
            await new Promise(r => setTimeout(r, 300));
        } catch (e) {
            console.log(`  ❌ ${pos.label} error: ${e.message}`);
        }
    }

    console.log("\n✅ Image upload process complete!");
}

main();
