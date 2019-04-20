package fh.ws17.conterm

class Kahn internal constructor() : Vehicle(1, 3) {

    /**
     * Generates an right order to lade the requested container from/ to the vehicle
     * Add all necessary moves to the moves stack
     * @param ids ids of the requested containers
     * @param inbound in the terminal
     * @param moves the result stack of moves
     */
    @Throws(ContractFailureException::class)
    public override fun addMoves(ids: IntArray?, inbound: Boolean, moves: Stack<Move>) {
        if (ids != null) {
            val allEffectedStacks = Stack<Stack<Container>>()

            for (id in ids) {
                val one = stock.getRightStack(id)!!
                if (!allEffectedStacks.contains(one)) {
                    allEffectedStacks.add(one)
                }
            }

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
}
