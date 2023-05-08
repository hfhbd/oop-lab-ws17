package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class TerminalTestAufgabe1 {

    @Test
    fun testNeuesTerminal() = runTest {

        /* Anlegen eines Container-Terminals mit 4 mal 4 Stellpl�tzen. */
        val terminal = Terminal(4, 4, testTimeSource)

        /* Die Anzahl der eingelagerten Container ist anfangs 0 */
        assertEquals(0, terminal.genutzteKapazitaet)

        /* Die freie Kapazit�t betr�gt aktuell 16 Container */
        assertEquals(16, terminal.freieKapazitaet)

        /* Bisher keine Containerbewegungen */
        assertEquals(0, terminal.anzahlBewegungen)
    }

    @Test
    fun testNeueContainer() {

        // leichte Bauweise, Inhalt Luftmatrazen
        val cont1 = Container(false, "Luftmatrazen")
        // stabile Container, Inhalt Strandbar
        val cont2 = Container(true, "Strandbar")

        // Der erste Container hat eine ID>0 und hat keine stabile Bauweise
        assertEquals("Luftmatrazen", cont1.beschreibung)
        assertTrue(cont1.id > 0)
        assertFalse(cont1.isStable)

        // Der zweite Container hat die ID 2 und hat eine stabile Bauweise
        // Container-IDs werdem fortlaufend automatisch vergeben
        assertEquals("Strandbar", cont2.beschreibung)
        assertEquals(cont1.id + 1, cont2.id)
        assertTrue(cont2.isStable)
    }

    @Test
    fun testNeuerLKW() = runTest{
        // Anlegen zweier Container
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")
        val cont1ID = cont1.id
        val cont1SB = cont1.isStable
        val cont1Bes = cont1.beschreibung

        val cont2ID = cont2.id
        val cont2SB = cont2.isStable
        val cont2Bes = cont2.beschreibung

        // Neuer LKW
        val lkw = LKW(testTimeSource)

        // LKW noch leer (1 Containerplatz frei, 0 genutzt)
        assertEquals(0, lkw.genutzteKapazitaet)
        assertEquals(1, lkw.freieKapazitaet)
        assertFalse(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        // mit c1 beladen (0 Containerplatz frei, 1 genutzt)
        // R�ckgabewert true, weil Beladung erfolgreich (false sonst)
        var ok = lkw.belade(cont1)
        assertTrue(ok)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertTrue(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        // kann man c2 noch draufpacken? Nein, kein Ver�nderung
        // R�ckgabewert false, weil Beladung nicht erfolgreich
        ok = lkw.belade(cont2)
        assertFalse(ok)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertTrue(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        // versuche c2 abzuladen
        // R�ckgabewert null, wenn Abladen nicht m�glich
        var c = try {
            lkw.entlade(cont2.id)
        } catch (e: ContractFailureException) {
            null
        }
        assertNull(c)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertTrue(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        // versuche c1 abzuladen
        // R�ckgabewert Container-Referenz, wenn Abladen m�glich
        c = lkw.entlade(cont1.id)
        assertEquals(cont1, c)
        assertEquals(0, lkw.genutzteKapazitaet)
        assertEquals(1, lkw.freieKapazitaet)
        assertFalse(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        // jetzt kann man auch c2 aufladen
        // R�ckgabewert true, weil Beladen erfolgreich
        ok = lkw.belade(cont2)
        assertTrue(ok)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertFalse(lkw.enthaelt(cont1.id))
        assertTrue(lkw.enthaelt(cont2.id))

        // Durch das Be/Entladen haben sich keine Eigenschaften der Container
        // ge�ndert
        assertEquals(cont1Bes, cont1.beschreibung)
        assertEquals(cont1ID, cont1.id)
        assertEquals(cont1SB, cont1.isStable)

        assertEquals(cont2Bes, cont2.beschreibung)
        assertEquals(cont2ID, cont2.id)
        assertEquals(cont2SB, cont2.isStable)
    }

    @Test
    fun testEinliefern() = runTest{
        val terminal = Terminal(4, 4, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /*
         * Beim TerminalController wird ein Transport angek�ndig: Der Transport ist
         * f�r die Zeit 2100 angek�ndigt und es wird ein Container mit der
         * ID c2.id eingelagert (erstes Array). Es werden keine
         * Container abgeholt (zweites Argument null). Wir benutzen Arrays,
         * weil zuk�nftige Transporte mehr als einen Container umfassen
         * k�nnen.
         */

        val lkw1 = LKW(testTimeSource)
        lkw1.auftrag = terminal.avisierung(2100.seconds, intArrayOf(cont2.id))
        lkw1.belade(cont2)

        /*
         * Beim TerminalController wird ein Transport angek�ndig: Der Transport ist
         * f�r die Zeit 2120 angek�ndigt und es wird ein Container mit der
         * ID c1.id eingelagert (erstes Array) und ein Container mit
         * der ID c2.id abgeholt (zweites Array).
         */

        val lkw2 = LKW(testTimeSource)
        lkw2.auftrag = terminal.avisierung(2120.seconds, intArrayOf(cont1.id), intArrayOf(cont2.id))
        lkw2.belade(cont1)

        assertEquals(1, lkw2.genutzteKapazitaet)
        assertEquals(0, lkw2.freieKapazitaet)
        assertTrue(lkw2.enthaelt(cont1.id))
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(16, terminal.freieKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)

        /*
         * Jetzt setzen wir die Zeit auf 2119 und der LKW kommt beim
         * TerminalController an. Er wird gem�� Ank�ndigung abgefertigt, d.h. er wird
         * be/entladen. Container 2 kann nicht ausgeliefert werden, weil er
         * nicht im TerminalController ist (daher nur eine Containerbewegung).
         */
        // -------------------------------------------
        delay(2100.seconds)
        val okLKW1 = terminal.abfertigung(lkw1)
        delay(20.seconds)
        val okLKW2 = terminal.abfertigung(lkw2)

        assertTrue(okLKW1 && okLKW2)
        assertEquals(0, lkw1.genutzteKapazitaet)
        assertEquals(1, lkw1.freieKapazitaet)
        assertEquals(1, lkw2.genutzteKapazitaet)
        assertEquals(0, lkw2.freieKapazitaet)
        assertFalse(lkw2.enthaelt(cont1.id))
        assertTrue(lkw2.enthaelt(cont2.id))
        assertTrue(terminal.enthaelt(cont1.id))
        assertFalse(terminal.enthaelt(cont2.id))
        assertEquals(3, terminal.anzahlBewegungen)
    }

    @Test
    fun testEinAusliefern() = runTest{

        //geändert zu 5,1 vorher 1,5
        val terminal = Terminal(2, 5, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* LKW 1 liefert Container 1 zum TerminalController */

        val lkw1 = LKW(testTimeSource)
        lkw1.auftrag = terminal.avisierung(2100.seconds, intArrayOf(cont1.id))
        lkw1.belade(cont1)
        delay(2100.seconds)
        val okLKW1 = terminal.abfertigung(lkw1)
        assertTrue(okLKW1)

        /* LKW 2 liefert Container 2 und holt Container 1 */

        val lkw2 = LKW(testTimeSource)
        lkw2.auftrag = terminal.avisierung(2160.seconds, intArrayOf(cont2.id), intArrayOf(cont1.id))
        lkw2.belade(cont2)
        delay(55)
        val okLKW2 = terminal.abfertigung(lkw2)
        assertTrue(okLKW2)

        /* hat LKW 2 nun genau den Container, der von LKW 1 angeliefert wurde? */
        val c = lkw2.entlade(cont1.id)
        assertEquals(cont1, c)

        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(9, terminal.freieKapazitaet)
        assertEquals(3, terminal.anzahlBewegungen)
    }

    @Test
    fun testGebuehren() = runTest {

        val terminal = Terminal(4, 4, testTimeSource)

        val c1 = Container(false, "Luftmatrazen")
        val c2 = Container(true, "Strandbar")

        val lkw1 = LKW(testTimeSource)
        lkw1.auftrag = terminal.avisierung(2120.seconds, intArrayOf(c1.id))
        lkw1.belade(c1)

        val lkw2 = LKW(testTimeSource)
        lkw2.auftrag = terminal.avisierung(2140.seconds, intArrayOf(c2.id), intArrayOf(c1.id))
        lkw2.belade(c2)

        delay(2120.seconds)
        val okLKW1 = terminal.abfertigung(lkw1)
        assertEquals(1, terminal.anzahlBewegungen)

        /* aktuelle Geb�hren f�r einzelne Container und gesamt */
        val fix = 20.0
        val rate = 1.5
        assertEquals(fix + 1 * rate, terminal.getGebuehren(c1.id))
        assertEquals(0.0, terminal.getGebuehren(c2.id))
        assertEquals(fix + 1 * rate, terminal.gebuehren)

        delay(51.seconds)
        val okLKW2 = terminal.abfertigung(lkw2)
        assertEquals(3, terminal.anzahlBewegungen)
        assertTrue(okLKW1 && okLKW2)

        /* aktuelle Geb�hren f�r einzelne Container und gesamt */
        assertEquals(fix + 52 * rate, terminal.getGebuehren(c1.id))
        assertEquals(fix + 1 * rate, terminal.getGebuehren(c2.id))
        assertEquals(2 * fix + 53 * rate, terminal.gebuehren)

        delay(1.seconds)

        /*
         * aktuelle Geb�hren f�r einzelne Container und gesamt: nur c2 im
         * TerminalController
         */
        assertEquals(fix + 52 * rate, terminal.getGebuehren(c1.id))
        assertEquals(fix + 2 * rate, terminal.getGebuehren(c2.id))
        assertEquals(2 * fix + 54 * rate, terminal.gebuehren)
    }
}
