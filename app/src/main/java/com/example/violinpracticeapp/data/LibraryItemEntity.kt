package com.example.violinpracticeapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "library_items",
    indices = [Index(value = ["title"], unique = true)]
)
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val category: String,
    val composer: String,
    val notes: String
)