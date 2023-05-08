package fh.ws17.conterm

import kotlin.time.TimeSource

class Kahn(timeSource: TimeSource.WithComparableMarks) : Vehicle(1, 3, "Kahn", timeSource) {

    /**
     * Generates an right order to lade the requested container from/ to the vehicle
     * Add all necessary moves to the moves stack
     * @param ids ids of the requested containers
     * @param inbound in the terminal
     * @param moves the result stack of moves
     */
    public override fun addMoves(ids: IntArray, inbound: Boolean, moves: Stack<Move>) {
        val allEffectedStacks = ids.map { stock.getRightStack(it) ?: throw ContractFailureException() }.toSet()

        for (stack in allEffectedStacks) {
            var deepest = 0
            for (id in ids) {
                val distance = stack.getDistanceFromTopTo(Container.toSearch(id))
                if (distance != null && distance > deepest) {
                    deepest = distance
                }
            }

            var current = stack.last
            for (i in 0 until deepest) {

                var contains = false
                for (id in ids) {
                    if (current!!.content.id == id) {
                        contains = true
                    }
                }
                if (!contains) {
                    moves.add(Move(current!!.content.id, !inbound))
                }
                current = current!!.previous
            }

            while (current != null) {
                moves.add(Move(current.content.id, inbound))
                current = current.next
            }
        }
    }
}
