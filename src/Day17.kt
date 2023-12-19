import java.util.Comparator
import java.util.PriorityQueue

fun main() {
    val blocks = readInput("Day17")
    val destination = blocks.lastIndex to blocks.last().lastIndex
    //PART 1
    run {
        val toVisit = PriorityQueue<Pair<PathSegment, Int>>(Comparator.comparing { (_, heatLoss) -> heatLoss })
        toVisit.add(PathSegment(0, 0, null, 0) to 0)
        val visited = mutableSetOf<PathSegment>()
        fun visit(pathSegment: PathSegment, heatLoss: Int) {
            if (pathSegment in visited) return
            visited.add(pathSegment)
            val nextDirections = Direction.entries.toMutableSet()
            pathSegment.lastDirection?.let { nextDirections.remove(it.reverse()) }
            toVisit.addAll(
                nextDirections.map {
                    PathSegment(
                        pathSegment.y + it.y,
                        pathSegment.x + it.x,
                        it,
                        if (it == pathSegment.lastDirection) pathSegment.stepsTakenInDirection.inc() else 1
                    )
                }.filter { it.stepsTakenInDirection <= 3 }
                    .filter { it.y in blocks.indices && it.x in blocks[it.y].indices }
                    .map { it to blocks[it.y][it.x].digitToInt() + heatLoss }
            )
        }
        while (toVisit.isNotEmpty()) {
            val (next, nextHeatLoss) = toVisit.remove()
            if (Pair(next.y, next.x) == destination) {
                println(nextHeatLoss)
                break
            }
            visit(next, nextHeatLoss)
        }
    }
    //part 2
    run {
        val toVisit = PriorityQueue<Pair<PathSegment, Int>>(Comparator.comparing { (_, heatLoss) -> heatLoss })
        toVisit.add(PathSegment(0, 4, Direction.EAST, 4) to 1.rangeTo(4).sumOf { blocks[0][it].digitToInt() })
        toVisit.add(PathSegment(4, 0, Direction.SOUTH, 4) to 1.rangeTo(4).sumOf { blocks[it][0].digitToInt() })

        val visited = mutableSetOf<PathSegment>()
        fun PathSegment.turn(direction: Direction): Pair<PathSegment, Int>? {
            val loss = 1.rangeTo(4).sumOf {
                blocks.getOrNull(y + direction.y * it)?.getOrNull(x + direction.x * it)?.digitToInt() ?: return null
            }
            return PathSegment(y + direction.y * 4, x + direction.x * 4, direction, 4) to loss
        }

        fun visit(pathSegment: PathSegment, heatLoss: Int) {
            if (pathSegment in visited) return
            visited.add(pathSegment)
            val turns = Direction.entries - setOf(pathSegment.lastDirection!!, pathSegment.lastDirection.reverse())
            toVisit.addAll(
                turns.mapNotNull { pathSegment.turn(it)?.let { (segment, newLoss) -> segment to newLoss + heatLoss } }
            )
            if (pathSegment.stepsTakenInDirection < 10) {
                PathSegment(
                    pathSegment.y + pathSegment.lastDirection.y,
                    pathSegment.x + pathSegment.lastDirection.x,
                    pathSegment.lastDirection,
                    pathSegment.stepsTakenInDirection.inc()
                ).takeIf { it.y in blocks.indices && it.x in blocks[it.y].indices }
                    ?.let { toVisit.add(Pair(it, heatLoss + blocks[it.y][it.x].digitToInt())) }
            }
        }
        while (toVisit.isNotEmpty()) {
            val (next, nextHeatLoss) = toVisit.remove()
            if (Pair(next.y, next.x) == destination) {
                println(nextHeatLoss)
                break
            }
            visit(next, nextHeatLoss)
        }
    }

}

private data class PathSegment(
    val y: Int,
    val x: Int,
    val lastDirection: Direction?,
    val stepsTakenInDirection: Int,
)

private enum class Direction(val y: Int, val x: Int) {
    NORTH(-1, 0),
    SOUTH(1, 0),
    EAST(0, 1),
    WEST(0, -1);

    fun reverse() = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        WEST -> EAST
        EAST -> WEST
    }
}