package com.example.violinpracticeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

import androidx.room.Index

@Entity(
    tableName = "practice_plans",
    foreignKeys = [
        ForeignKey(
            entity = LibraryItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryItemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("libraryItemId")]
)
data class PracticePlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    val category: String,

    val libraryItemId: Int?,

    val customNotes: String,

    val durationMinutes: Int
)