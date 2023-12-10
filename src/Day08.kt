
fun main() {
    val input = readInput("Day08")
    val directions = input.first()
    val nodes = input.drop(2).map {
        Node(
            id = it.take(3),
            left = it.substringAfter('(').take(3),
            right = it.substringAfter(',').trim().take(3)
        )
    }.associateBy { it.id }
    sequence {
        var currentNode = nodes["AAA"]!!
        var nextDirection = 0
        while (currentNode.id != "ZZZ") {
            currentNode = nodes[currentNode.nextNodeId(directions[nextDirection++])]!!
            yield(currentNode)
            if (nextDirection == directions.length) nextDirection = 0
        }
    }.count().let { println(it) }

}

data class Node(val id: String, val left: String, val right: String) {
    fun nextNodeId(direction: Char) = if (direction == 'L') left else right
}
