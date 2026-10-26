package com.example.violinpracticeapp.data

import androidx.room.Embedded
import androidx.room.Relation

data class PracticePlanWithLibraryItem(

    @Embedded
    val plan: PracticePlanEntity,

    @Relation(
        parentColumn = "libraryItemId",
        entityColumn = "id"
    )
    val libraryItem: LibraryItemEntity?
)