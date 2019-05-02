package fh.ws17.conterm.gui

import fh.ws17.conterm.Terminal
import javafx.scene.input.MouseEvent

class TerminalController(terminal: Terminal) : StockController<Terminal>(terminal) {

    override fun containerClicked(event: MouseEvent) {
        val containerButton = event.source as ContainerButton
        val container = containerButton.container!!
        val text = """ID: ${container.id}
                    Anlieferungszeitpunkt: ${container.incomeTime}
                    Gebühren: ${this.model.getGebuehren(container.id)}"""
        this.showAlert(text)

    }

    override fun biggerClicked(event: MouseEvent) = incTime(10)

    override fun exclamationMarkClicked(event: MouseEvent) {
        println(this.model.gebuehrenString)
        this.showAlert("Gebühren in der Konsole ausgegeben")
    }

    override fun questionMarkClicked(event: MouseEvent) = this.showAlert("Gebührensumme: ${this.model.gebuehren}")
}
