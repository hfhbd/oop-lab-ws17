package fh.ws17.conterm

import kotlin.test.*

class TerminalTestAufgabe1 {

    @BeforeTest
    fun setUp() {
        /* vor jedem Test die Uhr zur�cksetzen */
        Uhr.reset()
    }

    @Test
    fun testeUhr() {

        /* �ber getInstance() bekommen wir immer die gleiche Uhr */
        val clock1 = Uhr
        val clock2 = Uhr
        assertEquals(clock1, clock2)
        assertEquals(0, clock1.zeit)

        /* Zeit manuell weiterschalten */
        clock2.incZeit(10)
        assertEquals(10, clock1.zeit)
        clock2.incZeit(3)
        assertEquals(13, clock1.zeit)

        /* Zeit zur�cksetzen */
        clock2.reset()
        assertEquals(0, clock1.zeit)
    }

    @Test
    fun testNeuesTerminal() {

        /* Anlegen eines Container-Terminals mit 4 mal 4 Stellpl�tzen. */
        val terminal = Terminal(4, 4)

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
    fun testNeuerLKW() {
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
        val lkw = LKW()

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
    fun testEinliefern() {
        val terminal = Terminal(4, 4)

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

        val lkw1 = LKW()
        lkw1.auftrag = terminal.avisierung(2100, intArrayOf(cont2.id))
        lkw1.belade(cont2)

        /*
         * Beim TerminalController wird ein Transport angek�ndig: Der Transport ist
         * f�r die Zeit 2120 angek�ndigt und es wird ein Container mit der
         * ID c1.id eingelagert (erstes Array) und ein Container mit
         * der ID c2.id abgeholt (zweites Array).
         */

        val lkw2 = LKW()
        lkw2.auftrag = terminal.avisierung(2120, intArrayOf(cont1.id), intArrayOf(cont2.id))
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
        Uhr.incZeit(2100)
        val okLKW1 = terminal.abfertigung(lkw1)
        Uhr.incZeit(20)
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
    fun testEinAusliefern() {

        //geändert zu 5,1 vorher 1,5
        val terminal = Terminal(2, 5)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")

        /* LKW 1 liefert Container 1 zum TerminalController */

        val lkw1 = LKW()
        lkw1.auftrag = terminal.avisierung(2100, intArrayOf(cont1.id))
        lkw1.belade(cont1)
        Uhr.incZeit(2100)
        val okLKW1 = terminal.abfertigung(lkw1)
        assertTrue(okLKW1)

        /* LKW 2 liefert Container 2 und holt Container 1 */

        val lkw2 = LKW()
        lkw2.auftrag = terminal.avisierung(2160, intArrayOf(cont2.id), intArrayOf(cont1.id))
        lkw2.belade(cont2)
        Uhr.incZeit(55)
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
    fun testGebuehren() {

        val terminal = Terminal(4, 4)

        val c1 = Container(false, "Luftmatrazen")
        val c2 = Container(true, "Strandbar")

        val lkw1 = LKW()
        lkw1.auftrag = terminal.avisierung(2120, intArrayOf(c1.id))
        lkw1.belade(c1)

        val lkw2 = LKW()
        lkw2.auftrag = terminal.avisierung(2140, intArrayOf(c2.id), intArrayOf(c1.id))
        lkw2.belade(c2)

        Uhr.incZeit(2120)
        val okLKW1 = terminal.abfertigung(lkw1)
        assertEquals(1, terminal.anzahlBewegungen)

        /* aktuelle Geb�hren f�r einzelne Container und gesamt */
        val fix = 20.0
        val rate = 1.5
        assertEquals(fix + 1 * rate, terminal.getGebuehren(c1.id))
        assertEquals(0.0, terminal.getGebuehren(c2.id))
        assertEquals(fix + 1 * rate, terminal.gebuehren)

        Uhr.incZeit(51)
        val okLKW2 = terminal.abfertigung(lkw2)
        assertEquals(3, terminal.anzahlBewegungen)
        assertTrue(okLKW1 && okLKW2)

        /* aktuelle Geb�hren f�r einzelne Container und gesamt */
        assertEquals(fix + 52 * rate, terminal.getGebuehren(c1.id))
        assertEquals(fix + 1 * rate, terminal.getGebuehren(c2.id))
        assertEquals(2 * fix + 53 * rate, terminal.gebuehren)

        Uhr.incZeit(1)

        /*
         * aktuelle Geb�hren f�r einzelne Container und gesamt: nur c2 im
         * TerminalController
         */
        assertEquals(fix + 52 * rate, terminal.getGebuehren(c1.id))
        assertEquals(fix + 2 * rate, terminal.getGebuehren(c2.id))
        assertEquals(2 * fix + 54 * rate, terminal.gebuehren)
    }
}