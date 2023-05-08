package fh.ws17.conterm.gui

import fh.ws17.conterm.CapacityExceededException
import fh.ws17.conterm.Container
import fh.ws17.conterm.ContractFailureException
import fh.ws17.conterm.Vehicle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class TransporterViewModel(
    private val vehicle: Vehicle,
    private val increaseTime: (Duration) -> Unit,
) : ViewModel(vehicle.title, vehicle.stock) {
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
        increaseTime(1.seconds)
        return "Zeit erhöht um: ${1.seconds}"
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
