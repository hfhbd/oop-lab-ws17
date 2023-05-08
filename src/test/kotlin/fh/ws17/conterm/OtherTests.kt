package fh.ws17.conterm

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

class OtherTests {
    @Test
    fun capacityFull() = runTest {
        val terminal = Terminal(max, max, testTimeSource)
        for (i in 0 until max * max) {
            terminal.belade(Container(true, ""))
        }
        assertEquals(0, terminal.freieKapazitaet)
        assertEquals(max * max, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)
        assertFailsWith<CapacityExceededException> {
            for (i in 0 until max) {
                terminal.belade(Container(false, ""))
            }
        }
    }

    @Test
    fun testIDsNotInVehicle() = runTest {
        val terminal = Terminal(max, max, testTimeSource)

        assertEquals(max * max, terminal.freieKapazitaet)
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)

        val vehicle = Kahn(testTimeSource)
        val contTrue1 = Container(true, "Kahn1True")
        val contTrue2 = Container(true, "Kahn2True")
        val contFalse1 = Container(false, "Kahn3False")
        vehicle.belade(contTrue1)
        vehicle.belade(contTrue2)
        vehicle.belade(contFalse1)

        vehicle.auftrag = terminal.avisierung(10.seconds, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        delay(10.seconds)
        terminal.abfertigung(vehicle)

        vehicle.auftrag = terminal.avisierung(20.seconds, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        delay(10.seconds)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(vehicle)
        }
    }

    @Test
    fun vehicleFull() = runTest {
        val terminal = Terminal(max, max, testTimeSource)
        val c1 = Container(false, "")
        val c2 = Container(false, "")
        terminal.belade(c1)
        terminal.belade(c2)

        val vehicle = LKW(testTimeSource)
        vehicle.auftrag = terminal.avisierung(10.seconds, intArrayOf(), intArrayOf(c1.id))

        delay(10.seconds)
        assertTrue(terminal.abfertigung(vehicle))

        vehicle.auftrag = terminal.avisierung(20.seconds, intArrayOf(), intArrayOf(c1.id))
        delay(10.seconds)
        assertFailsWith<ContractFailureException> { terminal.abfertigung(vehicle) }
    }

    @Test
    fun testIDsNotInTerminal() = runTest {
        val terminal = Terminal(max, max, testTimeSource)
        val vehicle = LKW(testTimeSource)
        val container = Container(false, "")
        vehicle.belade(container)
        vehicle.auftrag = terminal.avisierung(10.seconds, intArrayOf(), intArrayOf(container.id))
        delay(10.seconds)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(vehicle)
        }
        assertEquals(0, terminal.anzahlBewegungen)
        assertFalse(terminal.enthaelt(container.id))
        assertTrue(vehicle.enthaelt(container.id))
        assertEquals(0.0, terminal.getGebuehren(container.id))
    }

    companion object {
        private const val max = 4
    }
}
