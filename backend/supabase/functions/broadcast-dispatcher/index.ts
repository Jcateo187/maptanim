import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

interface BroadcastRequest {
  title: string;
  body: string;
  notification_type?: "SYSTEM_UPDATE" | "PEST_ADVISORY" | "HARVEST_READY" | "ANNOUNCEMENT";
  user_id?: string | null; // Nullable for system-wide broadcast to all mobile farmers
}

serve(async (req: Request) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseServiceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || Deno.env.get("SUPABASE_ANON_KEY") || "";
    const supabase = createClient(supabaseUrl, supabaseServiceKey);

    const payload = (await req.json()) as BroadcastRequest;

    if (!payload.title || !payload.body) {
      return new Response(
        JSON.stringify({ error: "Missing required 'title' or 'body' parameter" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const notificationRecord = {
      title: payload.title.trim(),
      body: payload.body.trim(),
      notification_type: payload.notification_type || "SYSTEM_UPDATE",
      user_id: payload.user_id ? payload.user_id : null,
      is_read: false,
      created_at: new Date().toISOString(),
    };

    const { data, error } = await supabase
      .from("notifications")
      .insert([notificationRecord])
      .select()
      .single();

    if (error) throw error;

    return new Response(
      JSON.stringify({
        success: true,
        message: payload.user_id ? `Notification sent to user ${payload.user_id}` : "Broadcast notification dispatched to all farmers",
        notification: data,
      }),
      { status: 201, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error) {
    console.error("Broadcast Dispatcher Error:", error);
    return new Response(
      JSON.stringify({ error: (error as Error).message }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
