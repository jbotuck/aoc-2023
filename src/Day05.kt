import java.util.NavigableSet
import java.util.TreeSet

fun main() {
    val input = readInput("Day05")
    val seeds = input.first().substringAfter("seeds: ").split(' ').map { it.toLong() }

    val mappings = sequence {
        var index = 3
        while (index < input.size) {
            yield(Mapping(sequence {
                while (input.getOrNull(index)?.isNotBlank() == true) {
                    val (destination, source, length) = input[index++].split(' ').map { it.toLong() }
                    this.yield(MappingRule(destination, source, length))
                }
            }.let { TreeSet(it.toList()) }))
            index += 2
        }
    }.toList()
    part1(seeds, mappings).println()
    part2(seeds, mappings).println()
}

private data class Mapping(val mappingRules: NavigableSet<MappingRule>) {
    fun map(x: Long): Long {
        return mappingRules.floor(MappingRule( 0, x, 0))
            ?.takeIf { it.source + it.length > x }
            ?.let { it.destination + (x - it.source) }
            ?: x
    }

    fun map(x: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
        return x.flatMap { map(it) }
    }

    fun map(x: Pair<Long, Long>): List<Pair<Long, Long>> = sequence<Pair<Long, Long>> {
        var (start, length) = x
        mappingRules.floor(MappingRule(0,start,0))
            ?.let { it.destination + start - it.source to it.length - (start - it.source)}
            ?.takeIf { it.second > 0 }
            ?.also {
                yield(it)
                start += it.second
                length -= it.second
            }
    }.toList()

        val x = x.first..x.first + x.second.dec()
        return sequence {
            val subSet = mappingRules
                .subSet(
                    MappingRule(0, x.first, 0), true,
                    MappingRule(0, x.last, 0), true
                )

        }.toList()
    }
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

