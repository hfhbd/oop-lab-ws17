package fh.ws17.conterm

object Uhr {
    var zeit: Int = 0
        private set

    fun incZeit(deltaTime: Int) {
        this.zeit += deltaTime
    }

    fun reset() {
        this.zeit = 0
    }

    override fun toString(): String {
        return "Uhr{" +
                "time=" + this.zeit +
                '}'.toString()
    }
}
