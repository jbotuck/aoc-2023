fun main() {
    val input = readInput("Day14")
    val grid = Grid(Array(input.size) { y ->
        Array(input[y].length) { x -> input[y][x].toTileContents() }
    })
    //part1
    grid.copy().apply { tiltNorth() }.totalNorthLoad().println()

    //part2
    val turtle = grid.copy()
    val hare = grid.copy()
    do {
        turtle.executeNextStep()
        repeat(2){hare.executeNextStep()}
    }while (!turtle.myEquals(hare))
    val stepsRemaining = 4_000_000_000 % turtle.stepsExecuted
    repeat(stepsRemaining.toInt()){turtle.executeNextStep()}
    turtle.totalNorthLoad().println()
}

private class Grid(private val grid: Array<Array<TileContents>>) {
    var stepsExecuted = 0
    private val stepIterator =
        sequence { while (true) yieldAll(listOf(::tiltNorth, ::tiltWest, ::tiltSouth, ::tiltEast)) }.iterator()
    fun executeNextStep(){
        stepIterator.next()()
        stepsExecuted++
    }
    fun myEquals(otherGrid: Grid) =
        grid.contentDeepEquals(otherGrid.grid) && stepsExecuted % 4 == otherGrid.stepsExecuted % 4

    fun totalNorthLoad() = grid
        .mapIndexed { y, row -> row.count { it == TileContents.ROUND } * grid.size.minus(y) }
        .sum()

    fun tiltNorth() {
        for (y in 1..grid.lastIndex) {
            for (x in grid[y].indices) {
                if (grid[y][x] == TileContents.ROUND) {
                    val newY = y.dec().downTo(0).firstOrNull { grid[it][x] != TileContents.EMPTY }?.inc() ?: 0
                    roll(y to x, newY to x)
                }
            }
        }
    }

    fun tiltWest() {
        for (x in 1..grid.first().lastIndex) {
            for (y in grid.indices) {
                if (grid[y][x] == TileContents.ROUND) {
                    val newX = x.dec().downTo(0).firstOrNull { grid[y][it] != TileContents.EMPTY }?.inc() ?: 0
                    roll(y to x, y to newX)
                }
            }
        }
    }

    fun tiltSouth() {
        for (y in grid.lastIndex.dec() downTo 0) {
            for (x in grid[y].indices) {
                if (grid[y][x] == TileContents.ROUND) {
                    val newY = y.inc().rangeTo(grid.lastIndex).firstOrNull { grid[it][x] != TileContents.EMPTY }?.dec()
                        ?: grid.lastIndex
                    roll(y to x, newY to x)
                }
            }
        }
    }

    fun tiltEast() {
        for (x in grid.first().lastIndex.dec() downTo 0) {
            for (y in grid.indices) {
                if (grid[y][x] == TileContents.ROUND) {
                    val newX =
                        x.inc().rangeTo(grid.first().lastIndex).firstOrNull { grid[y][it] != TileContents.EMPTY }?.dec()
                            ?: grid.first().lastIndex
                    roll(y to x, y to newX)
                }
            }
        }
    }

    private fun Grid.roll(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        if (from == to) return
        grid[from.first][from.second] = TileContents.EMPTY
        grid[to.first][to.second] = TileContents.ROUND
    }

    fun copy() = Grid(Array(grid.size) { y -> grid[y].copyOf() })
}

private fun Char.toTileContents() = when (this) {
    '#' -> TileContents.SQUARE
    'O' -> TileContents.ROUND
    '.' -> TileContents.EMPTY
    else -> throw IllegalArgumentException()
}


private enum class TileContents {
    EMPTY, ROUND, SQUARE
}