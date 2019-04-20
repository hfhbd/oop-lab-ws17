package fh.ws17.conterm

class Container internal constructor(private val stable: Boolean, val beschreibung: String) : Comparable<Container> {
    var id: Int = 0
        private set

    var incomeTime: Int? = null
        internal set

    init {
        idCounter++
        id = idCounter
    }

    fun istStabil(): Boolean {
        return this.stable
    }

    override fun equals(other: Any?): Boolean {
        return other is Container && other.id == this.id
    }

    override fun toString(): String {
        return "ID: " + this.id + " : " + this.stable + ": " + this.beschreibung
    }

    override fun compareTo(other: Container) = Integer.compare(id, other.id)

    override fun hashCode(): Int {
        var result = this.beschreibung.hashCode()
        result = 31 * result + if (this.stable) 1 else 0
        result = 31 * result + this.id
        return result
    }

    companion object {
        private var idCounter: Int = 0

        /**
         * Creates an temp container with the searched container id
         *
         * @param id of the searched container
         * @return empty, instable container with the same id
         */
        internal fun toSearch(id: Int): Container {
            val toReturn = Container(false, "")
            toReturn.id = id
            return toReturn
        }
    }
}
