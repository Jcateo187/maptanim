package com.maptanim.mobile.domain

data class Farm(
    val id: String,
    val name: String,
    val location: String,
    val beds: List<Bed>
)
