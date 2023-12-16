import Day12.part1
import Day12.part2

fun main() {
    val input = readInputLines("Day12")

    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day12 {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val (record, groups) = parseLine(line)
            depthFirstSearch(record, groups)
        }
    }

    fun parseLine(line: String): Pair<String, List<Int>> =
        // The record is surrounded by dots to remove any edge cases where we need to place hashes on the edges
        line.split(' ').let { tokens -> return Pair(".${tokens.first()}.", tokens.last().split(',').map { it.toInt() }) }

    fun depthFirstSearch(record: String, groups: List<Int>): Int {
        // If the group is empty and there are still damaged springs left, it is not a possible arrangement
        if (groups.isEmpty()) return if ('#' in record) 0 else 1

        // Take the first size and create a new list with the first group removed
        val size = groups.first()
        val newGroups = groups.drop(1)

        var count = 0
        for (end in 0..record.lastIndex) {
            val start = end - (size - 1)

            if (fits(record, start, end)) count += depthFirstSearch(record.drop(end + 1), newGroups)
        }

        return count
    }

    fun fits(record: String, start: Int, end: Int): Boolean {
        // Check the bounds
        if (start - 1 < 0 || end + 1 >= record.length) return false

        // Segment can be surrounded by functional springs
        if (record[start - 1] == '#' || record[end + 1] == '#') return false

        // Every damaged pipe needs to be a part of the group
        if ('#' in record.take(start)) return false

        // Check if segment contains any functional pipes
        for (i in start..end) if (record[i] == '.') return false

        return true
    }

    val MEMO = mutableMapOf<Pair<String, List<Int>>, Long>()

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            val (record, groups) = parseLinePart2(line)
            dfsWithMemoization(record, groups)
        }
    }

    fun parseLinePart2(line: String): Pair<String, List<Int>> {
        val (r, g) = line.split(' ')

        val record = "$r?".repeat(5).dropLast(1)
        val groups = "$g,".repeat(5).split(',').filter { it.isNotBlank() }.map { it.toInt() }

        return Pair(record, groups)
    }

    fun dfsWithMemoization(record: String, groups: List<Int>): Long {
        if (groups.isEmpty()) return if ('#' in record) 0 else 1
        if (record.isEmpty()) return 0

        return MEMO.getOrPut(record to groups) {
            var count = 0L
            if (record.first() in ".?") count += dfsWithMemoization(record.drop(1), groups)
            if (record.first() in "#?" && groups.first() <= record.length && '.' !in record.take(groups.first()) && (groups.first() == record.length || record[groups.first()] != '#'))
                count += dfsWithMemoization(record.drop(groups.first() + 1), groups.drop(1))
            count
        }
    }
}