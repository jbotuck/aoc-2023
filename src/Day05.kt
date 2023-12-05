import java.util.*
import kotlin.collections.ArrayDeque

fun main() {
    val input = readInput("Day05")
    val seeds = input.first().substringAfter("seeds: ").split(' ').map { it.toLong() }

    val mappings = sequence {
        var index = 3
        while (index < input.size) {
            yield(Mapping(
                sequence {
                    while (input.getOrNull(index)?.isNotBlank() == true) {
                        val (destination, source, length) = input[index++].split(' ').map { it.toLong() }
                        yield(MappingRule(destination, source, length))
                    }
                }.associateBy { it.source }
                    .let { TreeMap(it) }
            ))
            index += 2
        }
    }.toList()
    part1(seeds, mappings).println()
    part2(seeds, mappings).println()
}

private data class Mapping(val mappingRules: TreeMap<Long, MappingRule>) {
    fun map(x: Long): Long {
        return mappingRules.floorEntry(x)
            ?.value
            ?.takeIf { it.source + it.length > x }
            ?.let { it.destination + (x - it.source) }
            ?: x
    }

    fun map(x: List<Pair<Long, Long>>) = x.flatMap { map(it) }

    fun map(x: Pair<Long, Long>) = sequence {
        var (start, length) = x
        val subset = ArrayDeque(mappingRules.subMap(start, start + length).values)
        if (subset.firstOrNull()?.source?.let { it == start } != true) {
            //create a synthetic rule to be processed when we go through the relevant rule subset
            mappingRules.floorEntry(start)
                ?.value
                ?.takeIf { it.source + it.length > start }
                ?.let {
                    val diff = start - it.source
                    subset.addFirst(
                        MappingRule(
                            destination = it.destination + diff,
                            source = it.source + diff,
                            length = it.length - diff
                        )
                    )
                }
        }
        while (subset.isNotEmpty()) {
            val nextRule = subset.removeFirst()
            if (nextRule.source > start) {
                val nextLength = nextRule.source - start
                yield(start to nextLength)
                start += nextLength
                length -= nextLength
            }
            val nextLength = nextRule.length.coerceAtMost(length)
            yield(nextRule.destination to nextLength)
            start += nextLength
            length -= nextLength
        }
        if (length > 0) yield(start to length)
    }.toList()

}

private data class MappingRule(val destination: Long, val source: Long, val length: Long) : Comparable<MappingRule> {
    override fun compareTo(other: MappingRule): Int {
        return source.compareTo(other.source)
    }
}


private fun part1(seeds: List<Long>, mappings: List<Mapping>): Long {
    return seeds.minOf { seed ->
        mappings.fold(seed) { x, mapper -> mapper.map(x) }
    }
}

private fun part2(seeds: List<Long>, mappings: List<Mapping>): Long {
    val seedPairs = seeds.windowed(size = 2, step = 2).map { it.first() to it.last() }
    return mappings.fold(seedPairs) { it, mapper -> mapper.map(it) }.minOf { it.first }
}

