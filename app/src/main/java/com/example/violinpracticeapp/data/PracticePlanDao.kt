package com.example.violinpracticeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction

@Dao
interface PracticePlanDao {

    @Insert
    suspend fun insertPlan(
        plan: PracticePlanEntity
    )

    @Update
    suspend fun updatePlan(
        plan: PracticePlanEntity
    )

    @Delete
    suspend fun deletePlan(
        plan: PracticePlanEntity
    )

    @Query(
        "SELECT * FROM practice_plans WHERE date = :date ORDER BY id ASC"
    )
    fun getPlansForDate(
        date: String
    ): Flow<List<PracticePlanEntity>>

    @Transaction
    @Query("""
    SELECT * FROM practice_plans
    WHERE date = :date
    ORDER BY id ASC
""")
    fun getPlansWithLibraryForDate(
        date: String
    ): Flow<List<PracticePlanWithLibraryItem>>
}