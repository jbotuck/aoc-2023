fun main() {


    // test if implementation meets criteria from the description, like:
//    val testInput = readInput("Day01_test")
//    check(part1(testInput) == 1)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

private fun part1(input: List<String>): Int {
    return input.sumOf { s ->
        s.filter { it.isDigit() }
            .let { "${it.first()}${it.last()}".toInt() }
    }
}

private fun part2(input: List<String>): Int {
    return input.sumOf { s ->
        "${s.firstDigit()}${s.lastDigit()}".toInt()
    }
}

private val words = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
).mapValues { it.value.toString() }
    .let { alphaMap -> alphaMap + alphaMap.values.associateBy { it } }

private fun String.firstDigit() = findAnyOf(words.keys)!!.numericalDigit()
private fun String.lastDigit() = findLastAnyOf(words.keys)!!.numericalDigit()
private fun Pair<Int, String>.numericalDigit() = second.numericalDigit()
private fun String.numericalDigit() = words[this]