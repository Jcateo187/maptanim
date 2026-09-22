/**
 * MapTanim -> Unified "Done" Frame Generator
 * 
 * Creates the clean standalone Frame:
 * "Done (Mga Natapos at Gumagana — Unified Architecture Flowchart)"
 * 
 * Styled identically to Frame: Lack:
 * - Parent Component on the LEFT (Yellow #FFF59D)
 * - Completed Feature Cards on the RIGHT (Green #C8E6C9, stacked vertically)
 * - Clean curved connectors with ZERO text labels
 * - Placed at X = 2800, Y = 9600 (side-by-side with Frame: Lack at X = 5200, Y = 9600)
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
            timeout: 8000,
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

        req.on('timeout', () => {
            req.destroy();
            reject(new Error('Miro request timed out'));
        });

        req.on('error', (err) => reject(err));
        if (dataString) req.write(dataString);
        req.end();
    });
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function createFrame({ x, y, width, height, title }) {
    const res = await miroRequest(`boards/${BOARD_ID}/frames`, 'POST', {
        data: { title, format: 'custom', type: 'freeform' },
        position: { origin: 'center', x, y },
        geometry: { width, height }
    });
    await sleep(150);
    return res;
}

async function box({ x, y, w, h, content, shp = 'round_rectangle',
    fill = '#FFFFFF', text = '#1A1A1A', border = '#E0E0E0', bw = '2.0',
    align = 'center', valign = 'middle' }) {
    const payload = {
        data: { shape: shp, content: `<p>${content}</p>` },
        style: {
            fillColor: fill,
            fillOpacity: '1.0',
            textAlign: align,
            textAlignVertical: valign,
            borderColor: border,
            borderWidth: bw === '1.5' ? '2.0' : String(parseFloat(bw) || 1.0),
            borderStyle: 'normal',
            color: text
        },
        position: { origin: 'center', x, y },
        geometry: { width: w, height: h }
    };
    const res = await miroRequest(`boards/${BOARD_ID}/shapes`, 'POST', payload);
    await sleep(80);
    return res;
}

async function connect(startId, endId, color = '#2E7D32') {
    try {
        const res = await miroRequest(`boards/${BOARD_ID}/connectors`, 'POST', {
            startItem: { id: startId, snapTo: 'right' },
            endItem: { id: endId, snapTo: 'left' },
            style: { strokeColor: color, strokeWidth: '2', strokeStyle: 'normal' },
            shape: 'curved'
        });
        await sleep(80);
        return res;
    } catch (e) {
        console.log(`     connector note: ${e.message.substring(0, 80)}`);
    }
}

async function main() {
    console.log('🚀 Generating Unified "Done" Frame on Miro...');
    console.log(`Board ID: ${BOARD_ID}`);

    // Coordinates setup for Frame: Done (sitting directly left of Frame: Lack)
    const FRAME_X = 2700;
    const FRAME_Y = 9600;
    const FRAME_W = 2100;
    const FRAME_H = 3400;

    console.log('🖼️ Creating Frame: Done (Mga Natapos at Gumagana)...');
    try {
        await createFrame({
            x: FRAME_X,
            y: FRAME_Y,
            width: FRAME_W,
            height: FRAME_H,
            title: 'Done (Mga Natapos at Gumagana — Unified Architecture Flowchart)'
        });
        console.log('   Frame: Done created successfully.');
    } catch (e) {
        console.log('   Frame notice:', e.message);
    }

    // Frame background shape (Soft Mint Green)
    await box({
        x: FRAME_X,
        y: FRAME_Y,
        w: FRAME_W - 40,
        h: FRAME_H - 40,
        shp: 'round_rectangle',
        fill: '#E8F5E9',
        border: '#81C784',
        bw: '3.0',
        content: ''
    });

    // Unified Done Architecture Structure (Combined from Frames 1-13)
    const sections = [
        {
            title: 'Mobile App',
            items: [
                'HomeScreen: Real-time Farm HUD, Active Plot Status & Weather-Free Advice',
                'FarmEditorScreen: 2D Isometric Grid, Tap-to-Plant & Drag Crop Tray',
                'CropDetailDialog: 6-Hakbang Agronomic Steps, Variety Engine & Why Science',
                'Community Forum: User-Authored Posts, Reaction Stream & Profile Activity',
                'Auth & Preferences: Remember Me Persistence & Multi-Farm State Isolation'
            ]
        },
        {
            title: 'Core Engines',
            items: [
                '2D Isometric Math: Diamond Tile Projection, Grid Snapping & Depth Z-Ordering',
                'Compose Vector Badges: Dynamic Pins (Water, Fertilizer, Harvest, Weeding)',
                'Crop Variety Engine: Dynamic 5-Stage Growth Timeline & Certified Cultivars',
                'Visual Agronomic Rulers: Bed/Furrow (20-30cm dig) & Trellis (1.5-2.0m)',
                'Companion Matrix: Synergistic & Antagonistic Plant Pairing Rules'
            ]
        },
        {
            title: 'Admin Web',
            items: [
                'Crop Library: Canonical 15 Philippine Crops Management & Metadata Editing',
                'Mobile Live Preview: Viewport-Centered Modal & Dynamic Hakbang Simulation',
                'Trellis & Planting Inputs: Configurable Trellis Type & Bed Preparation',
                'Farmer Inquiries & Feedback: Real-time Ticket Resolution Dashboard',
                'Storage Pipeline: Direct WebP Asset Uploads to Supabase Object Storage'
            ]
        },
        {
            title: 'Monitoring & Tasks',
            items: [
                'Operational Monitoring: Plot Lifecycle Stage 1-5 & Health Score (%)',
                "Today's Tasks: Daily Farmer Action Checklist (Dilig, Damo, Pataba, Balag)",
                'Separation of Concerns: Action Checklist vs Health Tracking Navigation',
                'Post-Harvest Records: Crop Cycle Archiving & Yield Logging Foundation',
                'Zero AI & Zero IoT: 100% Deterministic Empirical Agronomic Science'
            ]
        },
        {
            title: 'Database & Cloud',
            items: [
                'Local Room DB (SQLite): Offline-First DAO with Transactional Rollback',
                'Supabase PostgreSQL: Relational Schema (crops, varieties, pests, plots)',
                'Row-Level Security (RLS): Multi-Tenant Authentication & Session Guard',
                'Migration 021: task_type_enum, crop_rotation_log & practical agronomy',
                'Privacy-First Decoupled: Zero GPS Tracking & Zero External Weather APIs'
            ]
        }
    ];

    let currentY = FRAME_Y - (FRAME_H / 2) + 240;
    const LEFT_X = FRAME_X - 560;
    const RIGHT_X = FRAME_X + 200;

    for (let sIdx = 0; sIdx < sections.length; sIdx++) {
        const sec = sections[sIdx];
        const numItems = sec.items.length;
        const itemH = 75;
        const itemGap = 14;
        const totalItemsH = numItems * itemH + (numItems - 1) * itemGap;
        const sectionCenterY = currentY + (totalItemsH / 2);

        console.log(`📌 Building Done Section ${sIdx + 1}: ${sec.title} (${numItems} items)...`);

        // 1. Yellow Parent Box (LEFT)
        const parentRes = await box({
            x: LEFT_X,
            y: sectionCenterY,
            w: 240,
            h: Math.max(140, Math.min(240, totalItemsH * 0.75)),
            content: `<strong><span style="font-size: 16px;">${sec.title}</span></strong>`,
            fill: '#FFF59D',
            border: '#FBC02D',
            bw: '2.5',
            text: '#1B1B1B',
            align: 'center',
            valign: 'middle'
        });

        // 2. Green Child Boxes (RIGHT, Stacked Vertically - Done State)
        for (let iIdx = 0; iIdx < numItems; iIdx++) {
            const itemText = sec.items[iIdx];
            const itemY = currentY + (iIdx * (itemH + itemGap)) + (itemH / 2);

            const childRes = await box({
                x: RIGHT_X,
                y: itemY,
                w: 680,
                h: itemH,
                content: `<span style="font-size: 13px;">${itemText}</span>`,
                fill: '#C8E6C9',
                border: '#4CAF50',
                bw: '2.0',
                text: '#1B3B22',
                align: 'left',
                valign: 'middle'
            });

            // 3. Connect Parent -> Child with clean curved arrow (NO text labels)
            if (parentRes && parentRes.id && childRes && childRes.id) {
                await connect(parentRes.id, childRes.id, '#2E7D32');
            }
        }

        currentY += totalItemsH + 75; // Gap between sections
    }

    console.log('✅ Frame: Done generated in Miro successfully!');
    console.log(`View your clean board at: https://miro.com/app/board/${BOARD_ID}/`);
}

main().catch(console.error);
