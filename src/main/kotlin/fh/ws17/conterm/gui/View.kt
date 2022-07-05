package fh.ws17.conterm.gui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import fh.ws17.conterm.*
import fh.ws17.conterm.Stack as ContermStack

@Composable
private fun ContainerButton(
    container: Container,
    onClick: (Container) -> Unit,
    modifier: Modifier,
    image: Painter = painterResource("Container.jpg")
) =
    Button(onClick = { onClick(container) }, modifier = modifier) {
        val colorFilter = if (container.isStable) {
            ColorFilter.tint(Color.Blue, BlendMode.Hue)
        } else null
        Image(image, null, colorFilter = colorFilter)
    }

@Composable
private fun stockView(stock: ContermStack<ContermStack<Container>>, onContainerClicked: (Container) -> Unit) {
    val containerSize = Modifier.size(64.dp)
    stock.forEach {
        Column {
            val freeSpace = it.size - it.capacity
            repeat(freeSpace) {
                Spacer(containerSize)
            }
            it.forEach { container ->
                ContainerButton(container, onContainerClicked, containerSize)
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun View(viewModel: ViewModel) {
    var show by remember { mutableStateOf(true) }
    if (show) {
        Window(
            onKeyEvent = {
                if (it.key == Key.W && it.isMetaPressed && it.type == KeyEventType.KeyUp) {
                    show = false
                    false
                } else true
            },
            onCloseRequest = { show = false }, title = viewModel.title, resizable = true,
            state = WindowState(size = DpSize(400.dp, 300.dp))
        ) {
            val stock by viewModel.toState()
            var text: String? by remember { mutableStateOf(null) }
            Column {
                Row {
                    stockView(stock) {
                        text = viewModel.onContainerClicked(it)
                    }
                }
                Row {
                    Button(onClick = {
                        text = viewModel.onBiggerClicked()
                    }) {
                        Text(">")
                    }
                    Button(onClick = {
                        text = viewModel.onExclamationMarkClicked()
                    }) {
                        Text("!")
                    }
                    Button(onClick = {
                        text = viewModel.onQuestionMarkClicked()
                    }) {
                        Text("?")
                    }
                }
            }
            text?.let {
                Dialog(onCloseRequest = {
                    text = null
                }, title = "Information") {
                    Column {
                        Text(it)
                        Button(onClick = {
                            text = null
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
