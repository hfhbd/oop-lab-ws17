package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class TerminalTestAufgabe2b {
    @Test
    fun testKapazitaet() = runTest{

        /* Anlegen von Containern */
        val cont1 = Container(true, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")
        val cont3 = Container(true, "Jetski")
        val cont4 = Container(true, "Palme")
        val cont5 = Container(true, "Sonnenschirm")
        val cont6 = Container(true, "Handt�cher")
        val cont7 = Container(true, "Wasser")

        val lkw = LKW(testTimeSource)
        val lastzug = Lastzug(testTimeSource)
        val kahn = Kahn(testTimeSource)

        /*
		 * 1. Leerer Lkw wird mit Container 1 beladen.
		 *
		 * 2. Versuch Lkw mit zweitem Container zu beladen schl�gt fehl.
		 * 	  (max. Kapazit�t = 1)
		 */
        assertEquals(0, lkw.genutzteKapazitaet)
        assertEquals(1, lkw.freieKapazitaet)
        assertFalse(lkw.enthaelt(cont1.id))
        lkw.belade(cont1)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertTrue(lkw.enthaelt(cont1.id))
        lkw.belade(cont2)
        assertEquals(1, lkw.genutzteKapazitaet)
        assertEquals(0, lkw.freieKapazitaet)
        assertTrue(lkw.enthaelt(cont1.id))
        assertFalse(lkw.enthaelt(cont2.id))

        /*
		 * 1. Leerer Lastzug wird mit Container 2 beladen.
		 *
		 * 2. Lastzug wird zuxs�tzlich mit Container 3 beladen.
		 *
		 * 3. Versuch Lastzug mit Container 4 zu beladen schl�gt fehl.
		 * 	  (max. Kapazit�t = 2)
		 */
        assertEquals(0, lastzug.genutzteKapazitaet)
        assertEquals(2, lastzug.freieKapazitaet)
        assertFalse(lastzug.enthaelt(cont2.id))

        lastzug.belade(cont2)
        assertEquals(1, lastzug.genutzteKapazitaet)
        assertEquals(1, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont2.id))

        lastzug.belade(cont3)
        assertEquals(2, lastzug.genutzteKapazitaet)
        assertEquals(0, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont2.id))
        assertTrue(lastzug.enthaelt(cont3.id))

        lastzug.belade(cont4)
        assertEquals(2, lastzug.genutzteKapazitaet)
        assertEquals(0, lastzug.freieKapazitaet)
        assertTrue(lastzug.enthaelt(cont2.id))
        assertTrue(lastzug.enthaelt(cont3.id))
        assertFalse(lastzug.enthaelt(cont4.id))

        /*
		 * 1. Leerer Kahn wird mit Container 4 beladen.
		 *
		 * 2. Lastzug wird zuxs�tzlich mit Container 5+6 beladen.
		 *
		 * 3. Versuch Lastzug mit Container 7 zu beladen schl�gt fehl.
		 */
        assertEquals(0, kahn.genutzteKapazitaet)
        assertEquals(3, kahn.freieKapazitaet)

        kahn.belade(cont4)
        assertEquals(1, kahn.genutzteKapazitaet)
        assertEquals(2, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont4.id))

        kahn.belade(cont5)
        assertEquals(2, kahn.genutzteKapazitaet)
        assertEquals(1, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont4.id))
        assertTrue(kahn.enthaelt(cont5.id))

        kahn.belade(cont6)
        assertEquals(3, kahn.genutzteKapazitaet)
        assertEquals(0, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont4.id))
        assertTrue(kahn.enthaelt(cont5.id))
        assertTrue(kahn.enthaelt(cont6.id))

        kahn.belade(cont7)
        assertEquals(3, kahn.genutzteKapazitaet)
        assertEquals(0, kahn.freieKapazitaet)
        assertTrue(kahn.enthaelt(cont4.id))
        assertTrue(kahn.enthaelt(cont5.id))
        assertTrue(kahn.enthaelt(cont6.id))
        assertFalse(kahn.enthaelt(cont7.id))
    }


    @Test
    fun testContainerDoppeltEinliefern() = runTest{
        val terminal = Terminal(4, 4, testTimeSource)

        val t2 = Terminal(4, 4, testTimeSource)

        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "M�bel")
        val cont3 = Container(true, "Motorboot")

        val kahn = Kahn(testTimeSource)
        kahn.belade(cont3)
        kahn.belade(cont2)
        kahn.belade(cont1)

        /*
     * Kahn l�dt Container 3 im TerminalController ab
     */
        kahn.auftrag = (terminal.avisierung(10.seconds, intArrayOf(cont3.id), intArrayOf()))
        delay(10.seconds)
        terminal.abfertigung(kahn)

        /* Um Container 3 abzuladen, m�ssen beide anderen Container kurzfristig eingelagert werden. */
        assertEquals(1, terminal.genutzteKapazitaet)
        assertEquals(15, terminal.freieKapazitaet)
        assertEquals(5, terminal.anzahlBewegungen)
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
        /*
     * Lkw holt Container 3 ab
     */
        val lkw = LKW(testTimeSource)
        lkw.auftrag = terminal.avisierung(12.seconds, intArrayOf(), intArrayOf(cont3.id))
        delay(2.seconds)
        terminal.abfertigung(lkw)

        /*
     * Lkw l�dt Container 3 ab
     */
        lkw.auftrag = t2.avisierung(15.seconds, intArrayOf(cont3.id), intArrayOf())
        delay(3.seconds)
        t2.abfertigung(lkw)

        delay(10.seconds)

        /* Geb�hren T1 */

        assertEquals(fix + 1 * rate, terminal.getGebuehren(cont1.id))
        assertEquals(fix + 1 * rate, terminal.getGebuehren(cont2.id))
        assertEquals(fix + 13 * rate, terminal.getGebuehren(cont3.id))
        assertEquals(3 * fix + 15 * rate, terminal.gebuehren)

        /* Geb�hren T2 */

        assertEquals(0.0, t2.getGebuehren(cont1.id))
        assertEquals(0.0, t2.getGebuehren(cont2.id))
        assertEquals(fix + 11 * rate, t2.getGebuehren(cont3.id))
    }
}
