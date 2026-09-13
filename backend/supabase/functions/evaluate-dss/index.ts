import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

interface EvaluateRequest {
  farm_id: string;
  evaluation_date?: string; // Format: YYYY-MM-DD (Defaults to current date)
}

interface CropPlotRecord {
  id: string;
  farm_id: string;
  plot_label: string;
  crop_name: string | null;
  crop_variety: string | null;
  soil_type: string;
  pos_x: number;
  pos_y: number;
  width_m: number;
  height_m: number;
  planted_date: string | null;
  is_active: boolean;
}

interface CropRecord {
  id: string;
  name: string;
  local_name?: string;
  category: string;
  days_to_harvest: number;
  watering_interval_days?: number;
  fertilize_interval_days?: number;
  suitable_soils?: string[];
}

interface DssRuleRecord {
  id: string;
  crop_a: string;
  crop_b: string;
  relationship: "BENEFICIAL" | "ANTAGONIST" | "NEUTRAL";
  reason?: string;
}

interface CompanionAlert {
  plot_a_id: string;
  plot_a_label: string;
  crop_a_name: string;
  plot_b_id: string;
  plot_b_label: string;
  crop_b_name: string;
  relationship: "BENEFICIAL" | "ANTAGONIST" | "NEUTRAL";
  distance_m: number;
  reason: string;
}

interface GeneratedTask {
  farm_id: string;
  plot_id: string;
  task_type: "WATER" | "FERTILIZE" | "HARVEST" | "PEST_ALERT" | "SOIL_AMENDMENT";
  title: string;
  sub_label: string;
  due_date: string;
  is_completed: boolean;
  notes?: string;
}

interface PlotStageEvaluation {
  plot_id: string;
  plot_label: string;
  crop_name: string;
  crop_variety?: string | null;
  planted_date: string;
  days_planted: number;
  days_to_harvest: number;
  progress_percent: number;
  current_stage: "SPROUT" | "SEEDLING" | "VEGETATIVE" | "FLOWERING" | "HARVEST";
  stage_index: number; // 1 to 5
  is_harvest_ready: boolean;
  is_soil_suitable: boolean;
}

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseAnonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";
    const supabase = createClient(supabaseUrl, supabaseAnonKey);

    const body = (await req.json()) as EvaluateRequest;
    const farmId = body.farm_id;
    if (!farmId) {
      return new Response(
        JSON.stringify({ error: "Missing required farm_id parameter" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const evalDateStr = body.evaluation_date ?? new Date().toISOString().split("T")[0];
    const evalDate = new Date(evalDateStr);

    // 1. Fetch active crop plots for this farm
    const { data: rawPlots, error: plotsError } = await supabase
      .from("crop_plots")
      .select("*")
      .eq("farm_id", farmId)
      .eq("is_active", true);

    if (plotsError) throw plotsError;
    const plots: CropPlotRecord[] = (rawPlots as CropPlotRecord[]) || [];

    // Filter to planted plots with a known crop name
    const plantedPlots = plots.filter((p) => p.crop_name && p.crop_name.trim().length > 0);

    // 2. Fetch reference crops & companion planting rules
    const [{ data: rawCrops, error: cropsError }, { data: rawRules, error: rulesError }] =
      await Promise.all([
        supabase.from("crops").select("id, name, local_name, category, days_to_harvest, watering_interval_days, fertilize_interval_days, suitable_soils"),
        supabase.from("dss_rules").select("*"),
      ]);

    if (cropsError) throw cropsError;
    if (rulesError) throw rulesError;

    const crops: CropRecord[] = (rawCrops as CropRecord[]) || [];
    const rules: DssRuleRecord[] = (rawRules as DssRuleRecord[]) || [];

    // Lookup map for crops (case-insensitive name match)
    const cropMap = new Map<string, CropRecord>();
    for (const c of crops) {
      cropMap.set(c.name.toLowerCase(), c);
      if (c.local_name) {
        cropMap.set(c.local_name.toLowerCase(), c);
      }
    }

    const generatedTasks: GeneratedTask[] = [];
    const companionAlerts: CompanionAlert[] = [];
    const stageEvaluations: PlotStageEvaluation[] = [];

    // 3. Proximity Companion Evaluation between adjacent / nearby plots
    const PROXIMITY_THRESHOLD_M = 3.0; // Adjacent plots within 3 meters on the grid
    for (let i = 0; i < plantedPlots.length; i++) {
      for (let j = i + 1; j < plantedPlots.length; j++) {
        const plotA = plantedPlots[i];
        const plotB = plantedPlots[j];
        if (!plotA.crop_name || !plotB.crop_name) continue;

        const dist = Math.hypot(plotA.pos_x - plotB.pos_x, plotA.pos_y - plotB.pos_y);
        if (dist <= PROXIMITY_THRESHOLD_M) {
          const nameA = plotA.crop_name.toLowerCase();
          const nameB = plotB.crop_name.toLowerCase();

          // Find matching companion rule in dss_rules
          const matchedRule = rules.find(
            (r) =>
              (r.crop_a.toLowerCase() === nameA && r.crop_b.toLowerCase() === nameB) ||
              (r.crop_a.toLowerCase() === nameB && r.crop_b.toLowerCase() === nameA)
          );

          if (matchedRule) {
            const roundedDist = Math.round(dist * 10) / 10;
            const alert: CompanionAlert = {
              plot_a_id: plotA.id,
              plot_a_label: plotA.plot_label,
              crop_a_name: plotA.crop_name,
              plot_b_id: plotB.id,
              plot_b_label: plotB.plot_label,
              crop_b_name: plotB.crop_name,
              relationship: matchedRule.relationship,
              distance_m: roundedDist,
              reason: matchedRule.reason || `${plotA.crop_name} and ${plotB.crop_name} are ${matchedRule.relationship.toLowerCase()} companions.`,
            };
            companionAlerts.push(alert);

            // Antagonistic proximity triggers PEST_ALERT task for inspection
            if (matchedRule.relationship === "ANTAGONIST") {
              generatedTasks.push({
                farm_id: farmId,
                plot_id: plotA.id,
                task_type: "PEST_ALERT",
                title: `Pest Inspection: ${plotA.plot_label}`,
                sub_label: `Adjacent Antagonist: ${plotB.crop_name}`,
                due_date: evalDateStr,
                is_completed: false,
                notes: alert.reason,
              });
            }
          }
        }
      }
    }

    // 4. Agronomic Schedule, Soil Suitability & 5-Stage Timeline
    for (const plot of plantedPlots) {
      if (!plot.crop_name) continue;
      const cropRef = cropMap.get(plot.crop_name.toLowerCase());
      const daysToHarvest = cropRef?.days_to_harvest ?? 60;
      const wateringInterval = cropRef?.watering_interval_days ?? 2;
      const fertilizeInterval = cropRef?.fertilize_interval_days ?? 14;

      // Calculate days planted
      let daysPlanted = 0;
      if (plot.planted_date) {
        const pDate = new Date(plot.planted_date.slice(0, 10));
        const diffMs = evalDate.getTime() - pDate.getTime();
        daysPlanted = Math.max(0, Math.floor(diffMs / (1000 * 60 * 60 * 24)));
      }

      const progressPercent = Math.min(100, Math.round((daysPlanted / daysToHarvest) * 100));

      // 5 Timeline Stages:
      // 1. SPROUT (0% - 15%)
      // 2. SEEDLING (15% - 35%)
      // 3. VEGETATIVE (35% - 65%)
      // 4. FLOWERING (65% - 90%)
      // 5. HARVEST (90%+)
      let currentStage: "SPROUT" | "SEEDLING" | "VEGETATIVE" | "FLOWERING" | "HARVEST" = "SPROUT";
      let stageIndex = 1;

      if (progressPercent >= 90) {
        currentStage = "HARVEST";
        stageIndex = 5;
      } else if (progressPercent >= 65) {
        currentStage = "FLOWERING";
        stageIndex = 4;
      } else if (progressPercent >= 35) {
        currentStage = "VEGETATIVE";
        stageIndex = 3;
      } else if (progressPercent >= 15) {
        currentStage = "SEEDLING";
        stageIndex = 2;
      }

      // Soil Suitability Check
      const suitableSoils = cropRef?.suitable_soils ?? [];
      const isSoilSuitable = suitableSoils.length === 0 || suitableSoils.includes(plot.soil_type);

      stageEvaluations.push({
        plot_id: plot.id,
        plot_label: plot.plot_label,
        crop_name: plot.crop_name,
        crop_variety: plot.crop_variety,
        planted_date: plot.planted_date ?? evalDateStr,
        days_planted: daysPlanted,
        days_to_harvest: daysToHarvest,
        progress_percent: progressPercent,
        current_stage: currentStage,
        stage_index: stageIndex,
        is_harvest_ready: currentStage === "HARVEST" || daysPlanted >= daysToHarvest,
        is_soil_suitable: isSoilSuitable,
      });

      // Soil amendment task if soil is suboptimal
      if (!isSoilSuitable && daysPlanted <= 7) {
        generatedTasks.push({
          farm_id: farmId,
          plot_id: plot.id,
          task_type: "SOIL_AMENDMENT",
          title: `Amend Soil for ${plot.plot_label}`,
          sub_label: `${plot.soil_type} not optimal for ${plot.crop_name}`,
          due_date: evalDateStr,
          is_completed: false,
          notes: `Suitable soils: ${suitableSoils.join(", ")}`,
        });
      }

      // Scheduled Watering Task
      if (daysPlanted % wateringInterval === 0) {
        generatedTasks.push({
          farm_id: farmId,
          plot_id: plot.id,
          task_type: "WATER",
          title: `Water ${plot.plot_label}`,
          sub_label: `${plot.crop_name} (${currentStage})`,
          due_date: evalDateStr,
          is_completed: false,
        });
      }

      // Scheduled Fertilizing Task
      if (daysPlanted > 0 && daysPlanted % fertilizeInterval === 0 && currentStage !== "HARVEST") {
        generatedTasks.push({
          farm_id: farmId,
          plot_id: plot.id,
          task_type: "FERTILIZE",
          title: `Apply Fertilizer to ${plot.plot_label}`,
          sub_label: `${plot.crop_name} (${currentStage})`,
          due_date: evalDateStr,
          is_completed: false,
        });
      }

      // Harvest Task
      if (daysPlanted >= daysToHarvest || currentStage === "HARVEST") {
        generatedTasks.push({
          farm_id: farmId,
          plot_id: plot.id,
          task_type: "HARVEST",
          title: `Harvest ${plot.crop_name} (${plot.plot_label})`,
          sub_label: "Harvest Ready",
          due_date: evalDateStr,
          is_completed: false,
        });
      }
    }

    // 5. Deduplicate and upsert generated tasks into public.tasks
    // Fetch existing tasks for this farm & date to avoid duplication
    const { data: existingTasks } = await supabase
      .from("tasks")
      .select("plot_id, task_type, due_date")
      .eq("farm_id", farmId)
      .eq("due_date", evalDateStr);

    const existingKeySet = new Set(
      (existingTasks || []).map((t: any) => `${t.plot_id}_${t.task_type}_${t.due_date}`)
    );

    const newTasksToInsert = generatedTasks.filter(
      (t) => !existingKeySet.has(`${t.plot_id}_${t.task_type}_${t.due_date}`)
    );

    if (newTasksToInsert.length > 0) {
      const { error: insertError } = await supabase.from("tasks").insert(newTasksToInsert);
      if (insertError) {
        console.error("Warning: Failed to insert some generated tasks:", insertError);
      }
    }

    // 6. Return comprehensive DSS evaluation response
    const responsePayload = {
      success: true,
      farm_id: farmId,
      evaluation_date: evalDateStr,
      farm_summary: {
        total_plots: plots.length,
        total_planted_plots: plantedPlots.length,
        ready_to_harvest: stageEvaluations.filter((s) => s.is_harvest_ready).length,
        active_companion_alerts: companionAlerts.length,
        tasks_generated_today: newTasksToInsert.length,
      },
      companion_alerts: companionAlerts,
      stage_evaluations: stageEvaluations,
      tasks: generatedTasks,
    };

    return new Response(JSON.stringify(responsePayload), {
      status: 200,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });
  } catch (error) {
    console.error("DSS Evaluation Error:", error);
    return new Response(
      JSON.stringify({ error: (error as Error).message }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
