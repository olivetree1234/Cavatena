package com.example.violinpracticeapp.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

//violin colors

val ViolinBrown = Color(0xFF6D4C41)
val ViolinDarkBrown = Color(0xFF3E2723)
val ViolinGold = Color(0xFFD4A017)
val ViolinCream = Color(0xFFFFF8E7)
val ViolinLightCream = Color(0xFFFFFCF2)
val MusicRed = Color(0xFF8D2F23)

val SkyBlueBackground = Color(0xFFEAF6FF)

fun categoryColor(category: String): Color {
    return when (category) {
        "Scale" -> Color(0xFFE3F2FD)           // Light Blue
        "Etudes" -> Color(0xFFE8F5E9)          // Light Green
        "Pieces" -> Color(0xFFFFF3E0)          // Light Orange
        "Orchestral Pieces" -> Color(0xFFF3E5F5) // Light Purple
        "Bowing" -> Color(0xFFFFEBEE)          // Light Pink
        else -> Color(0xFFF5F5F5)              // Light Gray
    }
}