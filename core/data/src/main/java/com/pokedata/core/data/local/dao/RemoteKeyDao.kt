package com.pokedata.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pokedata.core.data.local.entity.RemoteKey

@Dao
interface RemoteKeyDao {
    @Query("SELECT * FROM remote_keys WHERE label = :label")
    suspend fun getRemoteKey(label: String): RemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
