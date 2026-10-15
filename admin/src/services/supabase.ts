import { createClient } from '@supabase/supabase-js';
import type { Database } from '../types/supabase';

const SUPABASE_URL = import.meta.env.VITE_SUPABASE_URL || 'https://ojilvcglpzbtpjxguhzj.supabase.co';
const SUPABASE_ANON_KEY = import.meta.env.VITE_SUPABASE_ANON_KEY || 'sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU';

export const isSupabaseConfigured = Boolean(
  SUPABASE_URL && SUPABASE_ANON_KEY && !SUPABASE_ANON_KEY.includes('dummy')
);

export const supabase = createClient<any>(SUPABASE_URL, SUPABASE_ANON_KEY);
export type { Database };
