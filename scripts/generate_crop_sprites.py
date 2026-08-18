"""
Script to Generate & Sync Detailed Isometric Philippine Crop Sprites
Renders realistic, recognizable botanical plant structures (leaf shapes, stems, pods, flowers, fruits).
"""

import math
from pathlib import Path
from PIL import Image, ImageDraw

BASE_DIR = Path(__file__).resolve().parent.parent
ASSETS_DIR = BASE_DIR / "assets"
MOBILE_ASSETS_DIR = BASE_DIR / "mobile" / "app" / "src" / "main" / "assets"

def hex_to_rgb(hex_str):
    hex_str = hex_str.lstrip('#')
    return tuple(int(hex_str[i:i+2], 16) for i in (0, 2, 4))

def draw_leaf(draw, cx, cy, angle_deg, length, width, color):
    """Draws a natural lanceolate/oval plant leaf rotated at angle_deg."""
    rad = math.radians(angle_deg)
    cos_a, sin_a = math.cos(rad), math.sin(rad)

    # Leaf tip & base points
    tip_x = cx + length * sin_a
    tip_y = cy - length * cos_a

    left_x = cx + (width / 2) * cos_a + (length / 2) * sin_a
    left_y = cy + (width / 2) * sin_a - (length / 2) * cos_a

    right_x = cx - (width / 2) * cos_a + (length / 2) * sin_a
    right_y = cy - (width / 2) * sin_a - (length / 2) * cos_a

    points = [(cx, cy), (left_x, left_y), (tip_x, tip_y), (right_x, right_y)]
    draw.polygon(points, fill=color)
    # Leaf midrib vein
    draw.line([(cx, cy), (tip_x, tip_y)], fill=(color[0]//2, min(255, color[1]+30), color[2]//2), width=2)


def draw_carrot_sprite(draw, cx, cy, stage, size):
    """Carrot (Karots 🥕): Feathery compound leaves and orange taproot."""
    fol_col = (34, 153, 84) # Carrot green
    root_col = (230, 126, 34) # Carrot orange

    if stage >= 4:
        # Orange root visible at ground level
        draw.polygon([(cx - 10, cy - 5), (cx + 10, cy - 5), (cx + 4, cy + 25), (cx - 4, cy + 25)], fill=root_col)

    # Feathery leaves
    leaf_count = 3 + stage * 2
    for i in range(leaf_count):
        angle = -50 + (100 / (leaf_count - 1)) * i
        length = 20 + stage * 16
        draw_leaf(draw, cx, cy - 5, angle, length, 8 + stage * 2, fol_col)
        # Extra side frills
        rad = math.radians(angle)
        mid_x = cx + (length * 0.6) * math.sin(rad)
        mid_y = cy - 5 - (length * 0.6) * math.cos(rad)
        draw_leaf(draw, int(mid_x), int(mid_y), angle - 35, length * 0.4, 4, fol_col)
        draw_leaf(draw, int(mid_x), int(mid_y), angle + 35, length * 0.4, 4, fol_col)


def draw_stringbeans_sprite(draw, cx, cy, stage, size):
    """String Beans (Sitaw 🫘): Vining stems with trifoliate leaves & hanging pods."""
    fol_col = (46, 139, 87)
    pod_col = (100, 200, 80)

    # Main vine stem
    draw.line([(cx, cy), (cx - 15, cy - 40), (cx + 15, cy - 80), (cx, cy - 110)], fill=(70, 120, 50), width=4)

    # Trifoliate leaves
    leaf_nodes = [(-15, -40), (15, -80), (0, -110)]
    for nx, ny in leaf_nodes[:stage]:
        for ang in [-40, 0, 40]:
            draw_leaf(draw, cx + nx, cy + ny, ang, 22, 14, fol_col)

    if stage == 5:
        # Hanging long sitaw pods
        for px in [-20, 10]:
            draw.line([(cx + px, cy - 60), (cx + px - 5, cy - 10)], fill=pod_col, width=4)


def draw_eggplant_sprite(draw, cx, cy, stage, size):
    """Eggplant (Talong 🍆): Broad lobed leaves & glossy purple fruit."""
    fol_col = (39, 110, 50)
    purple_col = (120, 40, 140)

    # Stem
    draw.line([(cx, cy), (cx, cy - 80)], fill=(60, 90, 40), width=6)

    # Broad leaves
    angles = [-60, 60, -45, 45, 0]
    for i in range(min(len(angles), stage + 1)):
        draw_leaf(draw, cx, cy - 20 - i * 15, angles[i], 35 + stage * 8, 25 + stage * 5, fol_col)

    if stage >= 4:
        # Purple star flower
        draw.ellipse([cx - 8, cy - 75, cx + 8, cy - 60], fill=(180, 100, 220))
    if stage == 5:
        # Hanging purple eggplant fruit
        draw.ellipse([cx - 14, cy - 45, cx + 14, cy - 10], fill=purple_col)
        draw.ellipse([cx - 6, cy - 50, cx + 6, cy - 42], fill=(40, 100, 40)) # Green calyx stem cap


def draw_tomato_sprite(draw, cx, cy, stage, size):
    """Tomato (Kamatis 🍅): Branching foliage with round red tomatoes."""
    fol_col = (40, 140, 55)
    red_col = (225, 45, 35)

    # Central stem & branches
    draw.line([(cx, cy), (cx, cy - 90)], fill=(50, 110, 40), width=5)
    draw.line([(cx, cy - 30), (cx - 30, cy - 60)], fill=(50, 110, 40), width=3)
    draw.line([(cx, cy - 45), (cx + 30, cy - 75)], fill=(50, 110, 40), width=3)

    # Tomato compound leaves
    for bx, by in [(0, -20), (-30, -60), (30, -75), (0, -90)]:
        if stage >= 2:
            for a in [-50, 0, 50]:
                draw_leaf(draw, cx + bx, cy + by, a, 20 + stage * 4, 12, fol_col)

    if stage >= 4:
        # Yellow flowers
        for fx, fy in [(-20, -50), (20, -65)]:
            draw.ellipse([cx + fx - 5, cy + fy - 5, cx + fx + 5, cy + fy + 5], fill=(255, 220, 40))

    if stage == 5:
        # Red tomatoes in clusters
        for tx, ty in [(-22, -45), (-10, -35), (22, -58), (12, -48)]:
            draw.ellipse([cx + tx - 12, cy + ty - 12, cx + tx + 12, cy + ty + 12], fill=red_col)
            draw.ellipse([cx + tx - 3, cy + ty - 14, cx + tx + 3, cy + ty - 10], fill=(30, 100, 30)) # Calyx


def draw_onion_sprite(draw, cx, cy, stage, size):
    """Onion (Sibuyas 🧅): Tubular erect shoots with purple/white bulb."""
    fol_col = (46, 175, 75)
    bulb_col = (180, 70, 80) # Red onion bulb

    if stage >= 3:
        # Onion bulb at base
        r = 10 + stage * 3
        draw.ellipse([cx - r, cy - r // 2, cx + r, cy + r // 2], fill=bulb_col)

    # Tubular shoots
    shoot_count = 3 + stage * 2
    for i in range(shoot_count):
        dx = -20 + (40 / (shoot_count - 1)) * i
        draw.line([(cx + dx * 0.3, cy - 5), (cx + dx, cy - 40 - stage * 14)], fill=fol_col, width=4)


def draw_pumpkin_sprite(draw, cx, cy, stage, size):
    """Pumpkin/Squash (Kalabasa 🎃): Sprawling vine with broad leaves & ribbed squash."""
    fol_col = (30, 130, 60)
    squash_col = (220, 120, 20)

    # Prostrate vine stems
    draw.arc([cx - 60, cy - 40, cx + 60, cy + 20], start=0, end=180, fill=(40, 90, 30), width=4)

    # Broad palmate leaves
    for lx, ly in [(-40, -15), (-20, -35), (0, -45), (20, -35), (40, -15)]:
        if stage >= 2:
            draw_leaf(draw, cx + lx, cy + ly, 0, 25 + stage * 5, 25 + stage * 5, fol_col)

    if stage == 5:
        # Ribbed squash resting on ground
        draw.ellipse([cx - 25, cy - 20, cx + 25, cy + 10], fill=squash_col)
        # Rib lines
        draw.arc([cx - 20, cy - 18, cx + 20, cy + 8], start=30, end=150, fill=(180, 90, 10), width=2)
        # Stem
        draw.polygon([(cx - 3, cy - 24), (cx + 3, cy - 24), (cx + 1, cy - 18), (cx - 1, cy - 18)], fill=(60, 90, 30))


def draw_corn_sprite(draw, cx, cy, stage, size):
    """Corn (Mais 🌽): Tall segmented stalk, arching leaves, top tassel & cobs."""
    fol_col = (45, 160, 65)
    cob_col = (240, 195, 30)

    # Segmented tall stalk
    height = 30 + stage * 22
    draw.line([(cx, cy), (cx, cy - height)], fill=(60, 140, 50), width=6)

    # Arching long leaves
    for i in range(1, stage + 2):
        ly = cy - i * 18
        draw.arc([cx - 50, ly - 20, cx, ly + 20], start=270, end=360, fill=fol_col, width=5)
        draw.arc([cx, ly - 20, cx + 50, ly + 20], start=180, end=270, fill=fol_col, width=5)

    if stage >= 4:
        # Top tassel
        for tx in [-10, 0, 10]:
            draw.line([(cx, cy - height), (cx + tx, cy - height - 15)], fill=(200, 180, 60), width=2)

    if stage == 5:
        # Corn cobs with yellow husks attached to stem
        for side in [-1, 1]:
            cob_x = cx + side * 8
            cob_y = cy - height * 0.5
            draw.ellipse([cob_x - 6, cob_y - 15, cob_x + 6, cob_y + 15], fill=cob_col)
            # Green husk wrap
            draw.polygon([(cob_x - 7, cob_y), (cob_x + 7, cob_y), (cob_x, cob_y + 16)], fill=(50, 140, 40))


def draw_cabbage_sprite(draw, cx, cy, stage, size):
    """Cabbage (Repolyo 🥬): Dense rosette of crinkled leaves forming a head."""
    fol_outer = (45, 150, 75)
    fol_inner = (130, 210, 110)

    # Outer spreading leaves
    leaf_count = 6 + stage * 2
    for i in range(leaf_count):
        ang = (360 / leaf_count) * i
        draw_leaf(draw, cx, cy - 20, ang, 25 + stage * 6, 20 + stage * 5, fol_outer)

    if stage >= 3:
        # Dense cabbage head center
        r = 12 + stage * 6
        draw.ellipse([cx - r, cy - 25 - r // 2, cx + r, cy - 25 + r // 2], fill=fol_inner)


def draw_pechay_sprite(draw, cx, cy, stage, size):
    """Pechay / Bok Choy (Pechay 🥬): White petioles with dark oval leaf blades."""
    petiole_col = (230, 245, 230)
    blade_col = (30, 135, 55)

    leaf_count = 4 + stage * 2
    for i in range(leaf_count):
        ang = -60 + (120 / (leaf_count - 1)) * i
        rad = math.radians(ang)
        length = 20 + stage * 10
        mid_x = cx + length * math.sin(rad)
        mid_y = cy - 10 - length * math.cos(rad)

        # White petiole stem base
        draw.line([(cx, cy - 5), (mid_x, mid_y)], fill=petiole_col, width=5)
        # Oval green blade tip
        draw_leaf(draw, int(mid_x), int(mid_y), ang, 20 + stage * 4, 18 + stage * 3, blade_col)


def draw_ampalaya_sprite(draw, cx, cy, stage, size):
    """Bitter Gourd (Ampalaya 🥒): Climbing vine, lobed leaves & wrinkled fruit."""
    fol_col = (25, 120, 65)
    fruit_col = (60, 160, 80)

    # Vining tendrils
    draw.line([(cx, cy), (cx - 20, cy - 50), (cx + 20, cy - 90)], fill=(40, 100, 50), width=3)

    # Deeply lobed leaves
    for lx, ly in [(-20, -50), (20, -90), (0, -30)]:
        if stage >= 2:
            draw_leaf(draw, cx + lx, cy + ly, 0, 22 + stage * 4, 22 + stage * 4, fol_col)

    if stage == 5:
        # Elongated wrinkled ampalaya fruit
        draw.polygon([(cx - 8, cy - 70), (cx + 8, cy - 70), (cx + 4, cy - 20), (cx - 4, cy - 20)], fill=fruit_col)


def draw_okra_sprite(draw, cx, cy, stage, size):
    """Okra (Okra 🌿): Upright stem, lobed leaves & ridged pods pointing up."""
    fol_col = (40, 140, 60)
    pod_col = (120, 190, 70)

    # Upright stem
    draw.line([(cx, cy), (cx, cy - 90)], fill=(50, 100, 40), width=6)

    # Lobed leaves
    for i in range(1, stage + 2):
        ly = cy - i * 20
        draw_leaf(draw, cx, ly, -50, 25 + stage * 4, 18, fol_col)
        draw_leaf(draw, cx, ly, 50, 25 + stage * 4, 18, fol_col)

    if stage == 5:
        # Ridged pods pointing upwards
        draw.polygon([(cx - 5, cy - 75), (cx + 5, cy - 75), (cx + 2, cy - 105), (cx - 2, cy - 105)], fill=pod_col)


def draw_sili_sprite(draw, cx, cy, stage, size):
    """Chili / Pepper (Sili 🌶️): Bushy foliage & pointed red chilis."""
    fol_col = (35, 125, 50)
    chili_col = (220, 30, 25)

    # Bushy branches
    draw.line([(cx, cy), (cx, cy - 75)], fill=(50, 95, 40), width=4)
    draw.line([(cx, cy - 35), (cx - 25, cy - 60)], fill=(50, 95, 40), width=3)
    draw.line([(cx, cy - 35), (cx + 25, cy - 60)], fill=(50, 95, 40), width=3)

    # Leaves
    for bx, by in [(0, -20), (-25, -60), (25, -60), (0, -75)]:
        if stage >= 2:
            draw_leaf(draw, cx + bx, cy + by, -30, 18 + stage * 3, 10, fol_col)
            draw_leaf(draw, cx + bx, cy + by, 30, 18 + stage * 3, 10, fol_col)

    if stage == 5:
        # Slender pointed chili peppers
        for px, py in [(-20, -50), (0, -65), (20, -50)]:
            draw.polygon([(cx + px - 4, cy + py), (cx + px + 4, cy + py), (cx + px, cy + py + 22)], fill=chili_col)


def draw_pipino_sprite(draw, cx, cy, stage, size):
    """Cucumber (Pipino 🥒): Vining stems, hairy triangular leaves & green cucumber."""
    fol_col = (40, 150, 65)
    cuc_col = (45, 140, 75)

    draw.arc([cx - 50, cy - 60, cx + 50, cy + 10], start=0, end=180, fill=(45, 110, 45), width=3)

    for lx, ly in [(-30, -30), (0, -50), (30, -30)]:
        if stage >= 2:
            draw_leaf(draw, cx + lx, cy + ly, 0, 22 + stage * 4, 20 + stage * 4, fol_col)

    if stage == 5:
        # Cylindrical green cucumber
        draw.ellipse([cx - 8, cy - 45, cx + 8, cy - 10], fill=cuc_col)


def draw_kangkong_sprite(draw, cx, cy, stage, size):
    """Water Spinach (Kangkong 🥬): Hollow trailing stems & spear leaves."""
    fol_col = (35, 160, 70)

    stem_count = 3 + stage * 2
    for i in range(stem_count):
        ang = -55 + (110 / (stem_count - 1)) * i
        draw_leaf(draw, cx, cy - 10, ang, 25 + stage * 8, 10 + stage * 2, fol_col)


def draw_lettuce_sprite(draw, cx, cy, stage, size):
    """Lettuce (Litsugas 🥗): Soft ruffled open head rosette."""
    fol_col = (75, 190, 85)
    inner_col = (140, 225, 120)

    leaf_count = 6 + stage * 2
    for i in range(leaf_count):
        ang = (360 / leaf_count) * i
        draw_leaf(draw, cx, cy - 15, ang, 22 + stage * 6, 22 + stage * 6, fol_col)

    if stage >= 3:
        r = 10 + stage * 4
        draw.ellipse([cx - r, cy - 15 - r // 2, cx + r, cy - 15 + r // 2], fill=inner_col)


CROP_DRAWERS = {
    "carrot": draw_carrot_sprite,
    "stringbeans": draw_stringbeans_sprite,
    "eggplant": draw_eggplant_sprite,
    "tomato": draw_tomato_sprite,
    "onion": draw_onion_sprite,
    "pumpkin": draw_pumpkin_sprite,
    "corn": draw_corn_sprite,
    "cabbage": draw_cabbage_sprite,
    "pechay": draw_pechay_sprite,
    "ampalaya": draw_ampalaya_sprite,
    "okra": draw_okra_sprite,
    "sili": draw_sili_sprite,
    "pipino": draw_pipino_sprite,
    "kangkong": draw_kangkong_sprite,
    "lettuce": draw_lettuce_sprite,
}


def create_detailed_crop_sprite(crop_id, stage, size=256):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size // 2, int(size * 0.80)

    # Soft isometric drop shadow
    draw.ellipse([cx - 40, cy - 12, cx + 40, cy + 12], fill=(0, 0, 0, 45))

    drawer = CROP_DRAWERS.get(crop_id)
    if drawer:
        drawer(draw, cx, cy, stage, size)
    else:
        # Fallback plant foliage
        draw_leaf(draw, cx, cy, 0, 30 + stage * 10, 20 + stage * 5, (40, 140, 60))

    return img


def main():
    print("[Detailed Sprites] Generating Realistic Botanical Philippine Crop Sprites...")

    sprites_crops_dir = ASSETS_DIR / "sprites" / "crops"
    mobile_crops_dir = MOBILE_ASSETS_DIR / "crops"

    sprites_crops_dir.mkdir(parents=True, exist_ok=True)
    mobile_crops_dir.mkdir(parents=True, exist_ok=True)

    for crop_id in CROP_DRAWERS.keys():
        crop_folder = sprites_crops_dir / crop_id
        crop_folder.mkdir(parents=True, exist_ok=True)

        for stage in range(1, 6):
            size = 128 if stage <= 2 else 256
            sprite_img = create_detailed_crop_sprite(crop_id, stage, size=size)

            # Save to assets/sprites/crops/<crop_id>/stage<stage>.png
            sprite_img.save(crop_folder / f"stage{stage}.png", format="PNG")

            # Save/Overwrite to mobile app assets so in-game farm editor renders true plants!
            mobile_file = mobile_crops_dir / f"crop_{crop_id}_{stage}.png"
            sprite_img.save(mobile_file, format="PNG")

        print(f"  + Rendered 5 detailed botanical plant growth stages for {crop_id}")

    print("[Detailed Sprites] All crop sprites updated with recognizable plant artwork!")


if __name__ == "__main__":
    main()
