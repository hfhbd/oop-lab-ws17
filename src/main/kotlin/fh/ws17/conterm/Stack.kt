package fh.ws17.conterm

class Stack<T : Comparable<T>> internal constructor(val size: Int) : Iterable<T>, Comparable<Stack<T>> {

    internal var last: Element<T>? = null

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
                val toReturn = current!!.content
                current = current!!.previous
                return toReturn
            }
        }
    }

    fun seeTop() = this.last?.content

    fun deleteTop(): T {
        val last = this.last!!
        val t = last.content
        this.last = last.previous
        capacity -= 1
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
            val toAdd = Element(t, previous = null)
            last = toAdd
            capacity += 1
            true
        }
        capacity != size -> {
            val toAdd = Element(t, previous = last)
            last!!.next = toAdd
            last = last!!.next
            capacity += 1
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

    override fun compareTo(other: Stack<T>) = capacity.compareTo(other.capacity)

    internal inner class Element<T>(val content: T, var previous: Element<T>?) {
        var next: Element<T>? = null
    }
}
