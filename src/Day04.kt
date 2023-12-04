import kotlin.math.pow

fun main() {
    val input = readInput("Day04").map { it.toCard() }
    part1(input).println()
    part2(input).println()
}

private data class Card(val id: Int, val winningNumbers: Set<Int>, val actualNumbers: Set<Int>) {
    val numberOfMatches = winningNumbers.intersect(actualNumbers).size
    fun points(): Int {
        if (numberOfMatches == 0) return 0
        return 2.0.pow(numberOfMatches - 1).toInt()
    }
}

private fun String.toCard(): Card {
    val (idPart, numbersPart) = split(':')
    val (winningNumbers, actualNumbers) = numbersPart.toNumberSets()
    return Card(idPart.extractId(), winningNumbers, actualNumbers)
}

private fun String.toNumberSets() = this.split('|').map { it.toNumberSet() }

private fun String.toNumberSet() = split(' ')
    .filter { it.isNotBlank() }
    .map { it.toInt() }
    .toSet()

private fun String.extractId() = filter { it.isDigit() }.toInt()

private fun part1(input: List<Card>) = input.sumOf { it.points() }

private fun part2(input: List<Card>): Int {
    val cards = listOf(Card(0, emptySet(), emptySet())).plus(input).toTypedArray()
    val cardCounts = IntArray(cards.size) { it.takeIf { it == 0 } ?: 1 }
    fun updateCounts(ids: IntRange, amountToAdd: Int) {
        for (id in ids) {
            cardCounts[id] += amountToAdd
        }
    }
    for (id in cardCounts.indices) {
        updateCounts(id.inc()..id + cards[id].numberOfMatches, cardCounts[id])
    }
    return cardCounts.sum()
}

