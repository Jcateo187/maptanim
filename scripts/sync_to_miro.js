/**
 * MapTanim -> Miro Board Architecture Generator
 * 
 * Automatically connects to Miro REST API v2, lists or creates a board,
 * and generates the complete MapTanim architecture diagram.
 */

const https = require('https');

const API_TOKEN = process.env.MIRO_API_TOKEN || process.argv[2] || "eyJtaXJvLm9yaWdpbiI6ImV1MDEifQ_RTwu2aHccMO7R_V5yvhcHL-FjiM";
let BOARD_ID = process.env.MIRO_BOARD_ID || process.argv[3];

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
                        resolve(JSON.parse(body));
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

async function getOrCreateBoard() {
    if (BOARD_ID) return BOARD_ID;

    console.log('🔍 Checking existing Miro boards...');
    try {
        const boardsList = await miroRequest('boards?limit=10', 'GET');
        if (boardsList && boardsList.data && boardsList.data.length > 0) {
            const existing = boardsList.data.find(b => b.name && b.name.includes('MapTanim')) || boardsList.data[0];
            console.log(`📌 Using existing Miro Board: "${existing.name}" (ID: ${existing.id})`);
            return existing.id;
        }
    } catch (e) {
        console.warn('Listing boards notice:', e.message);
    }

    console.log('✨ Creating a new Miro Board: "MapTanim System Architecture"...');
    const newBoard = await miroRequest('boards', 'POST', {
        name: 'MapTanim System Architecture',
        description: 'Complete architecture diagram for MapTanim agroecological mobile app & backend'
    });
    console.log(`🎉 Created Board: "${newBoard.name}" (ID: ${newBoard.id})`);
    return newBoard.id;
}

async function createShape({ boardId, x, y, width = 280, height = 120, content, shape = 'round_rectangle', fillColor = '#2E7D32', textColor = '#FFFFFF' }) {
    const payload = {
        data: {
            shape: shape,
            content: `<p><strong>${content}</strong></p>`
        },
        style: {
            fillColor: fillColor,
            textAlign: 'center',
            textAlignVertical: 'middle',
            borderColor: '#1B5E20',
            borderWidth: '2',
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
    await sleep(150);
    return res;
}

async function createConnector(boardId, startItem, endItem, caption = '') {
    try {
        const payload = {
            startItem: { id: startItem.id, snapTo: 'auto' },
            endItem: { id: endItem.id, snapTo: 'auto' },
            style: {
                strokeColor: '#4CAF50',
                strokeWidth: '3',
                strokeStyle: 'normal'
            },
            ...(caption ? { captions: [{ content: caption, position: '50%' }] } : {})
        };
        const res = await miroRequest(`boards/${boardId}/connectors`, 'POST', payload);
        await sleep(150);
        return res;
    } catch (e) {
        console.warn(`Connector info: ${e.message}`);
    }
}

async function createStickyNote({ boardId, x, y, content, color = 'light_green' }) {
    try {
        const payload = {
            data: {
                content: content,
                shape: 'square'
            },
            style: {
                fillColor: color,
                textAlign: 'left'
            },
            position: {
                origin: 'center',
                x: x,
                y: y
            }
        };
        const res = await miroRequest(`boards/${boardId}/sticky_notes`, 'POST', payload);
        await sleep(150);
        return res;
    } catch (e) {
        console.warn(`Sticky Note info: ${e.message}`);
    }
}

async function main() {
    console.log('🚀 Connecting to Miro API with your access token...');
    try {
        const targetBoardId = await getOrCreateBoard();
        BOARD_ID = targetBoardId;

        console.log(`\n📐 Constructing MapTanim Architecture on Board ${BOARD_ID}...\n`);

        // ── 1. Layer 1: Client App (Jetpack Compose UI & Engine) ──────────
        console.log('👉 Creating UI & ViewModel Shapes...');
        await createShape({
            boardId: BOARD_ID,
            x: 0, y: -500, width: 550, height: 80,
            content: '🌱 MAPTANIM AGROECOLOGICAL SYSTEM ARCHITECTURE',
            fillColor: '#1B5E20'
        });

        const uiShape = await createShape({
            boardId: BOARD_ID,
            x: -400, y: -300, width: 300, height: 130,
            content: '📱 UI Layer (Jetpack Compose)<br/>• HomeScreen (HUD & Realtime Plots)<br/>• FarmEditorScreen (Isometric Grid)<br/>• ProfileScreen & Farm Modals',
            fillColor: '#2E7D32'
        });

        const vmShape = await createShape({
            boardId: BOARD_ID,
            x: 0, y: -300, width: 300, height: 130,
            content: '⚙️ ViewModel Layer<br/>• HomeViewModel (Lifecycle Sync)<br/>• EditViewModel (Plot Placement & Discard)<br/>• ProfileViewModel (Multi-Farm Management)',
            fillColor: '#388E3C'
        });

        const engineShape = await createShape({
            boardId: BOARD_ID,
            x: 400, y: -300, width: 300, height: 130,
            content: '🗺️ 2D Isometric Rendering Engine<br/>• FarmCanvasRenderer (Grid math)<br/>• IsometricProjection (Screen to World)<br/>• Crop AssetLoader (Single Sprite Stages)',
            fillColor: '#43A047'
        });

        const dssShape = await createShape({
            boardId: BOARD_ID,
            x: 400, y: -100, width: 300, height: 120,
            content: '🧠 Agroecological DSS Engine<br/>• Companion Matrix & Scoring<br/>• Philippine Crop Knowledgebase<br/>• Organic Pest Management Tips',
            fillColor: '#689F38'
        });

        // ── 2. Layer 2: State, Preferences & Repositories ─────────────────
        console.log('👉 Creating State & Repository Layer...');
        const prefsShape = await createShape({
            boardId: BOARD_ID,
            x: -400, y: -100, width: 300, height: 120,
            content: '🔄 FarmPreferencesManager<br/>• activeFarmChanges (SharedFlow)<br/>• User & Guest Active Farm Persistence',
            fillColor: '#00796B'
        });

        const repoShape = await createShape({
            boardId: BOARD_ID,
            x: 0, y: -100, width: 300, height: 120,
            content: '📦 Repository Layer<br/>• FarmRepository & CropPlotRepository<br/>• UserRepository & TaskRepository<br/>• Notification & HarvestRepository',
            fillColor: '#00897B'
        });

        // ── 3. Layer 3: Persistence & Database ────────────────────────────
        console.log('👉 Creating Database & Backend Cloud Layer...');
        const roomShape = await createShape({
            boardId: BOARD_ID,
            x: -300, y: 150, width: 340, height: 140,
            content: '💾 Local Room Database (SQLite)<br/>• Offline Cache & Instant Startup<br/>• crop_plots & crop_zones<br/>• farms, tasks & harvest_records',
            fillColor: '#0288D1'
        });

        const supaShape = await createShape({
            boardId: BOARD_ID,
            x: 300, y: 150, width: 340, height: 140,
            content: '☁️ Supabase Cloud (PostgreSQL)<br/>• Auth / JWT User Sessions<br/>• Row-Level Security (RLS)<br/>• Realtime Cloud Sync & Profiles',
            fillColor: '#0097A7'
        });

        const adminShape = await createShape({
            boardId: BOARD_ID,
            x: 300, y: 380, width: 340, height: 120,
            content: '🖥️ Admin Web Portal (Next.js)<br/>• Support Feedback & Support Tickets<br/>• Crop Library Manager<br/>• System Maintenance & Monitoring',
            fillColor: '#5C6BC0'
        });

        // ── 4. Connectors / Data Flow Arrows ──────────────────────────────
        console.log('👉 Drawing Connectors & Flow Arrows...');
        await createConnector(BOARD_ID, uiShape, vmShape, 'StateFlow / UI Events');
        await createConnector(BOARD_ID, uiShape, engineShape, 'Direct 2D Render');
        await createConnector(BOARD_ID, vmShape, repoShape, 'UseCases / Data Access');
        await createConnector(BOARD_ID, vmShape, prefsShape, 'Active Farm ID');
        await createConnector(BOARD_ID, vmShape, dssShape, 'Agro Advice');
        await createConnector(BOARD_ID, repoShape, roomShape, 'Room DAO (Offline First)');
        await createConnector(BOARD_ID, repoShape, supaShape, 'HTTPS PostgREST API');
        await createConnector(BOARD_ID, adminShape, supaShape, 'Direct SQL / Supabase Client');

        // ── 5. Sticky Notes for Key Highlights ───────────────────────────
        console.log('👉 Adding Feature Badges & Notes...');
        await createStickyNote({
            boardId: BOARD_ID,
            x: -680, y: -300,
            content: '🎨 Pixel-art Crop Sprites:\n• 1 Integrated Asset per Crop\n• Stage 1 (Sprout)\n• Stage 2 (Growing)\n• Stage 3 (Mature / Harvest)',
            color: 'yellow'
        });

        await createStickyNote({
            boardId: BOARD_ID,
            x: -680, y: -100,
            content: '⚡ Reactive Multi-Farm:\n• Instant farm switching\n• Automatic resume refresh\n• Unsaved edits discarded on exit\n• Isolated plots per farm_id',
            color: 'cyan'
        });

        await createStickyNote({
            boardId: BOARD_ID,
            x: -680, y: 150,
            content: '🚀 Fast App Loading:\n• Room offline preloading\n• Instant session cache\n• Seamless splash to workspace',
            color: 'light_green'
        });

        console.log(`
=============================================================================
🎉 SUCCESS: MapTanim Architecture generated successfully on Miro!
🔗 Open your Miro board:
   https://miro.com/app/board/${BOARD_ID}/
=============================================================================
`);

    } catch (err) {
        console.error('❌ Error executing Miro script:', err.message);
    }
}

main();
