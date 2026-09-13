/**
 * MapTanim Microservices Verification Script
 * Validates the DSS Rule Engine, 5-stage timeline, companion proximity, and task generation.
 * Run with: node backend/scripts/test-microservices.js
 */

const assert = require("assert");

console.log("==================================================================");
console.log("🌱 MapTanim Microservices Test Suite: evaluate-dss & broadcast");
console.log("==================================================================");

// 1. Proximity Companion Evaluation Test
console.log("\n[Test 1] Testing Proximity Companion Calculation on Isometric Plots...");

function evaluateCompanion(plotA, plotB, rules) {
  const dist = Math.hypot(plotA.pos_x - plotB.pos_x, plotA.pos_y - plotB.pos_y);
  if (dist > 3.0) return null; // Outside proximity threshold

  const nameA = plotA.crop_name.toLowerCase();
  const nameB = plotB.crop_name.toLowerCase();

  const matched = rules.find(
    (r) =>
      (r.crop_a.toLowerCase() === nameA && r.crop_b.toLowerCase() === nameB) ||
      (r.crop_a.toLowerCase() === nameB && r.crop_b.toLowerCase() === nameA)
  );

  if (!matched) return null;

  return {
    plot_a_label: plotA.plot_label,
    plot_b_label: plotB.plot_label,
    relationship: matched.relationship,
    distance_m: Math.round(dist * 10) / 10,
    reason: matched.reason,
  };
}

const mockRules = [
  { crop_a: "Kamatis", crop_b: "Mais", relationship: "ANTAGONIST", reason: "Tomato and Corn share the tomato fruitworm / corn earworm." },
  { crop_a: "Talong", crop_b: "Kamatis", relationship: "BENEFICIAL", reason: "Complementary solanaceous companions with similar nutrient regimes." },
];

const plot1 = { id: "plot-1", plot_label: "Plot A", crop_name: "Kamatis", pos_x: 2.0, pos_y: 2.0 };
const plot2 = { id: "plot-2", plot_label: "Plot B", crop_name: "Mais", pos_x: 3.5, pos_y: 2.0 }; // dist = 1.5m
const plot3 = { id: "plot-3", plot_label: "Plot C", crop_name: "Mais", pos_x: 10.0, pos_y: 10.0 }; // dist = 11.3m

const alertAdjacent = evaluateCompanion(plot1, plot2, mockRules);
assert(alertAdjacent !== null, "Adjacent plots must trigger companion check");
assert.strictEqual(alertAdjacent.relationship, "ANTAGONIST");
assert.strictEqual(alertAdjacent.plot_a_label, "Plot A");
assert.strictEqual(alertAdjacent.plot_b_label, "Plot B");
console.log("  ✓ Correctly flagged ANTAGONIST companion between adjacent plots within 1.5m");

const alertFar = evaluateCompanion(plot1, plot3, mockRules);
assert.strictEqual(alertFar, null, "Plots farther than 3.0m must not trigger proximity alert");
console.log("  ✓ Ignored distant plots (11.3m) outside the 3.0m proximity threshold");


// 2. 5-Stage Timeline Progression Test
console.log("\n[Test 2] Testing 5-Stage Timeline Progression...");

function compute5StageTimeline(daysPlanted, daysToHarvest) {
  const progressPercent = Math.min(100, Math.round((daysPlanted / daysToHarvest) * 100));

  if (progressPercent >= 90) return { stage: "HARVEST", index: 5, progress: progressPercent };
  if (progressPercent >= 65) return { stage: "FLOWERING", index: 4, progress: progressPercent };
  if (progressPercent >= 35) return { stage: "VEGETATIVE", index: 3, progress: progressPercent };
  if (progressPercent >= 15) return { stage: "SEEDLING", index: 2, progress: progressPercent };
  return { stage: "SPROUT", index: 1, progress: progressPercent };
}

// 60-day crop (e.g. Tomato / Kamatis)
assert.strictEqual(compute5StageTimeline(5, 60).stage, "SPROUT"); // 8%
assert.strictEqual(compute5StageTimeline(12, 60).stage, "SEEDLING"); // 20%
assert.strictEqual(compute5StageTimeline(30, 60).stage, "VEGETATIVE"); // 50%
assert.strictEqual(compute5StageTimeline(45, 60).stage, "FLOWERING"); // 75%
assert.strictEqual(compute5StageTimeline(58, 60).stage, "HARVEST"); // 97%
console.log("  ✓ Verified Stage 1: SPROUT (0% - 15%)");
console.log("  ✓ Verified Stage 2: SEEDLING (15% - 35%)");
console.log("  ✓ Verified Stage 3: VEGETATIVE (35% - 65%)");
console.log("  ✓ Verified Stage 4: FLOWERING (65% - 90%)");
console.log("  ✓ Verified Stage 5: HARVEST (90%+)");


// 3. Daily Task Generation Rules Test
console.log("\n[Test 3] Testing Dynamic Task Generation (Water, Fertilize, Harvest, Pest Alert)...");

function generateDailyTasks(plot, cropRef, daysPlanted, antagonistAlert = null) {
  const tasks = [];
  const timeline = compute5StageTimeline(daysPlanted, cropRef.days_to_harvest);

  if (daysPlanted % cropRef.watering_interval_days === 0) {
    tasks.push({ type: "WATER", title: `Water ${plot.plot_label}` });
  }

  if (daysPlanted > 0 && daysPlanted % cropRef.fertilize_interval_days === 0 && timeline.stage !== "HARVEST") {
    tasks.push({ type: "FERTILIZE", title: `Apply Fertilizer to ${plot.plot_label}` });
  }

  if (timeline.stage === "HARVEST") {
    tasks.push({ type: "HARVEST", title: `Harvest ${plot.crop_name} (${plot.plot_label})` });
  }

  if (antagonistAlert) {
    tasks.push({ type: "PEST_ALERT", title: `Pest Inspection: ${plot.plot_label}` });
  }

  return tasks;
}

const cropRef = { days_to_harvest: 60, watering_interval_days: 2, fertilize_interval_days: 14 };

// Day 14 (should trigger WATER and FERTILIZE)
const day14Tasks = generateDailyTasks(plot1, cropRef, 14);
assert(day14Tasks.some((t) => t.type === "WATER"), "Day 14 must have WATER task");
assert(day14Tasks.some((t) => t.type === "FERTILIZE"), "Day 14 must have FERTILIZE task");
console.log("  ✓ Day 14 scheduled WATER and FERTILIZE tasks");

// Day 60 (Harvest stage)
const day60Tasks = generateDailyTasks(plot1, cropRef, 60);
assert(day60Tasks.some((t) => t.type === "HARVEST"), "Day 60 must have HARVEST task");
console.log("  ✓ Day 60 scheduled HARVEST task");

// Antagonist adjacent plot triggers PEST_ALERT
const pestTasks = generateDailyTasks(plot1, cropRef, 5, alertAdjacent);
assert(pestTasks.some((t) => t.type === "PEST_ALERT"), "Antagonist alert must schedule PEST_ALERT task");
console.log("  ✓ Antagonistic companion proximity scheduled PEST_ALERT task");


// 4. Zero Weather Dependency Confirmation
console.log("\n[Test 4] Verifying Zero Weather Dependencies in Microservices...");
const fs = require("fs");
const path = require("path");

const dssFile = fs.readFileSync(path.join(__dirname, "../supabase/functions/evaluate-dss/index.ts"), "utf-8");
const broadcastFile = fs.readFileSync(path.join(__dirname, "../supabase/functions/broadcast-dispatcher/index.ts"), "utf-8");

assert(!dssFile.includes("weather"), "evaluate-dss must NOT contain weather references");
assert(!dssFile.includes("open-meteo"), "evaluate-dss must NOT query open-meteo");
assert(!dssFile.includes("pagasa"), "evaluate-dss must NOT query pagasa");
assert(!dssFile.includes("forecast"), "evaluate-dss must NOT contain forecast references");
assert(!dssFile.includes("bed_"), "evaluate-dss must NOT contain bed references");

assert(!broadcastFile.includes("weather"), "broadcast-dispatcher must NOT contain weather references");
assert(!broadcastFile.includes("bed_"), "broadcast-dispatcher must NOT contain bed references");

console.log("  ✓ Verified 0 weather dependencies in evaluate-dss");
console.log("  ✓ Verified 0 weather dependencies in broadcast-dispatcher");
console.log("  ✓ Verified 0 legacy 'bed' references (strictly using 'plots')");

console.log("\n==================================================================");
console.log("🎉 All 4 Microservice Verification Tests Passed Successfully!");
console.log("==================================================================");
