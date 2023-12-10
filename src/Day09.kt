
fun main() {
    //part 1
    readInput("Day09")
        .sumOf { line ->
            line.split(" ")
                .map { it.toInt() }
                .let { initialSequence ->
                    var currentSequence = initialSequence
                    sequence {
                        while (currentSequence.any { it != 0 }) {
                            yield(currentSequence)
                            currentSequence = currentSequence.windowed(2).map { (a, b) -> b - a }
                        }
                    }.sumOf { it.last() }
                }
        }.let{ println(it) }
    //part 2
    readInput("Day09")
        .sumOf { line ->
            line.split(" ")
                .map { it.toInt() }
                .let { initialSequence ->
                    var currentSequence = initialSequence
                    sequence {
                        while (currentSequence.any { it != 0 }) {
                            yield(currentSequence)
                            currentSequence = currentSequence.windowed(2).map { (a, b) -> b - a }
                        }
                    }.map{it.first()}
                        .toList()
                        .reversed()
                        .reduce{a, b -> b - a}
                }
        }.let{ println(it) }


}
