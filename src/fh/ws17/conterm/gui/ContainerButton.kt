package fh.ws17.conterm.gui

import fh.ws17.conterm.Container
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.effect.BoxBlur
import javafx.scene.effect.ColorAdjust
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent


internal class ContainerButton(val container: Container?, value: EventHandler<in MouseEvent>) : Button() {

    init {
        val image = Image(javaClass.getResourceAsStream("Container.jpg"))
        val imageView = ImageView(image)
        if (container == null) {
            imageView.effect = BoxBlur(5.0, 5.0, 3)
            this.isDisable = true
        } else if (container.istStabil()) {
            imageView.effect = ColorAdjust(-0.8, -0.8, 0.0, -0.8)
        }
        graphic = imageView
        onMouseClicked = value
    }

    override fun toString(): String {
        return "ContainerButton{" +
                "container=" + container +
                '}'.toString()
    }
}
