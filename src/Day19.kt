fun main() {
    val (workflows, parts) = parseInput(readInput("Day19"))
    run {
        //part 1
        fun executeWorkFlow(workflow: String, part: Part): Boolean = when (workflow) {
            "A" -> true
            "R" -> false
            else -> {
                executeWorkFlow(workflows[workflow]!!.execute(part), part)
            }
        }
        parts.filter {
            executeWorkFlow("in", it)
        }.sumOf { it.totalRating() }.println()
    }
    fun acceptedCombinations(partConstraint: PartConstraint, workflow: String): Long {
        return when (workflow) {
            "R" -> 0
            "A" -> partConstraint.combinations()
            else -> {
                val rules = ArrayDeque(workflows[workflow]!!.rules)
                var remainingConstraint: PartConstraint? = partConstraint
                generateSequence {
                    if (remainingConstraint == null) return@generateSequence null
                    rules.removeFirstOrNull()?.let { rule ->
                        val (trueConstraint, falseConstraint) = remainingConstraint!!.partition(rule)
                        remainingConstraint = falseConstraint
                        trueConstraint?.let { acceptedCombinations(it, rule.workflow) } ?: 0
                    }
                }.sum()
            }
        }
    }
    acceptedCombinations(PartConstraint(), "in").println()
}

private fun parseInput(lines: List<String>): Pair<Map<String, Workflow>, List<Part>> {
    fun String.toWorkflow(): Workflow {
        fun String.toRule(): Rule {
            val (filterLine, workflow) = split(':')
                .takeIf { it.size == 2 }
                ?: return Rule('x', '>', 0, this)
            val (category, testValue) = filterLine.split('<', '>')
            return Rule(category.first(), filterLine.first { it in setOf('<', '>') }, testValue.toInt(), workflow)
        }
        val (id, rulesLine) = removeSuffix("}")
            .split('{')
        return Workflow(id, rulesLine.split(',').map { it.toRule() })
    }

    fun String.toPart(): Part {
        return Part(
            removePrefix("{")
                .removeSuffix("}")
                .split(',')
                .associate { it.first() to it.drop(2).toInt() }
        )
    }

    @Suppress("NAME_SHADOWING")
    val lines = ArrayDeque(lines)
    return Pair(
        generateSequence {
            lines.removeFirst()
                .takeIf { it.isNotBlank() }
                ?.toWorkflow()
        }.associateBy { it.id },
        lines.map { it.toPart() }
    )

}

private data class Part(val categories: Map<Char, Int>) {
    fun category(key: Char) = categories[key]!!
    fun totalRating(): Int {
        return categories.values.sum()
    }
}

private data class PartConstraint(
    val categories: Map<Char, IntRange> = listOf(
        'x',
        'm',
        'a',
        's'
    ).associateWith { 1..4000 }
) {
    fun combinations() = categories.values
        .map { it.last.inc() - it.first.toLong() }
        .reduce(Long::times)

    fun partition(rule: Rule): Pair<PartConstraint?, PartConstraint?> {
        val currentRange = categories[rule.category]!!
        val (trueRange, falseRange) = rule.testValue.let {
            if (rule.comparisonOperator == '>') {
                currentRange.portionOver(it) to currentRange.portionUnder(it.inc())
            } else { // <
                currentRange.portionUnder(it) to currentRange.portionOver(it.dec())
            }
        }
        return trueRange?.let { replaceCategory(rule.category, it) } to falseRange?.let {
            replaceCategory(
                rule.category,
                it
            )
        }
    }

    private fun replaceCategory(category: Char, range: IntRange): PartConstraint {
        return PartConstraint(categories + mapOf(category to range))
    }
}

private data class Workflow(val id: String, val rules: List<Rule>) {
    fun execute(part: Part) = rules.first { it.filter(part) }.workflow
}

private data class Rule(val category: Char, val comparisonOperator: Char, val testValue: Int, val workflow: String) {
    fun filter(part: Part) = part.category(category).let { value ->
        if (comparisonOperator == '<') value < testValue
        else value > testValue
    }
}

private fun IntRange.portionUnder(max: Int): IntRange? {
    if (max <= first) return null
    return first..last.coerceAtMost(max.dec())
}

private fun IntRange.portionOver(min: Int): IntRange? {
    if (min >= last) return null
    return first.coerceAtLeast(min.inc())..last
}
