/******************************************************************************
 * Copyright (c) 2026.                                                        *
 * valo.media GmbH                                                            *
 * All rights reserved.                                                       *
 ******************************************************************************/

package media.valo.tower_android.ui.routes.news

//
//  ReleaseNotes.kt
//  Tower_Android
//

/**
 * What changed for the user in one release of the app.
 *
 * @param version   The version the changes were released in.
 * @param changes   What changed, as html.
 */
data class ReleaseNotes(
    val version: String,
    val changes: String
)

/**
 * The release notes shown on the news screen, newest first.
 *
 * These are written for the user, so a release only belongs here if it changed something the user
 * can notice, and the entries say what the user gets rather than what we did. The technical record
 * of every release lives in the GitHub releases instead.
 */
val releaseNotes: List<ReleaseNotes> = listOf(
    ReleaseNotes(
        version = "1.3.0",
        changes = """
            <b>Wir sind umgezogen!</b>
            <br><br>
            Bitte installiere die neue TOWER Assist App,
            damit du unsere Assistenz weiterhin wie gewohnt nutzen kannst.
            Die bisherige TOWER Fernassistenz App wird ab jetzt nicht mehr gepflegt
            und demnächst abgeschaltet.
            Die neue App gehört zur TOWER Assist GmbH
            und ersetzt die bisherige App.
            Alles andere bleibt wie gewohnt.
            <br><br>
            <u><a href="https://tower-assist.de/app">TOWER Assist App jetzt installieren</a></u>
            """
    ),
    ReleaseNotes(
        version = "1.2.0",
        changes = "Unter „Neuigkeiten“ im Menü oben rechts kannst du jederzeit nachlesen, was "
                + "sich in der App geändert hat. Nach einem Update zeigen wir dir die "
                + "Neuigkeiten einmal automatisch."
                + "<br><br>"
                + "<b>Wichtige Info</b>: Der Fernassistenzservice wird bald kostenpflichtig. "
                + "Fünf Minuten Fernassistenz pro Monat werden weiterhin kostenlos sein. Zu den "
                + "genauen Preisen und Konditionen informieren wir dich über unsere üblichen "
                + "Kanäle und natürlich auch in der App."
    ),
    ReleaseNotes(
        version = "1.1.1",
        changes = "Wenn du einen Anruf gleich wieder beendest, kannst du jetzt sofort erneut "
                + "anrufen."
    ),
    ReleaseNotes(
        version = "1.1.0",
        changes = "Termine können jetzt auch außerhalb der Öffnungszeiten vereinbart werden. Die "
                + "Eingabe der E-Mail Adresse ist verpflichtend. Der Kontaktbildschirm ist noch "
                + "barrierefreier. Wenn während einem Anruf der/die Assistent/in die Kamera "
                + "wechselt oder ein Foto aufnimmt, gibt es einen Signalton. Der/Die Assistent/in "
                + "kann bei einem Anruf die Taschenlampe einschalten, um besser sehen zu können."
    ),
    ReleaseNotes(
        version = "1.0.0",
        changes = "Die erste Version von TOWER Fernassistenz. Du siehst auf einen Blick, wann die "
                + "Fernassistenz für dich erreichbar ist, und erreichst uns über den "
                + "Kontaktbildschirm auf allen Wegen. Während eines Anrufs kann der/die "
                + "Assistent/in zwischen deinen Kameras wechseln und Fotos aufnehmen. Der Anruf "
                + "läuft weiter, wenn du zwischendurch etwas anderes am Handy machst, und die App "
                + "lässt sich hochkant und quer benutzen. Unseren Newsletter kannst du direkt bei "
                + "der Anmeldung abonnieren, und die App sagt dir Bescheid, wenn du sie "
                + "aktualisieren musst."
    )
)

/**
 * The version of the newest release notes.
 *
 * This is the newest version the news screen has something to say about, and not the version of
 * the app. A release without user-visible changes has no release notes, and so does not put up a
 * news screen with nothing to report on it.
 */
val newestNewsVersion: String = releaseNotes.first().version
