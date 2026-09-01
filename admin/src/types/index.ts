// TypeScript Interfaces for MapTanim Admin Dashboard

export type UserRole = 'FARMER' | 'ADMINISTRATOR' | 'GUEST';
export type AccountStatus = 'ACTIVE' | 'PENDING' | 'SUSPENDED';
export type SoilType = 'LOAM' | 'CLAY' | 'SANDY' | 'SILTY' | 'PEATY' | 'CHALKY';
export type SeasonType = 'DRY' | 'WET' | 'YEAR_ROUND';
export type CategoryType = 'BULB' | 'STEM' | 'SHOOT' | 'LEAFY' | 'FLOWER' | 'FRUIT' | 'ROOT' | 'TUBER' | 'PODDED';
export type CompanionType = 'BENEFICIAL' | 'ANTAGONIST' | 'NEUTRAL';
export type TaskType = 'WATER' | 'FERTILIZE' | 'HARVEST' | 'PEST_ALERT' | 'APPLY_PESTICIDE' | 'SOIL_AMENDMENT' | 'PRUNING' | 'OBSERVATION';

export interface Farmer {
  id: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  role: UserRole;
  status: AccountStatus;
  farmName: string;
  activePlotsCount: number;
  avatarUrl?: string;
  createdAt: string;
  lastLoginAt: string;
}

export interface Farm {
  id: string;
  farmerId: string;
  farmerName: string;
  farmName: string;
  soilType: SoilType;
  bedsCount: number;
  createdAt: string;
}



// Direct Soil Planting Crop Zone Model (Chapter 34 Architecture)
export interface CropZone {
  id: string;
  farmId: string;
  cropZoneLabel: string;
  cropId?: string;
  cropName?: string;
  x: number;
  y: number;
  width: number;
  height: number;
  growthStage: number; // 1 to 4
  plantedDate?: string;
  expectedHarvestDate?: string;
  healthScore: number; // 0 to 100
}

// Alias for compatibility
export type BedPlot = CropZone;

export interface Crop {
  id: string;
  name: string;
  botanicalName: string;
  category: CategoryType;
  idealSoil: SoilType;
  season: SeasonType;
  daysToHarvest: number;
  waterReqMmPerWeek: number;
  npkRequirement: {
    nitrogen: number;
    phosphorus: number;
    potassium: number;
  };
  companionCropsGood: string[]; // crop names/ids
  companionCropsBad: string[];
  imageUrl: string;
  activePlantingCount?: number;
}

export interface PestGuide {
  id: string;
  name: string;
  localName: string;
  scientificName: string;
  affectedCrops: string[];
  category: string;
  organicControl: string;
  chemicalControl: string;
  preventionTips: string;
  imageUrl: string;
}

export interface SoilGuide {
  soilType: SoilType;
  title: string;
  localName: string;
  description: string;
  characteristics: string;
  drainageSpeed: string;
  phRange: string;
  texture: string;
  bestCrops: string[];
  imageUrl: string;
  colorHex?: string;
}

export interface DSSRule {
  id: string;
  cropA: string;
  cropB: string;
  relationship: CompanionType;
  reason: string;
  daReferenceDoc?: string;
}

export interface FeedbackItem {
  id: string;
  farmerId: string;
  farmerName: string;
  category: 'BUG' | 'FEATURE_REQUEST' | 'AGRONOMIC_QUERY' | 'GENERAL';
  subject: string;
  message: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'RESOLVED';
  createdAt: string;
  resolvedAt?: string;
  adminReply?: string;
}

export interface SystemAuditLog {
  id: string;
  timestamp: string;
  adminEmail: string;
  action: string;
  targetModule: string;
  details: string;
  status: 'SUCCESS' | 'WARNING' | 'FAILED';
  ipAddress: string;
}

export interface DashboardStats {
  totalFarmers: number;
  activeFarms: number;
  totalPlots: number;
  totalHarvestKgThisMonth: number;
  systemHealth: string;
  monthlyYield: { month: string; yieldKg: number; targetKg: number }[];
  cropDistribution: { category: string; value: number; color: string }[];
  farmerRegistrations: { date: string; count: number }[];
}

export type CommunityCategory = 'PEST_ALERT' | 'FARMING_TIP' | 'EQUIPMENT' | 'GENERAL' | 'OFFICIAL_ADVISORY';

export interface CommunityComment {
  id: string;
  postId: string;
  authorId?: string;
  authorName: string;
  authorAvatarUrl?: string;
  content: string;
  createdAt: string;
}

export interface CommunityPost {
  id: string;
  authorId?: string;
  authorName: string;
  authorAvatarUrl?: string;
  category: CommunityCategory;
  title: string;
  content: string;
  likesCount: number;
  commentsCount: number;
  isPinned: boolean;
  tags: string[];
  createdAt: string;
  updatedAt?: string;
  comments?: CommunityComment[];
}

export type ReportTargetType = 'POST' | 'USER' | 'COMMENT';
export type ReportStatus = 'PENDING' | 'INVESTIGATING' | 'RESOLVED' | 'DISMISSED';

export interface CommunityReport {
  id: string;
  reporterId?: string;
  reporterName: string;
  targetType: ReportTargetType;
  targetId: string;
  targetName: string;
  targetContent?: string;
  reason: string;
  details?: string;
  status: ReportStatus;
  adminNotes?: string;
  createdAt: string;
  resolvedAt?: string;
}

// ─── Crop Lifecycle Types ──────────────────────────────────────────────────

export type GrowthStage = 'GERMINATION' | 'SEEDLING' | 'VEGETATIVE' | 'FLOWERING' | 'RIPENING' | 'HARVEST';
export type TileStatusType = 'EMPTY' | 'PLANTED' | 'GROWING' | 'READY_TO_HARVEST' | 'HARVESTED' | 'FALLOW';

/** Admin-managed crop enrichment profile with growth durations and agronomic guides */
export interface CropProfile {
  id: string;
  cropId: string;
  /** Configurable duration in days for each of the 6 growth stages */
  growthStageDurations: Record<GrowthStage, number>;
  plantingInstructions?: string;
  pestRisks?: string;
  fertilizerSchedule?: string;
  wateringGuide?: string;
  /** External image URLs (free hosting, no Supabase Storage) */
  imageUrls: string[];
  thumbnailUrl?: string;
  createdByAdmin: string;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

/** Single cell in the 45×45 isometric farm grid — plain white tile */
export interface FarmTile {
  id: string;
  farmId: string;
  gridX: number;
  gridY: number;
  status: TileStatusType;
  currentCropId?: string;
  tileLabel?: string;
  createdAt: string;
  updatedAt: string;
}

/** Drag-drop crop placement on a tile — supports resizable crops */
export interface TilePlanting {
  id: string;
  tileId: string;
  cropId: string;
  cropName: string;
  cropVariety?: string;
  widthM: number;
  heightM: number;
  offsetX: number;
  offsetY: number;
  currentStage: GrowthStage;
  stageChangedAt: string;
  plantedAt: string;
  expectedHarvestDate?: string;
  cropProfileId?: string;
  isActive: boolean;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

/** Monitoring/observation log & scheduled tasks for an active planting */
export interface PlantingMonitor {
  id: string;
  plantingId: string;
  cropId: string;
  cropName: string;
  cropVariety?: string;
  monitorType: TaskType;
  value?: number;
  unit?: string;
  notes?: string;
  dueDate?: string;
  isCompleted: boolean;
  completedAt?: string;
  recordedAt: string;
  createdAt: string;
}

/** Harvest record for a completed tile planting */
export interface PlantingHarvest {
  id: string;
  plantingId: string;
  cropName: string;
  cropVariety?: string;
  yieldKg: number;
  yieldUnits?: number;
  qualityGrade?: string;
  harvestDate: string;
  growingDays?: number;
  notes?: string;
  createdAt: string;
}

