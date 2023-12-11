import kotlin.math.abs

fun main() {
    val input = readInput("Day11")
    solve(input)
    solve(input, 999_999)
}
fun solve(input: List<String>, shift: Int = 1){
    val galaxies = input.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            c.takeIf { it == '#' }?.let { Galaxy(y, x) }
        }
    }
    //perform x shift
    run {
        val galaxiesByX = galaxies.groupBy { it.x }.toSortedMap()
        for (i in input.first().indices) {
            if (input.all { it[i] == '.' }) {
                for (galaxy in galaxiesByX.tailMap(i).values.flatten()) galaxy.shiftX(shift)
            }
        }
    }
    //perform y shift
    run {
        val galaxiesByY = galaxies.groupBy { it.y }.toSortedMap()
        for (i in input.indices) {
            if (input[i].all { it == '.' }) {
                for (galaxy in galaxiesByY.tailMap(i).values.flatten()) galaxy.shiftY(shift)
            }
        }
    }
    val xDiff = galaxies.indices.sumOf { i ->
        galaxies.asSequence().drop(i.inc()).sumOf { abs(galaxies[i].shiftedX() - it.shiftedX()) }
    }
    val yDiff = galaxies.indices.sumOf { i ->
        galaxies.asSequence().drop(i.inc()).sumOf { abs(galaxies[i].shiftedY() - it.shiftedY()) }
    }
    println(xDiff + yDiff)
}

private data class Galaxy(val y: Int, val x: Int) {
    private var xShift: Long = 0
    private var yShift: Long = 0
    fun shiftedY() = y + yShift
    fun shiftedX() = x + xShift

    fun shiftY(shift: Int) {
        yShift += shift
    }

    fun shiftX(shift: Int) {
        xShift += shift
    }
}