package fh.ws17.conterm.gui

import fh.ws17.conterm.Container
import fh.ws17.conterm.Stock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import fh.ws17.conterm.Stack as ContermStack

internal abstract class ViewModel(val title: String, private val stock: Stock) {

    abstract fun onContainerClicked(container: Container): String
    abstract fun onBiggerClicked(): String
    abstract fun onExclamationMarkClicked(): String
    abstract fun onQuestionMarkClicked(): String

    fun asFlow(coroutineScope: CoroutineScope): StateFlow<ContermStack<ContermStack<Container>>> = callbackFlow {
        val id = stock.subscribe {
            trySend(it)
        }
        awaitClose {
            stock.dispose(id)
        }
    }.stateIn(coroutineScope, SharingStarted.Lazily, initialValue = stock.stacks)
}
