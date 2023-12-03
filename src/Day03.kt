fun main() {
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}


private fun part1(input: List<String>): Int {
    fun isAdjacentToSymbol(y: Int, xRange: IntRange): Boolean {
        @Suppress("NAME_SHADOWING") val xRange = xRange.first.dec()..xRange.last.inc()
        return xRange.asSequence().map { input.getOrNull(y.dec())?.getOrNull(it) }
            .plus(xRange.asSequence().map { input.getOrNull(y.inc())?.getOrNull(it) })
            .plus(input[y].getOrNull(xRange.first))
            .plus(input[y].getOrNull(xRange.last))
            .any { it != null && it != '.' && !it.isDigit() }
    }

    fun sumPartNumbers(y: Int): Int {
        return sequence {
            var start: Int? = null
            for ((x, c) in input[y].withIndex()) {
                when {
                    c.isDigit() && start == null -> start = x
                    !c.isDigit() && start != null -> {
                        if (isAdjacentToSymbol(y, start until x)) yield(input[y].slice(start until x).toInt())
                        start = null
                    }
                }
            }
            if (start != null && isAdjacentToSymbol(y, start until input[y].length))
                yield(input[y].slice(start until input[y].length).toInt())
        }.sum()
    }
    return input.indices.sumOf { sumPartNumbers(it) }
}

private fun part2(input: List<String>): Int {
    fun gearRatio(y: Int, x: Int) = sequence{
        for(y in y.dec()..y.inc()){
            yieldAll(input.getOrNull(y)?.numbersNear(x).orEmpty())
        }
    }.take(3)
        .toList()
        .takeIf { it.size == 2 }
        ?.let { it.first() * it.last() }
        ?: 0

    return input.indices.asSequence()
        .flatMap { y -> input[y].indices.map { y to it } }
        .filter { (y, x) -> input[y][x] == '*' }
        .sumOf { (y, x) -> gearRatio(y, x) }
}

private fun String.numbersNear(i: Int): List<Int> {
    val preIStart = i.dec().downTo(0).asSequence().takeWhile { this[it].isDigit() }.lastOrNull()
    val postIEnd = i.inc().rangeUntil(length).asSequence().takeWhile { get(it).isDigit() }.lastOrNull()
    if(get(i).isDigit()) return listOf(slice((preIStart ?: i)..(postIEnd ?: i)).toInt())
    return sequence {
        if(preIStart != null) yield(slice(preIStart..i.dec()).toInt())
        if(postIEnd != null) yield(slice(i.inc()..postIEnd).toInt())
    }.toList()
}
