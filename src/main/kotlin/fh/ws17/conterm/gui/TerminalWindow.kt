package fh.ws17.conterm.gui

import fh.ws17.conterm.*

internal class TerminalViewModel(private val terminal: Terminal) : ViewModel(terminal.title, terminal.stock) {
    override fun onContainerClicked(container: Container): String =
        """
           ID: ${container.id}
           Anlieferungszeitpunkt: ${container.incomeTime}
           Gebühren: ${terminal.getGebuehren(container.id)}
        """.trimIndent()

    override fun onBiggerClicked(): String {
        Uhr.incZeit(10)
        return "Zeit erhöht um: 10"
    }

    override fun onExclamationMarkClicked(): String {
        println(terminal.gebuehrenString)
        return "Gebühren in der Konsole ausgegeben"
    }

    override fun onQuestionMarkClicked(): String {
        return "Gebührensumme: ${terminal.gebuehren}"
    }
}
