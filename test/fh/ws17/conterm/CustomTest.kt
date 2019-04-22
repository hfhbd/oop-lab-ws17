package fh.ws17.conterm

import org.junit.Assert
import org.junit.Test


class CustomTest {


    @Test
    @Throws(CapacityExceededException::class, ContractFailureException::class)
    fun testEinliefern() {


        try {
            val cont1 = Container(false, "Luftmatrazen")
            val cont2 = Container(true, "M�bel")
            val cont3 = Container(true, "Motorboot")
            val cont4 = Container(true, "Schlauchboot")
            val terminal = Terminal(3, 1)


            terminal.freieKapazitaet

            val kahn = Kahn()

            kahn.belade(cont3)
            kahn.belade(cont2)
            kahn.belade(cont1)
            kahn.belade(cont4)


            Assert.assertEquals(3, kahn.genutzteKapazitaet)

            kahn.auftrag = terminal.avisierung(10, intArrayOf(cont3.id, cont2.id, cont1.id, cont4.id), intArrayOf())
            Uhr.incZeit(10)
            terminal.abfertigung(kahn)

            Assert.assertEquals(0, kahn.genutzteKapazitaet)

            Assert.assertEquals(0, terminal.freieKapazitaet)

        } catch (e: CapacityExceededException) {

            e.printStackTrace()
            //Assert.fail();
        }

    }

    @Test
    @Throws(ContractFailureException::class)
    fun WrongIDInVehicle() {
        val cont1 = Container(false, "Luftmatrazen")

        val cont3 = Container(true, "Motorboot")

        val terminal = Terminal(4, 1)


        val lkw = LKW()

        lkw.belade(cont1)
        lkw.auftrag = terminal.avisierung(10, intArrayOf(cont3.id), intArrayOf())
        Uhr.incZeit(10)
        terminal.abfertigung(lkw)
    }


    @Test
    @Throws(ContractFailureException::class)
    fun ContainerNotInTerminal() {

        try {
            val cont1 = Container(false, "Luftmatrazen")
            val cont3 = Container(true, "Motorboot")
            val terminal = Terminal(4, 1)


            val lkw = LKW()

            lkw.belade(cont1)

            lkw.auftrag = terminal.avisierung(10, intArrayOf(), intArrayOf(cont3.id)) //cont1.getID()
            Uhr.incZeit(10)
            terminal.abfertigung(lkw)
        } catch (e: ContractFailureException) {

            e.printStackTrace()
            //Assert.fail();
        }

    }

}
