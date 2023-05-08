package fh.ws17.conterm.gui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import fh.ws17.conterm.Container
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
    for (it in stock) {
        Column {
            val freeSpace = it.size - it.capacity
            repeat(freeSpace) {
                Spacer(containerSize)
            }
            for (container in it) {
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
            val coroutineScope = rememberCoroutineScope()
            val stock by remember { viewModel.asFlow(coroutineScope) }.collectAsState()
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
