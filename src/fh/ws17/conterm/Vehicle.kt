package fh.ws17.conterm

import fh.ws17.conterm.Terminal.Order

abstract class Vehicle internal constructor(spaces: Int, height: Int) : StockControllerAbstractClass(Stock.Structure(spaces, height)) {
    var auftrag: Order? = null

    internal val ladingStack: Stack<Move>
        get() {
            val inbound = this.auftrag!!.containerInbound
            for (id in inbound) {
                if (!this.enthaelt(id)) {
                    throw ContractFailureException()
                }
            }

            val s = Stack<Move>()
            this.addMoves(this.auftrag!!.containerOutbound, false, s)
            this.addMoves(this.auftrag!!.containerInbound, true, s)
            return s
        }

    /**
     * Called by getLadingStack
     * should be implement by each specific vehicle
     *
     *
     * should add their moves based on the requested ids of container to the stack called moves
     *
     *
     * FROM TOP TO BUTTOM
     * LAST IN FIRST OUT
     *
     * @param ids    of the container
     * @param inbound is lading in the terminal
     * @param moves  the moves should add to this stack
     * @throws ContractFailureException failed if one container is not found or the order is wrong
     */
    protected abstract fun addMoves(ids: IntArray, inbound: Boolean, moves: Stack<Move>)

    /**
     * Tries to execute the order by the planned terminal
     *
     * @return true if success
     * @throws ContractFailureException if contract failed
     */
    fun abfertigung(): Boolean {
        return auftrag!!.terminal.abfertigung(this)
    }

    override fun toString() = "Vehicle{order=$auftrag, stacks=$stock}"
}
