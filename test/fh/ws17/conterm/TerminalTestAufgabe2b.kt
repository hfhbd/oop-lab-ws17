package fh.ws17.conterm

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TerminalTestAufgabe2b {

    @Before
    @Throws(Exception::class)
    fun setUp() {
        /* vor jedem Test die Uhr zur�cksetzen */
        Uhr.reset()
    }

    @Test
    fun testKapazitaet() {

        /* Anlegen von Containern */
        val cont1 = Container(true, "Luftmatrazen")
        val cont2 = Container(true, "Strandbar")
        val cont3 = Container(true, "Jetski")
        val cont4 = Container(true, "Palme")
        val cont5 = Container(true, "Sonnenschirm")
        val cont6 = Container(true, "Handt�cher")
        val cont7 = Container(true, "Wasser")

        val lkw = LKW()
        val lastzug = Lastzug()
        val kahn = Kahn()

        /*
		 * 1. Leerer Lkw wird mit Container 1 beladen.
		 *
		 * 2. Versuch Lkw mit zweitem Container zu beladen schl�gt fehl.
		 * 	  (max. Kapazit�t = 1)
		 */
        Assert.assertEquals(0, lkw.genutzteKapazitaet)
        Assert.assertEquals(1, lkw.freieKapazitaet)
        Assert.assertFalse(lkw.enthaelt(cont1.id))
        lkw.belade(cont1)
        Assert.assertEquals(1, lkw.genutzteKapazitaet)
        Assert.assertEquals(0, lkw.freieKapazitaet)
        Assert.assertTrue(lkw.enthaelt(cont1.id))
        lkw.belade(cont2)
        Assert.assertEquals(1, lkw.genutzteKapazitaet)
        Assert.assertEquals(0, lkw.freieKapazitaet)
        Assert.assertTrue(lkw.enthaelt(cont1.id))
        Assert.assertFalse(lkw.enthaelt(cont2.id))

        /*
		 * 1. Leerer Lastzug wird mit Container 2 beladen.
		 *
		 * 2. Lastzug wird zuxs�tzlich mit Container 3 beladen.
		 *
		 * 3. Versuch Lastzug mit Container 4 zu beladen schl�gt fehl.
		 * 	  (max. Kapazit�t = 2)
		 */
        Assert.assertEquals(0, lastzug.genutzteKapazitaet)
        Assert.assertEquals(2, lastzug.freieKapazitaet)
        Assert.assertFalse(lastzug.enthaelt(cont2.id))

        lastzug.belade(cont2)
        Assert.assertEquals(1, lastzug.genutzteKapazitaet)
        Assert.assertEquals(1, lastzug.freieKapazitaet)
        Assert.assertTrue(lastzug.enthaelt(cont2.id))

        lastzug.belade(cont3)
        Assert.assertEquals(2, lastzug.genutzteKapazitaet)
        Assert.assertEquals(0, lastzug.freieKapazitaet)
        Assert.assertTrue(lastzug.enthaelt(cont2.id))
        Assert.assertTrue(lastzug.enthaelt(cont3.id))

        lastzug.belade(cont4)
        Assert.assertEquals(2, lastzug.genutzteKapazitaet)
        Assert.assertEquals(0, lastzug.freieKapazitaet)
        Assert.assertTrue(lastzug.enthaelt(cont2.id))
        Assert.assertTrue(lastzug.enthaelt(cont3.id))
        Assert.assertFalse(lastzug.enthaelt(cont4.id))

        /*
		 * 1. Leerer Kahn wird mit Container 4 beladen.
		 *
		 * 2. Lastzug wird zuxs�tzlich mit Container 5+6 beladen.
		 *
		 * 3. Versuch Lastzug mit Container 7 zu beladen schl�gt fehl.
		 */
        Assert.assertEquals(0, kahn.genutzteKapazitaet)
        Assert.assertEquals(3, kahn.freieKapazitaet)

        kahn.belade(cont4)
        Assert.assertEquals(1, kahn.genutzteKapazitaet)
        Assert.assertEquals(2, kahn.freieKapazitaet)
        Assert.assertTrue(kahn.enthaelt(cont4.id))

        kahn.belade(cont5)
        Assert.assertEquals(2, kahn.genutzteKapazitaet)
        Assert.assertEquals(1, kahn.freieKapazitaet)
        Assert.assertTrue(kahn.enthaelt(cont4.id))
        Assert.assertTrue(kahn.enthaelt(cont5.id))

        kahn.belade(cont6)
        Assert.assertEquals(3, kahn.genutzteKapazitaet)
        Assert.assertEquals(0, kahn.freieKapazitaet)
        Assert.assertTrue(kahn.enthaelt(cont4.id))
        Assert.assertTrue(kahn.enthaelt(cont5.id))
        Assert.assertTrue(kahn.enthaelt(cont6.id))

        kahn.belade(cont7)
        Assert.assertEquals(3, kahn.genutzteKapazitaet)
        Assert.assertEquals(0, kahn.freieKapazitaet)
        Assert.assertTrue(kahn.enthaelt(cont4.id))
        Assert.assertTrue(kahn.enthaelt(cont5.id))
        Assert.assertTrue(kahn.enthaelt(cont6.id))
        Assert.assertFalse(kahn.enthaelt(cont7.id))
    }


    @Test
    fun testContainerDoppeltEinliefern() {

        try {

            val terminal = Terminal(4, 4)

            val t2 = Terminal(4, 4)

            val cont1 = Container(false, "Luftmatrazen")
            val cont2 = Container(true, "M�bel")
            val cont3 = Container(true, "Motorboot")

            val kahn = Kahn()
            kahn.belade(cont3)
            kahn.belade(cont2)
            kahn.belade(cont1)

            /*
		 * Kahn l�dt Container 3 im TerminalController ab
		 */
            kahn.auftrag = (terminal.avisierung(10, intArrayOf(cont3.id), intArrayOf()))
            Uhr.incZeit(10)
            terminal.abfertigung(kahn)

            /* Um Container 3 abzuladen, m�ssen beide anderen Container kurzfristig eingelagert werden. */
            Assert.assertEquals(1, terminal.genutzteKapazitaet)
            Assert.assertEquals(15, terminal.freieKapazitaet)
            Assert.assertEquals(5, terminal.anzahlBewegungen)
            Assert.assertFalse(terminal.enthaelt(cont1.id))
            Assert.assertFalse(terminal.enthaelt(cont2.id))
            Assert.assertTrue(terminal.enthaelt(cont3.id))

            /* Am Ende aber nur ein Container im TerminalController, 2 auf dem Kahn */
            Assert.assertEquals(2, kahn.genutzteKapazitaet)
            Assert.assertEquals(1, kahn.freieKapazitaet)
            Assert.assertTrue(kahn.enthaelt(cont1.id))
            Assert.assertTrue(kahn.enthaelt(cont2.id))
            Assert.assertFalse(kahn.enthaelt(cont3.id))

            /* Geb�hren */
            val fix = 20.0
            val rate = 1.5
            Uhr.incZeit(10)
            Assert.assertEquals(fix + 1 * rate, terminal.getGebuehren(cont1.id), 0.001)
            Assert.assertEquals(fix + 1 * rate, terminal.getGebuehren(cont2.id), 0.001)
            Assert.assertEquals(fix + 11 * rate, terminal.getGebuehren(cont3.id), 0.001)
            Assert.assertEquals(3 * fix + 13 * rate, terminal.gebuehren, 0.001)
            /*
		 * Lkw holt Container 3 ab
		 */
            val lkw = LKW()
            lkw.auftrag = (terminal.avisierung(12, intArrayOf(), intArrayOf(cont3.id)))
            Uhr.incZeit(2)
            terminal.abfertigung(lkw)

            /*
		 * Lkw l�dt Container 3 ab
		 */
            lkw.auftrag = (t2.avisierung(15, intArrayOf(cont3.id), intArrayOf()))
            Uhr.incZeit(3)
            t2.abfertigung(lkw)

            Uhr.incZeit(10)

            /* Geb�hren T1 */

            Assert.assertEquals(fix + 1 * rate, terminal.getGebuehren(cont1.id), 0.001)
            Assert.assertEquals(fix + 1 * rate, terminal.getGebuehren(cont2.id), 0.001)
            Assert.assertEquals(fix + 13 * rate, terminal.getGebuehren(cont3.id), 0.001)
            Assert.assertEquals(3 * fix + 15 * rate, terminal.gebuehren, 0.001)

            /* Geb�hren T2 */

            Assert.assertEquals(0.0, t2.getGebuehren(cont1.id), 0.001)
            Assert.assertEquals(0.0, t2.getGebuehren(cont2.id), 0.001)
            Assert.assertEquals(fix + 11 * rate, t2.getGebuehren(cont3.id), 0.001)
            Assert.assertEquals(fix + 11 * rate, t2.gebuehren, 0.001)

        } catch (e: Exception) {
            Assert.fail()
        }

    }


}
