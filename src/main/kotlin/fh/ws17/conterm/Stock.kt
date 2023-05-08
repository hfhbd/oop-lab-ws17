package fh.ws17.conterm

internal class Stock(private val structure: Structure) : Iterable<Container> {
    internal val stacks = Stack<Stack<Container>>(this.structure.spaces).apply {
        repeat(size) {
            add(Stack(structure.height) {
                for (sub in subscribers.values) {
                    sub(this)
                }
            })
        }
    }

    private var subscribers: MutableMap<Int, (Stack<Stack<Container>>) -> Unit> = mutableMapOf()
    private var counter = 0

    fun subscribe(onUpdate: (Stack<Stack<Container>>) -> Unit): Int {
        subscribers[counter] = onUpdate
        counter += 1
        return counter
    }

    fun dispose(id: Int) {
        subscribers.remove(id)
    }

    /**
     * Get the used capacity of the complete stacks
     *
     * @return the used capacity of the complete stacks
     */
    val used: Int
        get() {
            var used = 0
            for (it in stacks) {
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
            for (it in stacks) {
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
        for (stack in stacks) {
            if (toSearch in stack) {
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
        if (container.id in this) {
            return false
        }

        for (stack in stacks) {
            if (isLoadable(stack)) {
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
        val toSearch = Container.toSearch(id)
        for (stack in stacks) {
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
        val toSearch = Container.toSearch(id)
        for (stack in stacks) {
            if (toSearch in stack) {
                return stack
            }
        }
        return null
    }

    /**
     * Returns another stack containing not the requested container
     * Used to transshipping in the terminal.
     *
     * @param id of the requested container
     * @return stack with the requested container
     * @throws ContractFailureException if no other stack was found, or the container was not found.
     */
    fun getOtherStack(id: Int): Stack<Container> {
        val rightStack = getRightStack(id)
        for (stack in this.stacks) {
            if (stack != rightStack && isLoadable(stack)) {
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
        var containerIterator = stacks.elementAt(this.index).iterator()

        /**
         * First every container stack, then the next stack
         * @return true if a container is found
         */
        override fun hasNext(): Boolean = when {
            containerIterator.hasNext() -> true
            index != stacks.size -> {
                index++
                if (index == stacks.size) {
                    false
                } else {
                    containerIterator = stacks.elementAt(index).iterator()
                    hasNext()
                }
            }

            else -> false
        }

        /**
         * Returns the next of the stack iterator
         * @return the next iterator
         */
        override fun next() = containerIterator.next()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stock

        return stacks == other.stacks
    }

    override fun hashCode(): Int {
        return stacks.hashCode()
    }

    data class Structure(val spaces: Int, val height: Int)
}
