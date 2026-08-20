package com.maptanim.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maptanim.app.domain.model.Farm

@Entity(tableName = "farms")
data class FarmEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "farmer_id") val farmerId: String,
    @ColumnInfo(name = "farm_name") val farmName: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String
)

fun FarmEntity.toDomain() = Farm(
    id = id,
    farmerId = farmerId,
    farmName = farmName,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Farm.toEntity() = FarmEntity(
    id = id,
    farmerId = farmerId,
    farmName = farmName,
    createdAt = createdAt,
    updatedAt = updatedAt
)


