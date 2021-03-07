package fh.ws17.conterm.gui

import fh.ws17.conterm.*

internal abstract class ViewModel(val title: String, val stock: Stock) {

    abstract fun onContainerClicked(container: Container): String
    abstract fun onBiggerClicked(): String
    abstract fun onExclamationMarkClicked(): String
    abstract fun onQuestionMarkClicked(): String
}

/*
fun main() = Window {
    val viewModel = ViewModel()
    Column {
        a(viewModel)
        Button(onClick = {
            viewModel.model = Model("World")
        }) {
            Text("Change me")
        }
    }
}

@Composable
fun a(vm: ViewModel) {
    Text(text = vm.model.value)
}
*/
fun main() {
    val terminal = Terminal(4, 4)

    val cont1 = Container(true, "Luftmatrazen")
    val cont2 = Container(true, "Standliegen")
    val cont3 = Container(false, "Jetskis")
    val cont4 = Container(false, "Strandbar")
    val cont5 = Container(false, "Strandsportgerät")
    val cont6 = Container(true, "Steg")

    val lkw1 = LKW()
    lkw1.belade(cont3)
    lkw1.auftrag = terminal.avisierung(10, intArrayOf(cont3.id))

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
    View(TerminalViewModel(terminal))
//Doppelte View zum Sync testen
    View(TerminalViewModel(terminal))

    View(TransporterViewModel(lkw1))
    View(TransporterViewModel(lkw2))
    View(TransporterViewModel(kahn))
}
