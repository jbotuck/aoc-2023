fun main() {
    //part 1
    readInput("Day15")
        .first()
        .split(',')
        .sumOf {
            myHash(it)
        }.println()

    //part2
    val boxes = Array(256) { Box(it.inc()) }
    val operations = setOf('=', '-')
    readInput("Day15")
        .first()
        .split(',')
        .forEach { s ->
            val label = s.takeWhile { it !in operations }
            val box = boxes[myHash(label)]
            s.substring(label.length).let { s ->
                if (s.first() == '-') box.removeLabel(label)
                else box.addLense(label, s.drop(1).toInt())
            }
        }
    boxes.sumOf { it.focusingPower() }.println()
}

private fun myHash(s: String) = s.fold(0) { current, char ->
    current
        .plus(char.code)
        .times(17)
        .mod(256)
}

private class Box(val index: Int) {
    private val map = mutableMapOf<String, Int>()
    fun removeLabel(label: String) {
        map.remove(label)
    }

    fun addLense(label: String, focalLength: Int) {
        map[label] = focalLength
    }

    fun focusingPower() = map.values.mapIndexed { lenseIndex, focalLength ->
        index * lenseIndex.inc() * focalLength
    }.sum()
}