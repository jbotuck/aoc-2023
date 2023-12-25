fun main() {
    val grid = readInput("Day23")

    data class Point(val y: Int, val x: Int, val isSlippery: Boolean = true) {
        val links = mutableMapOf<Direction, Pair<Point, Int>>()
        private fun gridValue() = grid[y][x]
        fun neighbors(): Map<Direction, Point> {
            if (isSlippery) slopes[gridValue()]?.let { return mapOf(it to neighbor(it)) }
            return Direction.entries
                .map { it to neighbor(it) }
                .filter { (_, it) -> it.y in grid.indices && it.x in grid[y].indices && it.gridValue() != '#' }
                .toMap()
        }

        private fun neighbor(direction: Direction) = copy(y = y + direction.y, x = x + direction.x)
        fun initializeGraph() {
            val allNodes = mutableMapOf(this to this)
            val toVisit = ArrayDeque(setOf(this))
            while (toVisit.isNotEmpty()) {
                val current = allNodes[toVisit.removeFirst()]!!
                current.unlinkedNeighborNodes()
                    .map { (direction, neighbor) ->
                        val distance = neighbor.links.entries.first().value.second
                        val persistedNeighborNode =
                            allNodes[neighbor]?.apply { mergeLinks(neighbor) } ?: neighbor.also { allNodes[it] = it }
                        current.addLink(direction, persistedNeighborNode, distance)
                        persistedNeighborNode
                    }.let { toVisit.addAll(it) }
            }
        }

        private fun addLink(direction: Direction, point: Point, distance: Int) {
            links[direction] = point to distance
        }

        private fun mergeLinks(other: Point) = links.putAll(other.links)


        //finds unlinked neighbors and returns them with a back-link to self
        private fun unlinkedNeighborNodes(): Set<Pair<Direction, Point>> {
            return neighbors()
                .asSequence()
                .map { it.key }
                .filter { it !in links.keys }
                .mapNotNull { direction ->
                    findNextNode(direction)?.let{direction to it}
                }.toSet()
        }

        //finds the next node and returns with back link to self. Returns null
        private fun findNextNode(direction: Direction): Point? {
            val origin = this
            var count = 1
            var current = neighbor(direction)
            var lastDirection = direction
            while (true) {
                val neighbors = current.neighbors() - lastDirection.reversed()
                if (current.neighbors().isEmpty()) return null
                neighbors.entries.singleOrNull()
                    ?.let {
                        count++
                        current = it.value
                        lastDirection = it.key
                    } ?: return current.apply {
                    addLink(lastDirection.reversed(), origin, count)
                }
            }
        }
    }


    run { //Part 1
        val toVisit = ArrayDeque<Pair<Point, Set<Point>>>(listOf(Point(0, 1) to emptySet()))
        var max = 0
        while (toVisit.isNotEmpty()) {
            val (current, previousSteps) = toVisit.removeLast()
            if (current.y == grid.lastIndex) {
                max = maxOf(max, previousSteps.size)
                continue
            }
            current
                .neighbors()
                .map { it.value }
                .minus(previousSteps)
                .takeIf { it.isNotEmpty() }
                ?.let { nextSteps ->
                    @Suppress("NAME_SHADOWING")
                    val previousSteps = previousSteps + current
                    nextSteps.map { it to previousSteps }
                }
                ?.let { toVisit.addAll(it) }
        }
        max.println()
    }
    val start = Point(0, 1, false).apply { initializeGraph() }
    val toVisit = ArrayDeque(listOf(start to emptyMap<Point, Int>()))
    var max = 0
    while (toVisit.isNotEmpty()){
        val (current, previousSteps) = toVisit.removeLast()
        if (current.y == grid.lastIndex) {
            max = maxOf(max, previousSteps.values.sum())
            continue
        }
        current.links.values
            .filter { it.first !in previousSteps }
            .takeIf { it.isNotEmpty() }
            ?.map{(nextNode, distance) ->
                nextNode to previousSteps.plus(nextNode to distance)
            }?.let { toVisit.addAll(it) }
    }
    println(max)
}


private val slopes = mutableMapOf(
    'v' to Direction.SOUTH,
    '>' to Direction.EAST
)

private enum class Direction(val y: Int, val x: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1);

    fun reversed() = when(this){
        NORTH -> SOUTH
        SOUTH -> NORTH
        WEST -> EAST
        EAST -> WEST
    }
}