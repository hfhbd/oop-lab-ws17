package fh.ws17.conterm

import java.beans.PropertyChangeListener

internal class Stock(private val structure: Structure) : Iterable<Container> {
    internal val stock: Stack<Stack<Container>>

    /**
     * Get the used capacity of the complete stock
     *
     * @return the used capacity of the complete stock
     */
    val used: Int
        get() {
            var used = 0
            for (stack in this.stock) {
                used += stack.capacity
            }
            return used
        }

    /**
     * Get the free capacity of the complete stock
     *
     * @return the free capacity of the complete stock
     */
    val free: Int
        get() = structure.height * structure.spaces - this.used

    /**
     * Returns the best stack to have a balanced stock
     *
     * @return the best stack
     */
    val bestStack: Stack<Container>
        get() {

            val loadeableStacks = Stack<Stack<Container>>()
            for (stack in this.stock) {
                if (this.isLoadeable(stack)) {
                    loadeableStacks.add(stack)
                }
            }
            if (loadeableStacks.capacity == 0) {
                throw CapacityExceededException()
            }

            return loadeableStacks.sorted().first()
        }

    init {
        this.stock = Stack(this.structure.spaces)
        for (i in this.stock) {
            this.stock.add(Stack(this.structure.height))
        }
    }

    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        for (stack in stock) {
            stack.addPropertyChangeListener(listener)
        }
    }

    fun removePropertyChangeListener(listener: PropertyChangeListener) {
        for (stack in stock) {
            stack.removePropertyChangeListener(listener)
        }
    }

    /**
     * Check whether the stack has free capacity and the top container is stable
     *
     * @param stack to check
     * @return true if the stack is loadeable
     */
    private fun isLoadeable(stack: Stack<Container>): Boolean {
        return stack.size != stack.capacity && (stack.last == null || stack.last!!.content.istStabil())
    }

    /**
     * Check whether the id of the requested container is in the stock
     *
     * @param containerID of the requested stock
     * @return true if the container id is found
     */
    operator fun contains(containerID: Int): Boolean {
        val toSearch = Container.toSearch(containerID)
        for (stack in this.stock) {
            if (stack.contains(toSearch)) {
                return true
            }
        }
        return false
    }

    /**
     * lade the requested container the the first space in the stock
     *
     * @param container to lade
     * @return true if success
     */
    fun ladeTop(container: Container): Boolean {
        if (this.contains(container.id)) {
            return false
        }

        for (stack in this.stock) {
            if (this.isLoadeable(stack)) {
                return stack.add(container)
            }
        }
        return false
    }

    /**
     * Tries to return the requested container
     * The requested container should be on top of the right stack
     * if not, an exception will throw
     *
     * @param id of the requested container
     * @return the requested container
     * @throws ContractFailureException if not found
     */
    fun unladeTop(id: Int): Container {
        if (!this.contains(id)) {
            throw ContractFailureException()
        }

        val toSearch = Container.toSearch(id)
        for (stack in this.stock) {
            if (stack.seeTop() != null && stack.seeTop() == toSearch) {
                return stack.deleteTop()
            }
        }
        throw ContractFailureException()
    }

    /**
     * Returns the stack containing the requested container
     *
     * @param id of the requested container
     * @return the right stack
     */
    fun getRightStack(id: Int): Stack<Container>? {
        for (stack in this.stock) {
            if (stack.contains(Container.toSearch(id))) {
                return stack
            }
        }
        return null
    }

    /**
     * Returns an other stack containing not the requested container
     * Used to transhipping in the terminal
     *
     * @param id of the requested container
     * @return stack with the requested container
     * @throws ContractFailureException if no other stack was found or the container was not found
     */
    fun getOtherStack(id: Int): Stack<Container> {
        val rightStack = this.getRightStack(id)
        for (stack in this.stock) {
            if (stack != rightStack && this.isLoadeable(stack)) {
                return stack
            }
        }
        throw ContractFailureException()
    }

    /**
     * Returns an iterator of container
     * First every container stack, than the next stack
     *
     * @return the iterator
     */
    override fun iterator(): Iterator<Container> {
        return object : Iterator<Container> {

            var index: Int = 0
            var containerIterator = this@Stock.stock.elementAt(this.index).iterator()

            /**
             * First every container stack, than the next stack
             * @return true if a container is found
             */
            override fun hasNext(): Boolean {
                if (this.containerIterator.hasNext()) {
                    return true
                } else if (this.index != this@Stock.stock.size) {
                    this.index++
                    if (this.index == this@Stock.stock.size) {
                        return false
                    }
                    this.containerIterator = this@Stock.stock.elementAt(index).iterator()
                    return this.hasNext()
                }
                return false
            }

            /**
             * Returns the next of the stack iterator
             * @return the next iterator
             */
            override fun next(): Container {
                return this.containerIterator.next()
            }
        }
    }

    internal class Structure(val spaces: Int, val height: Int)
}
