package fh.ws17.conterm

import kotlin.time.TimeSource

class Lastzug(timeSource: TimeSource.WithComparableMarks) : Vehicle(2, 1, "Lastzug", timeSource) {

    /**
     * add each requested container id the the moves stack
     * order is not important, because every stack has only 1 element
     *
     * [ids] of the container
     * [inbound] is lading in the terminal
     * [moves]  the moves should add to this stack
     */
    public override fun addMoves(ids: IntArray, inbound: Boolean, moves: Stack<Move>) {
        for (id in ids) {
            moves.add(Move(id, inbound))
        }
    }
}
