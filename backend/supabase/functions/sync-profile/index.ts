import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

interface ProfileSyncRequest {
  user_id: string;
  nickname?: string;
  avatar_url?: string;
  onboarding_completed?: boolean;
}

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseAnonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";
    const supabase = createClient(supabaseUrl, supabaseAnonKey);

    const body = (await req.json()) as ProfileSyncRequest;
    if (!body.user_id) {
      return new Response(
        JSON.stringify({ error: "Missing user_id parameter" }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } }
      );
    }

    const { data, error } = await supabase
      .from("profiles")
      .upsert(
        {
          id: body.user_id,
          nickname: body.nickname,
          avatar: body.avatar_url,
          onboarding_completed: body.onboarding_completed ?? true,
          updated_at: new Date().toISOString(),
        },
        { onConflict: "id" }
      )
      .select();

    if (error) throw error;

    return new Response(
      JSON.stringify({ success: true, profile: data }),
      { headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error) {
    return new Response(
      JSON.stringify({ error: (error as Error).message }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  }
});
