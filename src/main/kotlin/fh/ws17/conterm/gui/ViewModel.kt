package fh.ws17.conterm.gui

import androidx.compose.runtime.*
import fh.ws17.conterm.*
import fh.ws17.conterm.Stock
import fh.ws17.conterm.Stack as ContermStack

internal abstract class ViewModel(val title: String, private val stock: Stock) {

    abstract fun onContainerClicked(container: Container): String
    abstract fun onBiggerClicked(): String
    abstract fun onExclamationMarkClicked(): String
    abstract fun onQuestionMarkClicked(): String

    @Composable
    fun toState(): State<ContermStack<ContermStack<Container>>> {
        val state = remember { mutableStateOf(stock.stacks, neverEqualPolicy()) }
        DisposableEffect(this) {
            val id = stock.subscribe {
                state.value = it
            }
            onDispose {
                stock.dispose(id)
            }
        }
        return state
    }
}
