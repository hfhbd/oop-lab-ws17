package fh.ws17.conterm

import kotlin.test.*


class OtherTests {

    @BeforeTest
    fun reset() {
        Uhr.reset()
    }

    @Test
    fun capacityFull() {
        val terminal = Terminal(max, max)
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
    fun testIDsNotInVehicle() {
        val terminal = Terminal(max, max)

        assertEquals(max * max, terminal.freieKapazitaet)
        assertEquals(0, terminal.genutzteKapazitaet)
        assertEquals(0, terminal.anzahlBewegungen)

        val vehicle = Kahn()
        val contTrue1 = Container(true, "Kahn1True")
        val contTrue2 = Container(true, "Kahn2True")
        val contFalse1 = Container(false, "Kahn3False")
        vehicle.belade(contTrue1)
        vehicle.belade(contTrue2)
        vehicle.belade(contFalse1)

        vehicle.auftrag = terminal.avisierung(10, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        Uhr.incZeit(10)
        terminal.abfertigung(vehicle)

        vehicle.auftrag = terminal.avisierung(20, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        Uhr.incZeit(10)
        assertFailsWith<ContractFailureException> {
            terminal.abfertigung(vehicle)
        }
    }

    @Test
    fun vehicleFull() {
        val terminal = Terminal(max, max)
        val c1 = Container(false, "")
        val c2 = Container(false, "")
        terminal.belade(c1)
        terminal.belade(c2)

        val vehicle = LKW()
        vehicle.auftrag = terminal.avisierung(10, intArrayOf(), intArrayOf(c1.id))

        Uhr.incZeit(10)
        assertTrue(terminal.abfertigung(vehicle))

        vehicle.auftrag = terminal.avisierung(20, intArrayOf(), intArrayOf(c1.id))
        Uhr.incZeit(10)
        assertFailsWith<ContractFailureException> { terminal.abfertigung(vehicle) }
    }

    @Test
    fun testIDsNotInTerminal() {
        val terminal = Terminal(max, max)
        val vehicle = LKW()
        val container = Container(false, "")
        vehicle.belade(container)
        vehicle.auftrag = terminal.avisierung(10, intArrayOf(), intArrayOf(container.id))
        Uhr.incZeit(10)
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
