/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

//
//  News.kt
//  Tower_Android
//

/**
 * A single entry in the "what's new" list, describing the changes of one app version.
 *
 * @param version   The version the changes were released in.
 * @param changes   The user-facing changes of that version.
 */
data class NewsEntry(
    val version: String,
    val changes: List<String>
)

/**
 * The hardcoded "what's new" entries, ordered from newest to oldest.
 *
 * Add a new entry to the top of this list for every release that introduces user-facing changes.
 * The first entry is treated as the latest news; when its version differs from the version the user
 * has last seen, the "what's new" screen is shown to them once (see `LoadingScreen`).
 */
val newsEntries: List<NewsEntry> = listOf(
    NewsEntry(
        version = "1.1.0",
        changes = listOf(
            "Termine können jetzt auch außerhalb der Öffnungszeiten vereinbart werden.",
            "Die Eingabe der E-Mail Adresse ist verpflichtend.",
            "Der Kontaktbildschirm ist noch barrierefreier.",
            "Wenn während einem Anruf der/die Assistent/in die Kamera wechselt oder ein Foto "
                    + "aufnimmt, gibt es einen Signalton.",
            "Der/Die Assistent/in kann bei einem Anruf die Taschenlampe einschalten, um besser "
                    + "sehen zu können."
        )
    )
)

/**
 * The version of the latest news entry, or `null` if there are no news entries.
 *
 * This is used to decide whether there is something new to show to the user.
 */
val latestNewsVersion: String? = newsEntries.firstOrNull()?.version
