package fh.ws17.conterm

import kotlin.time.TimeSource

/**
 * Controls the stack
 * Used by the terminal and the vehicle
 */
abstract class StockControllerAbstractClass internal constructor(
    structure: Stock.Structure,
    internal val title: String,
    internal val timeSource: TimeSource.WithComparableMarks
) : Iterable<Container> {

    internal val stock = Stock(structure)

    /**
     * Get the used capacity of the stacks
     *
     * @return the used capacity of the stacks
     */
    /**
     * Get the used capacity of the stacks
     *
     * @return the used capacity of the stacks
     */
    val genutzteKapazitaet: Int
        get() = this.stock.used

    /**
     * Get the free capacity of the stacks
     *
     * @return the free capacity of the stacks
     */
    val freieKapazitaet: Int
        get() = this.stock.free

    /**
     * Lade the container at the first free space
     *
     * @param cont1 to lade
     * @return true if success
     */
    open fun belade(cont1: Container): Boolean {
        if (this.stock.ladeTop(cont1)) {
            cont1.incomeTime = timeSource.markNow()
            return true
        }
        return false
    }

    /**
     * Tries to unlade the requested container
     *
     * @param id of the requested container
     * @return true if success
     * @throws ContractFailureException if the container was not found
     */
    internal open fun entlade(id: Int) = this.stock.unladeTop(id)

    /**
     * Checked whether the requested container is in the stacks
     *
     * @param id of the requested container
     * @return true if found
     */
    fun enthaelt(id: Int) = this.stock.contains(id)

    /**
     * Transshipping the requested container inside the stacks
     *
     * @param container the transshipping container
     * @return true if success
     * @throws ContractFailureException if contract failed
     */
    internal fun umlade(container: Container): Boolean {
        val current = this.stock.getRightStack(container.id) ?: throw ContractFailureException()
        val next = this.stock.getOtherStack(container.id)

        return current.seeTop() == container && next.add(current.deleteTop())
    }

    /**
     * Get all container of the stacks
     *
     * @return all container
     */
    override fun iterator() = this.stock.iterator()

    override fun toString() = "StockControllerAbstractClass{stacks=$stock}"
}
