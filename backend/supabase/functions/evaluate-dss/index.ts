import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

interface EvaluateRequest {
  farm_id: string;
}

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseAnonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";
    const supabase = createClient(supabaseUrl, supabaseAnonKey);

    const { farm_id } = (await req.json()) as EvaluateRequest;
    if (!farm_id) {
      return new Response(
        JSON.stringify({ error: "Missing farm_id parameter" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    // 1. Fetch active crop plots for farm
    const { data: plots, error: plotsError } = await supabase
      .from("crop_plots")
      .select("*, crops(*)")
      .eq("farm_id", farm_id)
      .eq("is_active", true);

    if (plotsError) {
      throw plotsError;
    }

    // 2. Fetch companion planting rules
    const { data: rules } = await supabase.from("dss_rules").select("*");

    const today = new Date();
    const generatedTasks = [];

    // 3. Evaluate each plot
    for (const plot of plots ?? []) {
      if (!plot.planted_date || !plot.crop_name) continue;

      const plantedDate = new Date(plot.planted_date);
      const diffTime = Math.abs(today.getTime() - plantedDate.getTime());
      const daysPlanted = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      const daysToHarvest = plot.crops?.days_to_harvest ?? 60;

      // Watering rule (every 2 days)
      if (daysPlanted % 2 === 0) {
        generatedTasks.push({
          farm_id: farm_id,
          plot_id: plot.id,
          task_type: "WATER",
          title: `Water ${plot.plot_label}`,
          sub_label: plot.crop_name,
          due_date: today.toISOString().split("T")[0],
          is_completed: false,
        });
      }

      // Fertilization rule (every 14 days)
      if (daysPlanted > 0 && daysPlanted % 14 === 0) {
        generatedTasks.push({
          farm_id: farm_id,
          plot_id: plot.id,
          task_type: "FERTILIZE",
          title: `Apply Fertilizer to ${plot.plot_label}`,
          sub_label: plot.crop_name,
          due_date: today.toISOString().split("T")[0],
          is_completed: false,
        });
      }

      // Harvest rule
      if (daysPlanted >= daysToHarvest) {
        generatedTasks.push({
          farm_id: farm_id,
          plot_id: plot.id,
          task_type: "HARVEST",
          title: `Harvest ${plot.crop_name} (${plot.plot_label})`,
          sub_label: "Harvest Ready",
          due_date: today.toISOString().split("T")[0],
          is_completed: false,
        });
      }
    }

    // 4. Upsert generated tasks
    if (generatedTasks.length > 0) {
      await supabase.from("tasks").upsert(generatedTasks, { onConflict: "id" });
    }

    return new Response(
      JSON.stringify({ success: true, tasks_count: generatedTasks.length, tasks: generatedTasks }),
      { headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error) {
    return new Response(
      JSON.stringify({ error: (error as Error).message }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
