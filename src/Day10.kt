fun main() {
    val grid = run {
        val input = readInput("Day10")
        Array(input.size) { y -> Array(input[y].length) { x -> Tile(y, x, input[y][x]) } }
    }
    run {//part1 draw the loop, print the measurement, replace S with F
        val start = grid.firstNotNullOf { row -> row.firstOrNull { it.value == 'S' } }
        var stepsTaken = 1
        //next 2 are hardcoded based on my input
        var currentLocation = start.y.inc() to start.x
        var previousDirectionFollowed = 1 to 0
        while (currentLocation != start.coordinates) {
            val currentTile = grid[currentLocation.first][currentLocation.second]
            currentTile.isPipe = true
            currentTile.isVisited = true
            //update current location and previous direction
            previousDirectionFollowed =
                nextDirection(currentTile.value, previousDirectionFollowed)
            currentLocation =
                currentTile.y + previousDirectionFollowed.first to currentTile.x + previousDirectionFollowed.second
            stepsTaken++
        }
        println(stepsTaken / 2)
        grid[start.y][start.x] = start.copy(value = 'F').apply {
            isPipe = true
            isVisited = true
        }
    }
    //part 2
    @Suppress("NAME_SHADOWING")
    suspend fun SequenceScope<Tile>.visit(tile: Tile, isEnclosed: Boolean? = null) {
        if (tile.isVisited) return
        val isEnclosed = isEnclosed ?: grid.isEnclosed(tile)
        tile.isVisited = true
        if (isEnclosed) yield(tile)
        for (tile in grid.adjacentTo(tile).filter { !it.isVisited}) {
            visit(tile, isEnclosed)
        }
    }
    sequence {
        for (tile in grid.flatMap { it.asSequence() }) {
            visit(tile)
        }
    }.count().println()
}

private fun Array<Array<Tile>>.adjacentTo(tile: Tile) =
    tile.adjacentCoordinates().mapNotNull { (y, x) -> getOrNull(y)?.getOrNull(x) }

private fun Array<Array<Tile>>.isEnclosed(tile: Tile) = get(tile.y)
    .slice(0 until tile.x)
    //if a tile doesn't connect to the one above it, sneak above it
    .count { it.isPipe && it.value in listOf('|', 'J', 'L') } % 2 == 1


private data class Tile(val y: Int, val x: Int, val value: Char) {
    fun adjacentCoordinates() = listOf(
        y.inc() to x,
        y.dec() to x,
        y to x.inc(),
        y to x.dec()
    )

    var isPipe = false
    var isVisited = false
    val coordinates by lazy { y to x }
}
private fun nextDirection(pipe: Char, previousDirectionFollowed: Pair<Int, Int>): Pair<Int, Int> {
    val (_, x) = previousDirectionFollowed
    return when (pipe) {
        '|', '-' -> previousDirectionFollowed
        'L' -> if (x == -1) -1 to 0 else 0 to 1
        'F' -> if (x == -1) 1 to 0 else 0 to 1
        'J' -> if (x == 1) -1 to 0 else 0 to -1
        else -> if (x == 1) 1 to 0 else 0 to -1
    }
}

