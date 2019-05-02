package fh.ws17.conterm


import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport

class Stack<T : Comparable<T>> internal constructor(sizeInit: Int = -1) : Iterable<T>, Comparable<Stack<T>> {
    private val pcs = PropertyChangeSupport(this)

    internal var last: Element<T>? = null

    var size = sizeInit
        private set
    var capacity = 0
        private set

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        this.pcs.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        this.pcs.removePropertyChangeListener(listener)
    }

    /**
     * From Top to Bottom!!
     */
    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {

            private var current = last

            override fun hasNext() = current != null

            override fun next(): T {
                val toReturn = current!!.content
                current = current!!.previous
                return toReturn
            }
        }
    }

    fun seeTop() = this.last?.content

    fun deleteTop(): T {
        val last = this.last
        val t = last!!.content
        this.last = last.previous
        capacity -= 1
        this.pcs.firePropertyChange(ChangedEvent())
        return t
    }

    fun getDistanceFromTopTo(toSearch: T): Int? {
        for ((index, t) in this.withIndex()) {
            if (t == toSearch) {
                return index
            }
        }
        return null
    }

    fun add(t: T) = when {
        capacity == 0 -> {
            val toAdd = Element(t)
            last = toAdd
            capacity += 1
            pcs.firePropertyChange(ChangedEvent())
            true
        }
        capacity != size -> {
            val toAdd = Element(t)
            toAdd.previous = last
            last!!.next = toAdd
            last = last!!.next
            capacity += 1
            pcs.firePropertyChange(ChangedEvent())
            true
        }
        else -> false
    }

    operator fun contains(toSearch: T) = getDistanceFromTopTo(toSearch) != null

    override fun toString(): String {
        val string = StringBuilder("Stack with $size used with $capacity:")
        forEach {
            string.append("{").append(it).append("}")
        }
        return string.toString()
    }

    override fun compareTo(other: Stack<T>) = Integer.compare(capacity, other.capacity)

    internal inner class Element<T>(val content: T) {
        var next: Element<T>? = null
        var previous: Element<T>? = null
    }

    internal inner class ChangedEvent : PropertyChangeEvent(this@Stack, "stackedChanged", null, null)
}
