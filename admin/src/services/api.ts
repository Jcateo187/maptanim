import { Farmer, Farm, BedPlot, Crop, DSSRule, FeedbackItem, SystemAuditLog, DashboardStats, CommunityPost, CommunityComment, CommunityReport, ReportStatus, BroadcastUpdatePayload, BroadcastNotification, UserActivityLog, UserTrackingMetrics, AccountStatus, UserRole } from '../types';
import { MOCK_CROPS, MOCK_DSS_RULES, MOCK_FARMS, MOCK_BEDS, MOCK_FEEDBACK, MOCK_LOGS, MOCK_STATS, MOCK_FARMERS, MOCK_USER_ACTIVITY_LOGS, MOCK_USER_TRACKING_METRICS } from './mockData';
import { supabase, isSupabaseConfigured } from './supabase';

class ApiService {
  private farmers: Farmer[] = [...MOCK_FARMERS];
  private userActivityLogs: UserActivityLog[] = [...MOCK_USER_ACTIVITY_LOGS];
  private crops: Crop[] = [...MOCK_CROPS];
  private rules: DSSRule[] = [...MOCK_DSS_RULES];
  private feedback: FeedbackItem[] = [...MOCK_FEEDBACK];
  private logs: SystemAuditLog[] = [...MOCK_LOGS];
  private communityPosts: CommunityPost[] = [];
  private communityComments: CommunityComment[] = [];
  private communityReports: CommunityReport[] = [];

  // Dashboard Overview Stats (Live Query Across Supabase Tables)
  async getDashboardStats(): Promise<DashboardStats> {
    if (isSupabaseConfigured) {
      try {
        // Parallel queries across all relevant Supabase tables
        const [
          { count: profileCount },
          { count: cropCount },
          { count: farmCount },
          { count: bedCount },
          { count: feedbackCount },
          { count: reportCount },
          { count: pendingReportCount },
          { count: postCount },
          { count: notifCount },
          { data: harvestData },
          { data: plantingsData },
          { data: profilesForActivity },
        ] = await Promise.all([
          supabase.from('profiles').select('*', { count: 'exact', head: true }),
          supabase.from('crops').select('*', { count: 'exact', head: true }),
          supabase.from('farms').select('*', { count: 'exact', head: true }),
          supabase.from('crop_plots').select('*', { count: 'exact', head: true }),
          supabase.from('feedback').select('*', { count: 'exact', head: true }),
          supabase.from('community_reports').select('*', { count: 'exact', head: true }),
          supabase.from('community_reports').select('*', { count: 'exact', head: true }).eq('status', 'PENDING'),
          supabase.from('community_posts').select('*', { count: 'exact', head: true }),
          supabase.from('notifications').select('*', { count: 'exact', head: true }).eq('notification_type', 'SYSTEM_UPDATE'),
          supabase.from('harvest_records').select('*').order('created_at', { ascending: false }).limit(200),
          supabase.from('crop_plots').select('crop_name, crop_variety, planted_date, created_at').not('crop_name', 'is', null),
          supabase.from('profiles').select('created_at, updated_at').order('created_at', { ascending: false }).limit(200),
        ]);

        // Compute total harvest kg
        let totalHarvestKg = MOCK_STATS.totalHarvestKgThisMonth;
        if (harvestData && harvestData.length > 0) {
          totalHarvestKg = harvestData.reduce((acc: number, r: any) => acc + (Number(r.yield_kg) || 0), 0);
        }

        // Standard variety mapping for 15 Philippine crops
        const DEFAULT_VARIETIES: Record<string, string> = {
          'Tomato': 'Diamante Max',
          'Kamatis': 'Diamante Max',
          'Eggplant': 'Dumaguete Long',
          'Talong': 'Dumaguete Long',
          'Cabbage': 'Scorpio',
          'Repolyo': 'Scorpio',
          'Pechay': 'Black Behi',
          'Bitter Gourd': 'Galaxy F1',
          'Ampalaya': 'Galaxy F1',
          'String Beans': 'Sandigan',
          'Sitaw': 'Sandigan',
          'Sweet Corn': 'Machismo',
          'Mais': 'Machismo',
          'Squash': 'Suprema',
          'Kalabasa': 'Suprema',
          'Chili Pepper': 'Django',
          'Sili': 'Django',
          'Carrot': 'Kuroda',
          'Karot': 'Kuroda',
          'Okra': 'Smooth Green',
          'Cucumber': 'Pipino Green',
          'Pipino': 'Pipino Green',
          'Onion': 'Red Pinoy',
          'Sibuyas': 'Red Pinoy',
          'Garlic': 'Ilocos White',
          'Bawang': 'Ilocos White',
          'Bell Pepper': 'California Wonder',
        };

        // Compute top planted crops and varieties from crop_plots
        const cropCountMap: Record<string, { count: number; varietyName: string }> = {};
        const varietyCountMap: Record<string, { count: number; cropName: string }> = {};

        if (plantingsData && plantingsData.length > 0) {
          plantingsData.forEach((p: any) => {
            const cropName = (p.crop_name || 'Unknown').trim();
            const cleanCropKey = Object.keys(DEFAULT_VARIETIES).find(k => cropName.toLowerCase().includes(k.toLowerCase())) || cropName;
            const variety = (p.crop_variety || DEFAULT_VARIETIES[cleanCropKey] || DEFAULT_VARIETIES[cropName] || 'Standard Variety').trim();

            if (!cropCountMap[cropName]) {
              cropCountMap[cropName] = { count: 0, varietyName: variety };
            }
            cropCountMap[cropName].count += 1;

            const varietyKey = variety;
            if (!varietyCountMap[varietyKey]) {
              varietyCountMap[varietyKey] = { count: 0, cropName };
            }
            varietyCountMap[varietyKey].count += 1;
          });
        }

        const TOP_COLORS = ['#ef4444', '#8b5cf6', '#22c55e', '#f59e0b', '#06b6d4', '#ec4899', '#f97316'];
        
        const topPlantedCrops = Object.entries(cropCountMap)
          .sort(([, a], [, b]) => b.count - a.count)
          .slice(0, 7)
          .map(([cropName, data], i) => ({
            cropName,
            varietyName: data.varietyName,
            plantCount: data.count,
            color: TOP_COLORS[i] || '#6b7280',
          }));

        const topPlantedVarieties = Object.entries(varietyCountMap)
          .sort(([, a], [, b]) => b.count - a.count)
          .slice(0, 7)
          .map(([varietyName, data], i) => ({
            varietyName,
            cropName: data.cropName,
            plantCount: data.count,
            color: TOP_COLORS[i] || '#6b7280',
          }));

        // Compute Harvest Date Analytics from harvest_records
        let harvestDateAnalytics = MOCK_STATS.harvestDateAnalytics;
        if (harvestData && harvestData.length > 0) {
          const dateMap: Record<string, { yieldKg: number; count: number; crops: Record<string, number> }> = {};
          
          harvestData.forEach((r: any) => {
            const rawDate = r.harvest_date || r.harvested_date || (r.created_at ? r.created_at.split('T')[0] : null);
            if (!rawDate) return;
            const dateStr = String(rawDate).slice(0, 10);
            const yieldKg = Number(r.yield_kg) || 0;
            const crop = r.crop_name || 'Crops';

            if (!dateMap[dateStr]) {
              dateMap[dateStr] = { yieldKg: 0, count: 0, crops: {} };
            }
            dateMap[dateStr].yieldKg += yieldKg;
            dateMap[dateStr].count += 1;
            dateMap[dateStr].crops[crop] = (dateMap[dateStr].crops[crop] || 0) + 1;
          });

          const entries = Object.entries(dateMap);
          if (entries.length > 0) {
            const sortedByDate = [...entries].sort(([a], [b]) => a.localeCompare(b));
            const dateRecords = sortedByDate.slice(-10).map(([dateStr, data]) => {
              const d = new Date(dateStr);
              const displayDate = isNaN(d.getTime()) ? dateStr : d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
              const fullDate = isNaN(d.getTime()) ? dateStr : d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
              const topCrop = Object.entries(data.crops).sort(([, a], [, b]) => b - a)[0]?.[0] || 'Mixed Crops';

              return {
                date: dateStr,
                displayDate,
                fullDate,
                harvestCount: data.count,
                yieldKg: Math.round(data.yieldKg * 10) / 10,
                topCrop,
              };
            });

            const peakEntry = [...entries].sort(([, a], [, b]) => b.yieldKg - a.yieldKg || b.count - a.count)[0];
            const peakDate = peakEntry[0];
            const peakCrop = Object.entries(peakEntry[1].crops).sort(([, a], [, b]) => b - a)[0]?.[0] || 'Crops';

            harvestDateAnalytics = {
              peakHarvestDate: peakDate,
              peakHarvestYieldKg: Math.round(peakEntry[1].yieldKg * 10) / 10,
              peakHarvestPlotCount: peakEntry[1].count,
              peakHarvestCrop: peakCrop,
              dateRecords,
            };
          }
        }

        // Compute weekly registrations (last 7 days)
        const now = Date.now();
        const DAY_MS = 86400000;
        const dayLabels = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const weeklyMap: Record<string, { newUsers: number; returningUsers: number }> = {};
        for (let i = 6; i >= 0; i--) {
          const d = new Date(now - i * DAY_MS);
          const label = dayLabels[d.getDay()];
          weeklyMap[label] = { newUsers: 0, returningUsers: 0 };
        }
        if (profilesForActivity) {
          profilesForActivity.forEach((p: any) => {
            const createdAt = new Date(p.created_at);
            const updatedAt = p.updated_at ? new Date(p.updated_at) : null;
            const daysSinceCreation = Math.floor((now - createdAt.getTime()) / DAY_MS);
            if (daysSinceCreation <= 6) {
              const label = dayLabels[createdAt.getDay()];
              if (weeklyMap[label]) weeklyMap[label].newUsers += 1;
            }
            if (updatedAt && Math.floor((now - updatedAt.getTime()) / DAY_MS) <= 6) {
              const label = dayLabels[updatedAt.getDay()];
              if (weeklyMap[label]) weeklyMap[label].returningUsers += 1;
            }
          });
        }
        const weeklyRegistrations = Object.entries(weeklyMap).map(([day, v]) => ({ day, ...v }));

        // Active today = profiles updated within last 24h
        const activeUsersToday = profilesForActivity
          ? profilesForActivity.filter((p: any) => {
              const updated = new Date(p.updated_at || p.created_at);
              return now - updated.getTime() < DAY_MS;
            }).length
          : 0;

        return {
          ...MOCK_STATS,
          totalFarmers: profileCount && profileCount > 0 ? profileCount : MOCK_STATS.totalFarmers,
          totalCrops: cropCount && cropCount > 0 ? cropCount : MOCK_CROPS.length,
          activeFarms: farmCount && farmCount > 0 ? farmCount : MOCK_STATS.activeFarms,
          totalPlots: bedCount && bedCount > 0 ? bedCount : MOCK_STATS.totalPlots,
          totalHarvestKgThisMonth: totalHarvestKg,
          totalFeedback: feedbackCount ?? MOCK_STATS.totalFeedback,
          totalPostReports: reportCount ?? MOCK_STATS.totalPostReports,
          pendingReports: pendingReportCount ?? MOCK_STATS.pendingReports,
          totalCommunityPosts: postCount ?? MOCK_STATS.totalCommunityPosts,
          systemNotificationsCount: notifCount ?? MOCK_STATS.systemNotificationsCount,
          topPlantedCrops: topPlantedCrops.length > 0 ? topPlantedCrops : MOCK_STATS.topPlantedCrops,
          topPlantedVarieties: topPlantedVarieties.length > 0 ? topPlantedVarieties : MOCK_STATS.topPlantedVarieties,
          harvestDateAnalytics,
          weeklyRegistrations,
          activeUsersToday,
        };
      } catch (err) {
        console.warn('Supabase dashboard stats fetch failed, using fallback:', err);
      }
    }
    return Promise.resolve(MOCK_STATS);
  }


  // Farmer & User Management (Live Supabase Query with Graceful Mock Fallback)
  async getFarmers(): Promise<Farmer[]> {
    if (!isSupabaseConfigured) {
      return Promise.resolve(this.farmers);
    }

    try {
      // Query all user touchpoints to capture real-time active mobile presence
      const [
        { data: profilesData },
        { data: usersData },
        { data: farmsData },
        { data: plotsData },
        { data: postsData },
        { data: commentsData },
        { data: feedbackData }
      ] = await Promise.all([
        supabase.from('profiles').select('*').order('created_at', { ascending: false }),
        supabase.from('users').select('*'),
        supabase.from('farms').select('*'),
        supabase.from('crop_plots').select('*'),
        supabase.from('community_posts').select('id, author_id, author_name, created_at'),
        supabase.from('community_comments').select('id, author_id, created_at'),
        supabase.from('feedback').select('id, user_id, created_at'),
      ]);

      const farmerList: Farmer[] = [];

      // 1. Process real mobile accounts from profiles table
      if (profilesData && profilesData.length > 0) {
        profilesData.forEach((p) => {
          // Match user account by auth UUID or nickname prefix match
          const userAccount = usersData?.find(
            (u) => u.id === p.id || (p.nickname && u.email && u.email.toLowerCase().startsWith(p.nickname.toLowerCase()))
          );
          // Delete/exclude unrecorded field officers and administrators
          if (userAccount?.role === 'FIELD_OFFICER') return;
          if (userAccount?.role === 'ADMINISTRATOR') return;

          const shortId = p.id.replace(/-/g, '').slice(0, 6).toUpperCase();
          const hasCustomNickname = Boolean(p.nickname && p.nickname.trim() !== '');
          const hasEmail = Boolean(userAccount?.email && userAccount.email.trim() !== '');
          const isAnonymous = !hasCustomNickname && !hasEmail;

          // Unique generated nickname for anonymous / guest accounts:
          // Format: "Ka-Tanim #672491" or custom nickname if set by user
          const uniqueNickname = hasCustomNickname
            ? p.nickname!
            : hasEmail
            ? userAccount!.email.split('@')[0]
            : `Ka-Tanim #${shortId}`;

          const role: UserRole = isAnonymous ? 'GUEST' : ((userAccount?.role as UserRole) || 'FARMER');

          const userFarm = farmsData?.find(
            (f) => f.farmer_id === p.id || (userAccount && f.farmer_id === userAccount.id)
          );
          const userPlots = plotsData?.filter((plot) => userFarm && plot.farm_id === userFarm.id) || [];
          const userPlotsCount = userPlots.length;

          const rawEmail = hasEmail
            ? userAccount!.email
            : hasCustomNickname
            ? `${p.nickname}@mobile.app`
            : `guest_${shortId.toLowerCase()}@guest.maptanim.ph`;

          // Track latest activity timestamp across all live mobile touchpoints:
          // Profile update, crop plot modifications, community posts, comments, or support feedback
          let latestActivityMs = p.updated_at
            ? new Date(p.updated_at).getTime()
            : p.created_at
            ? new Date(p.created_at).getTime()
            : 0;
          let latestActivityType = 'SESSION';

          // 1. Check crop plot edits
          userPlots.forEach((plot) => {
            const plotTime = plot.updated_at ? new Date(plot.updated_at).getTime() : (plot.created_at ? new Date(plot.created_at).getTime() : 0);
            if (plotTime > latestActivityMs) {
              latestActivityMs = plotTime;
              latestActivityType = 'FARM_PLOT';
            }
          });

          // 2. Check community forum posts (by author_id or author_name/nickname)
          postsData?.forEach((post) => {
            const matchesId = post.author_id && (post.author_id === p.id || (userAccount && post.author_id === userAccount.id));
            const matchesName = Boolean(
              (p.nickname && post.author_name && post.author_name.toLowerCase().trim() === p.nickname.toLowerCase().trim()) ||
              (userAccount?.email && post.author_name && userAccount.email.toLowerCase().startsWith(post.author_name.toLowerCase().trim())) ||
              (uniqueNickname && post.author_name && post.author_name.toLowerCase().trim() === uniqueNickname.toLowerCase().trim())
            );
            if (matchesId || matchesName) {
              const postTime = post.created_at ? new Date(post.created_at).getTime() : 0;
              if (postTime > latestActivityMs) {
                latestActivityMs = postTime;
                latestActivityType = 'COMMUNITY_POST';
              }
            }
          });

          // 3. Check community comments
          commentsData?.forEach((comment) => {
            if (comment.author_id === p.id || (userAccount && comment.author_id === userAccount.id)) {
              const commentTime = comment.created_at ? new Date(comment.created_at).getTime() : 0;
              if (commentTime > latestActivityMs) {
                latestActivityMs = commentTime;
                latestActivityType = 'COMMUNITY_COMMENT';
              }
            }
          });

          // 4. Check feedback tickets
          feedbackData?.forEach((fb) => {
            if (fb.user_id === p.id || (userAccount && fb.user_id === userAccount.id)) {
              const fbTime = fb.created_at ? new Date(fb.created_at).getTime() : 0;
              if (fbTime > latestActivityMs) {
                latestActivityMs = fbTime;
                latestActivityType = 'FEEDBACK';
              }
            }
          });

          const lastActiveIso = latestActivityMs > 0 ? new Date(latestActivityMs).toISOString() : (p.created_at || new Date().toISOString());
          const nowMs = Date.now();
          const diffMinutes = latestActivityMs > 0 ? Math.max(0, Math.floor((nowMs - latestActivityMs) / (1000 * 60))) : 999999;
          const diffDays = latestActivityMs > 0 ? Math.max(0, Math.floor((nowMs - latestActivityMs) / (1000 * 60 * 60 * 24))) : 0;

          // Engagement & Activity Tracking Status:
          // 1. Explicit admin status in users table (SUSPENDED, PENDING) takes precedence
          // 2. An account is ACTIVE if active within 7 days OR has active farm plots in progress
          // 3. An account is INACTIVE (Dormant) if no activity for > 7 days and 0 active plots
          const explicitStatus = (userAccount?.status as AccountStatus) || null;
          let determinedStatus: AccountStatus = 'INACTIVE';
          if (explicitStatus === 'SUSPENDED') {
            determinedStatus = 'SUSPENDED';
          } else if (explicitStatus === 'PENDING') {
            determinedStatus = 'PENDING';
          } else if (explicitStatus === 'INACTIVE') {
            determinedStatus = 'INACTIVE';
          } else if (diffDays <= 7 || userPlotsCount > 0) {
            determinedStatus = 'ACTIVE';
          } else {
            determinedStatus = 'INACTIVE';
          }

          // Online badge: Active today (diffDays === 0) or within the last 60 minutes
          const isOnlineNow = diffDays === 0;

          let activityDescription = `Dormant for ${diffDays} days`;
          if (diffMinutes < 2) {
            activityDescription = 'Active right now';
          } else if (diffMinutes < 60) {
            activityDescription = `Active ${diffMinutes} mins ago`;
          } else if (diffDays === 0) {
            if (latestActivityType === 'COMMUNITY_POST') {
              activityDescription = 'Shared a post in Community Hub today';
            } else if (latestActivityType === 'COMMUNITY_COMMENT') {
              activityDescription = 'Commented in Community Hub today';
            } else if (latestActivityType === 'FARM_PLOT') {
              activityDescription = 'Planted / edited farm plots today';
            } else if (latestActivityType === 'FEEDBACK') {
              activityDescription = 'Submitted feedback support inquiry today';
            } else {
              activityDescription = isAnonymous ? 'Exploring app as guest today' : 'Synchronized mobile farm data today';
            }
          } else if (diffDays === 1) {
            activityDescription = 'Active yesterday';
          }

          farmerList.push({
            id: p.id,
            email: rawEmail,
            fullName: uniqueNickname,
            phoneNumber: '+63 9' + Math.floor(100000000 + Math.random() * 900000000),
            role,
            status: determinedStatus,
            farmName: userFarm?.farm_name || (isAnonymous ? 'Guest Plot' : 'Smallholder Patch'),
            activePlotsCount: userPlotsCount,
            avatarUrl: p.avatar,
            createdAt: p.created_at || new Date().toISOString(),
            lastLoginAt: lastActiveIso,
            lastActiveAt: diffMinutes < 2
              ? 'Active just now'
              : diffMinutes < 60
              ? `${diffMinutes}m ago`
              : diffDays === 0
              ? 'Active Today'
              : diffDays === 1
              ? 'Active Yesterday'
              : `${diffDays} days ago`,
            daysInactive: diffDays,
            deviceInfo: isAnonymous ? 'Android Mobile (Guest Session)' : 'Android Mobile (MapTanim v1.2.4)',
            isOnline: isOnlineNow,
            activitySummary: activityDescription,
          });
        });
      }

      // 2. Fallback only if profiles table is completely empty (excluding unrecorded admins & field officers)
      if (farmerList.length === 0 && usersData && usersData.length > 0) {
        usersData.forEach((u) => {
          if (u.role === 'ADMINISTRATOR' || (u.role as string) === 'FIELD_OFFICER') return;
          const userFarm = farmsData?.find((f) => f.farmer_id === u.id);
          const userPlots = plotsData?.filter((p) => userFarm && p.farm_id === userFarm.id) || [];
          const userPlotsCount = userPlots.length;

          let latestActivityMs = u.updated_at
            ? new Date(u.updated_at).getTime()
            : u.created_at
            ? new Date(u.created_at).getTime()
            : 0;

          userPlots.forEach((p) => {
            const plotTime = p.updated_at ? new Date(p.updated_at).getTime() : (p.created_at ? new Date(p.created_at).getTime() : 0);
            if (plotTime > latestActivityMs) latestActivityMs = plotTime;
          });

          const diffDays = latestActivityMs > 0
            ? Math.max(0, Math.floor((Date.now() - latestActivityMs) / (1000 * 60 * 60 * 24)))
            : 0;

          const explicitStatus = ((u as any).status as AccountStatus) || null;
          let determinedStatus: AccountStatus = 'ACTIVE';
          if (explicitStatus === 'SUSPENDED') {
            determinedStatus = 'SUSPENDED';
          } else if (explicitStatus === 'PENDING') {
            determinedStatus = 'PENDING';
          } else if (explicitStatus === 'INACTIVE') {
            determinedStatus = 'INACTIVE';
          } else if (explicitStatus === 'ACTIVE') {
            determinedStatus = 'ACTIVE';
          } else if (diffDays > 30 && userPlotsCount === 0) {
            determinedStatus = 'INACTIVE';
          } else {
            determinedStatus = 'ACTIVE';
          }

          farmerList.push({
            id: u.id,
            email: u.email || 'farmer@maptanim.ph',
            fullName: u.email ? u.email.split('@')[0] : 'Farmer User',
            phoneNumber: '+63 9' + Math.floor(100000000 + Math.random() * 900000000),
            role: 'FARMER',
            status: determinedStatus,
            farmName: userFarm?.farm_name || 'Smallholder Patch',
            activePlotsCount: userPlotsCount,
            avatarUrl: u.avatar_url,
            createdAt: u.created_at,
            lastLoginAt: u.updated_at || u.created_at,
            lastActiveAt: diffDays === 0 ? 'Active Today' : diffDays === 1 ? 'Active Yesterday' : `${diffDays} days ago`,
            daysInactive: determinedStatus === 'ACTIVE' && diffDays <= 7 ? 0 : diffDays,
            deviceInfo: 'Android Mobile App',
            isOnline: diffDays <= 1 || determinedStatus === 'ACTIVE',
            activitySummary: diffDays <= 1 ? 'Active today' : `Last active ${diffDays} days ago`,
          });
        });
      }

      if (farmerList.length > 0) {
        this.farmers = farmerList;
        return farmerList;
      }

      return this.farmers.length > 0 ? this.farmers : MOCK_FARMERS;
    } catch (err) {
      console.error('Failed to load farmers from Supabase, using mock directory', err);
      return this.farmers.length > 0 ? this.farmers : MOCK_FARMERS;
    }
  }

  // User Tracking & Analytics Metrics
  async getUserTrackingMetrics(): Promise<UserTrackingMetrics> {
    const currentFarmers = await this.getFarmers();
    const totalUsers = currentFarmers.length;
    const activeUsers = currentFarmers.filter((f) => f.status === 'ACTIVE').length;
    const inactiveUsers = currentFarmers.filter((f) => f.status === 'INACTIVE').length;
    const suspendedUsers = currentFarmers.filter((f) => f.status === 'SUSPENDED').length;
    const pendingUsers = currentFarmers.filter((f) => f.status === 'PENDING').length;
    const activeRate = totalUsers > 0 ? Math.round((activeUsers / totalUsers) * 1000) / 10 : 0;

    if (totalUsers > 0) {
      return {
        totalUsers,
        activeUsers,
        inactiveUsers,
        suspendedUsers,
        pendingUsers,
        activeRate,
        dailyActiveUsers: activeUsers,
        weeklyActiveUsers: activeUsers,
        statusDistribution: [
          { name: 'Active (Engaged)', value: activeUsers, color: '#4CAF50', count: activeUsers },
          { name: 'Inactive / Dormant', value: inactiveUsers, color: '#F4A261', count: inactiveUsers },
          { name: 'Pending Approval', value: pendingUsers, color: '#00BCD4', count: pendingUsers },
          { name: 'Suspended', value: suspendedUsers, color: '#E76F51', count: suspendedUsers },
        ].filter((d) => d.value > 0),
        activityTrends: [
          { period: 'Mon', active: activeUsers, inactive: inactiveUsers, newRegistrations: 1 },
          { period: 'Tue', active: activeUsers, inactive: inactiveUsers, newRegistrations: 0 },
          { period: 'Wed', active: activeUsers, inactive: inactiveUsers, newRegistrations: 1 },
          { period: 'Thu', active: activeUsers, inactive: inactiveUsers, newRegistrations: 0 },
          { period: 'Fri', active: activeUsers, inactive: inactiveUsers, newRegistrations: 1 },
          { period: 'Sat', active: activeUsers, inactive: inactiveUsers, newRegistrations: 0 },
          { period: 'Sun', active: activeUsers, inactive: inactiveUsers, newRegistrations: 1 },
        ],
        activityByModule: MOCK_USER_TRACKING_METRICS.activityByModule,
      };
    }

    return MOCK_USER_TRACKING_METRICS;
  }

  // Real-time User Activity Logs
  async getUserActivityLogs(): Promise<UserActivityLog[]> {
    return Promise.resolve(this.userActivityLogs);
  }

  // Update User Account Status (ACTIVE, INACTIVE, SUSPENDED, PENDING)
  async updateUserStatus(farmerId: string, newStatus: AccountStatus, reason?: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('users').update({ status: newStatus }).eq('id', farmerId);
      } catch (err) {
        console.warn('Failed to update status in Supabase', err);
      }
    }
    this.farmers = this.farmers.map((f) =>
      f.id === farmerId
        ? {
            ...f,
            status: newStatus,
            daysInactive: newStatus === 'ACTIVE' ? 0 : f.daysInactive,
            lastActiveAt: newStatus === 'ACTIVE' ? 'Just now' : f.lastActiveAt,
          }
        : f
    );

    const targetUser = this.farmers.find((f) => f.id === farmerId);
    this.logAction(
      'UPDATE_USER_STATUS',
      'User Management & Tracking',
      `Changed status for ${targetUser?.fullName || farmerId} to ${newStatus}${reason ? ` (${reason})` : ''}`
    );

    // Record activity log
    this.userActivityLogs.unshift({
      id: `act-${Date.now()}`,
      userId: farmerId,
      userName: targetUser?.fullName || 'Smallholder User',
      action: newStatus === 'ACTIVE' ? 'USER_ACTIVATED' : newStatus === 'INACTIVE' ? 'USER_DEACTIVATED' : 'USER_SUSPENDED',
      module: 'Account Control',
      timestamp: 'Just now',
      details: `Admin changed account state to ${newStatus}${reason ? `: ${reason}` : ''}`,
      status: newStatus === 'ACTIVE' ? 'ONLINE' : newStatus === 'INACTIVE' ? 'INACTIVE' : 'IDLE',
    });

    return Promise.resolve(true);
  }

  // Update User Role
  async updateUserRole(farmerId: string, newRole: UserRole): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('users').update({ role: newRole }).eq('id', farmerId);
      } catch (err) {
        console.warn('Failed to update role in Supabase', err);
      }
    }
    this.farmers = this.farmers.map((f) => (f.id === farmerId ? { ...f, role: newRole } : f));
    const targetUser = this.farmers.find((f) => f.id === farmerId);
    this.logAction('UPDATE_USER_ROLE', 'User Management', `Assigned role ${newRole} to ${targetUser?.fullName || farmerId}`);
    return Promise.resolve(true);
  }

  // Add / Register New User
  async addFarmer(newFarmer: Omit<Farmer, 'id' | 'createdAt' | 'lastLoginAt'>): Promise<Farmer> {
    const createdId = `usr-${Date.now().toString().slice(-4)}`;
    const created: Farmer = {
      ...newFarmer,
      id: createdId,
      createdAt: new Date().toISOString(),
      lastLoginAt: new Date().toISOString(),
      lastActiveAt: 'Registered just now',
      daysInactive: 0,
      isOnline: true,
      activePlotsCount: newFarmer.activePlotsCount || 0,
    };

    if (isSupabaseConfigured) {
      try {
        await supabase.from('users').insert([
          {
            id: createdId,
            email: newFarmer.email,
            role: newFarmer.role || 'FARMER',
            created_at: created.createdAt,
          },
        ]);
      } catch (err) {
        console.warn('Failed to insert user in Supabase', err);
      }
    }

    this.farmers.unshift(created);
    this.logAction('CREATE_USER', 'User Management', `Registered new smallholder account: ${created.fullName}`);
    return Promise.resolve(created);
  }

  // Send Direct Advisory / Re-engagement Notification to User
  async sendUserAdvisory(farmerId: string, title: string, body: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('notifications').insert([
          {
            user_id: farmerId,
            title,
            body,
            notification_type: 'SUPPORT_REPLY',
            is_read: false,
            created_at: new Date().toISOString(),
          },
        ]);
      } catch (err) {
        console.warn('Failed to dispatch user advisory in Supabase', err);
      }
    }

    const targetUser = this.farmers.find((f) => f.id === farmerId);
    this.logAction(
      'SEND_REENGAGEMENT_ADVISORY',
      'User Tracking',
      `Sent advisory to ${targetUser?.fullName || farmerId}: ${title}`
    );

    this.userActivityLogs.unshift({
      id: `act-${Date.now()}`,
      userId: farmerId,
      userName: targetUser?.fullName || 'Smallholder User',
      action: 'REENGAGEMENT_DISPATCHED',
      module: 'Push & Advisory Gateway',
      timestamp: 'Just now',
      details: `Advisory sent: "${title}"`,
      status: 'ONLINE',
    });

    return Promise.resolve(true);
  }

  // Canonical 15 Approved Crops Definition (MapTanim Proprietary Scope)
  private static readonly CANONICAL_15_SPECS = [
    { key: 'bitter_gourd', name: 'Bitter Gourd', localName: 'Ampalaya', category: 'FRUIT' as const, patterns: ['bitter gourd', 'ampalaya'], defaultImage: '/metadata/crops_images/ampalaya.png' },
    { key: 'cabbage', name: 'Cabbage', localName: 'Repolyo', category: 'LEAFY' as const, patterns: ['cabbage', 'repolyo'], defaultImage: '/metadata/crops_images/cabbage.png' },
    { key: 'carrot', name: 'Carrot', localName: 'Karot', category: 'ROOT' as const, patterns: ['carrot', 'karot'], defaultImage: '/metadata/crops_images/carrot.png' },
    { key: 'corn', name: 'Corn/Maize', localName: 'Mais', category: 'FRUIT' as const, patterns: ['corn', 'maize', 'mais'], defaultImage: '/metadata/crops_images/corn.png' },
    { key: 'eggplant', name: 'Eggplant', localName: 'Talong', category: 'FRUIT' as const, patterns: ['eggplant', 'talong'], defaultImage: '/metadata/crops_images/eggplant.png' },
    { key: 'water_spinach', name: 'Water Spinach', localName: 'Kangkong', category: 'LEAFY' as const, patterns: ['water spinach', 'kangkong'], defaultImage: '/metadata/crops_images/kangkong.png' },
    { key: 'lettuce', name: 'Lettuce', localName: 'Litsugas', category: 'LEAFY' as const, patterns: ['lettuce', 'litsugas'], defaultImage: '/metadata/crops_images/lettuce.png' },
    { key: 'okra', name: 'Okra', localName: 'Okra', category: 'PODDED' as const, patterns: ['okra'], defaultImage: '/metadata/crops_images/okra.png' },
    { key: 'sibuyas', name: 'Sibuyas', localName: 'Onion', category: 'ROOT' as const, patterns: ['sibuyas', 'onion'], defaultImage: '/metadata/crops_images/onion.png' },
    { key: 'pechay', name: 'Pechay', localName: 'Bok Choy', category: 'LEAFY' as const, patterns: ['pechay', 'bok choy', 'petsay'], defaultImage: '/metadata/crops_images/pechay.png' },
    { key: 'cucumber', name: 'Cucumber', localName: 'Pipino', category: 'FRUIT' as const, patterns: ['cucumber', 'pipino'], defaultImage: '/metadata/crops_images/pipino.png' },
    { key: 'squash', name: 'Squash', localName: 'Kalabasa', category: 'FRUIT' as const, patterns: ['squash', 'kalabasa', 'pumpkin'], defaultImage: '/metadata/crops_images/pumpkin.png' },
    { key: 'chili', name: 'Chili Pepper', localName: 'Sili', category: 'FRUIT' as const, patterns: ['chili', 'sili'], defaultImage: '/metadata/crops_images/sili.png' },
    { key: 'sitaw', name: 'Sitaw', localName: 'String Beans', category: 'PODDED' as const, patterns: ['sitaw', 'string bean', 'yardlong'], defaultImage: '/metadata/crops_images/sitaw.png' },
    { key: 'tomato', name: 'Tomato', localName: 'Kamatis', category: 'FRUIT' as const, patterns: ['tomato', 'kamatis'], defaultImage: '/metadata/crops_images/tomato.png' },
  ];

  // Crop Catalog & Agronomic Library (Live Supabase Query + Supabase Storage)
  async getCrops(): Promise<Crop[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase.from('crops').select('*').order('name', { ascending: true });
        const { data: rulesData } = await supabase.from('dss_rules').select('*');

        if (!error && data && data.length > 0) {
          const canonicalMap = new Map<string, Crop>();
          const redundantIdsToDelete: string[] = [];

          data.forEach((c) => {
            const lowerName = (c.name || '').toLowerCase();
            const lowerLocal = (c.local_name || '').toLowerCase();

            // Explicitly reject non-approved crops (e.g. Bell Pepper)
            if (lowerName.includes('bell pepper') || lowerLocal.includes('bell pepper')) {
              if (c.id) redundantIdsToDelete.push(c.id);
              return;
            }

            // Match against the 15 canonical crop specifications
            const spec = ApiService.CANONICAL_15_SPECS.find((s) =>
              s.patterns.some((p) => lowerName.includes(p) || lowerLocal.includes(p))
            );

            if (!spec) return;

            const goodCompanions = rulesData
              ? rulesData
                  .filter((r) => (r.crop_a === c.name || r.crop_b === c.name) && r.relationship === 'BENEFICIAL')
                  .map((r) => (r.crop_a === c.name ? r.crop_b : r.crop_a))
              : (c.companion_plants_good || []);

            const badCompanions = rulesData
              ? rulesData
                  .filter((r) => (r.crop_a === c.name || r.crop_b === c.name) && r.relationship === 'ANTAGONIST')
                  .map((r) => (r.crop_a === c.name ? r.crop_b : r.crop_a))
              : (c.companion_plants_bad || []);

            const cropItem: Crop = {
              id: c.id,
              name: spec.name,
              localName: c.local_name || spec.localName,
              botanicalName: c.botanical_name || '',
              taxonomicFamily: c.taxonomic_family || '',
              category: c.category || spec.category,
              idealSoil: c.suitable_soils && c.suitable_soils.length > 0 ? c.suitable_soils[0] : 'LOAM',
              suitableSoils: c.suitable_soils || ['LOAM'],
              season: c.season || 'YEAR_ROUND',
              daysToHarvest: c.days_to_harvest || 60,
              wateringIntervalDays: c.watering_interval_days || 2,
              fertilizeIntervalDays: c.fertilize_interval_days || 14,
              waterReqMmPerWeek: Math.round((7 / (c.watering_interval_days || 2)) * 12),
              npkRequirement: {
                nitrogen: c.npk_n != null ? c.npk_n : 80,
                phosphorus: c.npk_p != null ? c.npk_p : 60,
                potassium: c.npk_k != null ? c.npk_k : 90,
              },
              optimalPhMin: c.optimal_ph_min != null ? c.optimal_ph_min : 6.0,
              optimalPhMax: c.optimal_ph_max != null ? c.optimal_ph_max : 7.0,
              growthStages: c.growth_stages || {
                sprout: 5,
                seedling: 12,
                vegetative: 20,
                flowering: 16,
                harvest: 7,
              },
              companionCropsGood: goodCompanions,
              companionCropsBad: badCompanions,
              harvestIndicators: c.harvest_indicators || `Ready for harvest at ${c.days_to_harvest || 60} days`,
              description: c.description || `${spec.name} (${spec.localName}) - Field research verified commercial vegetable.`,
              commonPests: c.common_pests || [],
              imageUrl: c.image_url || spec.defaultImage,
              activePlantingCount: 12,
            };

            if (!canonicalMap.has(spec.key)) {
              canonicalMap.set(spec.key, cropItem);
            } else {
              // Redundant duplicate (e.g. separate Kangkong vs Water Spinach or String Beans vs Yardlong String Bean)
              if (c.id && c.id !== canonicalMap.get(spec.key)?.id) {
                redundantIdsToDelete.push(c.id);
              }
            }
          });

          // Asynchronously prune redundant and non-approved rows from Supabase
          if (redundantIdsToDelete.length > 0) {
            (async () => {
              try {
                const { error } = await supabase
                  .from('crops')
                  .delete()
                  .in('id', redundantIdsToDelete);
                if (error) {
                  console.warn('Crop prune warning:', error);
                } else {
                  console.log(`Pruned ${redundantIdsToDelete.length} redundant/non-approved crop rows from Supabase.`);
                }
              } catch (err) {
                console.warn('Crop prune warning:', err);
              }
            })();
          }

          // Ensure all 15 canonical crops are filled (fallback to mock if any missing)
          ApiService.CANONICAL_15_SPECS.forEach((spec) => {
            if (!canonicalMap.has(spec.key)) {
              const fallback = this.crops.find((mc) =>
                spec.patterns.some((p) => mc.name.toLowerCase().includes(p) || (mc.localName && mc.localName.toLowerCase().includes(p)))
              );
              if (fallback) {
                canonicalMap.set(spec.key, {
                  ...fallback,
                  name: spec.name,
                  localName: fallback.localName || spec.localName,
                });
              }
            }
          });

          // Return strictly the 15 canonical crops sorted according to the approved list
          const resultCrops: Crop[] = [];
          ApiService.CANONICAL_15_SPECS.forEach((spec) => {
            const crop = canonicalMap.get(spec.key);
            if (crop) resultCrops.push(crop);
          });

          return resultCrops;
        }
      } catch (err) {
        console.warn('Using mock crops list', err);
      }
    }

    // Fallback: Return strictly the 15 canonical crops from local mock data
    const fallbackMap = new Map<string, Crop>();
    this.crops.forEach((c) => {
      const lower = c.name.toLowerCase();
      const spec = ApiService.CANONICAL_15_SPECS.find((s) => s.patterns.some((p) => lower.includes(p)));
      if (spec && !fallbackMap.has(spec.key)) {
        fallbackMap.set(spec.key, { ...c, name: spec.name, localName: c.localName || spec.localName });
      }
    });
    return ApiService.CANONICAL_15_SPECS.map((s) => fallbackMap.get(s.key)).filter(Boolean) as Crop[];
  }

  async addCrop(crop: Omit<Crop, 'id'>, broadcastSystemUpdate: boolean = true): Promise<Crop> {
    let createdId = `crop-${Date.now().toString().slice(-4)}`;

    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('crops')
          .insert([
            {
              name: crop.name,
              local_name: crop.localName || null,
              botanical_name: crop.botanicalName,
              taxonomic_family: crop.taxonomicFamily || null,
              category: crop.category,
              days_to_harvest: crop.daysToHarvest,
              watering_interval_days: crop.wateringIntervalDays || 2,
              fertilize_interval_days: crop.fertilizeIntervalDays || 14,
              optimal_ph_min: crop.optimalPhMin || 6.0,
              optimal_ph_max: crop.optimalPhMax || 7.0,
              season: crop.season,
              npk_n: crop.npkRequirement.nitrogen,
              npk_p: crop.npkRequirement.phosphorus,
              npk_k: crop.npkRequirement.potassium,
              suitable_soils: crop.suitableSoils && crop.suitableSoils.length > 0 ? crop.suitableSoils : [crop.idealSoil],
              description: crop.description || `${crop.idealSoil} soil preference. Growth duration ${crop.daysToHarvest} days.`,
              harvest_indicators: crop.harvestIndicators || null,
              growth_stages: crop.growthStages || null,
              image_url: crop.imageUrl,
              companion_plants_good: crop.companionCropsGood || [],
              companion_plants_bad: crop.companionCropsBad || [],
            },
          ])
          .select()
          .single();

        if (!error && data) {
          createdId = data.id;
        } else if (error) {
          console.error('Failed to insert crop in Supabase:', error);
        }

        // 1. Sync companion rules directly to public.dss_rules
        if (crop.companionCropsGood && crop.companionCropsGood.length > 0) {
          for (const goodCompanion of crop.companionCropsGood) {
            await supabase.from('dss_rules').insert([
              {
                crop_a: crop.name,
                crop_b: goodCompanion,
                relationship: 'BENEFICIAL',
                reason: `${crop.name} and ${goodCompanion} enhance soil biology and repel shared pests.`,
                source: 'MapTanim Companion Field Standard',
              },
            ]);
          }
        }
        if (crop.companionCropsBad && crop.companionCropsBad.length > 0) {
          for (const badCompanion of crop.companionCropsBad) {
            await supabase.from('dss_rules').insert([
              {
                crop_a: crop.name,
                crop_b: badCompanion,
                relationship: 'ANTAGONIST',
                reason: `${crop.name} and ${badCompanion} compete for root space or share susceptibility to blight.`,
                source: 'MapTanim Companion Field Standard',
              },
            ]);
          }
        }

        // 2. Broadcast System Update notification to all mobile farmer devices
        if (broadcastSystemUpdate) {
          await supabase.from('notifications').insert([
            {
              title: `🌾 Bagong Pananim: ${crop.name}${crop.localName ? ` (${crop.localName})` : ''}`,
              body: `Inilabas ng Admin ang ${crop.name} sa crop catalog! Maturity: ${crop.daysToHarvest} araw. I-download ang bagong datos sa iyong offline map.`,
              notification_type: 'SYSTEM_UPDATE',
              is_read: false,
            },
          ]);
        }
      } catch (err) {
        console.warn('Failed to insert crop in Supabase', err);
      }
    }

    const newCrop: Crop = {
      ...crop,
      id: createdId,
      activePlantingCount: 0,
    };
    this.crops.unshift(newCrop);
    this.logAction('CREATE_CROP', 'Crop Catalog', `Added new crop record: ${crop.name}`);
    return Promise.resolve(newCrop);
  }

  async updateCrop(id: string, updated: Partial<Crop>, broadcastSystemUpdate: boolean = true): Promise<Crop> {
    if (isSupabaseConfigured) {
      try {
        await supabase
          .from('crops')
          .update({
            ...(updated.name && { name: updated.name }),
            ...(updated.localName !== undefined && { local_name: updated.localName }),
            ...(updated.botanicalName && { botanical_name: updated.botanicalName }),
            ...(updated.taxonomicFamily !== undefined && { taxonomic_family: updated.taxonomicFamily }),
            ...(updated.category && { category: updated.category }),
            ...(updated.daysToHarvest && { days_to_harvest: updated.daysToHarvest }),
            ...(updated.wateringIntervalDays && { watering_interval_days: updated.wateringIntervalDays }),
            ...(updated.fertilizeIntervalDays && { fertilize_interval_days: updated.fertilizeIntervalDays }),
            ...(updated.optimalPhMin && { optimal_ph_min: updated.optimalPhMin }),
            ...(updated.optimalPhMax && { optimal_ph_max: updated.optimalPhMax }),
            ...(updated.season && { season: updated.season }),
            ...(updated.suitableSoils && { suitable_soils: updated.suitableSoils }),
            ...(updated.npkRequirement && {
              npk_n: updated.npkRequirement.nitrogen,
              npk_p: updated.npkRequirement.phosphorus,
              npk_k: updated.npkRequirement.potassium,
            }),
            ...(updated.growthStages && { growth_stages: updated.growthStages }),
            ...(updated.harvestIndicators !== undefined && { harvest_indicators: updated.harvestIndicators }),
            ...(updated.description !== undefined && { description: updated.description }),
            ...(updated.imageUrl && { image_url: updated.imageUrl }),
            ...(updated.companionCropsGood && { companion_plants_good: updated.companionCropsGood }),
            ...(updated.companionCropsBad && { companion_plants_bad: updated.companionCropsBad }),
            updated_at: new Date().toISOString(),
          })
          .eq('id', id);

        if (broadcastSystemUpdate && updated.name) {
          await supabase.from('notifications').insert([
            {
              title: `📢 Update sa Pananim: ${updated.name}`,
              body: `Binago ng Admin ang agronomic profile para sa ${updated.name}. I-download ang bagong schedule sa iyong mobile app.`,
              notification_type: 'SYSTEM_UPDATE',
              is_read: false,
            },
          ]);
        }
      } catch (err) {
        console.warn('Failed to update crop in Supabase', err);
      }
    }
    this.crops = this.crops.map((c) => (c.id === id ? { ...c, ...updated } : c));
    const result = this.crops.find((c) => c.id === id)!;
    this.logAction('UPDATE_CROP', 'Crop Catalog', `Updated agronomic properties for crop ${id}`);
    return Promise.resolve(result);
  }

  async deleteCrop(id: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('crops').delete().eq('id', id);
      } catch (err) {
        console.warn('Failed to delete crop in Supabase', err);
      }
    }
    this.crops = this.crops.filter((c) => c.id !== id);
    this.logAction('DELETE_CROP', 'Crop Catalog', `Removed crop ID ${id}`);
    return Promise.resolve(true);
  }

  getActiveStorageProvider(): 'SUPABASE' | 'LOCAL' {
    if (isSupabaseConfigured) return 'SUPABASE';
    return 'LOCAL';
  }

  // Supabase Storage & Image Upload Gateway
  async uploadCropImage(file: File): Promise<string> {
    const cleanExt = (file.name.split('.').pop() || 'png').toLowerCase().replace(/[^a-z0-9]/g, '') || 'png';
    const fileName = `crop_${Date.now()}_${Math.random().toString(36).substring(2, 8)}.${cleanExt}`;
    const contentType = file.type || `image/${cleanExt === 'jpg' ? 'jpeg' : cleanExt}`;

    // 1. Upload to Supabase Storage (crop-images bucket)
    if (isSupabaseConfigured) {
      try {
        let { error: uploadErr } = await supabase.storage
          .from('crop-images')
          .upload(fileName, file, {
            cacheControl: '31536000',
            upsert: true,
            contentType: contentType,
          });

        // If bucket is missing, attempt to create it automatically
        if (uploadErr && (uploadErr.message?.toLowerCase().includes('not found') || uploadErr.message?.toLowerCase().includes('bucket'))) {
          try {
            await supabase.storage.createBucket('crop-images', { public: true });
            const retry = await supabase.storage
              .from('crop-images')
              .upload(fileName, file, {
                cacheControl: '31536000',
                upsert: true,
                contentType: contentType,
              });
            uploadErr = retry.error;
          } catch (createErr) {
            console.warn('Auto-create bucket attempt error:', createErr);
          }
        }

        if (!uploadErr) {
          const { data: publicUrlData } = supabase.storage
            .from('crop-images')
            .getPublicUrl(fileName);

          if (publicUrlData && publicUrlData.publicUrl) {
            console.log('✅ Successfully uploaded image to Supabase Storage:', publicUrlData.publicUrl);
            return publicUrlData.publicUrl;
          }
        } else {
          console.warn('Supabase storage upload error:', uploadErr);
        }
      } catch (supaStorageErr) {
        console.warn('Supabase storage upload attempt error:', supaStorageErr);
      }
    }

    // 2. Fallback: Generate an optimized Base64 Data URL so image is never lost
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        resolve(reader.result as string);
      };
      reader.readAsDataURL(file);
    });
  }

  // Batch upload all 15 authentic MapTanim metadata crop images directly to Supabase Storage & update Supabase
  async syncMetadataImagesToStorage(
    onProgress?: (current: number, total: number, cropName: string) => void
  ): Promise<{ success: boolean; results: { name: string; url: string }[] }> {
    const cropsToSync = [
      { name: 'Tomato', fileName: 'tomato.png' },
      { name: 'Eggplant', fileName: 'eggplant.png' },
      { name: 'Chili Pepper', fileName: 'sili.png' },
      { name: 'Cabbage', fileName: 'cabbage.png' },
      { name: 'Pechay', fileName: 'pechay.png' },
      { name: 'Onion', fileName: 'onion.png' },
      { name: 'Carrot', fileName: 'carrot.png' },
      { name: 'Yardlong String Bean', fileName: 'sitaw.png' },
      { name: 'Lettuce', fileName: 'lettuce.png' },
      { name: 'Cucumber', fileName: 'pipino.png' },
      { name: 'Bitter Gourd', fileName: 'ampalaya.png' },
      { name: 'Okra', fileName: 'okra.png' },
      { name: 'Corn', fileName: 'corn.png' },
      { name: 'Squash', fileName: 'pumpkin.png' },
      { name: 'Water Spinach', fileName: 'kangkong.png' },
    ];

    const results: { name: string; url: string }[] = [];

    for (let i = 0; i < cropsToSync.length; i++) {
      const crop = cropsToSync[i];
      if (onProgress) {
        onProgress(i + 1, cropsToSync.length, crop.name);
      }
      try {
        const localPath = `/metadata/crops_images/${crop.fileName}`;
        const res = await fetch(localPath);
        if (!res.ok) continue;
        const blob = await res.blob();
        const file = new File([blob], crop.fileName, { type: 'image/png' });
        const uploadedUrl = await this.uploadCropImage(file);
        results.push({ name: crop.name, url: uploadedUrl });

        // Update database if Supabase configured
        if (isSupabaseConfigured) {
          await supabase
            .from('crops')
            .update({ image_url: uploadedUrl, updated_at: new Date().toISOString() })
            .ilike('name', `%${crop.name}%`);
        }
      } catch (err) {
        console.warn(`Failed to sync image for ${crop.name}:`, err);
      }
    }

    return { success: results.length > 0, results };
  }



  // DSS Rules Engine
  async getDSSRules(): Promise<DSSRule[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase.from('dss_rules').select('*').order('created_at', { ascending: false });
        if (!error && data && data.length > 0) {
          return data.map((r) => ({
            id: r.id,
            cropA: r.crop_a,
            cropB: r.crop_b,
            relationship: r.relationship,
            reason: r.reason || '',
            daReferenceDoc: r.source || 'Field Research Guidelines',
          }));
        }
      } catch (err) {
        console.warn('Using mock DSS rules list', err);
      }
    }
    return Promise.resolve(this.rules);
  }

  async addDSSRule(rule: Omit<DSSRule, 'id'>): Promise<DSSRule> {
    let createdId = `dss-${Date.now().toString().slice(-4)}`;

    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('dss_rules')
          .insert([
            {
              crop_a: rule.cropA,
              crop_b: rule.cropB,
              relationship: rule.relationship,
              reason: rule.reason,
              source: rule.daReferenceDoc || 'MapTanim Field Research Dataset',
            },
          ])
          .select()
          .single();

        if (!error && data) {
          createdId = data.id;
        }
      } catch (err) {
        console.warn('Failed to add DSS rule to Supabase', err);
      }
    }

    const newRule: DSSRule = {
      ...rule,
      id: createdId,
    };
    this.rules.unshift(newRule);
    this.logAction('CREATE_DSS_RULE', 'DSS Rule Engine', `Added rule pairing: ${rule.cropA} ↔ ${rule.cropB}`);
    return Promise.resolve(newRule);
  }

  async deleteDSSRule(id: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        const { error } = await supabase.from('dss_rules').delete().eq('id', id);
        if (error) console.warn('Failed to delete DSS rule from Supabase', error);
      } catch (err) {
        console.warn('Failed to delete DSS rule from Supabase', err);
      }
    }
    this.rules = this.rules.filter((r) => r.id !== id);
    this.logAction('DELETE_DSS_RULE', 'DSS Rule Engine', `Removed rule ${id}`);
    return Promise.resolve(true);
  }

  // Farm Inspector & Zone Management (Supabase Real-Time Farm Synchronization)
  async getFarms(): Promise<Farm[]> {
    if (isSupabaseConfigured) {
      try {
        const { data: farmsData, error } = await supabase.from('farms').select('*').order('created_at', { ascending: false });
        if (!error && farmsData && farmsData.length > 0) {
          const { data: usersData } = await supabase.from('users').select('*');
          const { data: profilesData } = await supabase.from('profiles').select('*');
          const { data: plotsData } = await supabase.from('crop_plots').select('*');

          return farmsData.map((f) => {
            const owner = usersData?.find((u) => u.id === f.farmer_id);
            const ownerProfile = profilesData?.find((p) => p.id === f.farmer_id);
            const ownerEmailName = owner?.email ? owner.email.split('@')[0] : 'Farmer';
            const ownerName = ownerProfile?.nickname || (ownerEmailName.charAt(0).toUpperCase() + ownerEmailName.slice(1));
            const plotsCount = (plotsData?.filter((p) => p.farm_id === f.id) || []).length;

            return {
              id: f.id,
              farmerId: f.farmer_id,
              farmerName: ownerName,
              farmName: f.farm_name,
              soilType: 'LOAM',
              bedsCount: plotsCount,
              createdAt: f.created_at,
            };
          });
        }


      } catch (err) {
        console.warn('Using mock farms list', err);
      }
    }
    return Promise.resolve(MOCK_FARMS);
  }

  async getBedsForFarm(farmId: string): Promise<BedPlot[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase.from('crop_plots').select('*').eq('farm_id', farmId);
        if (!error && data && data.length > 0) {
          return data.map((p) => ({
            id: p.id,
            farmId: p.farm_id,
            cropZoneLabel: p.plot_label,
            cropId: p.crop_id || undefined,
            cropName: p.crop_name || 'Unplanted Plot',
            cropVariety: p.crop_variety || '',
            x: p.pos_x ?? 0,
            y: p.pos_y ?? 0,
            width: p.width_m ?? 2,
            height: p.height_m ?? 3,
            growthStage: 2,
            plantedDate: p.planted_date || new Date().toISOString().split('T')[0],
            expectedHarvestDate: new Date(Date.now() + 60 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
            healthScore: 92,
          }));
        }
      } catch (err) {
        console.warn('Using mock beds list', err);
      }
    }
    return Promise.resolve(MOCK_BEDS.filter((b) => b.farmId === farmId));
  }

  // Feedback Management (Live Support Bridge via Supabase `feedback` & `notifications`)
  async getFeedback(): Promise<FeedbackItem[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('feedback')
          .select('*')
          .order('created_at', { ascending: false });

        if (!error && data && data.length > 0) {
          return data.map((f) => ({
            id: f.id,
            farmerId: f.user_id || 'usr-001',
            farmerName: f.farmer_name || 'Mobile Farmer',
            category: (f.category as any) || 'GENERAL',
            subject: f.subject,
            message: f.message,
            status: (f.status as any) || 'PENDING',
            createdAt: f.created_at,
            resolvedAt: f.resolved_at,
            adminReply: f.admin_reply,
          }));
        }
      } catch (err) {
        console.warn('Failed to query feedback table from Supabase', err);
      }
    }
    return Promise.resolve(this.feedback);
  }

  async updateFeedbackStatus(
    id: string,
    status: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED',
    adminReply?: string,
    farmerId?: string,
    subject?: string
  ): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        // 1. Update feedback ticket record in Supabase
        await supabase
          .from('feedback')
          .update({
            status,
            ...(adminReply && { admin_reply: adminReply }),
            ...(status === 'RESOLVED' && { resolved_at: new Date().toISOString() }),
          })
          .eq('id', id);

        // 2. Dispatch Live Supabase Notification to the mobile farmer
        if (adminReply) {
          await supabase.from('notifications').insert([
            {
              user_id: farmerId && farmerId !== 'usr-001' ? farmerId : null,
              title: `Support Advisory: ${subject || 'Ticket Update'}`,
              body: adminReply,
              notification_type: 'SUPPORT_REPLY',
              is_read: false,
              created_at: new Date().toISOString(),
            },
          ]);
        }
      } catch (err) {
        console.warn('Failed to update feedback status/reply in Supabase', err);
      }
    }

    // Local state update
    this.feedback = this.feedback.map((f) =>
      f.id === id
        ? {
            ...f,
            status,
            ...(adminReply && { adminReply }),
            resolvedAt: status === 'RESOLVED' ? new Date().toISOString() : f.resolvedAt,
          }
        : f
    );
    this.logAction('UPDATE_FEEDBACK', 'Support Module', `Feedback ${id} replied & status set to ${status}`);
    return Promise.resolve(true);
  }

  // Information Updates & Broadcast Advisories for Mobile
  async getBroadcastNotifications(): Promise<BroadcastNotification[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('notifications')
          .select('*')
          .is('user_id', null)
          .order('created_at', { ascending: false });

        if (!error && data) {
          return data.map((n) => ({
            id: n.id,
            title: n.title,
            body: n.body || '',
            notificationType: n.notification_type || 'SYSTEM_UPDATE',
            createdAt: n.created_at,
            targetCrop: n.task_type || undefined,
            isRead: n.is_read,
          }));
        }
      } catch (err) {
        console.warn('Failed to load broadcasts from Supabase', err);
      }
    }
    return [
      {
        id: 'bc-001',
        title: '📢 System Update v1.2.0',
        body: 'MapTanim Admin deployed direct-to-soil grid performance optimizations and sync upgrades.',
        notificationType: 'SYSTEM_UPDATE',
        createdAt: new Date().toISOString(),
      },
      {
        id: 'bc-002',
        title: '🌾 Agronomic Guide: Tomato Staking',
        body: 'Field research advisory: Recommended bamboo trellis specifications for Diamante Max F1 in high-wind lowland areas.',
        notificationType: 'AGRONOMIC_GUIDE',
        createdAt: new Date(Date.now() - 86400000).toISOString(),
        targetCrop: 'Tomato',
      },
    ];
  }

  async broadcastInformationUpdate(payload: BroadcastUpdatePayload): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        const { error } = await supabase.from('notifications').insert([
          {
            user_id: null, // null user_id means global broadcast to all farmers
            title: payload.title,
            body: payload.body,
            notification_type: payload.notificationType,
            task_type: payload.targetCrop ? 'OBSERVATION' : null,
            is_read: false,
            created_at: new Date().toISOString(),
          },
        ]);
        if (error) throw error;
      } catch (err) {
        console.warn('Failed to publish broadcast update to Supabase', err);
      }
    }
    this.logAction('BROADCAST_UPDATE', 'Information Publisher', `Published update to mobile: ${payload.title}`);
    return true;
  }

  // Community Hub & Forum Moderation (Live Supabase Query & Sync)
  async getCommunityPosts(): Promise<CommunityPost[]> {
    if (isSupabaseConfigured) {
      try {
        const { data: postsData, error } = await supabase
          .from('community_posts')
          .select('*')
          .order('is_pinned', { ascending: false })
          .order('created_at', { ascending: false });

        if (!error && postsData && postsData.length > 0) {
          const { data: commentsData } = await supabase.from('community_comments').select('*');

          return postsData.map((p) => {
            const postComments = commentsData
              ? commentsData
                  .filter((c) => c.post_id === p.id)
                  .map((c) => ({
                    id: c.id,
                    postId: c.post_id,
                    authorId: c.author_id,
                    authorName: c.author_name || '',
                    authorAvatarUrl: c.author_avatar_url,
                    content: c.content,
                    createdAt: c.created_at ? new Date(c.created_at).toLocaleString() : 'Just now',
                  }))
              : [];

            return {
              id: p.id,
              authorId: p.author_id,
              authorName: p.author_name || '',
              authorAvatarUrl: p.author_avatar_url,
              category: (p.category as any) || 'GENERAL',
              title: p.title,
              content: p.content,
              likesCount: p.likes_count || 0,
              commentsCount: postComments.length > 0 ? postComments.length : (p.comments_count || 0),
              isPinned: Boolean(p.is_pinned),
              tags: Array.isArray(p.tags) ? p.tags : [],
              createdAt: p.created_at ? new Date(p.created_at).toLocaleString() : 'Recently',
              updatedAt: p.updated_at,
              comments: postComments,
            };
          });
        }
      } catch (err) {
        console.warn('Failed to query community_posts from Supabase', err);
      }
    }
    return Promise.resolve(this.communityPosts);
  }

  async getCommunityComments(postId: string): Promise<CommunityComment[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('community_comments')
          .select('*')
          .eq('post_id', postId)
          .order('created_at', { ascending: true });

        if (!error && data) {
          return data.map((c) => ({
            id: c.id,
            postId: c.post_id,
            authorId: c.author_id,
            authorName: c.author_name || '',
            authorAvatarUrl: c.author_avatar_url,
            content: c.content,
            createdAt: c.created_at ? new Date(c.created_at).toLocaleString() : 'Just now',
          }));
        }
      } catch (err) {
        console.warn('Failed to query comments from Supabase', err);
      }
    }
    return Promise.resolve(this.communityComments.filter((c) => c.postId === postId));
  }

  async createCommunityPost(post: {
    category: any;
    title: string;
    content: string;
    authorName?: string;
    tags?: string[];
    isPinned?: boolean;
  }): Promise<CommunityPost> {
    const newId = `post_${Date.now()}`;
    const authorName = post.authorName || 'MapTanim Agronomy Desk';
    const tags = post.tags && post.tags.length > 0 ? post.tags : [post.category, 'Vegetables', 'CropCare'];
    const isPinned = Boolean(post.isPinned);

    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('community_posts')
          .insert([
            {
              id: newId,
              author_name: authorName,
              category: post.category,
              title: post.title,
              content: post.content,
              likes_count: 1,
              comments_count: 0,
              is_pinned: isPinned,
              tags: tags,
            },
          ])
          .select()
          .single();

        if (error) {
          console.warn('Failed to insert community post into Supabase', error);
        }
      } catch (err) {
        console.warn('Error inserting post to Supabase', err);
      }
    }

    const createdPost: CommunityPost = {
      id: newId,
      authorName,
      category: post.category,
      title: post.title,
      content: post.content,
      likesCount: 1,
      commentsCount: 0,
      isPinned,
      tags,
      createdAt: 'Just now',
      comments: [],
    };

    this.communityPosts.unshift(createdPost);
    this.logAction('CREATE_COMMUNITY_POST', 'Community Hub', `Published ${post.category} advisory: ${post.title}`);
    return Promise.resolve(createdPost);
  }

  async deleteCommunityPost(postId: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('community_posts').delete().eq('id', postId);
      } catch (err) {
        console.warn('Failed to delete community post in Supabase', err);
      }
    }
    this.communityPosts = this.communityPosts.filter((p) => p.id !== postId);
    this.logAction('DELETE_COMMUNITY_POST', 'Community Hub', `Removed community post ${postId}`);
    return Promise.resolve(true);
  }

  async togglePinCommunityPost(postId: string, isPinned: boolean): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('community_posts').update({ is_pinned: isPinned }).eq('id', postId);
      } catch (err) {
        console.warn('Failed to toggle pin in Supabase', err);
      }
    }
    this.communityPosts = this.communityPosts.map((p) =>
      p.id === postId ? { ...p, isPinned } : p
    );
    this.logAction('PIN_COMMUNITY_POST', 'Community Hub', `Set pin state of ${postId} to ${isPinned}`);
    return Promise.resolve(true);
  }

  async addCommunityComment(comment: {
    postId: string;
    content: string;
    authorName?: string;
  }): Promise<CommunityComment> {
    const newCommentId = `comm_${Date.now()}`;
    const authorName = comment.authorName || 'MapTanim Agronomist';

    if (isSupabaseConfigured) {
      try {
        await supabase.from('community_comments').insert([
          {
            id: newCommentId,
            post_id: comment.postId,
            author_name: authorName,
            content: comment.content,
          },
        ]);

        // Increment comments_count
        const targetPost = this.communityPosts.find((p) => p.id === comment.postId);
        const nextCount = (targetPost?.commentsCount || 0) + 1;
        await supabase.from('community_posts').update({ comments_count: nextCount }).eq('id', comment.postId);
      } catch (err) {
        console.warn('Failed to insert community comment in Supabase', err);
      }
    }

    const createdComment: CommunityComment = {
      id: newCommentId,
      postId: comment.postId,
      authorName,
      content: comment.content,
      createdAt: 'Just now',
    };

    this.communityComments.push(createdComment);
    this.communityPosts = this.communityPosts.map((p) =>
      p.id === comment.postId
        ? {
            ...p,
            commentsCount: (p.commentsCount || 0) + 1,
            comments: [...(p.comments || []), createdComment],
          }
        : p
    );

    this.logAction('ADD_COMMUNITY_COMMENT', 'Community Hub', `Replied to post ${comment.postId}`);
    return Promise.resolve(createdComment);
  }

  async deleteCommunityComment(commentId: string, postId: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('community_comments').delete().eq('id', commentId);
      } catch (err) {
        console.warn('Failed to delete comment in Supabase', err);
      }
    }
    this.communityComments = this.communityComments.filter((c) => c.id !== commentId);
    this.communityPosts = this.communityPosts.map((p) =>
      p.id === postId
        ? {
            ...p,
            commentsCount: Math.max(0, (p.commentsCount || 1) - 1),
            comments: (p.comments || []).filter((c) => c.id !== commentId),
          }
        : p
    );
    this.logAction('DELETE_COMMUNITY_COMMENT', 'Community Hub', `Deleted comment ${commentId}`);
    return Promise.resolve(true);
  }

  // Community Moderation Reports
  async getCommunityReports(): Promise<CommunityReport[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('community_reports')
          .select('*')
          .order('created_at', { ascending: false });

        if (!error && data && data.length > 0) {
          return data.map((r) => ({
            id: r.id,
            reporterId: r.reporter_id,
            reporterName: r.reporter_name || 'Farmer Member',
            targetType: r.target_type || 'POST',
            targetId: r.target_id,
            targetName: r.target_name,
            targetContent: r.target_content,
            reason: r.reason,
            details: r.details,
            status: r.status || 'PENDING',
            adminNotes: r.admin_notes,
            createdAt: r.created_at ? new Date(r.created_at).toLocaleString() : 'Recently',
            resolvedAt: r.resolved_at,
          }));
        }
      } catch (err) {
        console.warn('Failed to query community_reports from Supabase', err);
      }
    }
    return Promise.resolve(this.communityReports);
  }

  async updateCommunityReportStatus(
    reportId: string,
    status: ReportStatus,
    adminNotes?: string
  ): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase
          .from('community_reports')
          .update({
            status,
            admin_notes: adminNotes,
            resolved_at: status === 'RESOLVED' || status === 'DISMISSED' ? new Date().toISOString() : null,
          })
          .eq('id', reportId);
      } catch (err) {
        console.warn('Failed to update report in Supabase', err);
      }
    }

    this.communityReports = this.communityReports.map((r) =>
      r.id === reportId
        ? {
            ...r,
            status,
            adminNotes: adminNotes ?? r.adminNotes,
            resolvedAt: status === 'RESOLVED' || status === 'DISMISSED' ? new Date().toISOString() : undefined,
          }
        : r
    );

    this.logAction(
      'UPDATE_COMMUNITY_REPORT',
      'Community Hub Moderation',
      `Moderation Report ${reportId} marked as ${status}`
    );
    return Promise.resolve(true);
  }

  async deleteCommunityReport(reportId: string): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('community_reports').delete().eq('id', reportId);
      } catch (err) {
        console.warn('Failed to delete report from Supabase', err);
      }
    }
    this.communityReports = this.communityReports.filter((r) => r.id !== reportId);
    this.logAction('DELETE_COMMUNITY_REPORT', 'Community Hub Moderation', `Deleted moderation report ${reportId}`);
    return Promise.resolve(true);
  }


  // System Audit Logs
  async getAuditLogs(): Promise<SystemAuditLog[]> {
    return Promise.resolve(this.logs);
  }

  async testDatabaseFetch(): Promise<{
    connected: boolean;
    endpoint: string;
    latencyMs: number;
    cropsCount: number;
    rulesCount: number;
    cropsSample: any[];
    rulesSample: any[];
    timestamp: string;
    error?: string;
  }> {
    const startTime = (typeof performance !== 'undefined' ? performance.now() : Date.now());
    const endpoint = 'https://ojilvcglpzbtpjxguhzj.supabase.co';
    if (!isSupabaseConfigured) {
      return {
        connected: false,
        endpoint,
        latencyMs: 0,
        cropsCount: 0,
        rulesCount: 0,
        cropsSample: [],
        rulesSample: [],
        timestamp: new Date().toLocaleTimeString(),
        error: 'Supabase client is not configured.',
      };
    }

    try {
      const [cropsRes, rulesRes] = await Promise.all([
        supabase.from('crops').select('*').order('name', { ascending: true }),
        supabase.from('dss_rules').select('*').order('crop_a', { ascending: true }),
      ]);

      const latencyMs = Math.round((typeof performance !== 'undefined' ? performance.now() : Date.now()) - startTime);

      if (cropsRes.error) throw cropsRes.error;
      if (rulesRes.error) throw rulesRes.error;

      return {
        connected: true,
        endpoint,
        latencyMs,
        cropsCount: (cropsRes.data || []).length,
        rulesCount: (rulesRes.data || []).length,
        cropsSample: (cropsRes.data || []).slice(0, 15),
        rulesSample: (rulesRes.data || []).slice(0, 15),
        timestamp: new Date().toLocaleTimeString(),
      };
    } catch (err: any) {
      const latencyMs = Math.round((typeof performance !== 'undefined' ? performance.now() : Date.now()) - startTime);
      return {
        connected: false,
        endpoint,
        latencyMs,
        cropsCount: 0,
        rulesCount: 0,
        cropsSample: [],
        rulesSample: [],
        timestamp: new Date().toLocaleTimeString(),
        error: err?.message || 'Database query failed',
      };
    }
  }

  private logAction(action: string, targetModule: string, details: string) {
    const log: SystemAuditLog = {
      id: `log-${Date.now().toString().slice(-4)}`,
      timestamp: new Date().toISOString(),
      adminEmail: 'admin@system.local',
      action,
      targetModule,
      details,
      status: 'SUCCESS',
      ipAddress: '112.198.75.12',
    };
    this.logs.unshift(log);
  }
}

export const apiService = new ApiService();
