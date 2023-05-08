package fh.ws17.conterm

/**
 * Manages the moves from one vehicle into one terminal
 * [containerID] is the ID of the container and [inbound] is true, if the container should be to go in the terminal
 */
data class Move(val containerID: Int, val inbound: Boolean) : Comparable<Move> {
    override fun compareTo(other: Move): Int = containerID.compareTo(other.containerID)
}
