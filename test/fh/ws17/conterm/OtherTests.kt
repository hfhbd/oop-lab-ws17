package fh.ws17.conterm

import org.junit.Assert
import org.junit.Before
import org.junit.Test


class OtherTests {

    @Before
    fun reset() {
        Uhr.reset()
    }

    @Test
    fun capacityFull() {
        val terminal = Terminal(max, max)
        for (i in 0 until max * max) {
            terminal.belade(Container(true, ""))
        }
        Assert.assertEquals(0, terminal.freieKapazitaet)
        Assert.assertEquals(max * max, terminal.genutzteKapazitaet)
        Assert.assertEquals(0, terminal.anzahlBewegungen)
        var exception = false
        try {
            for (i in 0 until max) {
                terminal.belade(Container(false, ""))
            }
        } catch (e: CapacityExceededException) {
            exception = true
        }

        if (!exception) {
            Assert.fail()
        }
    }

    @Test
    fun testIDsNotInVehicle() {
        val terminal = Terminal(max, max)

        Assert.assertEquals(max * max, terminal.freieKapazitaet)
        Assert.assertEquals(0, terminal.genutzteKapazitaet)
        Assert.assertEquals(0, terminal.anzahlBewegungen)

        val vehicle = Kahn()
        val contTrue1 = Container(true, "Kahn1True")
        val contTrue2 = Container(true, "Kahn2True")
        val contFalse1 = Container(false, "Kahn3False")
        vehicle.belade(contTrue1)
        vehicle.belade(contTrue2)
        vehicle.belade(contFalse1)

        vehicle.auftrag = terminal.avisierung(10, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        Uhr.incZeit(10)
        try {
            terminal.abfertigung(vehicle)
        } catch (e: ContractFailureException) {
            e.printStackTrace()
            Assert.fail()
        }

        vehicle.auftrag = terminal.avisierung(20, intArrayOf(contFalse1.id, contTrue1.id), intArrayOf())
        Uhr.incZeit(10)
        var exception = false
        try {
            terminal.abfertigung(vehicle)
        } catch (e: ContractFailureException) {
            exception = true
        }

        if (!exception) {
            Assert.fail()
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
        try {
            Assert.assertTrue(terminal.abfertigung(vehicle))
        } catch (e: ContractFailureException) {
            e.printStackTrace()
            Assert.fail()
        }

        vehicle.auftrag = terminal.avisierung(20, intArrayOf(), intArrayOf(c1.id))
        Uhr.incZeit(10)
        var exception = false
        try {
            terminal.abfertigung(vehicle)
        } catch (e: ContractFailureException) {
            exception = true
        }

        if (!exception) {
            Assert.fail()
        }
    }

    @Test
    fun testIDsNotInTerminal() {
        val terminal = Terminal(max, max)
        val vehicle = LKW()
        val container = Container(false, "")
        vehicle.belade(container)
        vehicle.auftrag = terminal.avisierung(10, intArrayOf(), intArrayOf(container.id))
        Uhr.incZeit(10)
        var exception = false
        try {
            terminal.abfertigung(vehicle)
        } catch (e: ContractFailureException) {
            exception = true
        }

        if (!exception) {
            Assert.fail()
        }
        Assert.assertEquals(0, terminal.anzahlBewegungen)
        Assert.assertFalse(terminal.enthaelt(container.id))
        Assert.assertTrue(vehicle.enthaelt(container.id))
        Assert.assertEquals(0.0, terminal.getGebuehren(container.id), 0.001)
    }

    companion object {
        private const val max = 4
    }
}
