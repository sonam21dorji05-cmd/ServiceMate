package com.example.servicemate.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.servicemate.data.model.Vehicle

@Dao
interface VehicleDao {
    @Insert
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle): Int

    @Delete
    suspend fun delete(vehicle: Vehicle): Int

    @Query("SELECT * FROM vehicles WHERE ownerId = :ownerId ORDER BY id DESC")
    fun getVehiclesForOwner(ownerId: Long): LiveData<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Vehicle?

    @Query("SELECT * FROM vehicles")
    suspend fun getAllVehicles(): List<Vehicle>
}
