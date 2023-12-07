import kotlin.math.pow

fun main() {
    val input = readInput("Day07")
        .map {
            val (part1, part2) = it.split(' ')
            Hand(part1) to part2.toInt()
        }
    solve(input).println()
    solve(input.map { it.first.copy(wild = true) to it.second }).println()
}

private const val base = 13.0

private val simpleLabels = "23456789TJQKA".withIndex().associate { (index, value) -> value to index }
private val wildLabels = "J23456789TQKA".withIndex().associate { (index, value) -> value to index }

data class Hand(val s: String, val wild: Boolean = false) : Comparable<Hand> {
    private val strength by lazy {
        labelStrength() + typeStrength()
    }

    private fun labelStrength() = labelStrength(if (wild) wildLabels else simpleLabels)

    private fun labelStrength(labels: Map<Char, Int>) = s.reversed().withIndex()
        .sumOf { (index, c) -> base.pow(index) * labels[c]!! }


    private fun typeStrength(): Double {
        return typeStrength(s.groupBy { it }.mapValues { it.value.size })
    }

    private fun typeStrength(frequencies: Map<Char, Int>): Double {
        @Suppress("NAME_SHADOWING") val frequencies = frequencies.toMutableMap()
        if(wild && frequencies.size > 1){
            frequencies.remove('J')?.let { jCount ->
                frequencies.entries.maxBy { it.value }.let { it.setValue(it.value + jCount) }
            }
        }
        val topFrequencies = frequencies.values.sortedDescending()
        return topFrequencies.first() * base.pow(6) + (topFrequencies.getOrNull(1) ?: 0) * base.pow(5)
    }

    override fun compareTo(other: Hand) = strength.compareTo(other.strength)
}

private fun solve(input: List<Pair<Hand, Int>>) = input
    .sortedBy { it.first }
    .mapIndexed { index, pair -> index.inc() * pair.second }
    .sum()