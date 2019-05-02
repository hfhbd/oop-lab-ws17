package fh.ws17.conterm.gui

import fh.ws17.conterm.StockControllerAbstractClass
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.stage.Stage

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener


class View<T : StockControllerAbstractClass>(stockView: StockController<T>) {

    private val scene: Scene
    private val title: String
    private val main: GridPane = GridPane()
    private var theGrid: GridPane? = null


    init {
        stockView.addPropertyChangeListener(PropertyChangeListener { this.updateGrid(it) })
        stockView.requestGrid()

        val button1 = Button(">")
        val button2 = Button("!")
        val button3 = Button("?")

        button1.onMouseClicked = EventHandler<MouseEvent> { stockView.biggerClicked(it) }
        button2.onMouseClicked = EventHandler<MouseEvent> { stockView.exclamationMarkClicked(it) }
        button3.onMouseClicked = EventHandler<MouseEvent> { stockView.questionMarkClicked(it) }

        main.add(button1, 0, 1)
        main.add(button2, 1, 1)
        main.add(button3, 2, 1)


        scene = Scene(main)
        title = stockView.title
    }

    private fun updateGrid(propertyChangeEvent: PropertyChangeEvent) {
        main.children.remove(theGrid)
        theGrid = propertyChangeEvent.newValue as GridPane
        main.add(theGrid, 0, 0, 3, 1)
    }

    fun start(stage: Stage) {
        stage.title = title
        stage.scene = scene
        stage.show()
    }

    override fun toString() = "View{scene=$scene, title='$title', main=$main, theGrid=$theGrid}"
}
