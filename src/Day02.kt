import Color.RED
import Color.GREEN
import Color.BLUE

fun main() {
    val input = readInput("Day02").map { it.toGame() }
    part1(input).println()
    part2(input).println()
}

private data class Game(val id: Int, val rounds: List<Round>) {
    fun isPossibleGivenMaximums(maximums: Map<Color, Int>) = rounds.all { it.isPossibleGivenMaximums(maximums) }
    fun power(): Int = Color.entries.map { color -> rounds.maxOf { it.getCount(color) } }.reduce(Int::times)
}

private data class Round(val colors: Map<Color, Int>) {
    fun isPossibleGivenMaximums(maximums: Map<Color, Int>) = colors.entries.all { it.value <= maximums[it.key]!! }
    fun getCount(color: Color) = colors[color] ?: 0
}

private enum class Color {
    RED, GREEN, BLUE;
}

private fun String.toGame(): Game {
    val (idPart, roundsPart) = split(':')
    return Game(idPart.extractId(), roundsPart.toRounds())
}

private fun String.toRounds() = split(';').map { it.toRound() }

private fun String.toRound() = Round(split(',').associate { s ->
    val (count, color) = s.trim().split(" ")
    Color.valueOf(color.uppercase()) to count.toInt()
})

private fun String.extractId() = filter { it.isDigit() }.toInt()

private fun part1(input: List<Game>): Int {
    val maximums = mapOf(RED to 12, GREEN to 13, BLUE to 14)
    return input.filter { it.isPossibleGivenMaximums(maximums) }.sumOf { it.id }
}

private fun part2(input: List<Game>): Int {
    return input.sumOf { it.power() }
}