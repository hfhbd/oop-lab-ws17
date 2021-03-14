package fh.ws17.conterm.gui

import fh.ws17.conterm.*

internal class TransporterViewModel(private val vehicle: Vehicle) : ViewModel(vehicle.title, vehicle.stock) {
    override fun onContainerClicked(container: Container): String {
        val containerID = container.id
        val order = vehicle.auftrag
        return """
            Container ID: $containerID
            Auslieferungszeit: ${order?.outcomingTime}
            Zielterminal: ${order?.terminal}
        """.trimIndent()
    }

    override fun onBiggerClicked(): String {
        Uhr.incZeit(1)
        return "Zeit erhöht um: 1"
    }

    override fun onExclamationMarkClicked(): String = try {
        if (vehicle.abfertigung()) {
            "Abfertigung erfolgreich"
        } else {
            "Abfertigung gescheitert"
        }
    } catch (e: ContractFailureException) {
        "Abfertigung gescheitert, nicht alle Container gefunden"
    } catch (e: CapacityExceededException) {
        "Abfertigung gescheitert, Terminal oder Fahrzeug voll"
    }

    override fun onQuestionMarkClicked(): String {
        val order = vehicle.auftrag
        return """
            Geplante Ankunftszeit: ${order?.scheduledTime}
            Eingehende Container: ${order?.containerInbound}
            Ausgehende Container: ${order?.containerOutbound}
        """.trimIndent()
    }
}
