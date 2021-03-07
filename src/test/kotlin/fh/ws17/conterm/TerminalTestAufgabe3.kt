package fh.ws17.conterm

import kotlin.test.*

class TerminalTestAufgabe3 {

    @BeforeTest
    fun setUp() {
        /* vor jedem Test die Uhr zur�cksetzen */
        Uhr.reset()
    }

    @Test
    fun testCapExceeded() {

        val terminal = Terminal(1, 1)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn()
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont1.id), intArrayOf())
        Uhr.incZeit(10)

        val ok = terminal.abfertigung(kahn)

        /* Container 1 abgeladen. */
        assertTrue(ok)
        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.freieKapazitaet)
        assertEquals(1, terminal.anzahlBewegungen)
        assertTrue(terminal.enthaelt(cont1.id))
        assertEquals(3, kahn.freieKapazitaet)

        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = (terminal.avisierung(10, intArrayOf(cont2.id), intArrayOf()))
        Uhr.incZeit(10)

        assertFailsWith<CapacityExceededException> {
            terminal.abfertigung(lkw)
        }

        /* Container 2 nicht abgeladen. */
        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.freieKapazitaet)
        assertEquals(1, terminal.anzahlBewegungen)
        assertTrue(terminal.enthaelt(cont1.id))
        assertFalse(terminal.enthaelt(cont2.id))
        assertTrue(lkw.enthaelt(cont2.id))

        /* Geb�hren */
        val fix = 20.0
        val rate = 1.5
        Uhr.incZeit(10)
        assertEquals(fix + 21 * rate, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))
        assertEquals(fix + 21 * rate, terminal.gebuehren)
    }


    @Test
    fun testContractFailure() {

        val terminal = Terminal(1, 1)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")

        val kahn = Kahn()
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont2.id), intArrayOf())
        Uhr.incZeit(10)

        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(kahn)
        }
        /* Container 1 nicht abgeladen. */
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(1, terminal.freieKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)
        assertFalse(terminal.enthaelt(cont1.id))
        assertEquals(2, kahn.freieKapazitaet)

        /* Geb�hren */
        Uhr.incZeit(10)
        assertEquals(0.0, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))
        assertEquals(0.0, terminal.gebuehren)

    }

    @Test
    fun testContractFailure2() {

        val terminal = Terminal(1, 1)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn()

        kahn.auftrag = terminal.avisierung(10, containerOutbound = intArrayOf(cont2.id))
        Uhr.incZeit(10)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(kahn)
        }
        /* Container 1 nicht abgeladen. */
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(1, terminal.freieKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)
        assertFalse(terminal.enthaelt(cont2.id))
        assertEquals(3, kahn.freieKapazitaet)
        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = terminal.avisierung(10, intArrayOf(cont2.id))

        /* Geb�hren */
        Uhr.incZeit(10)
        assertEquals(0.0, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))

        assertEquals(0.0, terminal.gebuehren)

    }

    @Test
    fun testIterate() {
        val terminal = Terminal(4, 2)
        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        Uhr.incZeit(10)
        val ok = terminal.abfertigung(kahn)
        assertTrue(ok)

        val sumCheck = cont1.id + cont2.id + cont3.id
        var sumContIDInTerm = 0
        // for (Container cont : terminal) {  // nur bei Iterable<Container>
        for (cont in terminal) {
            sumContIDInTerm += cont.id
        }

        assertEquals(3, terminal.genutzteKapazitaet)
        assertEquals(5, terminal.freieKapazitaet)
        assertEquals(3, terminal.anzahlBewegungen)
        assertTrue(terminal.enthaelt(cont1.id))
        assertTrue(terminal.enthaelt(cont2.id))
        assertTrue(terminal.enthaelt(cont3.id))
        assertFalse(kahn.enthaelt(cont1.id))
        assertFalse(kahn.enthaelt(cont2.id))
        assertFalse(kahn.enthaelt(cont3.id))
        assertEquals(3, kahn.freieKapazitaet)

        assertEquals(sumCheck, sumContIDInTerm)
    }

    /*
	 * TransporterController d�rfen nur Auftragskopien mit sich f�hren.
	 */
    @Test
    fun testNotOriginal() {
        val t = Terminal(4, 2)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")

        val kahn = Kahn()
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = t.avisierung(10, intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        assertFalse(kahn.auftrag!!.isOriginal)


        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = t.avisierung(10, intArrayOf(cont2.id), intArrayOf())
        assertFalse(lkw.auftrag!!.isOriginal)

    }

    /** Hilfsfunktion zum Anliefern dreier Container mit einem Kahn  */
    fun Terminal.anliefernKahn(zeit: Int): Kahn {

        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = this.avisierung(
            zeit,
            intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf()
        )
        Uhr.incZeit(10)

        this.abfertigung(kahn)

        return kahn
    }

    /* gleichnamiger Test aus Aufgabe 2, dieses Mal mit anderem Verhalten: Exception */
    @Test
    fun testeKapazitaet2x2() {
        val t1 = Terminal(2, 2)
        val k1 = t1.anliefernKahn(10)
        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
        assertEquals(3, k1.freieKapazitaet)


        assertFailsWith<CapacityExceededException> {
            val k2 = t1.anliefernKahn(20)
            // scheitert, weil nicht genug Kapazit�t vorhanden ist
            assertEquals(0, k2.freieKapazitaet)
        }

        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
    }
}