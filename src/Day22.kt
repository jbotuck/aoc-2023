import kotlin.reflect.KProperty1

fun main() {
    val bricks = readInput("Day22").map { line ->
        val (start, end) = line.split('~').map { point ->
            val (x, y, z) = point.split(',').map { it.toInt() }
            Point3d(z, y, x)
        }
        Brick(start, end)
    }.sortedBy { it.minZ() }
    val groundBrick = Brick(start = Point3d(0, -1, -1), end = Point3d(0, -1, -1))
    val grid = run {
        val maxZ = bricks.maxOf { it.maxZ() }
        val maxY = bricks.maxOf { it.maxY() }
        val maxX = bricks.maxOf { it.maxX() }
        Array(maxZ) { z -> Array(maxY.inc()) { Array(maxX.inc()) { groundBrick.takeIf { z == 0 } } } }
    }
    val destroyableBricks = mutableSetOf<Brick>().apply { addAll(bricks) }
    for (brick in bricks) {
        val newZ = brick.point2ds().maxOf { (y, x) ->
            brick.minZ().dec().downTo(0).first { z -> grid[z][y][x] != null }
        }.inc()
        val zShift = brick.minZ() - newZ
        brick.point2ds()
            .mapNotNull { (y, x) -> grid[newZ.dec()][y][x] }
            .toSet()
            .singleOrNull()
            ?.let { destroyableBricks.remove(it) }
        for ((z, y, x) in brick.points()){
            grid[z- zShift][y][x] = brick
        }

    }
    destroyableBricks.count().println()
}

private data class Point3d(val z: Int, val y: Int, val x: Int)

private data class Brick(val start: Point3d, val end: Point3d) {
    fun minZ() = extract(Point3d::z).min()
    fun minY() = extract(Point3d::y).min()

    fun minX() = extract(Point3d::x).min()


    fun maxY() = extract(Point3d::y).max()
    fun maxX() = extract(Point3d::x).max()
    fun maxZ() = extract(Point3d::z).max()
    private fun extract(property: KProperty1<Point3d, Int>) = listOf(start, end).map(property::get)
    fun point2ds(): List<Pair<Int, Int>> {
        return minY().rangeTo(maxY()).flatMap { y -> minX().rangeTo(maxX()).map { y to it } }
    }

    fun points(): List<Point3d> {
        return minZ().rangeTo(maxZ())
            .flatMap { z ->
                minY().rangeTo(maxY())
                    .flatMap { y ->
                        minX().rangeTo(maxX()).map {
                            Point3d(z, y, it)
                        }
                    }
            }
    }

}