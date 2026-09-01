package com.maptanim.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maptanim.app.data.local.entity.FarmTileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmTileDao {

    @Query("SELECT * FROM farm_tiles WHERE farm_id = :farmId ORDER BY grid_y, grid_x")
    fun observeTilesByFarmId(farmId: String): Flow<List<FarmTileEntity>>

    @Query("SELECT * FROM farm_tiles WHERE id = :tileId")
    fun observeTileById(tileId: String): Flow<FarmTileEntity?>

    @Query("SELECT * FROM farm_tiles WHERE farm_id = :farmId AND status != 'EMPTY'")
    fun observeOccupiedTiles(farmId: String): Flow<List<FarmTileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertTile(tile: FarmTileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertTiles(tiles: List<FarmTileEntity>)

    @Query("DELETE FROM farm_tiles WHERE id = :tileId")
    fun deleteTile(tileId: String): Int

    @Query("DELETE FROM farm_tiles WHERE farm_id = :farmId")
    fun deleteTilesByFarmId(farmId: String): Int

    @Query("DELETE FROM farm_tiles WHERE farm_id = :farmId AND id NOT IN (:keepIds)")
    fun deleteTilesNotInList(farmId: String, keepIds: List<String>): Int
}
