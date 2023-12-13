fun main() {
    val initialInput = readInput("Day12")
        .map { it.split(' ') }
        .map { (line, quantities) ->
            line to quantities
                .split(',')
                .map { it.toInt() }
        }
    initialInput.sumOf { (line, quantities) ->
        arrangementCount(line, quantities)
    }.println()
    initialInput
        .map { (line, quantities) -> "$line?$line?$line?$line?$line" to quantities + quantities + quantities + quantities + quantities }
        .sumOf { (line, quantities) ->
            arrangementCount(line, quantities)
        }.println()
}

fun arrangementCount(line: String, quantities: List<Int>): Long {
    val arrangementCount = mutableMapOf<Pair<Int, Int>, Long>()
    fun arrangementCount(lineIndex: Int = 0, quantityIndex: Int = 0): Long {
        arrangementCount[lineIndex to quantityIndex]?.let { return it }
        return run {
            val quantity = quantities.getOrNull(quantityIndex)
                ?: return@run if (
                    lineIndex
                        .rangeTo(line.lastIndex)
                        .any { i -> line.getOrNull(i)?.let { it == '#' } == true }
                ) 0L else 1
            val endSearchInclusive = line
                .indexOf(char = '#', startIndex = lineIndex)
                .takeIf { it != -1 }
                .let { it ?: line.lastIndex }
                .coerceAtMost(line.length - quantity)
            lineIndex.rangeTo(endSearchInclusive).sumOf { start ->
                val candidateLocation = line.slice(start until start + quantity)
                if (candidateLocation.contains('.')) return@sumOf 0
                if (line.getOrNull(start + quantity)?.let { it == '#' } == true) return@sumOf 0
                arrangementCount(start + quantity.inc(), quantityIndex.inc())
            }
        }.also {
            arrangementCount[lineIndex to quantityIndex] = it
        }
    }
    return arrangementCount()
}
