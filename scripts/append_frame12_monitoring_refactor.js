/**
 * MapTanim -> Frame 12: Monitoring Overlay Refactor (UI Components & MVVM Architecture) on Miro
 * 
 * Creates Frame 12 documenting ONLY the specific changes made to:
 * - MonitoringDashboardOverlay.kt (UI & Components layer: removed 190dp side nav, 100% landscape grid, plot badges)
 * - MonitoringViewModel.kt (MVVM StateFlow layer: purged dummy catalogCrops, simplified state, fast filtering)
 * - Separation of Concerns: AgriLibrary vs. Operational Monitoring
 * - Zero AI / Zero IoT Deterministic Agronomy
 * 
 * Placed safely at y = 6050, preserving all existing frames (Frame 1 to Frame 11).
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
    borderColor = '#2E7D32',
    borderWidth = '2.0',
    borderStyle = 'normal',
    textAlign = 'center',
    textAlignVertical = 'middle'
}) {
    const validBorderWidth = (!borderWidth || borderWidth === '0') ? '1.0' : (borderWidth === '1.5' ? '2.0' : String(parseFloat(borderWidth) || 1.0));
    const validBorderColor = (!borderColor || !borderColor.startsWith('#')) ? '#2E7D32' : borderColor;

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

async function createConnector(boardId, startItem, endItem, caption = '', color = '#2E7D32', strokeStyle = 'normal') {
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
    console.log(`🚀 Connecting to Miro Board: ${BOARD_ID} to build Frame 12 (Monitoring Overlay Refactor)...`);
    try {
        // 1. Clean existing Frame 12 items (y >= 5400) - NEVER touch Frame 11 (y < 5400)
        console.log('🧹 Cleaning any existing Frame 12 items (y >= 5400)...');
        let hasMore = true;
        let cursor = null;
        while (hasMore) {
            const url = `boards/${BOARD_ID}/items?limit=50${cursor ? `&cursor=${cursor}` : ''}`;
            const list = await miroRequest(url, 'GET');
            const items = (list.data || []).filter(item => item.position && item.position.y >= 5400);
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

        const FRAME_X = -250;
        const FRAME_Y = 6050;
        const FRAME_W = 2800;
        const FRAME_H = 1200;

        // 2. Create Frame 12
        console.log('🖼️ Creating Frame 12...');
        const frame12 = await createFrame({
            boardId: BOARD_ID,
            x: FRAME_X,
            y: FRAME_Y,
            width: FRAME_W,
            height: FRAME_H,
            title: '🌱 FRAME 12: MONITORING OVERLAY REFACTOR — UI COMPONENTS & MVVM ARCHITECTURE'
        });

        // 3. Canvas Background (#FAF7F2 warm parchment)
        await createShape({
            boardId: BOARD_ID,
            x: FRAME_X,
            y: FRAME_Y,
            width: FRAME_W,
            height: FRAME_H,
            shape: 'rectangle',
            fillColor: '#FAF7F2',
            borderColor: '#C8E6C9',
            borderWidth: '2.0',
            content: ''
        });

        // 4. Top-Left Title Block
        await createShape({
            boardId: BOARD_ID,
            x: FRAME_X - 900,
            y: FRAME_Y - 530,
            width: 820,
            height: 90,
            shape: 'round_rectangle',
            fillColor: '#1B2317',
            borderColor: '#4CAF50',
            borderWidth: '2.0',
            textAlign: 'left',
            textColor: '#FFFFFF',
            content: '<strong>🌱 FRAME 12: MONITORING OVERLAY REFACTOR</strong><br/><span style="font-size: 11px; color: #81C784;">Plot-First Agricultural Architecture • UI Components & Reactive MVVM</span><br/><span style="font-size: 10px; color: #E0E0E0;">Files: MonitoringDashboardOverlay.kt &amp; MonitoringViewModel.kt</span>'
        });

        // ══════════════════════════════════════════════════════════════════
        // TIER 1: UI & COMPONENT LAYER (MonitoringDashboardOverlay.kt)
        // ══════════════════════════════════════════════════════════════════
        console.log('🎨 Building Tier 1: UI & Components Layer...');
        const TIER1_X = FRAME_X - 860;
        const TIER1_Y = FRAME_Y + 30;
        const TIER1_W = 820;
        const TIER1_H = 920;

        // Container
        await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y,
            width: TIER1_W,
            height: TIER1_H,
            shape: 'round_rectangle',
            fillColor: '#FFFFFF',
            borderColor: '#2E7D32',
            borderWidth: '3.0',
            content: ''
        });

        // Header
        const uiHeader = await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y - 420,
            width: TIER1_W - 30,
            height: 44,
            shape: 'round_rectangle',
            fillColor: '#2E7D32',
            textColor: '#FFFFFF',
            content: '<strong>🎨 1. UI &amp; COMPONENT LAYER (MonitoringDashboardOverlay.kt)</strong>'
        });

        // Box 1A: 190dp Sidebar Removal & Full Width Grid
        const box1A = await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y - 290,
            width: TIER1_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#F1F8E9',
            borderColor: '#4CAF50',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🚫 Removed 190dp Left Sidebar → 100% Full-Width Grid</strong><br/>' +
                '• <strong>Legacy Flaw</strong>: 190dp sidebar wasted ~25% of landscape screen width with arbitrary "6 Soil Types" &amp; "3 Seasons" buttons that loaded unplanted catalog crops.<br/>' +
                '• <strong>Refactored Layout</strong>: Removed left navigation completely from Screen 1.<br/>' +
                '• <strong>4-Column Plot Grid</strong>: <code>LazyVerticalGrid(columns = GridCells.Fixed(4))</code> now utilizes full landscape display to showcase all active farm plots.'
        });

        // Box 1B: Redesigned Action & Filter Header
        const box1B = await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y - 100,
            width: TIER1_W - 40,
            height: 150,
            shape: 'round_rectangle',
            fillColor: '#F1F8E9',
            borderColor: '#4CAF50',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🔍 Streamlined Top Filter &amp; Action Bar</strong><br/>' +
                '• <strong>Active Plots Badge</strong>: <code>🌱 Active Farm Crops (${filteredCrops.size})</code> indicates real plot count.<br/>' +
                '• <strong>Fast Search Bar</strong>: Live text search by crop name, local Tagalog name, or plot label (e.g. <code>Plot 1</code>, <code>Talong</code>).<br/>' +
                '• <strong>Category Filter Dropdown</strong>: Filter active plots by vegetable type (<code>Leafy</code>, <code>Fruit</code>, <code>Root</code>, etc.).'
        });

        // Box 1C: Enriched CropSelectionGridCard
        const box1C = await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y + 100,
            width: TIER1_W - 40,
            height: 190,
            shape: 'round_rectangle',
            fillColor: '#F1F8E9',
            borderColor: '#4CAF50',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🌱 Enriched CropSelectionGridCard (Plot Context Badges)</strong><br/>' +
                '• <strong>Plot Label</strong>: Clearly labeled by assigned isometric plot (e.g. <code>Plot 1</code>, <code>Plot 2</code>).<br/>' +
                '• <strong>Plot Soil Type Badge</strong>: <code>🌱 Loam</code> / <code>Clay</code> displayed directly from <code>crop_plots.soil_type</code>.<br/>' +
                '• <strong>Seasonal Status Badge</strong>: <code>☀️ Tag-araw (Dry)</code> / <code>🌧️ Tag-ulan (Wet)</code> / <code>🔄 Buong Taon</code>.<br/>' +
                '• <strong>5-Stage Timeline Progress</strong>: Real-time linear progress bar with <code>Day X / Y</code> duration ratio.<br/>' +
                '• <strong>Stage &amp; Variety Chip</strong>: Displays current stage (Sprout → Harvest) and planted variety.'
        });

        // Box 1D: Screen 2 DSS Panels
        const box1D = await createShape({
            boardId: BOARD_ID,
            x: TIER1_X,
            y: TIER1_Y + 310,
            width: TIER1_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#F1F8E9',
            borderColor: '#4CAF50',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>📊 Screen 2: Dedicated Operational DSS Panels</strong><br/>' +
                '• Tapping any plot card navigates to the dual-pane detail view for that specific planted crop.<br/>' +
                '• <strong>6 Operational Panels</strong>: Overview, 5-Stage Timeline, Calendar, Companions Matrix, Growing Tips, Pest &amp; Disease.<br/>' +
                '• <strong>Streamlined NavSectionItem</strong>: Removed legacy <code>SOIL_TYPES</code> &amp; <code>SEASONAL</code> enum entries; strictly focuses on the 6 DSS panels.'
        });

        // ══════════════════════════════════════════════════════════════════
        // TIER 2: MVVM & STATEFLOW LAYER (MonitoringViewModel.kt)
        // ══════════════════════════════════════════════════════════════════
        console.log('⚙️ Building Tier 2: MVVM & StateFlow Layer...');
        const TIER2_X = FRAME_X;
        const TIER2_Y = FRAME_Y + 30;
        const TIER2_W = 760;
        const TIER2_H = 920;

        // Container
        await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y,
            width: TIER2_W,
            height: TIER2_H,
            shape: 'round_rectangle',
            fillColor: '#FFFFFF',
            borderColor: '#1565C0',
            borderWidth: '3.0',
            content: ''
        });

        // Header
        const mvvmHeader = await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y - 420,
            width: TIER2_W - 30,
            height: 44,
            shape: 'round_rectangle',
            fillColor: '#1565C0',
            textColor: '#FFFFFF',
            content: '<strong>⚙️ 2. MVVM &amp; REACTIVE STATEFLOW LAYER (MonitoringViewModel.kt)</strong>'
        });

        // Box 2A: Purged Dummy Catalog from UI State
        const box2A = await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y - 290,
            width: TIER2_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#E3F2FD',
            borderColor: '#1E88E5',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🧹 Simplified MonitoringUiState (Zero Dummy Data)</strong><br/>' +
                '• <strong>Removed Obsolete Fields</strong>: Purged <code>catalogCrops</code>, <code>filterMode</code>, <code>selectedSoilType</code>, <code>selectedSeason</code>.<br/>' +
                '• <strong>Eliminated Synthetic "Reference Catalog" Objects</strong>: No longer constructs fake <code>MonitoredPlant</code> items for unplanted crops.<br/>' +
                '• <strong>Clean Reactive State</strong>: StateFlow strictly carries active <code>plantedCrops: List&lt;MonitoredPlant&gt;</code> directly from <code>crop_plots</code>.'
        });

        // Box 2B: loadData Coroutine Stream
        const box2B = await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y - 100,
            width: TIER2_W - 40,
            height: 150,
            shape: 'round_rectangle',
            fillColor: '#E3F2FD',
            borderColor: '#1E88E5',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>⚡ Optimized Coroutine Flow (loadData)</strong><br/>' +
                '• Combines <code>cropPlotRepository.getPlotsFlow()</code> with <code>cropRepository.getCropsFlow()</code>.<br/>' +
                '• Evaluates plot-specific DSS tasks and companion adjacency in real-time.<br/>' +
                '• Emits only active <code>monitoredList</code> via <code>Dispatchers.Default</code>; saves CPU cycles on every 500ms ticker pulse.'
        });

        // Box 2C: Deterministic getFilteredCrops()
        const box2C = await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y + 100,
            width: TIER2_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#E3F2FD',
            borderColor: '#1E88E5',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🎯 Fast In-Memory Filtering (getFilteredCrops)</strong><br/>' +
                '• Filters directly on <code>state.plantedCrops</code> without synthesizing catalog lists.<br/>' +
                '• Multi-attribute search matches <code>cropName</code>, <code>localName</code>, and <code>plotLabel</code>.<br/>' +
                '• High-performance instantaneous responsiveness for the farmer on mobile devices.'
        });

        // Box 2D: Section & Task Management
        const box2D = await createShape({
            boardId: BOARD_ID,
            x: TIER2_X,
            y: TIER2_Y + 300,
            width: TIER2_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#E3F2FD',
            borderColor: '#1E88E5',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>✅ Streamlined Navigation &amp; Task Completion</strong><br/>' +
                '• <code>selectNavSection(section)</code>: Directly switches among the 6 DSS detail panels.<br/>' +
                '• <code>completeDssTask(cropId, farmId, taskType)</code>: Dispatches task completion to <code>ActivityRepository</code> and Room DB.<br/>' +
                '• <code>completeHarvest(...)</code>: Invokes <code>CropPlotRepository.recordHarvest()</code> and triggers remote Supabase synchronization.'
        });

        // ══════════════════════════════════════════════════════════════════
        // TIER 3: DOMAIN & ARCHITECTURAL SEPARATION OF CONCERNS
        // ══════════════════════════════════════════════════════════════════
        console.log('🏛️ Building Tier 3: Domain & Architectural Separation...');
        const TIER3_X = FRAME_X + 860;
        const TIER3_Y = FRAME_Y + 30;
        const TIER3_W = 820;
        const TIER3_H = 920;

        // Container
        await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y,
            width: TIER3_W,
            height: TIER3_H,
            shape: 'round_rectangle',
            fillColor: '#FFFFFF',
            borderColor: '#6A1B9A',
            borderWidth: '3.0',
            content: ''
        });

        // Header
        const domainHeader = await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y - 420,
            width: TIER3_W - 30,
            height: 44,
            shape: 'round_rectangle',
            fillColor: '#6A1B9A',
            textColor: '#FFFFFF',
            content: '<strong>🏛️ 3. SYSTEM DESIGN &amp; SEPARATION OF CONCERNS</strong>'
        });

        // Box 3A: Separation of Concerns
        const box3A = await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y - 290,
            width: TIER3_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#F3E5F5',
            borderColor: '#8E24AA',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>📚 Clear Separation: Monitoring vs. AgriLibrary</strong><br/>' +
                '• <strong>AgriLibrary (CropLibrary)</strong>: Botanical encyclopedia for researching all 15 Philippine crops, soil suitability, and planting windows <em>before planting</em>.<br/>' +
                '• <strong>Monitoring (MonitoringDashboardOverlay)</strong>: Real-time operational tracking of <em>active planted plots</em> on the 2D isometric farm.<br/>' +
                '• Farmers now have a clear mental model: Library = Study, Monitoring = Farm Action.'
        });

        // Box 3B: Contextual Plot Agronomy
        const box3B = await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y - 100,
            width: TIER3_W - 40,
            height: 150,
            shape: 'round_rectangle',
            fillColor: '#F3E5F5',
            borderColor: '#8E24AA',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🌾 Plot-First Contextual Agronomy</strong><br/>' +
                '• <strong>Soil Type is Plot Data</strong>: Stored in <code>crop_plots.soil_type</code> (e.g. Plot 1 has Loam Soil). Displayed directly on that plot card.<br/>' +
                '• <strong>Season is Crop Growth Data</strong>: Indicates if the planted crop is in-season or year-round.<br/>' +
                '• Neither soil type nor season belong as sidebar catalog navigation buttons.'
        });

        // Box 3C: Zero AI & Zero IoT Guarantee
        const box3C = await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y + 100,
            width: TIER3_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#F3E5F5',
            borderColor: '#8E24AA',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🛡️ Zero AI &amp; Zero IoT Deterministic State</strong><br/>' +
                '• <strong>No Live IoT Sensors</strong>: Soil suitability is calculated via static Department of Agriculture lookup tables (<code>idealSoils</code>, <code>suitableSoils</code>).<br/>' +
                '• <strong>No Black-Box AI</strong>: Replaced confusing AI "percentage match" claims with clear agricultural badges: <code>Loam Soil (Optimal)</code>.<br/>' +
                '• 100% predictable, offline-capable agronomic guidance.'
        });

        // Box 3D: Database Integrity
        const box3D = await createShape({
            boardId: BOARD_ID,
            x: TIER3_X,
            y: TIER3_Y + 300,
            width: TIER3_W - 40,
            height: 170,
            shape: 'round_rectangle',
            fillColor: '#F3E5F5',
            borderColor: '#8E24AA',
            borderWidth: '1.5',
            textAlign: 'left',
            content: '<strong>🗄️ Database Integrity &amp; Migration 020 Alignment</strong><br/>' +
                '• Strictly operates over the 10 core tables (<code>crop_plots</code>, <code>crops</code>, <code>tasks</code>, <code>harvest_records</code>).<br/>' +
                '• Completely purged legacy tables: <code>planting_monitors</code>, <code>beds</code>, <code>tile_plantings</code>, and <code>crop_profiles</code>.<br/>' +
                '• Aligned with Migration 020 schema and automated sync pipeline.'
        });

        // ══════════════════════════════════════════════════════════════════
        // BIDIRECTIONAL CONNECTORS
        // ══════════════════════════════════════════════════════════════════
        console.log('🔗 Creating Directional Data Flow Connectors...');
        await createConnector(BOARD_ID, box1B, box2C, 'Search & Category Events', '#2E7D32');
        await createConnector(BOARD_ID, box2A, box1C, 'StateFlow<MonitoringUiState>', '#1565C0');
        await createConnector(BOARD_ID, box2B, box3B, 'Plot Queries (crop_plots)', '#1565C0');
        await createConnector(BOARD_ID, box3C, box1C, 'Deterministic DA Suitability', '#6A1B9A');
        await createConnector(BOARD_ID, box1D, box2D, 'Task Completion / Harvest Events', '#2E7D32');

        // ══════════════════════════════════════════════════════════════════
        // BRANDED FOOTER
        // ══════════════════════════════════════════════════════════════════
        await createShape({
            boardId: BOARD_ID,
            x: FRAME_X,
            y: FRAME_Y + 540,
            width: 1800,
            height: 48,
            shape: 'round_rectangle',
            fillColor: '#1B2317',
            borderColor: '#4CAF50',
            borderWidth: '1.5',
            textColor: '#FFFFFF',
            content: '<strong>MapTanim Agricultural Architecture</strong> • Monitoring Overlay &amp; MVVM Refactor • Plot-First Determinism • Zero IoT • Zero AI • Pure Room / Supabase Sync'
        });

        console.log('✅ Frame 12 built successfully on Miro board!');
    } catch (e) {
        console.error('❌ Error building Frame 12:', e);
    }
}

main();
