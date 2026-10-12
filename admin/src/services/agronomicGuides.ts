import { PestGuide, SoilGuide } from '../types';

export const STANDARD_PESTS_GUIDE: PestGuide[] = [
  {
    id: 'pest-001',
    name: 'Fruit Borer / Corn Earworm',
    localName: 'Ubod ng Kamatis / Harabas',
    scientificName: 'Helicoverpa armigera',
    affectedCrops: ['Tomato', 'Eggplant', 'Corn', 'Okra', 'Chili Pepper'],
    category: 'Insect Pest',
    organicControl: 'Spray Neem Oil extract (30ml/L water) or Bacillus thuringiensis (Bt). Handpick caterpillars early morning.',
    chemicalControl: 'Apply DA-approved Chlorantraniliprole or Emamectin benzoate at early instar stage.',
    preventionTips: 'Practice crop rotation with non-host crops. Install yellow sticky traps.',
    imageUrl: '/metadata/pest/Fruit_borer.png'
  },
  {
    id: 'pest-002',
    name: 'Tomato Leaf Curl Virus (TyLCV)',
    localName: 'Kulot sa Kamatis / Whitefly Disease',
    scientificName: 'Begomovirus (transmitted by Bemisia tabaci)',
    affectedCrops: ['Tomato', 'Chili Pepper', 'Squash'],
    category: 'Viral Disease',
    organicControl: 'Spray soapy water or Neem oil to target whitefly vector. Remove infected plants immediately.',
    chemicalControl: 'Control vector whiteflies with Imidacloprid or Thiamethoxam during seedling stage.',
    preventionTips: 'Use TyLCV-resistant varieties (e.g. Diamante Max F1). Install insect netting.',
    imageUrl: '/metadata/pest/Tomato_leaf_curlvirus.png'
  },
  {
    id: 'pest-003',
    name: 'Diamondback Moth',
    localName: 'Ulod sa Repolyo',
    scientificName: 'Plutella xylostella',
    affectedCrops: ['Cabbage', 'Pechay', 'Lettuce'],
    category: 'Insect Pest',
    organicControl: 'Apply Bt (Bacillus thuringiensis) kurstaki strain every 5-7 days.',
    chemicalControl: 'Rotate Spinetoram foliar sprays to prevent pesticide resistance.',
    preventionTips: 'Use overhead sprinkler irrigation to disturb egg-laying adult moths.',
    imageUrl: '/metadata/pest/Diamondback_moth.png'
  },
  {
    id: 'pest-004',
    name: 'Onion Thrips',
    localName: 'Peste sa Sibuyas / Thrips',
    scientificName: 'Thrips tabaci',
    affectedCrops: ['Onion', 'Carrot', 'Cabbage'],
    category: 'Insect Pest',
    organicControl: 'Blue sticky card traps (20 traps/ha). Spray bio-pesticide Beauveria bassiana.',
    chemicalControl: 'Apply Abamectin or Fipronil in severe infestations during bulb establishment.',
    preventionTips: 'Maintain proper soil moisture. Avoid planting near older onion fields.',
    imageUrl: '/metadata/pest/Onion_thrips.png'
  },
  {
    id: 'pest-005',
    name: 'Fall Armyworm',
    localName: 'Armyworm / Harabas sa Mais',
    scientificName: 'Spodoptera frugiperda',
    affectedCrops: ['Corn', 'String Beans', 'Cucumber'],
    category: 'Insect Pest',
    organicControl: 'Drop sand/ash mixed with Neem powder into corn whorls.',
    chemicalControl: 'Target whorls with Spinetoram or Methomyl sprays during early egg hatch.',
    preventionTips: 'Deep plowing after harvest to destroy pupae in soil.',
    imageUrl: '/metadata/pest/Fall_armyworm.png'
  },
  {
    id: 'pest-006',
    name: 'Powdery Mildew',
    localName: 'Pulbos sa Dahon ng Kalabasa',
    scientificName: 'Erysiphe cichoracearum',
    affectedCrops: ['Squash', 'Cucumber', 'Okra', 'Ampalaya'],
    category: 'Fungal Disease',
    organicControl: 'Foliar spray of 10% baking soda solution or diluted milk spray.',
    chemicalControl: 'Apply Potassium bicarbonate or Sulfur-based fungicide.',
    preventionTips: 'Ensure wider plant spacing for air circulation. Avoid overhead watering.',
    imageUrl: '/metadata/pest/Powdery_mildew.png'
  }
];

export const STANDARD_SOILS_GUIDE: SoilGuide[] = [
  {
    soilType: 'LOAM',
    title: 'Loam Soil',
    localName: 'Lupang Luto / Ideal Loam',
    description: 'Dark, rich, crumbly soil with a balanced mixture of sand, silt, and clay.',
    characteristics: 'Excellent moisture retention with optimal internal drainage; rich in humus.',
    drainageSpeed: 'Moderate / Ideal (15–25 mm/hr)',
    phRange: '6.0 – 7.0',
    texture: 'Crumbly and soft when dry, forms a loose ball when moist.',
    bestCrops: ['Tomato', 'Eggplant', 'Chili Pepper', 'Carrot', 'Onion', 'Lettuce', 'Corn', 'Squash'],
    imageUrl: '/metadata/soil_images/Loam_soil.png',
    colorHex: '#3E2723'
  },
  {
    soilType: 'CLAY',
    title: 'Clay Soil',
    localName: 'Lupang Malagkit / Pula',
    description: 'Fine-textured soil composed of dense clay mineral particles.',
    characteristics: 'Holds abundant plant nutrients; high water capacity; slow to drain.',
    drainageSpeed: 'Slow (< 5 mm/hr)',
    phRange: '5.5 – 7.0',
    texture: 'Sticky and smooth when wet, forms hard clods when dry.',
    bestCrops: ['Eggplant', 'Okra', 'Kangkong', 'Squash', 'Corn'],
    imageUrl: '/metadata/soil_images/Clay_soil.png',
    colorHex: '#5D4037'
  },
  {
    soilType: 'SANDY',
    title: 'Sandy Soil',
    localName: 'Lupang Buhangin',
    description: 'Coarse-grained soil with large quartz particles. Highly porous and fast-draining.',
    characteristics: 'Warms up rapidly in dry season; excellent root penetration.',
    drainageSpeed: 'Fast (> 50 mm/hr)',
    phRange: '5.5 – 6.8',
    texture: 'Gritty and loose, cannot hold form when squeezed.',
    bestCrops: ['Carrot', 'Chili Pepper', 'Onion', 'Tomato', 'Eggplant'],
    imageUrl: '/metadata/soil_images/Sandy_soil.png',
    colorHex: '#C6A700'
  },
  {
    soilType: 'SILTY',
    title: 'Silty Soil',
    localName: 'Lupang Banlik / River Silt',
    description: 'Smooth, fertile soil deposited by alluvial river beds.',
    characteristics: 'Very smooth texture; easily tilled; retains moisture efficiently.',
    drainageSpeed: 'Moderate (10–20 mm/hr)',
    phRange: '6.0 – 7.0',
    texture: 'Floury and smooth when dry, silky when moist.',
    bestCrops: ['Cabbage', 'Kangkong', 'Lettuce', 'String Beans', 'Pechay'],
    imageUrl: '/metadata/soil_images/Silty_soil.png',
    colorHex: '#795548'
  },
  {
    soilType: 'PEATY',
    title: 'Peaty Soil',
    localName: 'Lupang Organiko / Peat',
    description: 'Dark, spongy soil high in decomposed organic plant materials.',
    characteristics: 'High organic content; rich in nitrogen; naturally acidic.',
    drainageSpeed: 'Moderate to Slow',
    phRange: '4.5 – 6.0',
    texture: 'Spongy, dark brown or black, lightweight.',
    bestCrops: ['Lettuce', 'Cabbage', 'Kangkong', 'Pechay'],
    imageUrl: '/metadata/soil_images/Peaty_soil.png',
    colorHex: '#212121'
  },
  {
    soilType: 'CHALKY',
    title: 'Chalky Soil',
    localName: 'Lupang Apog / Alkaline',
    description: 'Light soil sitting over limestone bedrock. Rich in calcium carbonate.',
    characteristics: 'Alkaline pH; fast draining; benefits from regular organic matter.',
    drainageSpeed: 'Fast to Moderate',
    phRange: '7.0 – 8.0',
    texture: 'Stony or chalky white flecks throughout dry soil.',
    bestCrops: ['Okra', 'Corn', 'String Beans'],
    imageUrl: '/metadata/soil_images/Chalky_soil.png',
    colorHex: '#9E9E9E'
  }
];
