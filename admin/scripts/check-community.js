import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const envPath = path.resolve(__dirname, '../.env');
let envSupabaseUrl = '';
let envSupabaseKey = '';

if (fs.existsSync(envPath)) {
  const content = fs.readFileSync(envPath, 'utf8');
  for (const line of content.split('\n')) {
    const trimmed = line.trim();
    if (trimmed.startsWith('VITE_SUPABASE_URL=')) {
      envSupabaseUrl = trimmed.split('=')[1].trim();
    } else if (trimmed.startsWith('VITE_SUPABASE_ANON_KEY=')) {
      envSupabaseKey = trimmed.split('=')[1].trim();
    }
  }
}

const SUPABASE_URL = envSupabaseUrl || 'https://ojilvcglpzbtpjxguhzj.supabase.co';
const SUPABASE_ANON_KEY = envSupabaseKey || 'sb_publishable_fH5qY2HaAg-coV89IxOl2Q_Xf9ySGMU';

async function dumpPosts() {
  const url = `${SUPABASE_URL}/rest/v1/community_posts?select=*`;
  const res = await fetch(url, {
    headers: {
      'apikey': SUPABASE_ANON_KEY,
      'Authorization': `Bearer ${SUPABASE_ANON_KEY}`
    }
  });
  if (res.ok) {
    const posts = await res.json();
    console.log(`Found ${posts.length} posts in community_posts:`);
    for (const p of posts) {
      console.log(`\nID: ${p.id}`);
      console.log(`Title: ${p.title}`);
      console.log(`Author: ${p.author_name}`);
      console.log(`Image URL: ${p.image_url}`);
    }
  } else {
    console.log('Error fetching community_posts:', res.status, res.statusText);
  }
}

dumpPosts().catch(console.error);
