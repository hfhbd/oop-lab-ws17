package fh.ws17.conterm.gui

import fh.ws17.conterm.CapacityExceededException
import fh.ws17.conterm.ContractFailureException
import fh.ws17.conterm.Vehicle
import javafx.scene.input.MouseEvent
import java.util.*

class TransporterController(vehicle: Vehicle) : StockController<Vehicle>(vehicle) {

    override fun containerClicked(event: MouseEvent) {
        val containerButton = event.source as ContainerButton
        val containerID = containerButton.container!!.id
        val order = this.model.auftrag
        val text = """Container ID: $containerID
Auslieferungszeit: ${order?.outcomingTime}
Zielterminal: ${order?.terminal}"""
        this.showAlert(text)

    }

    override fun biggerClicked(event: MouseEvent) = this.incTime(1)

    override fun exclamationMarkClicked(event: MouseEvent) = try {
        if (model.abfertigung()) {
            showAlert("Abfertigung erfolgreich")
        } else {
            showAlert("Abfertigung gescheitert")
        }
    } catch (e: ContractFailureException) {
        this.showAlert("Abfertigung gescheitert, nicht alle Container gefunden")
    } catch (e: CapacityExceededException) {
        this.showAlert("Abfertigung gescheitert, Terminal oder Fahrzeug voll")
    }

    override fun questionMarkClicked(event: MouseEvent) {
        val order = model.auftrag
        val text = """Geplante Ankunftszeit: ${order?.scheduledTime}
Eingehende Container: ${Arrays.toString(order?.containerInbound)}
Ausgehende Container: ${Arrays.toString(order?.containerOutbound)}"""
        showAlert(text)
    }
}
