package fh.ws17.conterm

class Terminal(spaces: Int, height: Int) : StockControllerAbstractClass(Stock.Structure(spaces, height), "Terminal") {
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
        get() {
            val sb = StringBuilder()
            val allUniqueIDs = Stack<Int>(size = -1)
            for (order in this.charges) {
                for (i in order.containerInbound) {
                    if (!allUniqueIDs.contains(i)) {
                        allUniqueIDs.add(i)
                    }
                }
            }
            val ids = allUniqueIDs.sorted()
            for (id in ids) {
                sb.append("ID: ").append(id).append(" = ").append(this.getGebuehren(id)).append('\n')
            }
            return sb.toString().trim { it <= ' ' }
        }

    /**
     * Lade the requested container at the best stack to have a balanced terminal stacks
     *
     * @param cont1 to lade
     * @return true if success
     */
    override fun belade(cont1: Container): Boolean {
        if (this.stock.bestStack.add(cont1)) {
            cont1.incomeTime = Uhr.zeit
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
        scheduledTime: Int,
        containerInbound: IntArray = intArrayOf(),
        containerOutbound: IntArray = intArrayOf()
    ): Order? {
        val order = OrderImpl(scheduledTime, containerInbound, containerOutbound, null, null)
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
        var contains = false
        for (i in ids) {
            if (i == id) {
                contains = true
            }
        }
        if (!contains) {
            this.charges.add(OrderImpl(Container.toSearch(id)))
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
        vehicle.auftrag?.let {
            //Testing if order is registered in TerminalController
            if (!this.charges.contains(it)) {
                return false
            }
            if (it.containerInbound.size > this.freieKapazitaet) {
                throw CapacityExceededException()
            }
            val toLade = vehicle.ladingStack
            for (move in toLade) {
                if (move.inbound) {

                    val c = vehicle.entlade(move.containerID)
                    this.addTempOrder(c.id, it.containerInbound)

                    if (this.belade(c)) {
                        this.getOrder(it)!!.incomingTime = Uhr.zeit
                        it.incomingTime = Uhr.zeit
                    } else {
                        return false
                    }

                } else {
                    if (vehicle.belade(this.entlade(move.containerID))) {
                        this.getOrder(it)!!.outcomingTime = Uhr.zeit
                        it.outcomingTime = Uhr.zeit
                    } else {
                        return false
                    }
                }
                this.anzahlBewegungen += 1
            }
            return true
        } ?: throw ContractFailureException()
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
        var incomeTime = -1
        var outcomeTime = -1
        for (order in charges) {
            for (idO in order.containerInbound) {
                if (idO == id && order.incomingTime != null) {
                    incomeTime = order.incomingTime!!
                }
            }
            for (idO in order.containerOutbound) {
                if (idO == id && order.outcomingTime != null) {
                    outcomeTime = order.outcomingTime!!
                }
            }
        }
        if (incomeTime == -1) {
            return 0.0
        }
        if (outcomeTime == -1) {
            outcomeTime = Uhr.zeit
        }
        outcomeTime += 1
        var sum = (outcomeTime - incomeTime) * 1.5
        if (sum != 0.0) {
            sum += 20.0
        }
        return sum
    }

    override fun toString() = "Terminal{ charges= $charges, moves= $anzahlBewegungen, stacks= $stock}"

    interface Order {
        val isOriginal: Boolean
        val scheduledTime: Int
        val containerInbound: IntArray
        val containerOutbound: IntArray
        var incomingTime: Int?
        var outcomingTime: Int?
        val terminal: Terminal
        fun clone(): Order
    }

    private inner class OrderImpl constructor(
        override val scheduledTime: Int,
        override val containerInbound: IntArray,
        override val containerOutbound: IntArray,
        override var incomingTime: Int? = scheduledTime,
        override var outcomingTime: Int? = scheduledTime
    ) : Order {

        override val terminal: Terminal = this@Terminal

        override var isOriginal = true

        constructor(container: Container) : this(Uhr.zeit, intArrayOf(container.id), intArrayOf(container.id))

        override fun hashCode(): Int {
            return scheduledTime + containerInbound.hashCode() + containerOutbound.hashCode()
        }

        override fun clone(): Order {
            return OrderImpl(scheduledTime, containerInbound, containerOutbound, incomingTime, outcomingTime).apply {
                isOriginal = false
            }
        }

        override fun equals(other: Any?): Boolean {
            return other is Order && other.hashCode() == this.hashCode()
        }
    }
}
