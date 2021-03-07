package fh.ws17.conterm

import kotlin.test.*

class CustomTest {

    @Test
    fun testEinliefern() {
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")
        val cont3 = Container(true, "Motorboot")
        val cont4 = Container(true, "Schlauchboot")
        val terminal = Terminal(3, 1)

        val kahn = Kahn()
        assertTrue(kahn.belade(cont3))
        assertTrue(kahn.belade(cont2))
        assertTrue(kahn.belade(cont1))
        assertFalse(kahn.belade(cont4))

        assertEquals(3, kahn.genutzteKapazitaet)

        kahn.auftrag = terminal.avisierung(10, intArrayOf(cont3.id, cont2.id, cont1.id))
        Uhr.incZeit(10)
        terminal.abfertigung(kahn)

        assertEquals(0, kahn.genutzteKapazitaet)
        assertEquals(0, terminal.freieKapazitaet)
    }

    @Test
    fun wrongIDInVehicle() {
        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")

        val terminal = Terminal(4, 1)

        val lkw = LKW()

        lkw.belade(cont1)
        lkw.auftrag = terminal.avisierung(10, intArrayOf(cont3.id))
        Uhr.incZeit(10)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(lkw)
        }
    }


    @Test
    fun containerNotInTerminal() {
        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")
        val terminal = Terminal(4, 1)

        val lkw = LKW()

        lkw.belade(cont1)

        lkw.auftrag = terminal.avisierung(10, containerOutbound = intArrayOf(cont3.id))
        Uhr.incZeit(10)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(lkw)
        }
    }
}
