package fh.ws17.conterm.gui

import fh.ws17.conterm.StockControllerAbstractClass
import fh.ws17.conterm.Uhr
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport


abstract class StockController<Model : StockControllerAbstractClass>(val model: Model) : PropertyChangeListener {
    private val propertyChangeSupport = PropertyChangeSupport(this)

    private val grid: GridPane
        get() {
            val gridpane = GridPane()
            val stock = model.stock.stacks

            for (column in 0 until stock.size) {
                for (row in 0 until stock.elementAt(column).size) {
                    val container = stock.elementAtOrNull(column)?.elementAtOrNull(row)
                    gridpane.add(ContainerButton(container, EventHandler { this.containerClicked(it) }), column, row)
                }
            }
            return gridpane
        }

    val title: String
        get() = this.model.javaClass.simpleName

    init {
        this.model.addPropertyChangeListener(this)
    }

    override fun propertyChange(propertyChangeEvent: PropertyChangeEvent) {
        propertyChangeSupport.firePropertyChange(GRIDCHANGED, null, grid)
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener)
    }

    fun showAlert(text: String) {
        Alert(Alert.AlertType.INFORMATION, text).show()
    }

    internal abstract fun containerClicked(event: MouseEvent)

    fun requestGrid() {
        propertyChangeSupport.firePropertyChange(GRIDCHANGED, null, grid)
    }

    internal abstract fun biggerClicked(event: MouseEvent)

    internal abstract fun exclamationMarkClicked(event: MouseEvent)

    internal abstract fun questionMarkClicked(event: MouseEvent)

    override fun toString() = "StockController{model=$model, propertyChangeSupport=$propertyChangeSupport}"

    fun incTime(i: Int) {
        Uhr.incZeit(i)
        this.showAlert("Zeit erhöht um: $i")
    }

    companion object {
        private const val GRIDCHANGED = "gridChanged"
    }
}
