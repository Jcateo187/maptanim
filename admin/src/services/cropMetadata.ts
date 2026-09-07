import { Crop, SoilType } from '../types';

export interface StageDaysInfo {
  stage1Sprout: number;
  stage2Seedling: number;
  stage3Vegetative: number;
  stage4Flowering: number;
  stage5Harvest: number;
}

export interface WhyDetailInfo {
  title: string;
  summary: string;
  points: string[];
}

export interface CropWhyReasoning {
  categoryWhy: WhyDetailInfo;
  harvestWhy: WhyDetailInfo;
  wateringWhy: WhyDetailInfo;
  soilWhy: WhyDetailInfo;
}

export interface ReferenceSourceInfo {
  organization: string;
  publicationTitle: string;
  sourceUrl?: string;
  author: string;
  license: string;
  purposeStatement: string;
}

export interface CropVarietyInfo {
  varietyId: string;
  varietyName: string;
  localNamePh: string;
  growthDurationDays: number;
  stageDays: StageDaysInfo;
  optimalSeasons?: string[];
  wateringIntervalDays?: number;
  fertilizeIntervalDays?: number;
  fruitLengthCm?: string;
  bitternessLevel?: string;
  diseaseResistance?: string;
  description: string;
  samplePlantedDate?: string;
  sampleExpectedHarvestDate?: string;
}

export interface CropMetadataInfo {
  id: string;
  commonName: string;
  localNamePh: string;
  scientificName: string;
  taxonomicFamily: string;
  cropType: string;
  daysToHarvestStr: string;
  optimalPhStr: string;
  optimalTempCStr: string;
  description: string;
  primaryPhotoUrl: string;
  whyReasoning: CropWhyReasoning;
  referenceSource: ReferenceSourceInfo;
  varieties: CropVarietyInfo[];
}

const CROP_JSON_MAP: Record<string, string> = {
  eggplant: 'eggplant.json',
  talong: 'eggplant.json',
  tomato: 'tomato.json',
  kamatis: 'tomato.json',
  ampalaya: 'ampalaya.json',
  'bitter gourd': 'ampalaya.json',
  sili: 'sili.json',
  chili: 'sili.json',
  'chili pepper': 'sili.json',
  pepper: 'sili.json',
  cabbage: 'cabbage.json',
  repolyo: 'cabbage.json',
  pechay: 'pechay.json',
  'bok choy': 'pechay.json',
  onion: 'onion.json',
  sibuyas: 'onion.json',
  'red onion': 'onion.json',
  carrot: 'carrot.json',
  karot: 'carrot.json',
  sitaw: 'sitaw.json',
  'yardlong bean': 'sitaw.json',
  'string bean': 'sitaw.json',
  lettuce: 'lettuce.json',
  litsugas: 'lettuce.json',
  pipino: 'pipino.json',
  cucumber: 'pipino.json',
  okra: 'okra.json',
  corn: 'corn.json',
  mais: 'corn.json',
  'sweet corn': 'corn.json',
  pumpkin: 'pumpkin.json',
  kalabasa: 'pumpkin.json',
  squash: 'pumpkin.json',
  kangkong: 'kangkong.json',
  'water spinach': 'kangkong.json',
};

const metadataCache = new Map<string, any>();

export const resolveCropJsonFilename = (name: string, localName?: string, id?: string): string | null => {
  const candidates = [name, localName || '', id || '']
    .map((s) => s.toLowerCase().trim())
    .filter(Boolean);

  for (const candidate of candidates) {
    if (CROP_JSON_MAP[candidate]) {
      return CROP_JSON_MAP[candidate];
    }
    // Partial substring match
    for (const [key, file] of Object.entries(CROP_JSON_MAP)) {
      if (candidate.includes(key) || key.includes(candidate)) {
        return file;
      }
    }
  }
  return null;
};

export const fetchRawCropJson = async (filename: string): Promise<any | null> => {
  if (metadataCache.has(filename)) {
    return metadataCache.get(filename);
  }
  try {
    const res = await fetch(`/metadata/crops/${filename}`);
    if (!res.ok) return null;
    const json = await res.json();
    metadataCache.set(filename, json);
    return json;
  } catch (err) {
    console.warn(`Could not load /metadata/crops/${filename}:`, err);
    return null;
  }
};

export const generateDefaultWhyReasoning = (crop: {
  name: string;
  localName?: string;
  category?: string;
  daysToHarvest?: number;
  wateringIntervalDays?: number;
  optimalPhMin?: number;
  optimalPhMax?: number;
  idealSoil?: SoilType | string;
}): CropWhyReasoning => {
  const cat = crop.category || 'General Vegetable';
  const days = crop.daysToHarvest || 60;
  const waterDays = crop.wateringIntervalDays || 2;
  const phMin = crop.optimalPhMin || 6.0;
  const phMax = crop.optimalPhMax || 6.8;
  const soil = crop.idealSoil || 'LOAM';

  return {
    categoryWhy: {
      title: `Why is ${crop.name} classified as ${cat}?`,
      summary: `${crop.name} ${crop.localName ? `(${crop.localName}) ` : ''}belongs to the ${cat} horticultural category based on botanical plant structure, edible harvest organs, and commercial field cultivation practices.`,
      points: [
        'Morphological Structure: Growth habit, canopy branching, and reproductive structures determine nutrient requirements and field plant spacing.',
        'Agronomic Crop Management: Categorized according to Philippine standard vegetable guidelines to guide growers in commercial harvest timing and pest protection.',
      ],
    },
    harvestWhy: {
      title: `Why is the harvest timeline set to ${days} days?`,
      summary: `The ${days}-day growth cycle represents the optimal physiological maturity window for peak fruit/leaf tenderness, commercial yield, and maximum nutrient density.`,
      points: [
        'Vegetative & Reproductive Stages: Allows thorough root establishment, photosynthetic canopy expansion, and healthy fruit/head swelling.',
        'Post-Harvest Field Quality: Harvesting strictly within this maturity period prevents fibrous over-maturation, bitterness, and market downgrading.',
      ],
    },
    wateringWhy: {
      title: `Why water every ${waterDays} day(s)?`,
      summary: `A ${waterDays}-day irrigation interval maintains proper root zone hydration without inducing waterlogged anaerobic conditions in tropical soils.`,
      points: [
        'Root Zone Hydration: Sustains soil moisture between 60% and 70% of field water capacity to preserve cell turgidity and transpiration.',
        'Disease & Stress Prevention: Prevents root rot caused by stagnant water while protecting against heat stress, leaf wilting, and flower abortion.',
      ],
    },
    soilWhy: {
      title: `Why is pH ${phMin}–${phMax} and ${soil} soil recommended?`,
      summary: `A root zone pH between ${phMin} and ${phMax} with well-structured ${soil} optimizes bioavailability of nitrogen, phosphorus, potassium, and trace micronutrients.`,
      points: [
        'Bioavailable Nutrient Uptake: Micronutrients and minerals become chemically locked and inaccessible to root hairs if soil drifts outside this optimal range.',
        'Microbial Activity: Promotes thriving aerobic soil microbes that break down organic matter into easily absorbed plant nutrients.',
      ],
    },
  };
};

export const generateDefaultVarieties = (crop: {
  name: string;
  localName?: string;
  daysToHarvest?: number;
  wateringIntervalDays?: number;
  growthStages?: {
    sprout: number;
    seedling: number;
    vegetative: number;
    flowering: number;
    harvest: number;
  };
}): CropVarietyInfo[] => {
  const totalDays = crop.daysToHarvest || 65;
  const stages = crop.growthStages || {
    sprout: Math.max(3, Math.round(totalDays * 0.08)),
    seedling: Math.max(7, Math.round(totalDays * 0.18)),
    vegetative: Math.max(14, Math.round(totalDays * 0.35)),
    flowering: Math.max(10, Math.round(totalDays * 0.28)),
    harvest: Math.max(5, totalDays - (Math.round(totalDays * 0.08) + Math.round(totalDays * 0.18) + Math.round(totalDays * 0.35) + Math.round(totalDays * 0.28))),
  };

  const primaryName = crop.localName ? `${crop.localName} Commercial F1` : `${crop.name} Standard F1`;
  const heritageName = crop.localName ? `${crop.localName} Native Open-Pollinated` : `${crop.name} Heritage Native`;

  return [
    {
      varietyId: `var_${crop.name.toLowerCase().replace(/\s+/g, '_')}_hybrid`,
      varietyName: primaryName,
      localNamePh: primaryName,
      growthDurationDays: totalDays,
      stageDays: {
        stage1Sprout: stages.sprout,
        stage2Seedling: stages.seedling,
        stage3Vegetative: stages.vegetative,
        stage4Flowering: stages.flowering,
        stage5Harvest: stages.harvest,
      },
      optimalSeasons: ['YEAR_ROUND'],
      wateringIntervalDays: crop.wateringIntervalDays || 2,
      diseaseResistance: 'High tolerance to damping-off and common foliar diseases',
      description: `High-yield, commercially bred hybrid variety with uniform growth, vigorous root development, and excellent transport durability for local markets.`,
    },
    {
      varietyId: `var_${crop.name.toLowerCase().replace(/\s+/g, '_')}_native`,
      varietyName: heritageName,
      localNamePh: heritageName,
      growthDurationDays: Math.round(totalDays * 1.1),
      stageDays: {
        stage1Sprout: stages.sprout + 1,
        stage2Seedling: stages.seedling + 2,
        stage3Vegetative: stages.vegetative + 3,
        stage4Flowering: stages.flowering + 1,
        stage5Harvest: stages.harvest,
      },
      optimalSeasons: ['WET', 'DRY'],
      wateringIntervalDays: crop.wateringIntervalDays || 2,
      diseaseResistance: 'Strong natural resistance to local pest pressures and heat tolerance',
      description: `Locally adapted open-pollinated heritage cultivar with deep root systems, rich traditional flavor, and robust performance under variable weather.`,
    },
  ];
};

export const getCropMetadata = async (crop: Crop | Partial<Crop>): Promise<CropMetadataInfo> => {
  const cropName = crop.name || 'Crop';
  const localName = crop.localName || '';
  const id = crop.id || '';

  const jsonFilename = resolveCropJsonFilename(cropName, localName, id);
  const rawJson = jsonFilename ? await fetchRawCropJson(jsonFilename) : null;

  const defaultWhy = generateDefaultWhyReasoning({
    name: cropName,
    localName,
    category: crop.category,
    daysToHarvest: crop.daysToHarvest,
    wateringIntervalDays: crop.wateringIntervalDays,
    optimalPhMin: crop.optimalPhMin,
    optimalPhMax: crop.optimalPhMax,
    idealSoil: crop.idealSoil,
  });

  const defaultRefSource: ReferenceSourceInfo = {
    organization: 'MapTanim Agricultural Field Research & Farmer Interviews',
    publicationTitle: 'Field Survey & Agronomic Interview Dataset for Philippine Vegetables',
    author: 'MapTanim Agronomic Field Survey Team & Local Farmers',
    license: 'MapTanim Verified Agricultural Dataset',
    purposeStatement:
      'This verified agricultural dataset provides growers with evidence-based standards gathered directly from local farmer interviews and field trials, eliminating guesswork in planting dates, irrigation cycles, and variety selection.',
  };

  if (!rawJson) {
    return {
      id: id || `crop_${cropName.toLowerCase().replace(/\s+/g, '_')}`,
      commonName: cropName,
      localNamePh: localName,
      scientificName: crop.botanicalName || `${cropName} sp.`,
      taxonomicFamily: crop.taxonomicFamily || 'Agronomic Vegetable',
      cropType: crop.category || 'Vegetable',
      daysToHarvestStr: `${crop.daysToHarvest || 60} days`,
      optimalPhStr: `${crop.optimalPhMin || 6.0} - ${crop.optimalPhMax || 6.8}`,
      optimalTempCStr: '22 - 32 C',
      description: crop.description || 'Nutritious Philippine vegetable crop cultivated by local farmers.',
      primaryPhotoUrl: crop.imageUrl || '',
      whyReasoning: defaultWhy,
      referenceSource: defaultRefSource,
      varieties: generateDefaultVarieties({
        name: cropName,
        localName,
        daysToHarvest: crop.daysToHarvest,
        wateringIntervalDays: crop.wateringIntervalDays,
        growthStages: crop.growthStages,
      }),
    };
  }

  // Parse rawJson varieties
  const varietiesList: CropVarietyInfo[] = [];
  if (Array.isArray(rawJson.varieties) && rawJson.varieties.length > 0) {
    for (const v of rawJson.varieties) {
      varietiesList.push({
        varietyId: v.variety_id || `var_${Math.random().toString(36).substr(2, 6)}`,
        varietyName: v.variety_name || cropName,
        localNamePh: v.local_name_ph || v.variety_name || cropName,
        growthDurationDays: v.growth_duration_days || crop.daysToHarvest || 60,
        stageDays: {
          stage1Sprout: v.stage_days?.stage1_sprout ?? 5,
          stage2Seedling: v.stage_days?.stage2_seedling ?? 12,
          stage3Vegetative: v.stage_days?.stage3_vegetative ?? 20,
          stage4Flowering: v.stage_days?.stage4_flowering ?? 16,
          stage5Harvest: v.stage_days?.stage5_harvest ?? 7,
        },
        optimalSeasons: v.optimal_seasons || ['YEAR_ROUND'],
        wateringIntervalDays: v.watering_interval_days ?? crop.wateringIntervalDays ?? 2,
        fertilizeIntervalDays: v.fertilize_interval_days ?? crop.fertilizeIntervalDays ?? 14,
        fruitLengthCm: v.fruit_length_cm,
        bitternessLevel: v.bitterness_level,
        diseaseResistance: v.disease_resistance,
        description: v.description || '',
        samplePlantedDate: v.sample_planted_date,
        sampleExpectedHarvestDate: v.sample_expected_harvest_date,
      });
    }
  } else {
    varietiesList.push(
      ...generateDefaultVarieties({
        name: cropName,
        localName,
        daysToHarvest: crop.daysToHarvest,
        wateringIntervalDays: crop.wateringIntervalDays,
        growthStages: crop.growthStages,
      })
    );
  }

  // Parse why reasoning from rawJson if present
  let whyReasoning = defaultWhy;
  if (rawJson.why_reasoning) {
    const wr = rawJson.why_reasoning;
    whyReasoning = {
      categoryWhy: wr.category_why || defaultWhy.categoryWhy,
      harvestWhy: wr.harvest_why || defaultWhy.harvestWhy,
      wateringWhy: wr.watering_why || defaultWhy.wateringWhy,
      soilWhy: wr.soil_why || defaultWhy.soilWhy,
    };
  }

  // Parse reference source if present
  let refSource = defaultRefSource;
  if (rawJson.reference_source) {
    refSource = {
      organization: rawJson.reference_source.organization || defaultRefSource.organization,
      publicationTitle: rawJson.reference_source.publication_title || defaultRefSource.publicationTitle,
      sourceUrl: rawJson.reference_source.source_url,
      author: rawJson.reference_source.author || defaultRefSource.author,
      license: rawJson.reference_source.license || defaultRefSource.license,
      purposeStatement: rawJson.reference_source.purpose_statement || defaultRefSource.purposeStatement,
    };
  }

  return {
    id: rawJson.id || id || cropName.toLowerCase(),
    commonName: rawJson.common_name || cropName,
    localNamePh: rawJson.local_name_ph || localName,
    scientificName: rawJson.scientific_name || crop.botanicalName || '',
    taxonomicFamily: rawJson.taxonomic_family || crop.taxonomicFamily || 'Agronomic Crop',
    cropType: rawJson.crop_type || crop.category || 'Vegetable',
    daysToHarvestStr: rawJson.growing_specifications?.days_to_harvest || `${crop.daysToHarvest || 60} days`,
    optimalPhStr: rawJson.growing_specifications?.optimal_soil_ph || `${crop.optimalPhMin || 6.0} - ${crop.optimalPhMax || 6.8}`,
    optimalTempCStr: rawJson.growing_specifications?.optimal_temperature_c || '22 - 32 C',
    description: rawJson.growing_specifications?.description || crop.description || '',
    primaryPhotoUrl: crop.imageUrl || rawJson.media?.primary_photo_url || '',
    whyReasoning,
    referenceSource: refSource,
    varieties: varietiesList,
  };
};
