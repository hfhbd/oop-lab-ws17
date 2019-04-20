package fh.ws17.conterm

import fh.ws17.conterm.gui.TerminalController
import fh.ws17.conterm.gui.TransporterController
import fh.ws17.conterm.gui.View
import javafx.application.Application
import javafx.stage.Stage

class TerminalStartAufgabe4 : Application() {

    @Throws(Exception::class)
    override fun start(arg0: Stage) {

        val terminal = fh.ws17.conterm.Terminal(4, 4)

        val cont1 = Container(true, "Luftmatrazen")
        val cont2 = Container(true, "Standliegen")
        val cont3 = Container(false, "Jetskis")
        val cont4 = Container(false, "Strandbar")
        val cont5 = Container(false, "Strandsportger�t")
        val cont6 = Container(true, "Steg")

        val lkw1 = LKW()
        lkw1.belade(cont3)
        lkw1.auftrag = terminal.avisierung(10, intArrayOf(cont3.id), intArrayOf())

        val lkw2 = Lastzug()
        lkw2.belade(cont5)
        lkw2.belade(cont6)
        lkw2.auftrag = terminal.avisierung(20, intArrayOf(cont5.id, cont6.id), intArrayOf(cont3.id))

        val kahn = Kahn()
        kahn.belade(cont1)
        kahn.belade(cont2)
        kahn.belade(cont4)
        kahn.auftrag = terminal.avisierung(30, intArrayOf(cont1.id, cont2.id, cont4.id), intArrayOf())

        // Views einrichten

        var v1 = View(terminal, TerminalController(terminal))
        v1.start(Stage())

        //Doppelte View zum Sync testen
        v1 = View(terminal, TerminalController(terminal))
        v1.start(Stage())

        val v2 = View(lkw1, TransporterController(lkw1))
        v2.start(Stage())

        val v3 = View(lkw2, TransporterController(lkw2))
        v3.start(Stage())

        val v4 = View(kahn, TransporterController(kahn))
        v4.start(Stage())

    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(*args)
        }
    }

}
