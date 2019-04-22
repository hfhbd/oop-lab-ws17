package fh.ws17.conterm

class Container private constructor(val isStable: Boolean, val beschreibung: String, val id: Int) : Comparable<Container> {

    var incomeTime: Int? = null
        internal set

    constructor(stable: Boolean, beschreibung: String) : this(stable, beschreibung, idCounter++)

    override fun equals(other: Any?): Boolean {
        return other is Container && other.id == this.id
    }

    override fun toString(): String {
        return "ID: " + this.id + " : " + this.isStable + ": " + this.beschreibung
    }

    override fun compareTo(other: Container) = Integer.compare(id, other.id)

    override fun hashCode(): Int {
        var result = this.beschreibung.hashCode()
        result = 31 * result + if (this.isStable) 1 else 0
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
        internal fun toSearch(id: Int) = Container(false, "", id)
    }
}
