package com.maptanim.mobile.domain

data class Bed(
    val id: String,
    val name: String,
    val crop: Crop?,
    val soilType: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
