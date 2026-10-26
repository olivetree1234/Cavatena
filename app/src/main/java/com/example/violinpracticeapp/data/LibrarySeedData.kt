package com.example.violinpracticeapp.data

object LibrarySeedData {

    fun getStarterLibrary(): List<LibraryItemEntity> {
        return buildList {
            addAll(majorScales())
            addAll(naturalMinorScales())
            addAll(harmonicMinorScales())
            addAll(melodicMinorScales())

            addAll(arpeggios())
            addAll(chromaticScales())
            addAll(dominantSevenths())
            addAll(diminishedSevenths())
            addAll(doubleStopScales())

            addAll(bowingTechniques())

            addAll(beginnerEtudes())
            addAll(intermediateEtudes())
            addAll(advancedEtudes())
        }
    }

    // ==========================================================
    // KEYS
    // ==========================================================

    private val majorKeys = listOf(
        "C", "G", "D", "A", "E", "B",
        "F#", "Db", "Ab", "Eb", "Bb", "F"
    )

    private val minorKeys = listOf(
        "A", "E", "B", "F#", "C#", "G#",
        "Eb", "Bb", "F", "C", "G", "D"
    )

    // ==========================================================
    // SCALES
    // ==========================================================

    private fun majorScales(): List<LibraryItemEntity> {
        return majorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Major Scale",
                category = "Scales",
                composer = "Technique",
                notes = "Major scale practice"
            )
        }
    }

    private fun naturalMinorScales(): List<LibraryItemEntity> {
        return minorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Natural Minor Scale",
                category = "Scales",
                composer = "Technique",
                notes = "Natural minor scale practice"
            )
        }
    }

    private fun harmonicMinorScales(): List<LibraryItemEntity> {
        return minorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Harmonic Minor Scale",
                category = "Scales",
                composer = "Technique",
                notes = "Harmonic minor scale practice"
            )
        }
    }

    private fun melodicMinorScales(): List<LibraryItemEntity> {
        return minorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Melodic Minor Scale",
                category = "Scales",
                composer = "Technique",
                notes = "Melodic minor scale practice"
            )
        }
    }

    private fun chromaticScales(): List<LibraryItemEntity> {
        return listOf("G", "A", "B", "C", "D", "E", "F").map { key ->
            LibraryItemEntity(
                title = "Chromatic Scale Starting on $key",
                category = "Scales",
                composer = "Technique",
                notes = "Chromatic scale practice"
            )
        }
    }

    // ==========================================================
    // ARPEGGIOS (under Scales)
    // ==========================================================

    private fun arpeggios(): List<LibraryItemEntity> {
        val majorArpeggios = majorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Major Arpeggio",
                category = "Scales",
                composer = "Technique",
                notes = "Major arpeggio practice"
            )
        }

        val minorArpeggios = minorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Minor Arpeggio",
                category = "Scales",
                composer = "Technique",
                notes = "Minor arpeggio practice"
            )
        }

        return majorArpeggios + minorArpeggios
    }

    private fun dominantSevenths(): List<LibraryItemEntity> {
        return majorKeys.map { key ->
            LibraryItemEntity(
                title = "$key Dominant Seventh",
                category = "Scales",
                composer = "Technique",
                notes = "Dominant seventh arpeggio"
            )
        }
    }

    private fun diminishedSevenths(): List<LibraryItemEntity> {
        return listOf("G", "A", "B", "C", "D", "E", "F").map { note ->
            LibraryItemEntity(
                title = "Diminished Seventh Starting on $note",
                category = "Scales",
                composer = "Technique",
                notes = "Diminished seventh arpeggio"
            )
        }
    }

    // ==========================================================
    // DOUBLE STOPS (under Technique)
    // ==========================================================

    private fun doubleStopScales(): List<LibraryItemEntity> {
        val types = listOf("Thirds", "Sixths", "Octaves")

        return majorKeys.flatMap { key ->
            types.map { type ->
                LibraryItemEntity(
                    title = "$key Major Double Stop - $type",
                    category = "Technique",
                    composer = "Technique",
                    notes = "Double-stop scale practice"
                )
            }
        }
    }

    // ==========================================================
    // BOWING TECHNIQUES (under Technique)
    // ==========================================================

    private fun bowingTechniques(): List<LibraryItemEntity> {
        val techniques = listOf(
            "Long Bows",
            "Whole Bow",
            "Upper Half Bow",
            "Lower Half Bow",
            "Middle Bow",
            "Detaché",
            "Legato",
            "Slurred Bowing",
            "Two Notes Slurred",
            "Four Notes Slurred",
            "Martelé",
            "Staccato",
            "Spiccato",
            "Sautillé",
            "Ricochet",
            "Hooked Bowing",
            "Louré",
            "Portato",
            "Tremolo",
            "Col Legno",
            "Sul Tasto",
            "Sul Ponticello",
            "String Crossing",
            "Double-Stop Bowing"
        )

        return techniques.map { technique ->
            LibraryItemEntity(
                title = technique,
                category = "Technique",
                composer = "Technique",
                notes = "Bowing technique practice"
            )
        }
    }

    // ==========================================================
    // ETUDES
    // ==========================================================

    private fun beginnerEtudes(): List<LibraryItemEntity> {
        return (1..60).map { number ->
            LibraryItemEntity(
                title = "Wohlfahrt Op.45 No. $number",
                category = "Etudes",
                composer = "Franz Wohlfahrt",
                notes = "Elementary violin study"
            )
        }
    }

    private fun intermediateEtudes(): List<LibraryItemEntity> {
        return (1..36).map { number ->
            LibraryItemEntity(
                title = "Kayser Op.20 No. $number",
                category = "Etudes",
                composer = "Heinrich Ernst Kayser",
                notes = "Intermediate violin study"
            )
        }
    }

    private fun advancedEtudes(): List<LibraryItemEntity> {
        return (1..42).map { number ->
            LibraryItemEntity(
                title = "Kreutzer No. $number",
                category = "Etudes",
                composer = "Rodolphe Kreutzer",
                notes = "Advanced violin study"
            )
        }
    }
}
