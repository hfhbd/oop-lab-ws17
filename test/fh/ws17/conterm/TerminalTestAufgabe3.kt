package fh.ws17.conterm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TerminalTestAufgabe3 {

    @Before
    fun setUp() {
        /* vor jedem Test die Uhr zur�cksetzen */
        Uhr.reset()
    }

    @Test(expected = CapacityExceededException::class)
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
        Assert.assertTrue(ok)
        Assert.assertEquals(1, terminal.genutzteKapazitaet)
        Assert.assertEquals(0, terminal.freieKapazitaet)
        Assert.assertEquals(1, terminal.anzahlBewegungen)
        Assert.assertTrue(terminal.enthaelt(cont1.id))
        Assert.assertEquals(3, kahn.freieKapazitaet)

        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = (terminal.avisierung(10, intArrayOf(cont2.id), intArrayOf()))
        Uhr.incZeit(10)

        terminal.abfertigung(lkw)

        /* Container 2 nicht abgeladen. */
        Assert.assertEquals(1, terminal.genutzteKapazitaet)
        Assert.assertEquals(0, terminal.freieKapazitaet)
        Assert.assertEquals(1, terminal.anzahlBewegungen)
        Assert.assertTrue(terminal.enthaelt(cont1.id))
        Assert.assertFalse(terminal.enthaelt(cont2.id))
        Assert.assertTrue(lkw.enthaelt(cont2.id))

        /* Geb�hren */
        val fix = 20.0
        val rate = 1.5
        Uhr.incZeit(10)
        Assert.assertEquals(fix + 21 * rate, terminal.getGebuehren(cont1.id), 0.001)
        Assert.assertEquals(0.0, terminal.getGebuehren(cont2.id), 0.001)
        Assert.assertEquals(fix + 21 * rate, terminal.gebuehren, 0.001)

    }


    @Test(expected = ContractFailureException::class)
    @Throws(ContractFailureException::class)
    fun testContractFailure() {

        val terminal = Terminal(1, 1)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn()
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont2.id), intArrayOf())
        Uhr.incZeit(10)

        terminal.abfertigung(kahn)

        /* Container 1 nicht abgeladen. */
        Assert.assertEquals(0, terminal.genutzteKapazitaet)
        Assert.assertEquals(1, terminal.freieKapazitaet)
        Assert.assertEquals(0, terminal.anzahlBewegungen)
        Assert.assertFalse(terminal.enthaelt(cont1.id))
        Assert.assertEquals(2, kahn.freieKapazitaet)

        /* Geb�hren */
        Uhr.incZeit(10)
        Assert.assertEquals(0.0, terminal.getGebuehren(cont1.id), 0.001)
        Assert.assertEquals(0.0, terminal.getGebuehren(cont2.id), 0.001)
        Assert.assertEquals(0.0, terminal.gebuehren, 0.001)

    }

    @Test(expected = ContractFailureException::class)
    fun testContractFailure2() {

        val terminal = Terminal(1, 1)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn()

        kahn.auftrag = terminal.avisierung(10, containerOutbound = intArrayOf(cont2.id))
        Uhr.incZeit(10)

        terminal.abfertigung(kahn)

        /* Container 1 nicht abgeladen. */
        Assert.assertEquals(0, terminal.genutzteKapazitaet)
        Assert.assertEquals(1, terminal.freieKapazitaet)
        Assert.assertEquals(0, terminal.anzahlBewegungen)
        Assert.assertFalse(terminal.enthaelt(cont2.id))
        Assert.assertEquals(3, kahn.freieKapazitaet)
        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = terminal.avisierung(10, intArrayOf(cont2.id))

        /* Geb�hren */
        Uhr.incZeit(10)
        Assert.assertEquals(0.0, terminal.getGebuehren(cont1.id), 0.001)
        Assert.assertEquals(0.0, terminal.getGebuehren(cont2.id), 0.001)

        Assert.assertEquals(0.0, terminal.gebuehren, 0.001)

    }

    @Test
    fun testIterate() {
        val terminal = Terminal(4, 2)
        val kahn = Kahn()
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        Uhr.incZeit(10)
        val ok = terminal.abfertigung(kahn)
        Assert.assertTrue(ok)

        val sumCheck = cont1.id + cont2.id + cont3.id
        var sumContIDInTerm = 0
        // for (Container cont : terminal) {  // nur bei Iterable<Container>
        for (cont in terminal) {
            sumContIDInTerm += cont.id
        }

        Assert.assertEquals(3, terminal.genutzteKapazitaet)
        Assert.assertEquals(5, terminal.freieKapazitaet)
        Assert.assertEquals(3, terminal.anzahlBewegungen)
        Assert.assertTrue(terminal.enthaelt(cont1.id))
        Assert.assertTrue(terminal.enthaelt(cont2.id))
        Assert.assertTrue(terminal.enthaelt(cont3.id))
        Assert.assertFalse(kahn.enthaelt(cont1.id))
        Assert.assertFalse(kahn.enthaelt(cont2.id))
        Assert.assertFalse(kahn.enthaelt(cont3.id))
        Assert.assertEquals(3, kahn.freieKapazitaet)

        Assert.assertEquals(sumCheck, sumContIDInTerm)
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
        Assert.assertFalse(kahn.auftrag!!.isOriginal)


        val lkw = LKW()
        lkw.belade(cont2)

        lkw.auftrag = t.avisierung(10, intArrayOf(cont2.id), intArrayOf())
        Assert.assertFalse(lkw.auftrag!!.isOriginal)

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

        kahn.auftrag = this.avisierung(zeit,
                intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        Uhr.incZeit(10)

        this.abfertigung(kahn)

        return kahn
    }

    /* gleichnamiger Test aus Aufgabe 2, dieses Mal mit anderem Verhalten: Exception */
    @Test(expected = CapacityExceededException::class)
    fun testeKapazitaet2x2() {
        val t1 = Terminal(2, 2)
        val k1 = t1.anliefernKahn(10)
        Assert.assertEquals(3, t1.genutzteKapazitaet)
        Assert.assertEquals(1, t1.freieKapazitaet)
        Assert.assertEquals(3, t1.anzahlBewegungen)
        Assert.assertEquals(3, k1.freieKapazitaet)

        val k2 = t1.anliefernKahn(20) // scheitert, weil nicht genug Kapazit�t vorhanden ist
        Assert.assertEquals(0, k2.freieKapazitaet)


        Assert.assertEquals(3, t1.genutzteKapazitaet)
        Assert.assertEquals(1, t1.freieKapazitaet)
        Assert.assertEquals(3, t1.anzahlBewegungen)
    }
}