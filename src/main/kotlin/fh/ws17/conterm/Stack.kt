package fh.ws17.conterm

class Stack<T : Comparable<T>> internal constructor(val size: Int, private val onChange: () -> Unit = {}) : Iterable<T>,
    Comparable<Stack<T>> {

    var last: Element<T>? = null
        private set

    var capacity = 0
        private set

    /**
     * From Top to Bottom!!
     */
    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {

            private var current = last

            override fun hasNext() = current != null

            override fun next(): T {
                val tmpCurrent = requireNotNull(current)
                val toReturn = tmpCurrent.content
                current = tmpCurrent.previous
                return toReturn
            }
        }
    }

    fun seeTop(): T? = this.last?.content

    fun deleteTop(): T {
        val last = requireNotNull(this.last)
        val t = last.content
        this.last = last.previous
        capacity -= 1
        onChange()
        return t
    }

    fun getDistanceFromTopTo(toSearch: T): Int? {
        forEachIndexed { index, t ->
            if (t == toSearch) {
                return index
            }
        }
        return null
    }

    fun add(t: T) = when {
        capacity == 0 -> {
            val toAdd = Element(t, previous = null)
            last = toAdd
            capacity += 1
            onChange()
            true
        }
        capacity != size -> {
            val tmpLast = requireNotNull(last)
            val toAdd = Element(t, previous = tmpLast)
            tmpLast.next = toAdd
            last = toAdd
            capacity += 1
            onChange()
            true
        }
        else -> false
    }

    operator fun contains(toSearch: T): Boolean = getDistanceFromTopTo(toSearch) != null

    override fun toString() = buildString {
        append("Stack with $size used with $capacity:")
        this@Stack.forEach {
            append("{")
            append(it)
            append("}")
        }
    }


    override fun compareTo(other: Stack<T>): Int = capacity.compareTo(other.capacity)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stack<*>

        if (last != other.last) return false

        return true
    }

    override fun hashCode(): Int = last?.hashCode() ?: 0

    data class Element<T>(val content: T, var previous: Element<T>?) {
        var next: Element<T>? = null
    }
}
