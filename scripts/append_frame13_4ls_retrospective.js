/**
 * MapTanim -> Frame 13: 4Ls RETROSPECTIVE (Two-Box Connected Style)
 * 
 * Each function = LEFT box (file/function name) → arrow → RIGHT box (what it does)
 * 4 Columns: LIKED | LEARNED | LACKED | LONGED FOR
 * Only real admin web screenshots (no generated mobile mockups)
 * 
 * Date: September 13, 2026
 */

const https = require('https');
const fs = require('fs');
const path = require('path');

const API_TOKEN = "eyJtaXJvLm9yaWdpbiI6ImV1MDEifQ_RTwu2aHccMO7R_V5yvhcHL-FjiM";
const BOARD_ID = "uXjVHxtgZgg=";
const BASE_DIR = "C:\\Users\\james cateo\\.gemini\\antigravity-ide\\brain\\f1c602f1-dfea-4de7-8ee0-63e59fb46c2e";

function miroRequest(endpoint, method = 'GET', payload = null) {
    return new Promise((resolve, reject) => {
        const dataString = payload ? JSON.stringify(payload) : null;
        const options = {
            hostname: 'api.miro.com', port: 443,
            path: endpoint.startsWith('/v2/') ? endpoint : `/v2/${endpoint}`,
            method,
            headers: {
                'Authorization': `Bearer ${API_TOKEN}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...(dataString ? { 'Content-Length': Buffer.byteLength(dataString) } : {})
            }
        };
        const req = https.request(options, (res) => {
            let body = '';
            res.on('data', (chunk) => body += chunk);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    try { resolve(body ? JSON.parse(body) : {}); } catch (e) { resolve(body); }
                } else { reject(new Error(`Miro ${res.statusCode}: ${body}`)); }
            });
        });
        req.on('error', reject);
        if (dataString) req.write(dataString);
        req.end();
    });
}

const sleep = (ms) => new Promise(r => setTimeout(r, ms));

async function createFrame({ x, y, width, height, title }) {
    const res = await miroRequest(`boards/${BOARD_ID}/frames`, 'POST', {
        data: { title, format: 'custom', type: 'freeform' },
        position: { origin: 'center', x, y },
        geometry: { width, height }
    });
    await sleep(200);
    return res;
}

async function box({ x, y, w, h, content, shp = 'round_rectangle',
    fill = '#FFFFFF', text = '#1A1A1A', border = '#E0E0E0', bw = '2.0',
    align = 'left', valign = 'middle' }) {
    const payload = {
        data: { shape: shp, content: `<p>${content}</p>` },
        style: {
            fillColor: fill, fillOpacity: '1.0', textAlign: align,
            textAlignVertical: valign, borderColor: border,
            borderWidth: bw === '1.5' ? '2.0' : String(parseFloat(bw) || 1.0),
            borderStyle: 'normal', color: text
        },
        position: { origin: 'center', x, y },
        geometry: { width: w, height: h }
    };
    const res = await miroRequest(`boards/${BOARD_ID}/shapes`, 'POST', payload);
    await sleep(80);
    return res;
}

async function connect(startId, endId, color = '#555555') {
    try {
        const res = await miroRequest(`boards/${BOARD_ID}/connectors`, 'POST', {
            startItem: { id: startId, snapTo: 'right' },
            endItem: { id: endId, snapTo: 'left' },
            style: { strokeColor: color, strokeWidth: '2', strokeStyle: 'normal' },
            shape: 'elbowed'
        });
        await sleep(80);
        return res;
    } catch (e) {
        console.log(`     connector note: ${e.message.substring(0, 80)}`);
    }
}

async function uploadImage(filePath, x, y, width = 550) {
    const fileData = fs.readFileSync(filePath);
    const ext = path.extname(filePath).toLowerCase();
    const mime = ext === '.png' ? 'image/png' : 'image/jpeg';
    const filename = path.basename(filePath);
    const boundary = '----Miro' + Date.now() + Math.random().toString(36).substring(2);

    const dataJson = JSON.stringify({
        title: filename,
        position: { origin: 'center', x, y },
        geometry: { width }
    });

    const headerBuf = Buffer.from(
        `--${boundary}\r\nContent-Disposition: form-data; name="resource"; filename="${filename}"\r\nContent-Type: ${mime}\r\n\r\n`
    );
    const dataBuf = Buffer.from(
        `\r\n--${boundary}\r\nContent-Disposition: form-data; name="data"\r\nContent-Type: application/json\r\n\r\n${dataJson}\r\n--${boundary}--\r\n`
    );
    const body = Buffer.concat([headerBuf, fileData, dataBuf]);

    return new Promise((resolve) => {
        const req = https.request({
            hostname: 'api.miro.com', port: 443,
            path: `/v2/boards/${BOARD_ID}/images`, method: 'POST',
            headers: {
                'Authorization': `Bearer ${API_TOKEN}`,
                'Content-Type': `multipart/form-data; boundary=${boundary}`,
                'Content-Length': body.length
            }
        }, (res) => {
            let d = '';
            res.on('data', (c) => d += c);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    try { resolve(JSON.parse(d)); } catch (e) { resolve(d); }
                } else {
                    console.log(`  ⚠️ img ${res.statusCode}: ${d.substring(0, 100)}`);
                    resolve(null);
                }
            });
        });
        req.on('error', () => resolve(null));
        req.write(body);
        req.end();
    });
}

// ═══════════════════════════════════════════════════════════════
// 4Ls DATA — Each item: LEFT (file + func) → RIGHT (job)
// ═══════════════════════════════════════════════════════════════

const LIKED = [
    { file: 'MonitoringDashboardOverlay.kt', func: 'CropSelectionGridCard()', job: 'Full-width 4-column landscape grid with plot badges (Soil Type, Season, Progress Bar). Removed 190dp sidebar.' },
    { file: 'MonitoringViewModel.kt', func: 'loadData()', job: 'Combines cropPlotRepository + cropRepository flows. Emits only active planted crops via StateFlow.' },
    { file: 'DssEngine.kt', func: 'evaluateCompanions()', job: 'Calculates Euclidean distance between plots. Flags antagonist pairs within 3.0m radius. Zero AI.' },
    { file: 'FarmCanvasRenderer.kt', func: 'renderGridOverlay()', job: 'Procedural 2D isometric diamond grid. Zero DB tile rows. Canvas-only math at 60fps.' },
    { file: 'evaluate-dss/index.ts', func: 'handlePost()', job: 'Serverless edge function. Evaluates DSS rules for all farms. Writes tasks to public.tasks table.' },
    { file: 'AppDatabase.kt', func: 'Migration 020', job: 'Purged 5 legacy tables (planting_monitors, beds, tile_plantings). 10 core tables locked.' },
    { file: 'CropMetadataAssetDataSource.kt', func: 'resolveCropImage()', job: 'Resolves bundled WebP crop images from APK assets. Zero cloud cost. 100% offline.' },
    { file: 'HomeScreen.kt', func: 'IsometricFarmView()', job: 'Main farm canvas composable. Touch gestures, zoom, pan. Plot tap → plant or monitor crop.' },
];

const LEARNED = [
    { file: 'MonitoringDashboardOverlay.kt', func: 'NavSectionItem enum', job: 'Removed SOIL_TYPES & SEASONAL entries. Monitoring is strictly for active plots, not catalog browsing.' },
    { file: 'MonitoringViewModel.kt', func: 'getFilteredCrops()', job: 'In-memory filtering on plantedCrops. Multi-attribute search: cropName, localName, plotLabel.' },
    { file: 'IsometricProjection.kt', func: 'cartesianToIsometric()', job: 'Screen-to-world math computed natively on device. Faster than DB-backed tile lookups.' },
    { file: 'CropRepositoryImpl.kt', func: 'syncFromRemote()', job: 'UDF pattern: Room cache first, then background Supabase fetch. Never blocks UI on network.' },
    { file: 'DSSRuleEditor.tsx', func: 'CompanionMatrix', job: 'Companion rules must be symmetric: if A→B antagonist, B→A must also be inserted.' },
    { file: 'broadcast-dispatcher/', func: 'dispatchNotifications()', job: 'Pushes urgent broadcast advisories to all farmers. Decoupled from DSS evaluation.' },
    { file: 'api.ts', func: 'mockFallback()', job: 'Admin API falls back to mock data during DB maintenance. Prevents crashes and blank pages.' },
    { file: 'SoilSuitabilityScorer.kt', func: 'calculateScore()', job: 'DA lookup tables (idealSoils, suitableSoils). No AI scores. Pure deterministic agronomy.' },
];

const LACKED = [
    { file: 'CropRepositoryImpl.kt', func: 'syncFromRemote()', job: 'No incremental delta sync (last_synced_at). Currently full-table upsert on every sync.' },
    { file: 'HarvestRepositoryImpl.kt', func: 'recordHarvest()', job: 'Was Room-only initially. Had to add HarvestRemoteDataSource for Supabase push.' },
    { file: 'MonitoringViewModel.kt', func: 'getFilteredCrops()', job: 'Missing "Harvest Ready First" urgency sort. Farmers can\'t prioritize near-harvest plots.' },
    { file: 'CommunityViewModel.kt', func: 'loadPosts()', job: 'Loads only latest 50 posts. No pagination or infinite scroll for community forums.' },
    { file: 'FeedbackManagement.tsx', func: 'ticketList', job: 'No email notification when urgent farmer ticket is submitted to admin.' },
    { file: 'evaluate-dss/index.ts', func: 'pg_cron trigger', job: 'No automated midnight cron inside Supabase. Admin must manually invoke evaluate-dss.' },
    { file: 'admin/vercel.json', func: 'CI/CD pipeline', job: 'No Cypress or Playwright E2E tests on Vercel PR preview deployments.' },
    { file: '020_cleanup.sql', func: 'docs linter', job: 'No automated linter to detect obsolete schema terms (beds, activities) in .md files.' },
];

const LONGEDFOR = [
    { file: 'FarmCanvasRenderer.kt', func: 'onPlotTap()', job: 'Canvas tap → opens DSS monitoring panel directly. Deep link isometric plot to Screen 2.' },
    { file: 'MonitoringViewModel.kt', func: 'realtimePush()', job: 'WebSocket / Supabase Realtime push. Instant mobile HUD badge on Admin broadcast.' },
    { file: 'FeedbackDialog.kt', func: 'replyThread()', job: 'In-app feedback reply: farmer sees admin resolution note on mobile.' },
    { file: 'CropLibrary.tsx', func: 'audioGuide()', job: 'Audio pronunciation guide for crop varieties in Tagalog and regional dialects.' },
    { file: 'DSSRuleEditor.tsx', func: 'exportReport()', job: 'Export DSS simulation results as PDF/CSV for DA record-keeping.' },
    { file: 'admin/src/auth/', func: 'RBAC system', job: 'Role-based access: Super Admin vs. Regional Observer. Different permissions.' },
    { file: 'HomeScreen.kt', func: 'sunlightMode()', job: 'High-contrast sunlight readability mode for outdoor mobile farming.' },
    { file: 'CommunityForumScreen.kt', func: 'photoAttach()', job: 'Photo attachment: farmers snap diseased leaf photos into community forum.' },
];

// ═══════════════════════════════════════════════════════════════
// MAIN
// ═══════════════════════════════════════════════════════════════
async function main() {
    console.log('🚀 Rebuilding Frame 13: 4Ls Retrospective (Two-Box Connected Style)...');

    try {
        // ──── 1. CLEAN old Frame 13 items (y >= 7200) ────
        console.log('🧹 Cleaning existing Frame 13 items (y >= 7200)...');
        let hasMore = true, cursor = null, cleaned = 0;
        while (hasMore) {
            const url = `boards/${BOARD_ID}/items?limit=50${cursor ? `&cursor=${cursor}` : ''}`;
            const list = await miroRequest(url, 'GET');
            const items = (list.data || []).filter(i => i.position && i.position.y >= 7200);
            for (const item of items) {
                try { await miroRequest(`boards/${BOARD_ID}/items/${item.id}`, 'DELETE'); await sleep(40); cleaned++; } catch (e) {}
            }
            cursor = list.cursor || null;
            hasMore = !!cursor;
        }
        console.log(`   Cleaned ${cleaned} items.`);

        // ──── 2. FRAME ────
        const FX = -250;
        const FY = 9600;
        const FW = 4400;
        const FH = 5200;

        console.log('🖼️ Creating Frame 13...');
        await createFrame({ x: FX, y: FY, width: FW, height: FH,
            title: '🔄 FRAME 13: 4Ls RETROSPECTIVE — LIKED • LEARNED • LACKED • LONGED FOR'
        });

        // Background
        await box({ x: FX, y: FY, w: FW, h: FH, shp: 'rectangle',
            fill: '#FAFAFA', border: '#E0E0E0', bw: '2.0', content: '', align: 'center', valign: 'middle'
        });

        // ──── 3. TITLE ────
        const TOP_Y = FY - (FH / 2) + 70;
        await box({ x: FX, y: TOP_Y, w: 4200, h: 80, fill: '#1B2317', border: '#4CAF50',
            text: '#FFFFFF', align: 'center', valign: 'middle',
            content: '<strong>🔄 FRAME 13: 4Ls RETROSPECTIVE — MapTanim Full System Audit</strong><br/>' +
                '<span style="font-size: 12px; color: #81C784;">September 13, 2026 &nbsp;•&nbsp; 94% System Maturity &nbsp;•&nbsp; Zero AI &nbsp;•&nbsp; Zero IoT &nbsp;•&nbsp; 100% Deterministic Agronomy</span>'
        });

        // ──── 4. COLUMN HEADERS ────
        const COL_HDR_Y = TOP_Y + 80;
        const COL_W = 1020;
        const COL_GAP = 30;
        const TOTAL_COLS_W = 4 * COL_W + 3 * COL_GAP;
        const COL_START_X = FX - TOTAL_COLS_W / 2 + COL_W / 2;

        const colDefs = [
            { label: '👍 LIKED', sub: 'What went well', hdrFill: '#2E7D32', leftFill: '#FFF9C4', leftBorder: '#F9A825', rightFill: '#FFCCBC', rightBorder: '#E64A19', arrowColor: '#2E7D32' },
            { label: '🧠 LEARNED', sub: 'Key insights', hdrFill: '#1565C0', leftFill: '#E3F2FD', leftBorder: '#1E88E5', rightFill: '#B3E5FC', rightBorder: '#0277BD', arrowColor: '#1565C0' },
            { label: '🔍 LACKED', sub: 'Gaps & bottlenecks', hdrFill: '#E65100', leftFill: '#FFF3E0', leftBorder: '#FB8C00', rightFill: '#FFE0B2', rightBorder: '#E65100', arrowColor: '#E65100' },
            { label: '🚀 LONGED FOR', sub: 'Future aspirations', hdrFill: '#6A1B9A', leftFill: '#F3E5F5', leftBorder: '#AB47BC', rightFill: '#E1BEE7', rightBorder: '#7B1FA2', arrowColor: '#6A1B9A' }
        ];

        for (let i = 0; i < colDefs.length; i++) {
            const col = colDefs[i];
            const cx = COL_START_X + i * (COL_W + COL_GAP);
            await box({ x: cx, y: COL_HDR_Y, w: COL_W, h: 55, fill: col.hdrFill, border: col.hdrFill,
                text: '#FFFFFF', align: 'center', valign: 'middle',
                content: `<strong>${col.label}</strong> — ${col.sub}`
            });
        }

        // ──── 5. TWO-BOX CONNECTED STICKY NOTES ────
        console.log('📝 Building two-box connected sticky notes...');
        const allData = [LIKED, LEARNED, LACKED, LONGEDFOR];

        const LEFT_W = 260;
        const RIGHT_W = 340;
        const BOX_H = 110;
        const PAIR_GAP_X = 10;
        const ROW_GAP = 18;
        const NOTES_START_Y = COL_HDR_Y + 55;

        for (let ci = 0; ci < allData.length; ci++) {
            const notes = allData[ci];
            const col = colDefs[ci];
            const colCenterX = COL_START_X + ci * (COL_W + COL_GAP);

            // Left box center X and Right box center X within the column
            const leftX = colCenterX - (RIGHT_W / 2) - (PAIR_GAP_X / 2);
            const rightX = colCenterX + (LEFT_W / 2) + (PAIR_GAP_X / 2);

            for (let ni = 0; ni < notes.length; ni++) {
                const note = notes[ni];
                const ny = NOTES_START_Y + ni * (BOX_H + ROW_GAP) + BOX_H / 2;

                // LEFT BOX — file name + function
                const leftBox = await box({
                    x: leftX, y: ny, w: LEFT_W, h: BOX_H,
                    fill: col.leftFill, border: col.leftBorder, bw: '2.0',
                    text: '#1A1A1A', align: 'left', valign: 'middle',
                    content: `<strong style="font-size: 12px;">${note.file}</strong><br/><span style="font-size: 11px; color: ${col.hdrFill};">${note.func}</span>`
                });

                // RIGHT BOX — job description
                const rightBox = await box({
                    x: rightX, y: ny, w: RIGHT_W, h: BOX_H,
                    fill: col.rightFill, border: col.rightBorder, bw: '2.0',
                    text: '#333333', align: 'left', valign: 'middle',
                    content: `<span style="font-size: 11px;">${note.job}</span>`
                });

                // CONNECTOR arrow: left → right
                if (leftBox && leftBox.id && rightBox && rightBox.id) {
                    await connect(leftBox.id, rightBox.id, col.arrowColor);
                }
            }
            console.log(`   ✅ ${col.label} — ${notes.length} pairs built`);
        }

        // ──── 6. APP SCREENSHOTS SECTION (Admin Web Only) ────
        console.log('🖼️ Uploading admin web screenshots...');

        const IMG_SECTION_Y = NOTES_START_Y + 8 * (BOX_H + ROW_GAP) + 80;

        await box({ x: FX, y: IMG_SECTION_Y, w: 4200, h: 50, fill: '#1B2317', border: '#4CAF50',
            text: '#FFFFFF', align: 'center', valign: 'middle',
            content: '<strong>🖥️ ADMIN WEB STUDIO SCREENSHOTS — Live Application Captures</strong>'
        });

        const adminImages = [
            { file: 'admin_dashboard_loaded_1789284598454.png', label: '🖥️ Analytics Dashboard' },
            { file: 'dss_seasonal_schedules_1789284622797.png', label: '🖥️ DSS Seasonal Hub' },
            { file: 'crop_library_catalog_1789284993836.png', label: '🖥️ Crop Library' },
            { file: 'community_moderation_reports_1789285070187.png', label: '🖥️ Community Moderation' },
        ];

        const IMG_W = 900;
        const IMG_GAP = 60;
        const IMG_COLS = 2;
        const IMG_ROW_H = 550;
        const imgStartX = FX - ((IMG_COLS - 1) * (IMG_W + IMG_GAP)) / 2;

        for (let i = 0; i < adminImages.length; i++) {
            const img = adminImages[i];
            const col = i % IMG_COLS;
            const row = Math.floor(i / IMG_COLS);
            const ix = imgStartX + col * (IMG_W + IMG_GAP);
            const iy = IMG_SECTION_Y + 70 + row * IMG_ROW_H;

            const filepath = path.join(BASE_DIR, img.file);
            if (fs.existsSync(filepath)) {
                // Label
                await box({ x: ix, y: iy, w: IMG_W, h: 35, fill: '#37474F', border: '#37474F',
                    text: '#FFFFFF', align: 'center', valign: 'middle',
                    content: `<strong>${img.label}</strong>`
                });
                // Image
                const result = await uploadImage(filepath, ix, iy + 230, IMG_W);
                console.log(`   ${result ? '✅' : '⚠️'} ${img.label}`);
                await sleep(300);
            }
        }

        // ──── 7. FOOTER ────
        const FOOTER_Y = IMG_SECTION_Y + 70 + 2 * IMG_ROW_H + 30;
        await box({ x: FX, y: FOOTER_Y, w: 4200, h: 48, fill: '#1B2317', border: '#4CAF50',
            text: '#FFFFFF', align: 'center', valign: 'middle',
            content: '<strong>MapTanim 4Ls Retrospective</strong> • September 13, 2026 • 32 File/Function Pairs • 4 Admin Screenshots • 94% Maturity • Plot-First Deterministic Agronomy'
        });

        console.log('\n✅ Frame 13 rebuilt successfully (Two-Box Connected Style)!');
    } catch (e) {
        console.error('❌ Error:', e);
    }
}

main();
