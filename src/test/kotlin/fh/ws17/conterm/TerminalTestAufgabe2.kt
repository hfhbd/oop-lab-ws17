package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * Tests f�r Aufgabe 2
 *
 *
 * Hinweis: Die Stapelung auf dem Kahn ergibt sich durch die Reihenfolge der
 * Aufrufe von belade und entlade. Die Reihenfolge beim Abladen wird in einem
 * Auftrag eigentlich nicht vorgegeben, ein Auftrag enth�lt einfach die Menge
 * der auf/abzuladenden Container. Damit k�nnen Auftr�ge aber auch
 * "unm�glich sein" (zum Beispiel zwei Container in leichter Bauweise von einem
 * Kahn abholen lassen). Es m�sste somit theoretisch gepr�ft werden, ob ein
 * Auftrag �berhaupt ausf�hrbar ist. Um solche Komplikationen wollen wir uns
 * nicht k�mmern m�ssen, gehen Sie davon aus, dass alle Auftr�ge ausf�hrbar
 * sind.
 */

class TerminalTestAufgabe2 {

    @Test
    fun testNeuerLastzug() = runTest {
        /* Anlegen zweier Container */
        val cont1 = Container(true, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")
        val cont3 = Container(true, "Jetski")

        /* Einzige Methode zum Beladen eines Lastzugs ist wieder die
     * belade-Methode, zum Abladen die entlade-Methode. */
        val lastzug = Lastzug(testTimeSource)

        /* Kahn ist anfangs leer */
        assertEquals(0, lastzug.genutzteKapazitaet)
        assertEquals(2, lastzug.freieKapazitaet)
        assertFalse(lastzug.enthaelt(cont1.id))
        assertFalse(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))

        /* mit erstem Container cont1 beladen */
        lastzug.belade(cont1)
        assertEquals(1, lastzug.genutzteKapazitaet)
        assertEquals(1, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont1.id))
        assertFalse(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))

        /* kann man cont2 noch draufpacken? Ja. */
        lastzug.belade(cont2)
        assertEquals(2, lastzug.genutzteKapazitaet)
        assertEquals(0, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont1.id))
        assertTrue(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))

        /* kann man noch mehr aufladen? Nein. */
        lastzug.belade(cont3)
        assertEquals(2, lastzug.genutzteKapazitaet)
        assertEquals(0, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont1.id))
        assertTrue(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))

        /* versuche cont1 zuerst abzuladen, ok */
        var c = lastzug.entlade(cont1.id)
        assertEquals(cont1, c)
        assertEquals(1, lastzug.genutzteKapazitaet)
        assertEquals(1, lastzug.freieKapazitaet)
        assertFalse(lastzug.enthaelt(cont1.id))
        assertTrue(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))

        /* dann cont2 abladen */
        c = lastzug.entlade(cont2.id)
        assertEquals(cont2, c)
        assertEquals(0, lastzug.genutzteKapazitaet)
        assertEquals(2, lastzug.freieKapazitaet)
        assertFalse(lastzug.enthaelt(cont1.id))
        assertFalse(lastzug.enthaelt(cont2.id))
        assertFalse(lastzug.enthaelt(cont3.id))
    }

    @Test
    fun testNeuerKahn() = runTest {
        /* Anlegen zweier Container */
        val cont1 = Container(true, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")
        val cont1ID = cont1.id
        val cont2ID = cont2.id

        /*
     * Einzige Methode zum Beladen eines Kahns ist wieder die
     * belade-Methode, zum Abladen die entlade-Methode. Die Stapelung auf
     * dem Kahn ergibt sich durch die Reihenfolge der Aufrufe von belade und
     * entlade.
     */
        val kahn = Kahn(testTimeSource)

        /* Kahn ist anfangs leer */
        assertEquals(0, kahn.genutzteKapazitaet)
        assertEquals(3, kahn.freieKapazitaet)
        assertFalse(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))

        /* mit erstem Container cont1 beladen */
        kahn.belade(cont1)
        assertEquals(1, kahn.genutzteKapazitaet)
        assertEquals(2, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))

        /* kann man cont2 noch draufpacken? Ja. */
        kahn.belade(cont2)
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertTrue(kahn.enthaelt(cont2.id))

        /*
     * kann man ihn noch mal aufladen? Nein, schon geladen, keine
     * Ver�nderung.
     */
        kahn.belade(cont2)
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertTrue(kahn.enthaelt(cont2.id))

        /* versuche cont1 abzuladen - geht nicht, weil er nicht oben liegt */
        var c = try {
            kahn.entlade(cont1.id)
        } catch (e: ContractFailureException) {
            null
        }
        assertNull(c)
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertTrue(kahn.enthaelt(cont2.id))

        /* erst cont2 abladen - dann geht es */
        c = kahn.entlade(cont2.id)
        assertEquals(cont2, c)
        assertEquals(1, kahn.genutzteKapazitaet)
        assertEquals(2, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))

        /* dann cont1 abladen */
        c = kahn.entlade(cont1.id)
        assertEquals(cont1, c)
        assertEquals(0, kahn.genutzteKapazitaet)
        assertEquals(3, kahn.freieKapazitaet)
        assertFalse(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))

        // Durch das Be/Entladen haben sich keine Eigenschaften der Container
        // ge�ndert
        assertEquals("Luftmatrazen", cont1.beschreibung)
        assertEquals(cont1ID, cont1.id)
        assertTrue(cont1.isStable)

        assertEquals("Strandbar", cont2.beschreibung)
        assertEquals(cont2ID, cont2.id)
        assertTrue(cont2.isStable)

    }

    @Test
    fun testKahnStapelLS() = runTest {

        val kahn = Kahn(testTimeSource)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* unten leichte Bausweise, oben stabile Bausweise ist NICHT in Ordnung */
        val ok1 = kahn.belade(cont1)
        val ok2 = kahn.belade(cont2)
        assertTrue(ok1)
        assertFalse(ok2)
        assertEquals(1, kahn.genutzteKapazitaet)
        assertEquals(2, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))
    }

    @Test
    fun testKahnStapelSL() = runTest {

        val kahn = Kahn(testTimeSource)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* unten stabile Bausweise, oben leichte Bausweise ist in Ordnung */
        val ok1 = kahn.belade(cont2)
        val ok2 = kahn.belade(cont1)
        assertTrue(ok1)
        assertTrue(ok2)
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertTrue(kahn.enthaelt(cont2.id))

    }

    @Test
    fun testEinliefern() = runTest {

        val terminal = Terminal(4, 4, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")

        val kahn = Kahn(testTimeSource)
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        /*
         * Beim Kahn haben wir erstmals gestapelte Container bei der
         * Avisierung. Die Container aus den Avisierungs-Arrays sind
         * (beginnend bei Index 0) der Reihe nach abzuarbeiten. (Es reicht
         * eine einfache Einlagerungs-Strategie, Sie m�ssen die Anzahl der
         * Container-Bewegungen nicht minimieren.)
         */
        kahn.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont3.id))
        delay(10.seconds)
        terminal.abfertigung(kahn)

        /*
         * Um Container 3 abzuladen, m�ssen beide anderen Container
         * kurzfristig eingelagert werden.
         */
        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(15, terminal.freieKapazitaet)
        val b = terminal.anzahlBewegungen
        assertTrue(b in 5..6) // 5-6 Bewegungen
        assertFalse(terminal.enthaelt(cont1.id))
        assertFalse(terminal.enthaelt(cont2.id))
        assertTrue(terminal.enthaelt(cont3.id))

        /* Am Ende aber nur ein Container im TerminalController, 2 auf dem Kahn */
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont1.id))
        assertTrue(kahn.enthaelt(cont2.id))
        assertFalse(kahn.enthaelt(cont3.id))

        /* Geb�hren */
        val fix = 20.0
        val rate = 1.5
        delay(10.seconds)
        assertEquals(fix + 1 * rate, terminal.getGebuehren(cont1.id))
        assertEquals(fix + 1 * rate, terminal.getGebuehren(cont2.id))
        assertEquals(fix + 11 * rate, terminal.getGebuehren(cont3.id))
        assertEquals(3 * fix + 13 * rate, terminal.gebuehren)
    }

    /**
     * Hilfsfunktion zum Anliefern dreier Container mit einem Kahn
     */
    private suspend fun anliefernKahn(
        timeSource: TimeSource.WithComparableMarks,
        zeit: Duration,
        t: Terminal,
        klappt: Boolean
    ): Kahn {
        val kahn = Kahn(timeSource)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = t.avisierung(zeit, intArrayOf(cont1.id, cont2.id, cont3.id))
        delay(10.seconds)
        val ok = t.abfertigung(kahn)
        assertEquals(ok, klappt)
        return kahn
    }

    @Test
    fun testeKapazitaet2x2() = runTest {

        val t1 = Terminal(2, 2, testTimeSource)
        val k1 = anliefernKahn(testTimeSource, 10.seconds, t1, true)
        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
        assertEquals(3, k1.freieKapazitaet)

        try { // Dieses Kommando in Abgabe 1+2 ignorieren

            /* scheitert, weil nicht genug Kapazit�t vorhanden ist */
            val k2 = anliefernKahn(testTimeSource, 10.seconds, t1, false)
            assertEquals(0, k2.freieKapazitaet)

        } catch (e: Exception) {
            // ab Abgabe 3 wird die Situation anders behandelt, keine Pr�fung
        }

        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)

    }

    @Test
    fun testeKapazitaet4x2() = runTest {

        val t1 = Terminal(4, 2, testTimeSource)
        val k1 = anliefernKahn(testTimeSource, 10.seconds, t1, true)
        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(5, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
        assertEquals(3, k1.freieKapazitaet)
        /* viel Platz, kein Problem */
        val k2 = anliefernKahn(testTimeSource, 20.seconds, t1, true)
        assertEquals(6, t1.genutzteKapazitaet)
        assertEquals(2, t1.freieKapazitaet)
        assertEquals(6, t1.anzahlBewegungen)
        assertEquals(3, k2.freieKapazitaet)
    }
}
