package fh.ws17.conterm.gui

import androidx.compose.ui.window.application
import fh.ws17.conterm.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TestTimeSource

fun main() = application {
    val timeSource = TestTimeSource()

    val terminal = Terminal(4, 4, timeSource)

    val cont1 = Container(true, "Luftmatrazen")
    val cont2 = Container(true, "Standliegen")
    val cont3 = Container(false, "Jetskis")
    val cont4 = Container(false, "Strandbar")
    val cont5 = Container(false, "Strandsportgerät")
    val cont6 = Container(true, "Steg")

    val lkw1 = LKW(timeSource)
    lkw1.belade(cont3)
    lkw1.auftrag = terminal.avisierung(10.seconds, intArrayOf(cont3.id))

    val lkw2 = Lastzug(timeSource)
    lkw2.belade(cont5)
    lkw2.belade(cont6)
    lkw2.auftrag = terminal.avisierung(20.seconds, intArrayOf(cont5.id, cont6.id), intArrayOf(cont3.id))

    val kahn = Kahn(timeSource)
    kahn.belade(cont1)
    kahn.belade(cont2)
    kahn.belade(cont4)
    kahn.auftrag = terminal.avisierung(30.seconds, intArrayOf(cont1.id, cont2.id, cont4.id), intArrayOf())

// Views einrichten
    View(TerminalViewModel(terminal) {
        timeSource += it
    })
//Doppelte View zum Sync testen
    View(TerminalViewModel(terminal) {
        timeSource += it
    })

    View(TransporterViewModel(lkw1) {
        timeSource += it
    })
    View(TransporterViewModel(lkw2) {
        timeSource += it
    })
    View(TransporterViewModel(kahn) {
        timeSource += it
    })
}
