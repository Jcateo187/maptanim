-- MapTanim Versioned Migration 018: Clean up legacy unrecorded admin user
-- Administrator authentication is managed via Vercel Environment Variables (stateless admin)
-- Removes legacy seed admin record from public.users

DELETE FROM public.users 
WHERE role = 'ADMINISTRATOR' 
   OR email = 'admin@maptanim.ph';
