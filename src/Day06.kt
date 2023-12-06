fun main() {
    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

private data class Race(val time: Long, val record: Long) {
    fun numberOfWaysToWin(): Int {
        return 1L.rangeUntil(time).count { distance(it) > record }
    }

    private fun distance(buttonLength: Long) = (time - buttonLength) * buttonLength
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
    .map { line ->
        line.filter { it.isDigit() }.toLong()
    }.let { Race(it.first(), it.last()) }
    .numberOfWaysToWin()


