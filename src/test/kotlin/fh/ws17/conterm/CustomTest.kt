package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class CustomTest {

    @Test
    fun testEinliefern() = runTest {
        val cont1 = Container(false, "Luftmatrazen")
        val cont2 = Container(true, "Möbel")
        val cont3 = Container(true, "Motorboot")
        val cont4 = Container(true, "Schlauchboot")
        val terminal = Terminal(3, 1, testTimeSource)

        val kahn = Kahn(testTimeSource)
        assertTrue(kahn.belade(cont3))
        assertTrue(kahn.belade(cont2))
        assertTrue(kahn.belade(cont1))
        assertFalse(kahn.belade(cont4))

        assertEquals(3, kahn.genutzteKapazitaet)

        kahn.auftrag =
            terminal.avisierung(10.seconds, intArrayOf(cont3.id, cont2.id, cont1.id))
        delay(10.seconds)
        terminal.abfertigung(kahn)

        assertEquals(0, kahn.genutzteKapazitaet)
        assertEquals(0, terminal.freieKapazitaet)
    }

    @Test
    fun wrongIDInVehicle() = runTest {
        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")

        val terminal = Terminal(4, 1, testTimeSource)

        val lkw = LKW(testTimeSource)

        lkw.belade(cont1)
        lkw.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont3.id))
        delay(10.seconds)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(lkw)
        }
    }


    @Test
    fun containerNotInTerminal() = runTest {
        val cont1 = Container(false, "Luftmatrazen")
        val cont3 = Container(true, "Motorboot")
        val terminal = Terminal(4, 1, testTimeSource)

        val lkw = LKW(testTimeSource)

        lkw.belade(cont1)

        lkw.auftrag =
            terminal.avisierung(10.seconds, containerOutbound = intArrayOf(cont3.id))
        delay(10.seconds)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(lkw)
        }
    }
}
