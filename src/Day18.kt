import kotlin.math.absoluteValue

fun main() {
    val instructions1 = readInput("Day18")
        .map {
            it.split(" ")
                .take(2)
                .let { (direction, count) -> direction.first() to count.toLong() }
        }
    solve(instructions1)
    solve(
        readInput("Day18")
            .map { line ->
                val color = line.substringAfterLast(" ")
                    .removePrefix("(#")
                    .removeSuffix(")")
                val direction = when(color.last().digitToInt()){
                    0 -> 'R'
                    1 -> 'D'
                    2 -> 'L'
                    3 -> 'U'
                    else -> throw IllegalArgumentException()
                }
                direction to color.dropLast(1).toLong(16)
            }
    )
}
private fun solve(instructions: List<Pair<Char, Long>>){
    var current = 0L
    var area = 0L
    var perimeter = 0L
    for ((direction, count) in instructions) {
        perimeter += count
        when (direction) {
            'U' -> area += count * current
            'D' -> area -= count * current
            'L' -> current -= count
            'R' -> current += count
        }
    }
    println(perimeter / 2 + area.absoluteValue.inc())
}