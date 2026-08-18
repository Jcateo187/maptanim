package com.maptanim.app.domain.repository

import com.maptanim.app.domain.model.PestGuide
import com.maptanim.app.domain.model.SeasonalWindowInfo
import com.maptanim.app.domain.model.SoilGuide
import kotlinx.coroutines.flow.Flow

interface KnowledgeBaseRepository {
    fun observePestGuides(): Flow<List<PestGuide>>
    fun observeSoilGuides(): Flow<List<SoilGuide>>
    fun observeSeasonalWindows(): Flow<List<SeasonalWindowInfo>>
}
