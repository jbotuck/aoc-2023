import kotlin.time.measureTime

fun main() {
    val input = readInput("Day06")
//    part1(input).println()
//    part2(input).println()
    val race = input
        .map { line -> line.filter { it.isDigit() }.toLong() }
        .let { Race(it.first(), it.last()) }
    measureTime {
        race
            .numberOfWaysToWin()
            .also { println(it) }
    }.let { println("brute force done in $it") }
    measureTime {
        race
            .numberOfWaysToWinBs()
            .also { println(it) }
    }.let { println("binary search done in $it") }
    measureTime {
        race
            .numberOfWaysToWinQuadratic()
            .also { println(it) }
    }.let { println("quadratic done in $it") }

}

private data class Race(val time: Long, val record: Long) {
    fun numberOfWaysToWin(): Int {
        return 1L.rangeUntil(time).count { distance(it) > record }
    }

    fun numberOfWaysToWinQuadratic(): Int {
        //(time - buttonLength) * buttonLength > record
        //buttonLength^2 - time*buttonLength + record < 0
        measureTime {
            val b = -time
            val c = record
            val discriminant = b * b - 4 * c
            val positiveRoot = kotlin.math.sqrt(discriminant.toDouble())
            val negativeRoot = -positiveRoot
            val lowerBound = kotlin.math.ceil((negativeRoot - b) / 2).toLong()
            val upperBound = ((positiveRoot - b) / 2).toLong()
            return (upperBound.inc() - lowerBound).toInt()
        }
    }

    private fun distance(buttonLength: Long) = (time - buttonLength) * buttonLength
    fun numberOfWaysToWinBs(): Long {
        var r = time / 2
        var l = 0L
        fun ok(buttonLength: Long) = distance(buttonLength) > record
        while(r - l > 1){
            val m = (l + r) / 2
            if (ok(m)){
                r = m
            } else {
                l = m
            }
        }
        return time.dec() - (r.dec() * 2)
    }
}


private fun part1(input: List<String>): Int {
    val races = input.map { line ->
        line
            .split(' ')
            .filter { it.isNotBlank() }
            .drop(1)
            .map { it.toLong() }
    }.let { it.first().zip(it.last()) }
        .map { (time, distance) -> Race(time, distance) }
    return races.map { it.numberOfWaysToWin() }.reduce(Int::times)
}

private fun part2(input: List<String>) = input
    .map { line -> line.filter { it.isDigit() }.toLong() }
    .let { Race(it.first(), it.last()) }
    .numberOfWaysToWinQuadratic()


