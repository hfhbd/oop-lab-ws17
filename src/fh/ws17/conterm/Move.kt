package fh.ws17.conterm

/**
 * Manages the moves from one vehicle into one terminal
 */
class Move(
        /**
         * id of the moving container
         */
        val containerID: Int,
        /**
         * in the terminal
         */
        val inbound: Boolean) : Comparable<Move> {

    override fun toString(): String {
        return "Move{" +
                "inbound=" + inbound +
                ", containerID=" + containerID +
                '}'.toString()
    }

    override fun compareTo(other: Move): Int {
        return Integer.compare(this.containerID, other.containerID)
    }
}
