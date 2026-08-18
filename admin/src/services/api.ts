import { Farmer, Farm, BedPlot, Crop, DSSRule, FeedbackItem, SystemAuditLog, DashboardStats, CommunityPost, CommunityComment, CommunityReport, ReportStatus } from '../types';
import { MOCK_CROPS, MOCK_DSS_RULES, MOCK_FARMS, MOCK_BEDS, MOCK_FEEDBACK, MOCK_LOGS, MOCK_STATS } from './mockData';
import { supabase, isSupabaseConfigured } from './supabase';

class ApiService {
  private farmers: Farmer[] = [];
  private crops: Crop[] = [...MOCK_CROPS];
  private rules: DSSRule[] = [...MOCK_DSS_RULES];
  private feedback: FeedbackItem[] = [...MOCK_FEEDBACK];
  private logs: SystemAuditLog[] = [...MOCK_LOGS];
  private communityPosts: CommunityPost[] = [];
  private communityComments: CommunityComment[] = [];
  private communityReports: CommunityReport[] = [
    {
      id: 'rep_1',
      reporterName: 'Ka Ryan Vasquez',
      targetType: 'POST',
      targetId: 'post_3',
      targetName: 'Aling Maria Juanillo',
      targetContent: '🚜 Bamboo Stakes & Insect Netting Seed Swap — Extra Sitaw Seeds',
      reason: 'Spam / Commercial Selling',
      details: 'Selling untreated seeds without phytosanitary clearance or certified label.',
      status: 'PENDING',
      createdAt: '3 hours ago',
    },
    {
      id: 'rep_2',
      reporterName: 'Farmer Partner',
      targetType: 'USER',
      targetId: 'james',
      targetName: 'Farmer James',
      targetContent: 'Chat participant: Farmer James',
      reason: 'Harassment / Unsolicited Direct Messaging',
      details: 'Sent repetitive unsolicited promotional messages in direct chat.',
      status: 'PENDING',
      createdAt: '1 day ago',
    },
  ];

  // Dashboard Overview Stats (Live Query Across Supabase Tables)
  async getDashboardStats(): Promise<DashboardStats> {
    if (isSupabaseConfigured) {
      try {
        const { count: profileCount } = await supabase.from('profiles').select('*', { count: 'exact', head: true });
        const { count: farmCount } = await supabase.from('farms').select('*', { count: 'exact', head: true });
        const { count: bedCount } = await supabase.from('crop_plots').select('*', { count: 'exact', head: true });
        const { data: harvestData } = await supabase.from('harvest_records').select('yield_kg');

        let totalHarvestKg = MOCK_STATS.totalHarvestKgThisMonth;
        if (harvestData && harvestData.length > 0) {
          totalHarvestKg = harvestData.reduce((acc, r) => acc + (r.yield_kg || 0), 0);
        }

        return {
          ...MOCK_STATS,
          totalFarmers: profileCount && profileCount > 0 ? profileCount : 2,
          activeFarms: farmCount && farmCount > 0 ? farmCount : MOCK_STATS.activeFarms,
          totalPlots: bedCount && bedCount > 0 ? bedCount : MOCK_STATS.totalPlots,
          totalHarvestKgThisMonth: totalHarvestKg,
        };
      } catch (err) {
        console.warn('Supabase fetch failed, falling back to stats', err);
      }
    }
    return Promise.resolve(MOCK_STATS);
  }

  // Farmer Management (Primary Source: Real Mobile App Accounts from Supabase `profiles`)
  async getFarmers(): Promise<Farmer[]> {
    if (!isSupabaseConfigured) {
      return [];
    }

    try {
      // Primary source: public.profiles table (real mobile app farmer accounts)
      const { data: profilesData } = await supabase
        .from('profiles')
        .select('*')
        .order('created_at', { ascending: false });

      const { data: usersData } = await supabase.from('users').select('*');
      const { data: farmsData } = await supabase.from('farms').select('*');
      const { data: plotsData } = await supabase.from('crop_plots').select('*');

      const farmerList: Farmer[] = [];

      // 1. Process real mobile accounts from profiles table
      if (profilesData && profilesData.length > 0) {
        profilesData.forEach((p) => {
          const userAccount = usersData?.find((u) => u.id === p.id);
          // Skip if administrator account
          if (userAccount?.role === 'ADMINISTRATOR') return;

          const userFarm = farmsData?.find((f) => f.farmer_id === p.id);
          const userPlotsCount = userFarm
            ? (plotsData?.filter((plot) => plot.farm_id === userFarm.id) || []).length
            : 0;

          const rawEmail = userAccount?.email && userAccount.email.trim() !== ''
            ? userAccount.email
            : `${p.nickname || 'farmer'}@mobile.app`;

          farmerList.push({
            id: p.id,
            email: rawEmail,
            fullName: p.nickname || 'Mobile Farmer',
            phoneNumber: 'Unspecified',
            role: 'FARMER',
            status: (userAccount?.status as any) || 'ACTIVE',
            farmName: userFarm?.farm_name || 'No Farm Configured',
            activePlotsCount: userPlotsCount,
            avatarUrl: p.avatar,
            createdAt: p.created_at || new Date().toISOString(),
            lastLoginAt: p.updated_at || p.created_at || new Date().toISOString(),
          });
        });
      }

      // If profiles table is empty, fallback to non-admin users from users table
      if (farmerList.length === 0 && usersData && usersData.length > 0) {
        usersData.forEach((u) => {
          if (u.role === 'ADMINISTRATOR') return;
          const userFarm = farmsData?.find((f) => f.farmer_id === u.id);
          const userPlotsCount = userFarm
            ? (plotsData?.filter((p) => p.farm_id === userFarm.id) || []).length
            : 0;

          farmerList.push({
            id: u.id,
            email: u.email || 'No Email Registered',
            fullName: u.email ? u.email.split('@')[0] : 'Farmer User',
            phoneNumber: 'Unspecified',
            role: 'FARMER',
            status: (u as any).status || 'ACTIVE',
            farmName: userFarm?.farm_name || 'No Farm Configured',
            activePlotsCount: userPlotsCount,
            avatarUrl: u.avatar_url,
            createdAt: u.created_at,
            lastLoginAt: u.updated_at || u.created_at,
          });
        });
      }



      return farmerList;
    } catch (err) {
      console.error('Failed to load farmers from Supabase', err);
      return [];
    }
  }

  async updateFarmerStatus(farmerId: string, newStatus: 'ACTIVE' | 'SUSPENDED' | 'PENDING'): Promise<boolean> {
    if (isSupabaseConfigured) {
      try {
        await supabase.from('users').update({ status: newStatus }).eq('id', farmerId);
      } catch (err) {
        console.warn('Failed to update status in Supabase', err);
      }
    }
    this.farmers = this.farmers.map((f) => (f.id === farmerId ? { ...f, status: newStatus } : f));
    this.logAction('UPDATE_FARMER_STATUS', 'Farmer Management', `Changed status for ${farmerId} to ${newStatus}`);
    return Promise.resolve(true);
  }

  // Crop Catalog & Agronomic Library (Live Supabase Query)
  async getCrops(): Promise<Crop[]> {
    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase.from('crops').select('*').order('name', { ascending: true });
        const { data: rulesData } = await supabase.from('dss_rules').select('*');

        if (!error && data && data.length > 0) {
          return data.map((c) => {
            const goodCompanions = rulesData
              ? rulesData
                  .filter((r) => (r.crop_a === c.name || r.crop_b === c.name) && r.relationship === 'BENEFICIAL')
                  .map((r) => (r.crop_a === c.name ? r.crop_b : r.crop_a))
              : [];

            const badCompanions = rulesData
              ? rulesData
                  .filter((r) => (r.crop_a === c.name || r.crop_b === c.name) && r.relationship === 'INCOMPATIBLE')
                  .map((r) => (r.crop_a === c.name ? r.crop_b : r.crop_a))
              : [];

            return {
              id: c.id,
              name: c.name,
              botanicalName: c.botanical_name || '',
              category: c.category,
              idealSoil: c.suitable_soils && c.suitable_soils.length > 0 ? c.suitable_soils[0] : 'LOAM',
              season: c.season || 'YEAR_ROUND',
              daysToHarvest: c.days_to_harvest || 60,
              waterReqMmPerWeek: 40,
              npkRequirement: {
                nitrogen: c.npk_n || 80,
                phosphorus: c.npk_p || 60,
                potassium: c.npk_k || 90,
              },
              companionCropsGood: goodCompanions,
              companionCropsBad: badCompanions,
              imageUrl:
                c.image_url ||
                'https://images.unsplash.com/photo-1598170845058-12ef4a457539?auto=format&fit=crop&w=300&q=80',
              activePlantingCount: 12,
            };
          });
        }
      } catch (err) {
        console.warn('Using mock crops list', err);
      }
    }
    return Promise.resolve(this.crops);
  }

  async addCrop(crop: Omit<Crop, 'id'>): Promise<Crop> {
    let createdId = `crop-${Date.now().toString().slice(-4)}`;

    if (isSupabaseConfigured) {
      try {
        const { data, error } = await supabase
          .from('crops')
          .insert([
            {
              name: crop.name,
              botanical_name: crop.botanicalName,
              category: crop.category,
              days_to_harvest: crop.daysToHarvest,
              season: crop.season,
              description: `${crop.idealSoil} soil requirement`,
              image_url: crop.imageUrl,
            },
          ])
          .select()
          .single();

        if (!error && data) {
          createdId = data.id;
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

  async updateCrop(id: string, updated: Partial<Crop>): Promise<Crop> {
    if (isSupabaseConfigured) {
      try {
        await supabase
          .from('crops')
          .update({
            ...(updated.name && { name: updated.name }),
            ...(updated.botanicalName && { botanical_name: updated.botanicalName }),
            ...(updated.category && { category: updated.category }),
            ...(updated.daysToHarvest && { days_to_harvest: updated.daysToHarvest }),
            ...(updated.season && { season: updated.season }),
            ...(updated.imageUrl && { image_url: updated.imageUrl }),
          })
          .eq('id', id);
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
            daReferenceDoc: r.source || 'BPI Guidelines',
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
              source: rule.daReferenceDoc || 'DA-BAR Companion Guide',
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

  // Farm Inspector & Zone Telemetry (Supabase Real-Time Farm Synchronization)
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
                    authorName: c.author_name || 'Farmer Partner',
                    authorAvatarUrl: c.author_avatar_url,
                    content: c.content,
                    createdAt: c.created_at ? new Date(c.created_at).toLocaleString() : 'Just now',
                  }))
              : [];

            return {
              id: p.id,
              authorId: p.author_id,
              authorName: p.author_name || 'Mobile Farmer',
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
            authorName: c.author_name || 'Farmer Partner',
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
    const authorName = post.authorName || 'MapTanim Agronomy Admin';
    const tags = post.tags || [post.category, 'OfficialAdvisory'];
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
