fun main() {
    val masterGrid = readInput("Day16")
    fun totalEnergized(direction: Direction16, i: Int): Int {
        val grid = masterGrid.map { row -> row.map { Tile(it) } }
        val (y, x) = direction.coordinatesOfFirstEdge(i)

        val toEnergize = ArrayDeque<Triple<Int, Int, Direction16>>().apply {
            add(
                Triple(
                    y.coerceAtMost(grid.lastIndex),
                    x.coerceAtMost(grid.first().lastIndex),
                    direction
                )
            )
        }
        while (toEnergize.isNotEmpty()) {
            val (y, x, direction) = toEnergize.removeLast()
            grid[y][x]
                .energize(direction)
                .mapNotNull { direction ->
                    val y = y + direction.y
                    val x = x + direction.x
                    grid.getOrNull(y)?.getOrNull(x)?.let { Triple(y, x, direction) }
                }.let { toEnergize.addAll(it) }
        }
        return grid.sumOf { row -> row.count { it.isEnergized() } }
    }

    //part1
    totalEnergized(Direction16.EAST, 0).println()

    //part 2
    masterGrid.indices
        .flatMap { listOf(totalEnergized(Direction16.EAST, it), totalEnergized(Direction16.WEST, it)) }
        .plus(
            masterGrid.first().indices.flatMap {
                listOf(
                    totalEnergized(Direction16.SOUTH, it),
                    totalEnergized(Direction16.NORTH, it)
                )
            }
        ).max().println()

}

private data class Tile(val c: Char, private val lightDirections: MutableSet<Direction16> = mutableSetOf()) {
    fun isEnergized() = lightDirections.isNotEmpty()

    fun energize(direction: Direction16): Set<Direction16> {
        if (direction in lightDirections) return emptySet()
        lightDirections.add(direction)
        return when (c) {
            '-' -> if (direction.isVertical()) setOf(Direction16.EAST, Direction16.WEST) else setOf(direction)
            '|' -> if (direction.isVertical()) setOf(direction) else setOf(Direction16.NORTH, Direction16.SOUTH)
            '/' -> setOf(
                when (direction) {
                    Direction16.NORTH -> Direction16.EAST
                    Direction16.EAST -> Direction16.NORTH
                    Direction16.SOUTH -> Direction16.WEST
                    Direction16.WEST -> Direction16.SOUTH
                }
            )

            '\\' -> setOf(
                when (direction) {
                    Direction16.NORTH -> Direction16.WEST
                    Direction16.WEST -> Direction16.NORTH
                    Direction16.SOUTH -> Direction16.EAST
                    Direction16.EAST -> Direction16.SOUTH
                }
            )

            else -> setOf(direction)
        }
    }
}


private enum class Direction16(val y: Int, val x: Int, val coordinatesOfFirstEdge: (Int) -> Pair<Int, Int>) {
    NORTH(-1, 0, { Int.MAX_VALUE to it }),
    SOUTH(1, 0, { 0 to it }),
    EAST(0, 1, { it to 0 }),
    WEST(0, -1, { it to Int.MAX_VALUE });

    fun isVertical(): Boolean {
        return this in setOf(NORTH, SOUTH)
    }

}