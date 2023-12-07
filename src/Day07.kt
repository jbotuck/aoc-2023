import kotlin.math.pow

fun main() {
    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}

private enum class CardType {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

private fun part1(input: List<String>): Int {
    val labels = "23456789TJQKA".withIndex().associate { (index, value) -> value to index }
    val base = labels.size.toDouble()

    data class Hand(val s: String) : Comparable<Hand> {
        override fun compareTo(other: Hand): Int {
            return strength.compareTo(other.strength)
        }

        private val strength by lazy {
            s.reversed().withIndex()
                .sumOf { (index, c) -> base.pow(index) * labels[c]!! }
                .plus(base.pow(5) * type.ordinal)
        }

        private val type by lazy {
            val frequencies = s.groupBy { it }.mapValues { it.value.size }.values
            when (frequencies.size) {
                5 -> CardType.HIGH_CARD
                1 -> CardType.FIVE_OF_A_KIND
                4 -> CardType.ONE_PAIR
                3 -> if (3 in frequencies) CardType.THREE_OF_A_KIND else CardType.TWO_PAIR
                else -> if (4 in frequencies) CardType.FOUR_OF_A_KIND else CardType.FULL_HOUSE
            }
        }
    }

    return input
        .map {
            val (part1, part2) = it.split(' ')
            Hand(part1) to part2.toInt()
        }.sortedBy { it.first }
        .mapIndexed { index, pair -> index.inc() * pair.second }
        .sum()
}

private fun part2(input: List<String>): Int {
    val labels = "J23456789TQKA".withIndex().associate { (index, value) -> value to index }
    val base = labels.size.toDouble()

    data class Hand(val s: String) : Comparable<Hand> {
        override fun compareTo(other: Hand): Int {
            return strength.compareTo(other.strength)
        }

        private val strength by lazy {
            s.reversed().withIndex()
                .sumOf { (index, c) -> base.pow(index) * labels[c]!! }
                .plus(base.pow(5) * type.ordinal)
        }

        private val type by lazy {
            val frequencies = s.groupBy { it }.mapValues { it.value.size }
            when (frequencies.size) {
                5 -> if ('J' !in frequencies.keys) CardType.HIGH_CARD else CardType.ONE_PAIR
                1 -> CardType.FIVE_OF_A_KIND
                4 -> if ('J' !in frequencies.keys) CardType.ONE_PAIR else CardType.THREE_OF_A_KIND
                2 -> when {
                    'J' in frequencies.keys -> CardType.FIVE_OF_A_KIND
                    4 in frequencies.values -> CardType.FOUR_OF_A_KIND
                    else -> CardType.FULL_HOUSE
                }
                else -> when{
                    frequencies['J']?.let { it > 1} == true -> CardType.FOUR_OF_A_KIND
                    'J' in frequencies.keys -> if(3 in frequencies.values) CardType.FOUR_OF_A_KIND else CardType.FULL_HOUSE
                    3 in frequencies.values -> CardType.THREE_OF_A_KIND
                    else -> CardType.TWO_PAIR
                }
            }
        }
    }

    return input
        .map {
            val (part1, part2) = it.split(' ')
            Hand(part1) to part2.toInt()
        }.sortedBy { it.first }
        .mapIndexed { index, pair -> index.inc() * pair.second }
        .sum()
}


