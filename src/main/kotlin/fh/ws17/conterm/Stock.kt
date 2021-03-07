package fh.ws17.conterm

import androidx.compose.runtime.*

internal class Stock(private val structure: Structure) : Iterable<Container>, State<List<List<Container>>> {
    internal val stacks = Stack<Stack<Container>>(this.structure.spaces).apply {
        repeat(size) {
            this.add(Stack(this@Stock.structure.height) {
                update()
            })
        }
    }

    private fun update() {
        value = stacks.map { it.map { it } }
        println("update called")
    }

    override var value: List<List<Container>> = stacks.map { it.toList() }
        private set

    /**
     * Get the used capacity of the complete stacks
     *
     * @return the used capacity of the complete stacks
     */
    val used: Int
        get() {
            var used = 0
            stacks.forEach {
                used += it.capacity
            }
            return used
        }

    /**
     * Get the free capacity of the complete stacks
     *
     * @return the free capacity of the complete stacks
     */
    val free: Int
        get() = structure.height * structure.spaces - this.used

    /**
     * Returns the best stack to have a balanced stacks
     *
     * @return the best stack
     */
    val bestStack: Stack<Container>
        get() {

            val loadableStacks = Stack<Stack<Container>>(size = -1)
            stacks.forEach {
                if (this.isLoadable(it)) {
                    loadableStacks.add(it)
                }
            }
            if (loadableStacks.capacity == 0) {
                throw CapacityExceededException()
            }

            return loadableStacks.minOrNull()!!
        }

    /**
     * Check whether the stack has free capacity and the top container is stable
     *
     * @param stack to check
     * @return true if the stack is loadeable
     */
    private fun isLoadable(stack: Stack<Container>) =
        stack.size != stack.capacity && (stack.last == null || stack.last!!.content.isStable)

    /**
     * Check whether the id of the requested container is in the stacks
     *
     * @param containerID of the requested stacks
     * @return true if the container id is found
     */
    operator fun contains(containerID: Int): Boolean {
        val toSearch = Container.toSearch(containerID)
        for (stack in this.stacks) {
            if (stack.contains(toSearch)) {
                return true
            }
        }
        return false
    }

    /**
     * lade the requested container the the first space in the stacks
     *
     * @param container to lade
     * @return true if success
     */
    fun ladeTop(container: Container): Boolean {
        if (this.contains(container.id)) {
            return false
        }

        for (stack in this.stacks) {
            if (this.isLoadable(stack)) {
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
        for (stack in this.stacks) {
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
        for (stack in this.stacks) {
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
        for (stack in this.stacks) {
            if (stack != rightStack && this.isLoadable(stack)) {
                return stack
            }
        }
        throw ContractFailureException()
    }

    override fun toString() = "$stacks"

    /**
     * Returns an iterator of container
     * First every container stack, than the next stack
     *
     * @return the iterator
     */
    override fun iterator() = object : Iterator<Container> {

        var index: Int = 0
        var containerIterator = this@Stock.stacks.elementAt(this.index).iterator()

        /**
         * First every container stack, than the next stack
         * @return true if a container is found
         */
        /**
         * First every container stack, than the next stack
         * @return true if a container is found
         */
        override fun hasNext(): Boolean {
            return when {
                this.containerIterator.hasNext() -> true
                this.index != this@Stock.stacks.size -> {
                    this.index++
                    if (this.index == this@Stock.stacks.size) {
                        return false
                    }
                    this.containerIterator = this@Stock.stacks.elementAt(index).iterator()
                    this.hasNext()
                }
                else -> false
            }
        }

        /**
         * Returns the next of the stack iterator
         * @return the next iterator
         */
        /**
         * Returns the next of the stack iterator
         * @return the next iterator
         */
        override fun next() = this.containerIterator.next()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stock

        if (stacks != other.stacks) return false

        return true
    }

    override fun hashCode(): Int {
        return stacks.hashCode()
    }

    data class Structure(val spaces: Int, val height: Int)
}
