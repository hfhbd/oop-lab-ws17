package fh.ws17.conterm.gui

import androidx.compose.desktop.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import fh.ws17.conterm.*
import fh.ws17.conterm.Stack as ContermStack

@Composable
private fun ContainerButton(container: Container, onClick: (Container) -> Unit) =
    Button(onClick = { onClick(container) }) {
        val colorFilter = if (container.isStable) {
            ColorFilter.tint(Color.Blue, BlendMode.Hue)
        } else null
        Image(imageResource("Container.jpg"), null, colorFilter = colorFilter)
    }

@Composable
private fun stockView(stock: ContermStack<ContermStack<Container>>, onContainerClicked: (Container) -> Unit) {
    stock.forEach {
        Column {
            it.forEach { container ->
                ContainerButton(container, onContainerClicked)
            }
        }
    }
}

@Composable
internal fun Stock.toState(): State<ContermStack<ContermStack<Container>>> {
    val state = remember { mutableStateOf(stacks, neverEqualPolicy()) }
    DisposableEffect(this) {
        val id = subscribe {
            state.value = it
        }
        onDispose {
            dispose(id)
        }
    }
    return state
}

internal fun View(viewModel: ViewModel) = Window(title = viewModel.title, size = IntSize(400, 400)) {
    val stock by viewModel.stock.toState()
    var showAlert by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    Column {
        Row {
            stockView(stock) {
                text = viewModel.onContainerClicked(it)
                showAlert = true
            }
        }
        Row {
            Button(onClick = {
                text = viewModel.onBiggerClicked()
                showAlert = true
            }) {
                Text(">")
            }
            Button(onClick = {
                text = viewModel.onExclamationMarkClicked()
                showAlert = true
            }) {
                Text("!")
            }
            Button(onClick = {
                text = viewModel.onQuestionMarkClicked()
                showAlert = true
            }) {
                Text("?")
            }
        }
    }
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Information") },
            text = { Text(text) },
            confirmButton = {
                Button(onClick = {
                    showAlert = false
                }) {
                    Text("A button")
                }
            }
        )
    }
}
