package fh.ws17.conterm.gui

import fh.ws17.conterm.Container
import fh.ws17.conterm.Terminal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class TerminalViewModel(
    private val terminal: Terminal,
    private val increaseTime: (Duration) -> Unit
) : ViewModel(terminal.title, terminal.stock) {
    override fun onContainerClicked(container: Container): String =
        """
           ID: ${container.id}
           Anlieferungszeitpunkt: ${container.incomeTime}
           Gebühren: ${terminal.getGebuehren(container.id)}
        """.trimIndent()

    override fun onBiggerClicked(): String {
        increaseTime(10.seconds)
        return "Zeit erhöht um: ${10.seconds}"
    }

    override fun onExclamationMarkClicked(): String {
        println(terminal.gebuehrenString)
        return "Gebühren in der Konsole ausgegeben"
    }

    override fun onQuestionMarkClicked(): String {
        return "Gebührensumme: ${terminal.gebuehren}"
    }
}
