package fh.ws17.conterm

import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

class Terminal(spaces: Int, height: Int, timeSource: TimeSource.WithComparableMarks) : StockControllerAbstractClass(Stock.Structure(spaces, height), "Terminal", timeSource) {
    private val initTime = timeSource.markNow()
    
    /**
     * Set of the original orders
     */
    private val charges = mutableSetOf<Order>()

    /**
     * the moves of the terminal
     */
    /**
     * Get the moves of the terminal
     *
     * @return the moves of the terminal
     */
    var anzahlBewegungen: Int = 0
        private set

    /**
     * Get the sum of alltime fees for all containers
     *
     * @return the sum of alltime fees
     */
    val gebuehren: Double
        get() {
            var result = 0.0
            for (order in charges) {
                for (idO in order.containerInbound) {
                    result += getGebuehren(idO)
                }
            }
            return result
        }

    /**
     * Returns a multiline String with all Container IDs inside the terminal with the charges
     *
     * @return ID: 1 = 21.5 etc.
     */
    val gebuehrenString: String
        get() = buildString {
            val allUniqueIDs = Stack<Int>(size = -1)
            for (order in this@Terminal.charges) {
                for (i in order.containerInbound) {
                    if (!allUniqueIDs.contains(i)) {
                        allUniqueIDs.add(i)
                    }
                }
            }
            val ids = allUniqueIDs.sorted()
            for (id in ids) {
                append("ID: ")
                append(id)
                append(" = ")
                append(this@Terminal.getGebuehren(id))
                appendLine()
            }
        }

    /**
     * Lade the requested container at the best stack to have a balanced terminal stacks
     *
     * @param cont1 to lade
     * @return true if success
     */
    override fun belade(cont1: Container): Boolean {
        if (this.stock.bestStack.add(cont1)) {
            cont1.incomeTime = timeSource.markNow()
            return true
        }
        return false
    }

    /**
     * Tries to unlade the requested container
     * transshipping all neccessarry containers to get the right container
     *
     * @param id of the requested container
     * @return true if success
     * @throws ContractFailureException if contract failed
     */
    override fun entlade(id: Int): Container {
        val rightStack = this.stock.getRightStack(id) ?: throw ContractFailureException()
        var top = rightStack.seeTop()!!
        while (top != Container.toSearch(id)) {
            if (this.umlade(top)) {
                this.anzahlBewegungen += 1
                top = rightStack.seeTop()!!
            }
        }
        return rightStack.deleteTop()
    }

    /**
     * Creates an order with the requested container ids at the specific time
     *
     * @param scheduledTime     time of the incoming
     * @param containerInbound  of the incoming container
     * @param containerOutbound of the outcoming container
     * @return an copy of the order or null if the order is already set
     */
    fun avisierung(
        scheduledTime: Duration,
        containerInbound: IntArray = intArrayOf(),
        containerOutbound: IntArray = intArrayOf()
    ): Order? {
        val order = OrderImpl(initTime + scheduledTime, containerInbound, containerOutbound, null, null)
        return if (this.charges.add(order)) {
            order.clone()
        } else null
    }

    /**
     * creates an transshipping order inside the terminal
     *
     * @param id  of the transshipping container
     * @param ids of all the container, which should move
     */
    private fun addTempOrder(id: Int, ids: IntArray) {
        if (id !in ids) {
            this.charges.add(OrderImpl(Container.toSearch(id), timeSource))
        }
    }

    /**
     * Returns the order from the set
     *
     * @param vehicleOrder to copy of the order
     * @return the original order
     */
    private fun getOrder(vehicleOrder: Order) = this.charges.find { it == vehicleOrder }

    /**
     * Handling the vehicle
     * calls the vehicle.getLadingStack method to get the right order of container to handle the vehicle
     *
     * @param vehicle the handling container
     * @return true if success
     * @throws ContractFailureException if contract failed
     */
    fun abfertigung(vehicle: Vehicle): Boolean {
        val it = requireNotNull(vehicle.auftrag) { throw ContractFailureException() }
        //Testing if order is registered in TerminalController
        if (!this.charges.contains(it)) {
            return false
        }
        require(it.containerInbound.size <= this.freieKapazitaet) {
            throw CapacityExceededException()
        }
        val toLade = vehicle.ladingStack
        for (move in toLade) {
            if (move.inbound) {

                val c = vehicle.entlade(move.containerID)
                this.addTempOrder(c.id, it.containerInbound)

                if (this.belade(c)) {
                    this.getOrder(it)!!.incomingTime = timeSource.markNow()
                    it.incomingTime = timeSource.markNow()
                } else {
                    return false
                }

            } else {
                if (vehicle.belade(this.entlade(move.containerID))) {
                    this.getOrder(it)!!.outcomingTime = timeSource.markNow()
                    it.outcomingTime = timeSource.markNow()
                } else {
                    return false
                }
            }
            this.anzahlBewegungen += 1
        }
        return true
    }


    /**
     * Calculate the fees of the requested container
     * 20 is the start fee
     * 1.5 is fee per time unit
     *
     * @param id of the requested container
     * @return the fees of the requested container
     */
    fun getGebuehren(id: Int): Double {
        var incomeTime: ComparableTimeMark? = null
        var outcomeTime: ComparableTimeMark? = null
        for (order in charges) {
            for (idO in order.containerInbound) {
                val incomingTime = order.incomingTime
                if (idO == id && incomingTime != null) {
                    incomeTime = incomingTime
                }
            }
            for (idO in order.containerOutbound) {
                val outcomingTime = order.outcomingTime
                if (idO == id && outcomingTime != null) {
                    outcomeTime = outcomingTime
                }
            }
        }
        if (incomeTime == null) {
            return 0.0
        }
        if (outcomeTime == null) {
            outcomeTime = timeSource.markNow()
        }
        outcomeTime += 1.seconds
        var sum = (outcomeTime - incomeTime).toDouble(DurationUnit.SECONDS) * 1.5
        if (sum != 0.0) {
            sum += 20.0
        }
        return sum
    }

    override fun toString() = "Terminal{charges= $charges, moves= $anzahlBewegungen, stacks= $stock}"

    interface Order {
        val isOriginal: Boolean
        val scheduledTime: ComparableTimeMark
        val containerInbound: IntArray
        val containerOutbound: IntArray
        var incomingTime: ComparableTimeMark?
        var outcomingTime: ComparableTimeMark?
        val terminal: Terminal
        fun clone(): Order
    }

    private inner class OrderImpl(
        override val scheduledTime: ComparableTimeMark,
        override val containerInbound: IntArray,
        override val containerOutbound: IntArray,
        override var incomingTime: ComparableTimeMark? = scheduledTime,
        override var outcomingTime: ComparableTimeMark? = scheduledTime
    ) : Order {

        override val terminal: Terminal = this@Terminal

        override var isOriginal = true

        constructor(container: Container, timeSource: TimeSource.WithComparableMarks) : this(timeSource.markNow(), intArrayOf(container.id), intArrayOf(container.id))

        override fun hashCode(): Int = scheduledTime.hashCode() + containerInbound.hashCode() + containerOutbound.hashCode()

        override fun clone(): Order {
            return OrderImpl(scheduledTime, containerInbound, containerOutbound, incomingTime, outcomingTime).apply {
                isOriginal = false
            }
        }


        override fun equals(other: Any?): Boolean {
            return other is Order && other.hashCode() == this.hashCode()
        }

        override fun toString(): String =
            "OrderImpl(scheduledTime=$scheduledTime, containerInbound=${containerInbound.contentToString()}, containerOutbound=${containerOutbound.contentToString()}, incomingTime=$incomingTime, outcomingTime=$outcomingTime, isOriginal=$isOriginal)"
    }
}
