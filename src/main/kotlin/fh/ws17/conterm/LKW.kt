package fh.ws17.conterm

import kotlin.time.TimeSource

class LKW(timeSource: TimeSource.WithComparableMarks) : Vehicle(1, 1, "LKW", timeSource) {

    /**
     * add the requested container to the moves stack
     *
     * @param ids    of the container
     * @param inbound is lading in the terminal
     * @param moves  the moves should add to this stack
     */
    public override fun addMoves(ids: IntArray, inbound: Boolean, moves: Stack<Move>) {
        if (ids.size == 1) {
            moves.add(Move(ids.first(), inbound))
        }
    }
}
