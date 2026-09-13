/**
 * MapTanim -> AWS-Architecture Style Frame 11 on Miro
 * 
 * Recreates Frame 11 with the exact AWS Miro reference architecture aesthetic:
 * - Warm canvas background (#FAF7F2)
 * - Three main service containers with solid colored borders (front end, backend, supabase cloud)
 * - Nested dashed sub-boxes (mobile client, admin console, serverless engine, database resources)
 * - Solid accent boxes (Task runner / evaluate-dss in magenta, PostgreSQL Core in purple)
 * - Black circular numbered flow step badges (1 to 10)
 * - Clean directional connector arrows
 * - Top-left title block with "Updated: September 13, 2026"
 * - Bottom-left branding footer with "Updated: September 13, 2026"
 */

const https = require('https');

const API_TOKEN = process.env.MIRO_API_TOKEN || process.argv[2] || "eyJtaXJvLm9yaWdpbiI6ImV1MDEifQ_RTwu2aHccMO7R_V5yvhcHL-FjiM";
const BOARD_ID = process.env.MIRO_BOARD_ID || process.argv[3] || "uXjVHxtgZgg=";

function miroRequest(endpoint, method = 'GET', payload = null) {
    return new Promise((resolve, reject) => {
        const dataString = payload ? JSON.stringify(payload) : null;
        const options = {
            hostname: 'api.miro.com',
            port: 443,
            path: endpoint.startsWith('/v2/') ? endpoint : `/v2/${endpoint}`,
            method: method,
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
                    try {
                        resolve(body ? JSON.parse(body) : {});
                    } catch (e) {
                        resolve(body);
                    }
                } else {
                    reject(new Error(`Miro API Error (${res.statusCode}): ${body}`));
                }
            });
        });

        req.on('error', (err) => reject(err));
        if (dataString) req.write(dataString);
        req.end();
    });
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function createFrame({ boardId, x, y, width, height, title }) {
    const payload = {
        data: {
            title: title,
            format: 'custom',
            type: 'freeform'
        },
        position: {
            origin: 'center',
            x: x,
            y: y
        },
        geometry: {
            width: width,
            height: height
        }
    };
    const res = await miroRequest(`boards/${boardId}/frames`, 'POST', payload);
    await sleep(150);
    return res;
}

async function createShape({
    boardId,
    x,
    y,
    width = 280,
    height = 120,
    content,
    shape = 'rectangle',
    fillColor = '#FFFFFF',
    textColor = '#161E2E',
    borderColor = '#E67E22',
    borderWidth = '2.0',
    borderStyle = 'normal',
    textAlign = 'center',
    textAlignVertical = 'middle'
}) {
    const validBorderWidth = (!borderWidth || borderWidth === '0') ? '1.0' : (borderWidth === '1.5' ? '2.0' : String(parseFloat(borderWidth) || 1.0));
    const validBorderColor = (!borderColor || !borderColor.startsWith('#')) ? '#FAF7F2' : borderColor;

    const payload = {
        data: {
            shape: shape,
            content: `<p>${content}</p>`
        },
        style: {
            fillColor: fillColor,
            fillOpacity: '1.0',
            textAlign: textAlign,
            textAlignVertical: textAlignVertical,
            borderColor: validBorderColor,
            borderWidth: validBorderWidth,
            borderStyle: borderStyle,
            color: textColor
        },
        position: {
            origin: 'center',
            x: x,
            y: y
        },
        geometry: {
            width: width,
            height: height
        }
    };
    const res = await miroRequest(`boards/${boardId}/shapes`, 'POST', payload);
    await sleep(100);
    return res;
}

async function createBadge({ boardId, x, y, number }) {
    return await createShape({
        boardId,
        x,
        y,
        width: 34,
        height: 34,
        shape: 'circle',
        fillColor: '#161E2E',
        textColor: '#FFFFFF',
        borderColor: '#000000',
        borderWidth: '1.0',
        content: `<strong>${number}</strong>`,
        textAlign: 'center',
        textAlignVertical: 'middle'
    });
}

async function createConnector(boardId, startItem, endItem, caption = '', color = '#545B64', strokeStyle = 'normal') {
    try {
        const payload = {
            startItem: { id: startItem.id, snapTo: 'auto' },
            endItem: { id: endItem.id, snapTo: 'auto' },
            style: {
                strokeColor: color,
                strokeWidth: '2',
                strokeStyle: strokeStyle
            },
            ...(caption ? { captions: [{ content: caption, position: '50%' }] } : {})
        };
        const res = await miroRequest(`boards/${boardId}/connectors`, 'POST', payload);
        await sleep(100);
        return res;
    } catch (e) {
        console.warn(`Connector notice: ${e.message}`);
    }
}

async function main() {
    console.log(`🚀 Connecting to Miro Board: ${BOARD_ID} to build AWS-Style Microservices Architecture...`);
    try {
        // 1. Delete all existing items with y >= 3800
        console.log('🧹 Cleaning existing Frame 11 items...');
        let hasMore = true;
        let cursor = null;
        while (hasMore) {
            const url = `boards/${BOARD_ID}/items?limit=50${cursor ? `&cursor=${cursor}` : ''}`;
            const list = await miroRequest(url, 'GET');
            const items = (list.data || []).filter(item => item.position && item.position.y >= 3800);
            for (const item of items) {
                try {
                    await miroRequest(`boards/${BOARD_ID}/items/${item.id}`, 'DELETE');
                    await sleep(50);
                } catch (e) {}
            }
            if (list.cursor) {
                cursor = list.cursor;
            } else {
                hasMore = false;
            }
        }

        // Also delete frames in that range
        const framesList = await miroRequest(`boards/${BOARD_ID}/items?type=frame&limit=50`, 'GET');
        const f11Frames = (framesList.data || []).filter(f => f.data?.title?.includes('FRAME 11'));
        for (const f of f11Frames) {
            try {
                await miroRequest(`boards/${BOARD_ID}/frames/${f.id}`, 'DELETE');
                await sleep(100);
            } catch (e) {}
        }
        console.log('✨ Cleanup complete!');

        // ══════════════════════════════════════════════════════════════════
        // MAIN FRAME 11 (AWS Architectural Blueprint Style)
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Frame 11 container (2800 x 1200)...');
        await createFrame({
            boardId: BOARD_ID,
            x: 0, y: 4420, width: 2800, height: 1200,
            title: '⚡ FRAME 11: SERVERLESS MICROSERVICES ARCHITECTURE & SYSTEM DATAFLOW'
        });

        // ── TOP-LEFT TITLE BLOCK ──────────────────────────────────────────
        console.log('👉 Creating Title Block with Update Date...');
        await createShape({
            boardId: BOARD_ID,
            x: -850, y: 3910, width: 900, height: 75,
            content: '<strong style="font-size: 22px; color: #161E2E;">MapTanim Serverless Microservices Architecture</strong><br/><span style="font-size: 13px; color: #545B64;">For the architecture description, refer to the interactive flowchart. • <strong>Updated: September 13, 2026</strong></span>',
            fillColor: '#FAF7F2',
            borderColor: '#FAF7F2',
            borderWidth: '1.0',
            textAlign: 'left',
            textAlignVertical: 'middle'
        });

        // ── TOP-RIGHT AUXILIARY CONTAINER (Bundled Assets) ─────────────────
        console.log('👉 Creating Auxiliary Asset Repository Container...');
        const assetContainer = await createShape({
            boardId: BOARD_ID,
            x: 950, y: 3930, width: 520, height: 110,
            content: '<span style="font-size: 12px; color: #545B64; text-align: center;"><strong>Local APK Bundled Assets Repository</strong></span>',
            fillColor: '#FAF8F5',
            borderColor: '#7F8C8D',
            borderWidth: '1.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        const assetCard = await createShape({
            boardId: BOARD_ID,
            x: 950, y: 3945, width: 440, height: 60,
            content: '🎨 <strong>30-50KB WebP Crop Sprites</strong><br/><span style="font-size: 11px; color: #545B64;">Local APK Cache • Zero Unsplash URLs • Instant Offline Boot</span>',
            fillColor: '#FFFFFF',
            borderColor: '#BDC3C7',
            borderWidth: '1.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // ══════════════════════════════════════════════════════════════════
        // CONTAINER 1 (LEFT): FRONT END
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Container 1: front end...');
        const frontEndBox = await createShape({
            boardId: BOARD_ID,
            x: -950, y: 4460, width: 660, height: 820,
            content: '<strong style="color: #E67E22; font-size: 16px;">front end</strong>',
            fillColor: '#FAF8F5',
            borderColor: '#E67E22',
            borderWidth: '2.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        // Sub-box 1A: mobile client (dashed)
        await createShape({
            boardId: BOARD_ID,
            x: -950, y: 4260, width: 600, height: 350,
            content: '<strong style="color: #545B64; font-size: 13px;">mobile client (Jetpack Compose)</strong>',
            fillColor: '#FFFFFF',
            borderColor: '#BDC3C7',
            borderWidth: '1.0',
            borderStyle: 'dashed',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        const mobileHomeCard = await createShape({
            boardId: BOARD_ID,
            x: -1100, y: 4280, width: 250, height: 180,
            content: '📱 <strong>HomeScreen HUD</strong><br/><span style="font-size: 11px; color: #545B64;">• Real-time Farm HUD<br/>• Active Plot Coordinates<br/>• StateFlow ViewModels<br/>• Zero GPS Tracking</span>',
            fillColor: '#E8F5E9',
            borderColor: '#2E7D32',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const mobileTaskOverlayCard = await createShape({
            boardId: BOARD_ID,
            x: -800, y: 4280, width: 250, height: 180,
            content: '📋 <strong>TodaysTasksOverlay</strong><br/><span style="font-size: 11px; color: #545B64;">• 💧 Water / 🌿 Fertilize<br/>• 🌾 Harvest / 🐛 Pest<br/>• Daily DSS Trigger on Open<br/>• 1-Tap Action Execution</span>',
            fillColor: '#E8F5E9',
            borderColor: '#1B5E20',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // Sub-box 1B: admin console (dashed)
        await createShape({
            boardId: BOARD_ID,
            x: -950, y: 4660, width: 600, height: 340,
            content: '<strong style="color: #545B64; font-size: 13px;">admin web studio (React + Vite)</strong>',
            fillColor: '#FFFFFF',
            borderColor: '#BDC3C7',
            borderWidth: '1.0',
            borderStyle: 'dashed',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        const adminCatalogCard = await createShape({
            boardId: BOARD_ID,
            x: -1100, y: 4680, width: 250, height: 180,
            content: '🖥️ <strong>Crop Catalog & Matrix</strong><br/><span style="font-size: 11px; color: #545B64;">• Strict 15 Canonical Crops<br/>• 58 Companion Rules<br/>• Viewport-Centered Modal<br/>• Empirical Field Research</span>',
            fillColor: '#E3F2FD',
            borderColor: '#1565C0',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const adminBroadcastCard = await createShape({
            boardId: BOARD_ID,
            x: -800, y: 4680, width: 250, height: 180,
            content: '📢 <strong>Broadcast Center</strong><br/><span style="font-size: 11px; color: #545B64;">• Emergency Advisory Dispatch<br/>• Pest Outbreak Warnings<br/>• Zero Location Dependency<br/>• Instant Farmer Alerting</span>',
            fillColor: '#F3E5F5',
            borderColor: '#7B1FA2',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // ══════════════════════════════════════════════════════════════════
        // CONTAINER 2 (MIDDLE): BACKEND
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Container 2: backend...');
        const backendBox = await createShape({
            boardId: BOARD_ID,
            x: -180, y: 4460, width: 740, height: 820,
            content: '<strong style="color: #E67E22; font-size: 16px;">backend</strong>',
            fillColor: '#FAF8F5',
            borderColor: '#E67E22',
            borderWidth: '2.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        // Sub-box 2A: serverless edge engine (dashed)
        await createShape({
            boardId: BOARD_ID,
            x: -180, y: 4470, width: 680, height: 740,
            content: '<strong style="color: #545B64; font-size: 13px;">serverless edge engine (Deno / TypeScript)</strong>',
            fillColor: '#FFFFFF',
            borderColor: '#BDC3C7',
            borderWidth: '1.0',
            borderStyle: 'dashed',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        // Accent Box: Task runner / evaluate-dss (Solid Magenta Border)
        await createShape({
            boardId: BOARD_ID,
            x: -180, y: 4320, width: 620, height: 380,
            content: '<strong style="color: #C2185B; font-size: 13px;">Task runner & DSS engine</strong>',
            fillColor: '#FCE4EC',
            borderColor: '#E91E63',
            borderWidth: '2.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        const evaluateDssCard = await createShape({
            boardId: BOARD_ID,
            x: -180, y: 4340, width: 560, height: 260,
            content: '⚡ <strong>evaluate-dss Edge Function</strong><br/><span style="font-size: 12px; color: #161E2E;"><code>POST /functions/v1/evaluate-dss</code></span><br/><span style="font-size: 11px; color: #37474F;">• <strong>Spatial Proximity Engine</strong>: Euclidean distance d = √((x1-x2)² + (y1-y2)²) ≤ 3.0m<br/>• <strong>5-Stage Phenology Tracker</strong>: SPROUT → SEEDLING → VEGETATIVE → FLOWERING → HARVEST<br/>• <strong>Dynamic Task Generator</strong>: WATER, FERTILIZE, HARVEST, PEST_ALERT<br/>• <strong>Zero Weather Dependencies</strong>: Pure deterministic soil & agronomic science</span>',
            fillColor: '#FFFFFF',
            borderColor: '#00796B',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const broadcastDispatcherCard = await createShape({
            boardId: BOARD_ID,
            x: -180, y: 4680, width: 620, height: 160,
            content: '⚡ <strong>broadcast-dispatcher Edge Function</strong><br/><span style="font-size: 12px; color: #161E2E;"><code>POST /functions/v1/broadcast-dispatcher</code></span><br/><span style="font-size: 11px; color: #37474F;">• Dispatches emergency agronomic bulletins & pest advisories<br/>• Security: Validated by <code>service_role</code> JWT secret key<br/>• Direct high-speed write to <code>public.notifications</code></span>',
            fillColor: '#EDE7F6',
            borderColor: '#7B1FA2',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // ══════════════════════════════════════════════════════════════════
        // CONTAINER 3 (RIGHT): REGION / SUPABASE CLOUD
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Container 3: supabase cloud...');
        const cloudBox = await createShape({
            boardId: BOARD_ID,
            x: 650, y: 4460, width: 780, height: 820,
            content: '<strong style="color: #E67E22; font-size: 16px;">supabase cloud</strong>',
            fillColor: '#FAF8F5',
            borderColor: '#E67E22',
            borderWidth: '2.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        // Sub-box 3A: regional persistence resources (dashed)
        await createShape({
            boardId: BOARD_ID,
            x: 650, y: 4470, width: 720, height: 740,
            content: '<strong style="color: #545B64; font-size: 13px;">Regional persistence resources</strong>',
            fillColor: '#FFFFFF',
            borderColor: '#BDC3C7',
            borderWidth: '1.0',
            borderStyle: 'dashed',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        // Accent Box: PostgreSQL Core (Solid Purple Border like VPC in screenshot)
        await createShape({
            boardId: BOARD_ID,
            x: 650, y: 4330, width: 660, height: 410,
            content: '<strong style="color: #4A148C; font-size: 13px;">PostgreSQL Core Database</strong>',
            fillColor: '#F3E5F5',
            borderColor: '#7B1FA2',
            borderWidth: '2.0',
            borderStyle: 'normal',
            textAlign: 'center',
            textAlignVertical: 'top'
        });

        const plotsTableCard = await createShape({
            boardId: BOARD_ID,
            x: 480, y: 4310, width: 270, height: 140,
            content: '🗄️ <strong>crop_plots & crops</strong><br/><span style="font-size: 11px; color: #545B64;">• 45×45 plot coordinates<br/>• Planted dates & crop types<br/>• Watering & fertilizing cadences<br/>• Harvest day maturity thresholds</span>',
            shape: 'can',
            fillColor: '#E1F5FE',
            borderColor: '#0288D1',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const rulesTableCard = await createShape({
            boardId: BOARD_ID,
            x: 820, y: 4310, width: 270, height: 140,
            content: '🗄️ <strong>dss_rules (Matrix)</strong><br/><span style="font-size: 11px; color: #545B64;">• 58 Companion Pairings<br/>• BENEFICIAL & ANTAGONIST<br/>• Shared pest vulnerability flags<br/>• Agronomic scientific reasoning</span>',
            shape: 'can',
            fillColor: '#E0F7FA',
            borderColor: '#0097A7',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const tasksTableCard = await createShape({
            boardId: BOARD_ID,
            x: 480, y: 4465, width: 270, height: 120,
            content: '🗄️ <strong>tasks (Today\'s Tasks)</strong><br/><span style="font-size: 11px; color: #545B64;">• Deduplicated daily ops<br/>• Water 💧, Feed 🌿, Harvest 🌾<br/>• 🐛 Antagonist Pest Alerts</span>',
            shape: 'can',
            fillColor: '#E8F5E9',
            borderColor: '#2E7D32',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        const notifsTableCard = await createShape({
            boardId: BOARD_ID,
            x: 820, y: 4465, width: 270, height: 120,
            content: '🗄️ <strong>notifications (Advisories)</strong><br/><span style="font-size: 11px; color: #545B64;">• Mass broadcasts & advisories<br/>• Push notification payloads<br/>• Unread/read status tracking</span>',
            shape: 'can',
            fillColor: '#EDE7F6',
            borderColor: '#673AB7',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // Supabase Storage Bucket
        const storageCard = await createShape({
            boardId: BOARD_ID,
            x: 650, y: 4680, width: 660, height: 140,
            content: '☁️ <strong>Supabase Object Storage (crop-images bucket)</strong><br/><span style="font-size: 11px; color: #545B64;">• 1 GB Free Tier CDN • $0 Egress Fees • OTA Image Assets for Mobile & Web</span>',
            shape: 'cloud',
            fillColor: '#E0F2F1',
            borderColor: '#00796B',
            borderWidth: '2.0',
            textAlign: 'center',
            textAlignVertical: 'middle'
        });

        // ══════════════════════════════════════════════════════════════════
        // CIRCULAR NUMBERED STEP BADGES (1 to 10)
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Numbered Flow Badges (1 - 10)...');
        // Badge 1: On TodaysTasksOverlay
        await createBadge({ boardId: BOARD_ID, x: -660, y: 4240, number: '1' });
        // Badge 2: On Entry to evaluate-dss
        await createBadge({ boardId: BOARD_ID, x: -480, y: 4280, number: '2' });
        // Badge 3: Inside evaluate-dss (Proximity Check)
        await createBadge({ boardId: BOARD_ID, x: -180, y: 4230, number: '3' });
        // Badge 4: Inside evaluate-dss (5-Stage Timeline)
        await createBadge({ boardId: BOARD_ID, x: 120, y: 4280, number: '4' });
        // Badge 5: On Write to tasks table
        await createBadge({ boardId: BOARD_ID, x: 330, y: 4440, number: '5' });
        // Badge 6: On Sync to HomeScreen HUD
        await createBadge({ boardId: BOARD_ID, x: -950, y: 4200, number: '6' });
        // Badge 7: On Broadcast Center UI
        await createBadge({ boardId: BOARD_ID, x: -660, y: 4680, number: '7' });
        // Badge 8: On Entry to broadcast-dispatcher
        await createBadge({ boardId: BOARD_ID, x: -500, y: 4680, number: '8' });
        // Badge 9: On Write to notifications table
        await createBadge({ boardId: BOARD_ID, x: 670, y: 4600, number: '9' });
        // Badge 10: On Bundled Asset sync
        await createBadge({ boardId: BOARD_ID, x: 700, y: 3980, number: '10' });

        // ══════════════════════════════════════════════════════════════════
        // DIRECTIONAL CONNECTORS & ARROWS
        // ══════════════════════════════════════════════════════════════════
        console.log('👉 Creating Directional Dataflow Connectors...');
        // 1. Mobile triggers evaluate-dss
        await createConnector(BOARD_ID, mobileTaskOverlayCard, evaluateDssCard, 'POST /evaluate-dss', '#2E7D32');
        // 2. evaluate-dss reads plots & crops
        await createConnector(BOARD_ID, evaluateDssCard, plotsTableCard, 'Read Plots & Intervals', '#0288D1');
        // 3. evaluate-dss checks companion matrix
        await createConnector(BOARD_ID, evaluateDssCard, rulesTableCard, 'Query Companion Rules', '#0097A7');
        // 4. evaluate-dss upserts tasks
        await createConnector(BOARD_ID, evaluateDssCard, tasksTableCard, 'Upsert Daily Tasks', '#2E7D32');
        // 5. tasks table streams back to Mobile HUD
        await createConnector(BOARD_ID, tasksTableCard, mobileHomeCard, 'Real-time Task Stream', '#1565C0');
        // 6. Admin triggers broadcast-dispatcher
        await createConnector(BOARD_ID, adminBroadcastCard, broadcastDispatcherCard, 'POST /broadcast-dispatcher', '#7B1FA2');
        // 7. broadcast-dispatcher inserts to notifications
        await createConnector(BOARD_ID, broadcastDispatcherCard, notifsTableCard, 'Insert Alerts', '#673AB7');
        // 8. notifications streams to Mobile HUD
        await createConnector(BOARD_ID, notifsTableCard, mobileHomeCard, 'Push Advisory', '#E67E22');
        // 9. Bundled assets to mobile client
        await createConnector(BOARD_ID, assetCard, mobileHomeCard, 'Bundled Sprites', '#7F8C8D');
        // 10. Storage bucket OTA sync
        await createConnector(BOARD_ID, storageCard, adminCatalogCard, 'OTA Sync', '#00796B');

        // ── BOTTOM-LEFT FOOTER ────────────────────────────────────────────
        console.log('👉 Creating Footer with Update Date...');
        await createShape({
            boardId: BOARD_ID,
            x: -850, y: 4940, width: 850, height: 50,
            content: '🌱 <strong style="color: #2E7D32;">MapTanim Agroecological Platform</strong> • <span>Zero Weather APIs • Zero GPS Tracking • 15 Canonical Crops</span><br/><span style="font-size: 11px; color: #7F8C8D;">© 2026 MapTanim Project. All rights reserved. • <strong>Updated: September 13, 2026</strong></span>',
            fillColor: '#FAF7F2',
            borderColor: '#FAF7F2',
            borderWidth: '1.0',
            textAlign: 'left',
            textAlignVertical: 'middle'
        });

        console.log(`
=============================================================================
🎉 SUCCESS: Frame 11 recreated in exact AWS Reference Architecture Style!
📅 Date of Update: September 13, 2026
🔗 View live on Miro:
   https://miro.com/app/board/${BOARD_ID}/
=============================================================================
`);

    } catch (err) {
        console.error('❌ Error recreating AWS-style Frame 11 on Miro:', err.message);
    }
}

main();
