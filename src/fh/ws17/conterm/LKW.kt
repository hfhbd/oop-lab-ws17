package fh.ws17.conterm

class LKW internal constructor() : Vehicle(1, 1) {
    /**
     * add the requested container to the moves stack
     *
     * @param ids    of the container
     * @param inbound is lading in the terminal
     * @param moves  the moves should add to this stack
     */
    public override fun addMoves(ids: IntArray?, inbound: Boolean, moves: Stack<Move>) {
        if (ids != null && ids.size == 1) {
            moves.add(Move(ids[0], inbound))
        }
    }
}
