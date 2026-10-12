/**
 * MapTanim -> File-Level & Table-Level "Lack" Frame Generator for Miro
 * 
 * Uses ONLY exact File Names, Screen Names, and Database Table Names on the left:
 * (No "Mobile" or "Admin" parent categories)
 * E.g.: CropDetailDialog, HomeScreen, EditScreen, ProfileScreen, crops, pests, etc.
 * 
 * - Parent on LEFT: File name or Table name in bold yellow box
 * - Child Cards on RIGHT: Concrete, actionable, simple-word tasks & improvements
 * - Connectors: Clean curved arrows with ZERO text labels
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
    await sleep(70);
    return res;
}

async function connect(startId, endId, color = '#7E6FA0') {
    try {
        const res = await miroRequest(`boards/${BOARD_ID}/connectors`, 'POST', {
            startItem: { id: startId, snapTo: 'right' },
            endItem: { id: endId, snapTo: 'left' },
            style: { strokeColor: color, strokeWidth: '2', strokeStyle: 'normal' },
            shape: 'curved'
        });
        await sleep(70);
        return res;
    } catch (e) {
        console.log(`     connector note: ${e.message.substring(0, 80)}`);
    }
}

async function removeOldLackFrame() {
    console.log('🧹 Removing old Lack frame and items to replace with pure file/table level cards...');
    try {
        const framesRes = await miroRequest(`boards/${BOARD_ID}/frames?limit=50`, 'GET');
        const frames = framesRes.data || [];
        for (const f of frames) {
            const title = (f.data ? f.data.title : '') || '';
            if (title.toLowerCase().includes('lack')) {
                console.log(`   Deleting old lack frame: "${title}" (${f.id})...`);
                try {
                    await miroRequest(`boards/${BOARD_ID}/frames/${f.id}`, 'DELETE');
                    await sleep(100);
                } catch (e) {}
            }
        }

        // Remove shapes in the Lack zone (X >= 4200)
        let hasMore = true;
        let cursor = null;
        let cleaned = 0;
        while (hasMore) {
            const url = `boards/${BOARD_ID}/items?limit=50${cursor ? `&cursor=${cursor}` : ''}`;
            const list = await miroRequest(url, 'GET');
            const items = (list.data || []).filter(i => i.position && i.position.x >= 4200);
            for (const item of items) {
                try {
                    await miroRequest(`boards/${BOARD_ID}/items/${item.id}`, 'DELETE');
                    await sleep(30);
                    cleaned++;
                } catch (e) {}
            }
            cursor = list.cursor || null;
            hasMore = !!cursor && (list.data || []).length > 0;
            if (cleaned > 350) break;
        }
        console.log(`   Cleaned ${cleaned} items from old Lack zone.`);
    } catch (err) {
        console.warn('   Cleanup notice:', err.message);
    }
}

async function main() {
    console.log('🚀 Generating Pure File-Level & Table-Level "Lack" Frame on Miro...');
    console.log(`Board ID: ${BOARD_ID}`);

    await removeOldLackFrame();

    // Coordinates setup for Frame: Lack (sitting directly right of Frame: Done at X=2700)
    const FRAME_X = 5500;
    const FRAME_Y = 9600;
    const FRAME_W = 2300;
    const FRAME_H = 5400;

    console.log('🖼️ Creating Frame: Lack (Strict File & Table Level)...');
    try {
        await createFrame({
            x: FRAME_X,
            y: FRAME_Y,
            width: FRAME_W,
            height: FRAME_H,
            title: 'Lack (Mga Kakulangan at Aayusin — File & Table Level Roadmap)'
        });
        console.log('   Frame: Lack created successfully.');
    } catch (e) {
        console.log('   Frame notice:', e.message);
    }

    // Frame background shape (Lavender / Light Purple)
    await box({
        x: FRAME_X,
        y: FRAME_Y,
        w: FRAME_W - 40,
        h: FRAME_H - 40,
        shp: 'round_rectangle',
        fill: '#E8E3FA',
        border: '#B39DDB',
        bw: '3.0',
        content: ''
    });

    // Pure File-Level & Table-Level Roadmap (Strictly no broad "Mobile" / "Admin" categories)
    const sections = [
        {
            name: 'CropDetailDialog',
            items: [
                'Click word (Loam, Deep Dig, Balag) -> show popup with detail & sample images',
                'Shrink dialog to portrait width max 400dp (not wide landscape)',
                'Add visual soil cross-section diagram with 20-30cm digging depth guide',
                'Show variety traits (harvest days, yield, resistance) on selection'
            ]
        },
        {
            name: 'HomeScreen',
            items: [
                'Remove Weather API & PAGASA radar (system is guide-only)',
                'Remove all DA & DA-BPI agency logos & text (MapTanim independent)',
                'Show days-to-harvest countdown badge on active crop plots',
                'Sync banner: show count of new crops & varieties downloaded'
            ]
        },
        {
            name: 'EditScreen',
            items: [
                'Show visual plant spacing guide (cm furrow lines) when dragging crop',
                'Allow picking variety before dropping crop on canvas',
                'Show Tagalog warning banner if plot placement overlaps or exceeds bounds',
                'Add quick Undo button when moving or deleting plots'
            ]
        },
        {
            name: 'ProfileScreen',
            items: [
                'Fix Remember Me checkbox on Login to save session',
                'Fix Farm Switcher so switching farm updates canvas immediately',
                'Add total farm harvest weight (kg) summary and market earnings',
                'Add database cache clear button in Settings for offline reset'
            ]
        },
        {
            name: 'CropTray',
            items: [
                'Smooth vertical scrolling with click-before-drag (✅ Done)',
                'Enlarge crop thumbnail box to 46dp (✅ Done)',
                'Add category filter tabs (Gulay, Bungang-Ugat, Dahon)',
                'Show Tagalog name under English (e.g. Sitaw under String Beans)'
            ]
        },
        {
            name: 'TodaysTasksOverlay',
            items: [
                'Save completed task checkbox with timestamp to tasks table',
                'Add subtitle banner: Dilig sa umaga, bunot ng damo, pataba',
                'Filter tasks by plot when tapped from farm canvas pin'
            ]
        },
        {
            name: 'MonitoringDashboardOverlay',
            items: [
                'Calculate health score % from daily completed tasks',
                'Add harvest prompt: enter kilograms harvested & calculate market profit',
                'Open pest photo sample and biological remedy when pest alert tapped'
            ]
        },
        {
            name: 'crops',
            items: [
                'Add preferred_planting_method column (Direct Seeding, Plot Bed, Pot)',
                'Add soil_prep_tagalog & harvest_signs_tagalog text columns',
                'Add weeding_interval_days & plant_spacing_cm columns'
            ]
        },
        {
            name: 'crop_varieties',
            items: [
                'Create table in Supabase migration with crop_id, days_to_harvest, traits',
                'Migrate static local JSON files in assets/metadata/crops/*.json to table',
                'Sync varieties over-the-air to Room mobile database'
            ]
        },
        {
            name: 'plots',
            items: [
                'Add variety_id foreign key column to track specific cultivar planted',
                'Add planting_method column (Bed, Furrow, Container)'
            ]
        },
        {
            name: 'tasks',
            items: [
                'Add completed_at timestamp column to save farmer completion history',
                'Add farm_id foreign key to isolate tasks per farm'
            ]
        },
        {
            name: 'pests',
            items: [
                'Create public.pests table in Supabase migration',
                'Add columns: symptoms_tagalog, organic_control, image_url',
                'Create Supabase Storage bucket pest-images for real photos'
            ]
        },
        {
            name: 'Sidebar',
            items: [
                'Change Library icon from Sprout to BookOpen',
                'Change Community icon from BarChart2 to MessageSquare',
                'Add direct navigation link for Varieties & Pest Management'
            ]
        },
        {
            name: 'CropLibrary',
            items: [
                'Add Variety Editor modal to create & update certified cultivars',
                'Add Tagalog text input fields for soil prep, watering & harvest signs',
                'Connect companion planting matrix selector to database rules'
            ]
        },
        {
            name: 'PestLibrary',
            items: [
                'Connect to live Supabase pests table (delete static mockData.ts)',
                'Add photo upload dropzone to upload directly to Supabase Storage',
                'Add organic natural repellent recipe fields (Neem oil, chili spray)'
            ]
        }
    ];

    let currentY = FRAME_Y - (FRAME_H / 2) + 200;
    const LEFT_X = FRAME_X - 580;
    const RIGHT_X = FRAME_X + 220;

    for (let sIdx = 0; sIdx < sections.length; sIdx++) {
        const sec = sections[sIdx];
        const numItems = sec.items.length;
        const itemH = 58;
        const itemGap = 10;
        const totalItemsH = numItems * itemH + (numItems - 1) * itemGap;
        const sectionCenterY = currentY + (totalItemsH / 2);

        console.log(`📌 Building Lack Section ${sIdx + 1}: ${sec.name} (${numItems} items)...`);

        // 1. Yellow Parent Box (LEFT) — Pure File / Screen / Table Name
        const parentRes = await box({
            x: LEFT_X,
            y: sectionCenterY,
            w: 260,
            h: Math.max(90, Math.min(180, totalItemsH * 0.9)),
            content: `<strong><span style="font-size: 16px; color: #1B1B1B;">${sec.name}</span></strong>`,
            fill: '#FFF59D',
            border: '#FBC02D',
            bw: '2.5',
            text: '#1B1B1B',
            align: 'center',
            valign: 'middle'
        });

        // 2. Pink Child Boxes (RIGHT, Stacked Vertically — Specific Tasks)
        for (let iIdx = 0; iIdx < numItems; iIdx++) {
            const itemText = sec.items[iIdx];
            const itemY = currentY + (iIdx * (itemH + itemGap)) + (itemH / 2);
            const isDone = itemText.includes('✅ Done');

            const childRes = await box({
                x: RIGHT_X,
                y: itemY,
                w: 720,
                h: itemH,
                content: `<span style="font-size: 12px;">${itemText}</span>`,
                fill: isDone ? '#E8F5E9' : '#FFCDD2',
                border: isDone ? '#81C784' : '#E57373',
                bw: '1.5',
                text: isDone ? '#1B3B22' : '#3E1B1E',
                align: 'left',
                valign: 'middle'
            });

            // 3. Connect Parent -> Child with clean curved arrow (NO text labels)
            if (parentRes && parentRes.id && childRes && childRes.id) {
                await connect(parentRes.id, childRes.id, isDone ? '#4CAF50' : '#7E6FA0');
            }
        }

        currentY += totalItemsH + 45; // Gap between sections
    }

    console.log('✅ File & Table Level Frame: Lack generated in Miro successfully!');
    console.log(`View your updated board at: https://miro.com/app/board/${BOARD_ID}/`);
}

main().catch(console.error);
