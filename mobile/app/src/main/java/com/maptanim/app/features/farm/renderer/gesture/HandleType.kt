package com.maptanim.app.features.farm.renderer.gesture

/**
 * HandleType — Interactive resize and manipulation handles for 2D design canvas.
 * Used for resizing beds and crops from 4 corners and 4 edge midpoints.
 */
enum class HandleType {
    DRAG,
    DELETE_QUICK,
    CORNER_TL,
    CORNER_TR,
    CORNER_BL,
    CORNER_BR,
    MID_TOP,
    MID_BOTTOM,
    MID_LEFT,
    MID_RIGHT,
    ACTION_BTN
}
