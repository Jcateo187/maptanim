export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[];

export type RoleEnum = 'FARMER' | 'ADMINISTRATOR' | 'GUEST';
export type UserStatusEnum = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING';
export type SoilTypeEnum = 'LOAM' | 'CLAY' | 'SANDY' | 'SILTY' | 'PEATY' | 'CHALKY';
export type SeasonEnum = 'DRY' | 'WET' | 'YEAR_ROUND';
export type CategoryEnum = 'BULB' | 'STEM' | 'SHOOT' | 'LEAFY' | 'FLOWER' | 'FRUIT' | 'ROOT' | 'TUBER';
export type TaskTypeEnum =
  | 'WATER'
  | 'FERTILIZE'
  | 'HARVEST'
  | 'PEST_ALERT'
  | 'APPLY_PESTICIDE'
  | 'SOIL_AMENDMENT'
  | 'PRUNING'
  | 'OBSERVATION'
  | 'WEED'
  | 'TRELLIS'
  | 'NUTRITION'
  | 'ROTATION_ALERT';
export type CompanionRelationEnum = 'BENEFICIAL' | 'ANTAGONIST' | 'NEUTRAL';

export interface Database {
  public: {
    Tables: {
      users: {
        Row: {
          id: string;
          email: string | null;
          role: RoleEnum;
          status: UserStatusEnum;
          avatar_url: string | null;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          email?: string | null;
          role?: RoleEnum;
          status?: UserStatusEnum;
          avatar_url?: string | null;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          email?: string | null;
          role?: RoleEnum;
          status?: UserStatusEnum;
          avatar_url?: string | null;
          created_at?: string;
          updated_at?: string;
        };
      };
      profiles: {
        Row: {
          id: string;
          nickname: string | null;
          avatar: string | null;
          nickname_updated_at: string | null;
          tutorial_completed_at: string | null;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id: string;
          nickname?: string | null;
          avatar?: string | null;
          nickname_updated_at?: string | null;
          tutorial_completed_at?: string | null;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          nickname?: string | null;
          avatar?: string | null;
          nickname_updated_at?: string | null;
          tutorial_completed_at?: string | null;
          created_at?: string;
          updated_at?: string;
        };
      };
      crops: {
        Row: {
          id: string;
          name: string;
          local_name: string | null;
          botanical_name: string | null;
          taxonomic_family: string | null;
          botanical_family: string | null;
          category: CategoryEnum;
          days_to_harvest: number;
          watering_interval_days: number;
          fertilize_interval_days: number;
          weeding_interval_days: number;
          optimal_ph_min: number | null;
          optimal_ph_max: number | null;
          optimal_temp_min: number | null;
          optimal_temp_max: number | null;
          season: SeasonEnum;
          npk_n: number | null;
          npk_p: number | null;
          npk_k: number | null;
          suitable_soils: SoilTypeEnum[] | null;
          image_url: string | null;
          growth_stages: Json | null;
          harvest_indicators: string | null;
          needs_trellis: boolean | null;
          trellis_type: string | null;
          preferred_planting_method: string | null;
          soil_prep_tagalog: string | null;
          harvest_signs_tagalog: string | null;
          common_pests: string[] | null;
          companion_plants_good: string[] | null;
          companion_plants_bad: string[] | null;
          description: string | null;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          name: string;
          local_name?: string | null;
          botanical_name?: string | null;
          taxonomic_family?: string | null;
          botanical_family?: string | null;
          category: CategoryEnum;
          days_to_harvest: number;
          watering_interval_days?: number;
          fertilize_interval_days?: number;
          weeding_interval_days?: number;
          optimal_ph_min?: number | null;
          optimal_ph_max?: number | null;
          optimal_temp_min?: number | null;
          optimal_temp_max?: number | null;
          season?: SeasonEnum;
          npk_n?: number | null;
          npk_p?: number | null;
          npk_k?: number | null;
          suitable_soils?: SoilTypeEnum[] | null;
          image_url?: string | null;
          growth_stages?: Json | null;
          harvest_indicators?: string | null;
          needs_trellis?: boolean | null;
          trellis_type?: string | null;
          preferred_planting_method?: string | null;
          soil_prep_tagalog?: string | null;
          harvest_signs_tagalog?: string | null;
          common_pests?: string[] | null;
          companion_plants_good?: string[] | null;
          companion_plants_bad?: string[] | null;
          description?: string | null;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          name?: string;
          local_name?: string | null;
          botanical_name?: string | null;
          taxonomic_family?: string | null;
          botanical_family?: string | null;
          category?: CategoryEnum;
          days_to_harvest?: number;
          watering_interval_days?: number;
          fertilize_interval_days?: number;
          weeding_interval_days?: number;
          optimal_ph_min?: number | null;
          optimal_ph_max?: number | null;
          optimal_temp_min?: number | null;
          optimal_temp_max?: number | null;
          season?: SeasonEnum;
          npk_n?: number | null;
          npk_p?: number | null;
          npk_k?: number | null;
          suitable_soils?: SoilTypeEnum[] | null;
          image_url?: string | null;
          growth_stages?: Json | null;
          harvest_indicators?: string | null;
          needs_trellis?: boolean | null;
          trellis_type?: string | null;
          preferred_planting_method?: string | null;
          soil_prep_tagalog?: string | null;
          harvest_signs_tagalog?: string | null;
          common_pests?: string[] | null;
          companion_plants_good?: string[] | null;
          companion_plants_bad?: string[] | null;
          description?: string | null;
          created_at?: string;
          updated_at?: string;
        };
      };
      dss_rules: {
        Row: {
          id: string;
          crop_a: string;
          crop_b: string;
          relationship: CompanionRelationEnum;
          reason: string | null;
          source: string | null;
          created_at: string;
        };
        Insert: {
          id?: string;
          crop_a: string;
          crop_b: string;
          relationship: CompanionRelationEnum;
          reason?: string | null;
          source?: string | null;
          created_at?: string;
        };
        Update: {
          id?: string;
          crop_a?: string;
          crop_b?: string;
          relationship?: CompanionRelationEnum;
          reason?: string | null;
          source?: string | null;
          created_at?: string;
        };
      };
      farms: {
        Row: {
          id: string;
          farmer_id: string;
          farm_name: string;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          farmer_id: string;
          farm_name: string;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          farmer_id?: string;
          farm_name?: string;
          created_at?: string;
          updated_at?: string;
        };
      };
      crop_plots: {
        Row: {
          id: string;
          farm_id: string;
          plot_label: string;
          crop_name: string | null;
          crop_id: string | null;
          crop_variety: string | null;
          soil_type: string;
          pos_x: number;
          pos_y: number;
          width_m: number;
          height_m: number;
          rotation_deg: number;
          notes: string | null;
          planted_date: string | null;
          is_active: boolean;
          current_stage: string;
          harvest_count: number;
          total_yield_kg: number;
          previous_crops_history: string;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          farm_id: string;
          plot_label: string;
          crop_name?: string | null;
          crop_id?: string | null;
          crop_variety?: string | null;
          soil_type?: string;
          pos_x?: number;
          pos_y?: number;
          width_m?: number;
          height_m?: number;
          rotation_deg?: number;
          notes?: string | null;
          planted_date?: string | null;
          is_active?: boolean;
          current_stage?: string;
          harvest_count?: number;
          total_yield_kg?: number;
          previous_crops_history?: string;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          farm_id?: string;
          plot_label?: string;
          crop_name?: string | null;
          crop_id?: string | null;
          crop_variety?: string | null;
          soil_type?: string;
          pos_x?: number;
          pos_y?: number;
          width_m?: number;
          height_m?: number;
          rotation_deg?: number;
          notes?: string | null;
          planted_date?: string | null;
          is_active?: boolean;
          current_stage?: string;
          harvest_count?: number;
          total_yield_kg?: number;
          previous_crops_history?: string;
          created_at?: string;
          updated_at?: string;
        };
      };
      crop_zones: {
        Row: {
          id: string;
          plot_id: string;
          crop_name: string | null;
          crop_id: string | null;
          offset_x: number;
          offset_y: number;
          width_m: number;
          height_m: number;
          spacing_m: number;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id: string;
          plot_id: string;
          crop_name?: string | null;
          crop_id?: string | null;
          offset_x?: number;
          offset_y?: number;
          width_m?: number;
          height_m?: number;
          spacing_m?: number;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          plot_id?: string;
          crop_name?: string | null;
          crop_id?: string | null;
          offset_x?: number;
          offset_y?: number;
          width_m?: number;
          height_m?: number;
          spacing_m?: number;
          created_at?: string;
          updated_at?: string;
        };
      };
      tasks: {
        Row: {
          id: string;
          farm_id: string;
          plot_id: string | null;
          plot_label: string | null;
          crop_name: string | null;
          task_type: TaskTypeEnum;
          title: string;
          sub_label: string | null;
          due_date: string;
          is_completed: boolean;
          completed_at: string | null;
          notes: string | null;
          created_at: string;
        };
        Insert: {
          id?: string;
          farm_id: string;
          plot_id?: string | null;
          plot_label?: string | null;
          crop_name?: string | null;
          task_type: TaskTypeEnum;
          title: string;
          sub_label?: string | null;
          due_date: string;
          is_completed?: boolean;
          completed_at?: string | null;
          notes?: string | null;
          created_at?: string;
        };
        Update: {
          id?: string;
          farm_id?: string;
          plot_id?: string | null;
          plot_label?: string | null;
          crop_name?: string | null;
          task_type?: TaskTypeEnum;
          title?: string;
          sub_label?: string | null;
          due_date?: string;
          is_completed?: boolean;
          completed_at?: string | null;
          notes?: string | null;
          created_at?: string;
        };
      };
      harvest_records: {
        Row: {
          id: string;
          farm_id: string;
          plot_id: string | null;
          farm_name: string | null;
          plot_label: string | null;
          crop_name: string;
          crop_variety: string | null;
          planted_date: string | null;
          harvested_at: string;
          harvested_date: string | null;
          growing_duration_days: number | null;
          yield_kg: number;
          yield_units: number | null;
          quality_grade: string | null;
          quality_rating: number | null;
          seasonal_insight: string | null;
          rotation_suggestion: string | null;
          fallow_until_date: string | null;
          notes: string | null;
          created_at: string;
        };
        Insert: {
          id?: string;
          farm_id: string;
          plot_id?: string | null;
          farm_name?: string | null;
          plot_label?: string | null;
          crop_name: string;
          crop_variety?: string | null;
          planted_date?: string | null;
          harvested_at?: string;
          harvested_date?: string | null;
          growing_duration_days?: number | null;
          yield_kg?: number;
          yield_units?: number | null;
          quality_grade?: string | null;
          quality_rating?: number | null;
          seasonal_insight?: string | null;
          rotation_suggestion?: string | null;
          fallow_until_date?: string | null;
          notes?: string | null;
          created_at?: string;
        };
        Update: {
          id?: string;
          farm_id?: string;
          plot_id?: string | null;
          farm_name?: string | null;
          plot_label?: string | null;
          crop_name?: string;
          crop_variety?: string | null;
          planted_date?: string | null;
          harvested_at?: string;
          harvested_date?: string | null;
          growing_duration_days?: number | null;
          yield_kg?: number;
          yield_units?: number | null;
          quality_grade?: string | null;
          quality_rating?: number | null;
          seasonal_insight?: string | null;
          rotation_suggestion?: string | null;
          fallow_until_date?: string | null;
          notes?: string | null;
          created_at?: string;
        };
      };
      crop_rotation_log: {
        Row: {
          id: string;
          farm_id: string;
          plot_id: string;
          botanical_family: string;
          crop_name: string;
          planted_date: string;
          harvested_date: string | null;
          cycle_count: number | null;
          created_at: string;
        };
        Insert: {
          id?: string;
          farm_id: string;
          plot_id: string;
          botanical_family: string;
          crop_name: string;
          planted_date: string;
          harvested_date?: string | null;
          cycle_count?: number | null;
          created_at?: string;
        };
        Update: {
          id?: string;
          farm_id?: string;
          plot_id?: string;
          botanical_family?: string;
          crop_name?: string;
          planted_date?: string;
          harvested_date?: string | null;
          cycle_count?: number | null;
          created_at?: string;
        };
      };
      feedback: {
        Row: {
          id: string;
          user_id: string | null;
          farmer_name: string;
          farm_name: string | null;
          category: string;
          subject: string;
          message: string;
          status: string;
          admin_reply: string | null;
          created_at: string;
          resolved_at: string | null;
        };
        Insert: {
          id?: string;
          user_id?: string | null;
          farmer_name?: string;
          farm_name?: string | null;
          category?: string;
          subject: string;
          message: string;
          status?: string;
          admin_reply?: string | null;
          created_at?: string;
          resolved_at?: string | null;
        };
        Update: {
          id?: string;
          user_id?: string | null;
          farmer_name?: string;
          farm_name?: string | null;
          category?: string;
          subject?: string;
          message?: string;
          status?: string;
          admin_reply?: string | null;
          created_at?: string;
          resolved_at?: string | null;
        };
      };
      notifications: {
        Row: {
          id: string;
          user_id: string | null;
          title: string;
          body: string | null;
          task_type: TaskTypeEnum | null;
          notification_type: string;
          is_read: boolean;
          created_at: string;
        };
        Insert: {
          id?: string;
          user_id?: string | null;
          title: string;
          body?: string | null;
          task_type?: TaskTypeEnum | null;
          notification_type?: string;
          is_read?: boolean;
          created_at?: string;
        };
        Update: {
          id?: string;
          user_id?: string | null;
          title?: string;
          body?: string | null;
          task_type?: TaskTypeEnum | null;
          notification_type?: string;
          is_read?: boolean;
          created_at?: string;
        };
      };
      community_posts: {
        Row: {
          id: string;
          author_id: string | null;
          author_name: string;
          author_avatar_url: string | null;
          category: string;
          title: string;
          content: string;
          likes_count: number;
          comments_count: number;
          is_pinned: boolean;
          tags: string[];
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          author_id?: string | null;
          author_name: string;
          author_avatar_url?: string | null;
          category?: string;
          title: string;
          content: string;
          likes_count?: number;
          comments_count?: number;
          is_pinned?: boolean;
          tags?: string[];
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          author_id?: string | null;
          author_name?: string;
          author_avatar_url?: string | null;
          category?: string;
          title?: string;
          content?: string;
          likes_count?: number;
          comments_count?: number;
          is_pinned?: boolean;
          tags?: string[];
          created_at?: string;
          updated_at?: string;
        };
      };
      community_comments: {
        Row: {
          id: string;
          post_id: string;
          author_id: string | null;
          author_name: string;
          author_avatar_url: string | null;
          content: string;
          created_at: string;
        };
        Insert: {
          id?: string;
          post_id: string;
          author_id?: string | null;
          author_name: string;
          author_avatar_url?: string | null;
          content: string;
          created_at?: string;
        };
        Update: {
          id?: string;
          post_id?: string;
          author_id?: string | null;
          author_name?: string;
          author_avatar_url?: string | null;
          content?: string;
          created_at?: string;
        };
      };
      community_reports: {
        Row: {
          id: string;
          reporter_id: string | null;
          reporter_name: string;
          target_type: string;
          target_id: string;
          target_name: string;
          target_content: string | null;
          reason: string;
          details: string | null;
          status: string;
          admin_notes: string | null;
          created_at: string;
          resolved_at: string | null;
        };
        Insert: {
          id?: string;
          reporter_id?: string | null;
          reporter_name: string;
          target_type: string;
          target_id: string;
          target_name: string;
          target_content?: string | null;
          reason: string;
          details?: string | null;
          status?: string;
          admin_notes?: string | null;
          created_at?: string;
          resolved_at?: string | null;
        };
        Update: {
          id?: string;
          reporter_id?: string | null;
          reporter_name?: string;
          target_type?: string;
          target_id?: string;
          target_name?: string;
          target_content?: string | null;
          reason?: string;
          details?: string | null;
          status?: string;
          admin_notes?: string | null;
          created_at?: string;
          resolved_at?: string | null;
        };
      };
    };
    Views: {
      [_ in never]: never;
    };
    Functions: {
      [_ in never]: never;
    };
    Enums: {
      role_enum: RoleEnum;
      soil_type_enum: SoilTypeEnum;
      season_enum: SeasonEnum;
      category_enum: CategoryEnum;
      task_type_enum: TaskTypeEnum;
      companion_relation_enum: CompanionRelationEnum;
    };
  };
}
