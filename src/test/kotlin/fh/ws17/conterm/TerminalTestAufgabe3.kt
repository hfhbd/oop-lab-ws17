package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class TerminalTestAufgabe3 {

    @Test
    fun testCapExceeded() = runTest {

        val terminal = Terminal(1, 1, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn(testTimeSource)
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont1.id), intArrayOf())
        delay(10.seconds)

        val ok = terminal.abfertigung(kahn)

        /* Container 1 abgeladen. */
        assertTrue(ok)
        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.freieKapazitaet)
        assertEquals(1, terminal.anzahlBewegungen)
        assertTrue(terminal.enthaelt(cont1.id))
        assertEquals(3, kahn.freieKapazitaet)

        val lkw = LKW(testTimeSource)
        lkw.belade(cont2)

        lkw.auftrag = (terminal.avisierung(10.seconds, intArrayOf(cont2.id), intArrayOf()))
        delay(10.seconds)

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
        delay(10.seconds)
        assertEquals(fix + 21 * rate, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))
        assertEquals(fix + 21 * rate, terminal.gebuehren)
    }


    @Test
    fun testContractFailure() = runTest {

        val terminal = Terminal(1, 1, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")

        val kahn = Kahn(testTimeSource)
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont2.id), intArrayOf())
        delay(10.seconds)

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
        delay(10.seconds)
        assertEquals(0.0, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))
        assertEquals(0.0, terminal.gebuehren)

    }

    @Test
    fun testContractFailure2() = runTest {

        val terminal = Terminal(1, 1, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")

        val kahn = Kahn(testTimeSource)

        kahn.auftrag = terminal.avisierung(10.seconds, containerOutbound = intArrayOf(cont2.id))
        delay(10.seconds)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(kahn)
        }
        /* Container 1 nicht abgeladen. */
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(1, terminal.freieKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)
        assertFalse(terminal.enthaelt(cont2.id))
        assertEquals(3, kahn.freieKapazitaet)
        val lkw = LKW(testTimeSource)
        lkw.belade(cont2)

        lkw.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont2.id))

        /* Geb�hren */
        delay(10.seconds)
        assertEquals(0.0, terminal.getGebuehren(cont1.id))
        assertEquals(0.0, terminal.getGebuehren(cont2.id))

        assertEquals(0.0, terminal.gebuehren)

    }

    @Test
    fun testIterate() = runTest {
        val terminal = Terminal(4, 2, testTimeSource)
        val kahn = Kahn(testTimeSource)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")
        val cont3 = Container(true, "Motorboot")
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        delay(10.seconds)
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
    fun testNotOriginal() = runTest {
        val t = Terminal(4, 2, testTimeSource)
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")

        val kahn = Kahn(testTimeSource)
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        kahn.auftrag = t.avisierung(10.seconds, intArrayOf(cont1.id, cont2.id, cont3.id), intArrayOf())
        assertFalse(kahn.auftrag!!.isOriginal)


        val lkw = LKW(testTimeSource)
        lkw.belade(cont2)

        lkw.auftrag = t.avisierung(10.seconds, intArrayOf(cont2.id), intArrayOf())
        assertFalse(lkw.auftrag!!.isOriginal)

    }

    /** Hilfsfunktion zum Anliefern dreier Container mit einem Kahn  */
    suspend fun Terminal.anliefernKahn(timeSource: TimeSource.WithComparableMarks, zeit: Duration): Kahn {

        val kahn = Kahn(timeSource)
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
        delay(10.seconds)

        this.abfertigung(kahn)

        return kahn
    }

    /* gleichnamiger Test aus Aufgabe 2, dieses Mal mit anderem Verhalten: Exception */
    @Test
    fun testeKapazitaet2x2() = runTest {
        val t1 = Terminal(2, 2, testTimeSource)
        val k1 = t1.anliefernKahn(testTimeSource, 10.seconds)
        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
        assertEquals(3, k1.freieKapazitaet)


        assertFailsWith<CapacityExceededException> {
            val k2 = t1.anliefernKahn(testTimeSource, 20.seconds)
            // scheitert, weil nicht genug Kapazit�t vorhanden ist
            assertEquals(0, k2.freieKapazitaet)
        }

        assertEquals(3, t1.genutzteKapazitaet)
        assertEquals(1, t1.freieKapazitaet)
        assertEquals(3, t1.anzahlBewegungen)
    }
}
