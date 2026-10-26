package com.example.violinpracticeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeSessionDao {

    @Insert
    suspend fun insertSession(
        session: PracticeSessionEntity
    )

    @Query("""
        SELECT *
        FROM practice_sessions
        ORDER BY createdAt DESC
    """)
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Query("""
        SELECT *
        FROM practice_sessions
        WHERE date = :date
        ORDER BY createdAt DESC
    """)
    fun getSessionsForDate(
        date: String
    ): Flow<List<PracticeSessionEntity>>

    @Query("""
    SELECT SUM(completedSeconds)
    FROM practice_sessions
    WHERE date = :date
    """)
    fun getCompletedSecondsForDate(
        date: String
    ): Flow<Int?>

    @Query("""
    SELECT *
    FROM practice_sessions
    WHERE date = :date
    AND practicePlanId = :practicePlanId
    ORDER BY createdAt DESC
    LIMIT 1
""")
    fun getLatestSessionForPlan(
        date: String,
        practicePlanId: Int
    ): Flow<PracticeSessionEntity?>
}