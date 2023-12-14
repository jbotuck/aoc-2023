fun main() {
    val input = readInputAndChunkByBlankLines("Day13")

    input.sumOf {
        patternSummary(it) ?: throw IllegalArgumentException()
    }.println()

    input.sumOf {
        smudgeSummary(it)
    }.println()
}

private fun smudgeSummary(pattern: List<String>): Int {
    val current = patternSummary(pattern) ?: throw IllegalArgumentException()
    return pattern.indices.asSequence()
        .flatMap { y -> pattern[y].indices.asSequence().map { x -> y to x } }
        .firstNotNullOf { (y, x) ->
            smudgeSummary(pattern, y, x, current)
        }
}

private fun smudgeSummary(pattern: List<String>, y: Int, x: Int, current: Int) = patternSummary(pattern.flip(y, x), current)

private fun List<String>.flip(y: Int, x: Int) = mapIndexed { i, s ->
    if (i != y) s else s.replaceRange(x..x, "${if (s[x] == '#') '.' else '#'}")
}

private fun patternSummary(pattern: List<String>, current: Int? = null): Int? {
    val yExclude = current?.takeIf { it % 100 == 0 }?.let { it / 100 }
    val xExclude = current?.takeIf { it % 100 != 0 }
    return 1.rangeTo(pattern.lastIndex)
        .firstOrNull { yExclude != it && pattern.isReflectedAlongHorizontal(it) }
        ?.let { it * 100 }
        ?: 1.rangeTo(pattern.first().lastIndex)
            .firstOrNull {xExclude != it && pattern.isReflectedAlongVertical(it) }
}


private fun List<String>.isReflectedAlongHorizontal(y: Int) = first()
    .indices
    .map { x -> this.map { it[x] } }
    .all { it.isReflectedAlong(y) }

private fun List<String>.isReflectedAlongVertical(x: Int) = all {
    it.toList().isReflectedAlong(x)
}

private fun List<Char>.isReflectedAlong(i: Int): Boolean {
    val stack = ArrayDeque(
        slice((i - (size - i)).coerceAtLeast(0) until i)
    )

    @Suppress("NAME_SHADOWING") var i = i
    while (i < size && stack.isNotEmpty()) {
        if (stack.removeLast() != get(i)) return false
        i++
    }
    return true
}




