package fh.ws17.conterm

import java.beans.PropertyChangeListener

/**
 * Controlls the stack
 * Used by the terminal and the vehicle
 */
abstract class StockControllerAbstractClass internal constructor(structure: Stock.Structure) : Iterable<Container> {

    internal val stock = Stock(structure)

    /**
     * Get the used capacity of the stock
     *
     * @return the used capacity of the stock
     */
    /**
     * Get the used capacity of the stock
     *
     * @return the used capacity of the stock
     */
    val genutzteKapazitaet = this.stock.used

    /**
     * Get the free capacity of the stock
     *
     * @return the free capacity of the stock
     */
    val freieKapazitaet = this.stock.free

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        stock.addPropertyChangeListener(listener)
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        stock.removePropertyChangeListener(listener)
    }

    /**
     * Lade the container at the first free space
     *
     * @param cont1 to lade
     * @return true if success
     */
    open fun belade(cont1: Container): Boolean {
        if (this.stock.ladeTop(cont1)) {
            cont1.incomeTime = Uhr.zeit
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
    @Throws(ContractFailureException::class)
    internal open fun entlade(id: Int) = this.stock.unladeTop(id)

    /**
     * Checked whether the requested container is in the stock
     *
     * @param id of the requested container
     * @return true if found
     */
    fun enthaelt(id: Int) = this.stock.contains(id)

    /**
     * Transshipping the requested container inside the stock
     *
     * @param container the transshipping container
     * @return true if success
     * @throws ContractFailureException if contract failed
     */
    @Throws(ContractFailureException::class)
    internal fun umlade(container: Container): Boolean {
        val current = this.stock.getRightStack(container.id) ?: throw ContractFailureException()
        val next = this.stock.getOtherStack(container.id)

        return current.seeTop() == container && next.add(current.deleteTop())
    }

    /**
     * Get all container of the stock
     *
     * @return all container
     */
    override fun iterator() = this.stock.iterator()

    override fun toString() = "StockControllerAbstractClass{stock=$stock}"
}
