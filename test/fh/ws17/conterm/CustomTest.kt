package fh.ws17.conterm

import org.junit.Assert
import org.junit.Test


class CustomTest {


    @Test
    fun testEinliefern() {
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")
        val cont3 = Container(true, "Motorboot")
        val cont4 = Container(true, "Schlauchboot")
        val terminal = Terminal(3, 1)

        terminal.freieKapazitaet

        val kahn = Kahn()

        Assert.assertTrue(kahn.belade(cont3))
        Assert.assertTrue(kahn.belade(cont2))
        Assert.assertTrue(kahn.belade(cont1))
        Assert.assertFalse(kahn.belade(cont4))

        Assert.assertEquals(3, kahn.genutzteKapazitaet)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont3.id, cont2.id, cont1.id))
        Uhr.incZeit(10)
        terminal.abfertigung(kahn)

        Assert.assertEquals(0, kahn.genutzteKapazitaet)
        Assert.assertEquals(0, terminal.freieKapazitaet)
    }

    @Test(expected = ContractFailureException::class)
    fun wrongIDInVehicle() {
        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")

        val terminal = Terminal(4, 1)

        val lkw = LKW()

        lkw.belade(cont1)
        lkw.auftrag = terminal.avisierung(10, intArrayOf(cont3.id))
        Uhr.incZeit(10)
        terminal.abfertigung(lkw)
    }


    @Test(expected = ContractFailureException::class)
    fun containerNotInTerminal() {

        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")
        val terminal = Terminal(4, 1)

        val lkw = LKW()

        lkw.belade(cont1)

        lkw.auftrag = terminal.avisierung(10, containerOutbound = intArrayOf(cont3.id))
        Uhr.incZeit(10)
        terminal.abfertigung(lkw)
    }
}
