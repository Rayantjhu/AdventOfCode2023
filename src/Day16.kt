import Day16.part1
import Day16.part2
import java.util.*

fun main() {
    val input = readInputLines("Day16")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day16 {
    val SLASH_DIRECTIONS = mapOf(
        'L' to 'D',
        'R' to 'U',
        'U' to 'R',
        'D' to 'L',
    )

    val BACKSLASH_DIRECTIONS = mapOf(
        'L' to 'U',
        'R' to 'D',
        'U' to 'L',
        'D' to 'R',
    )

    fun part1(input: List<String>): Int {
        return calculateEnergizedTiles(input, Triple(0, 0, 'R'))
    }

    fun calculateEnergizedTiles(input: List<String>, start: Triple<Int, Int, Char>): Int {
        val visited = mutableSetOf(start)

        val queue: Queue<Triple<Int, Int, Char>> = LinkedList()
        queue.add(start)

        while (queue.isNotEmpty()) {
            val (first, second) = traverse(input, queue.poll())

            if (first != null && first !in visited && !splitterAlreadyVisited(input, first, visited)) {
                queue.add(first)
                visited.add(first)
            }
            if (second != null && second !in visited && !splitterAlreadyVisited(input, second, visited)) {
                queue.add(second)
                visited.add(second)
            }
        }

        return visited.distinctBy { it.first to it.second }.size
    }

    fun traverse(
        input: List<String>,
        current: Triple<Int, Int, Char>
    ): Pair<Triple<Int, Int, Char>?, Triple<Int, Int, Char>?> {
        val (row, col, direction) = current

        val newDirection = when (input[row][col]) {
            '.' -> direction to null
            '-' -> if (direction in "UD") 'L' to 'R' else direction to null
            '|' -> if (direction in "LR") 'U' to 'D' else direction to null
            '/' -> SLASH_DIRECTIONS[direction] to null
            '\\' -> BACKSLASH_DIRECTIONS[direction] to null
            else -> throw IllegalArgumentException("Unknown tile: ${input[row][col]}")
        }

        return nextTile(input, row, col, newDirection.first) to nextTile(input, row, col, newDirection.second)
    }

    fun nextTile(input: List<String>, row: Int, col: Int, direction: Char?): Triple<Int, Int, Char>? {
        if (direction == null) return null

        val (newRow, newCol) = when (direction) {
            'L' -> if (input[row].getOrNull(col - 1) == null) null to null else row to col - 1
            'R' -> if (input[row].getOrNull(col + 1) == null) null to null else row to col + 1
            'U' -> if (input.getOrNull(row - 1)?.get(col) == null) null to null else row - 1 to col
            'D' -> if (input.getOrNull(row + 1)?.get(col) == null) null to null else row + 1 to col
            else -> throw IllegalArgumentException("Unknown direction used: $direction")
        }

        return Triple(newRow ?: return null, newCol ?: return null, direction)
    }

    fun splitterAlreadyVisited(
        input: List<String>,
        tile: Triple<Int, Int, Char>,
        visited: Set<Triple<Int, Int, Char>>
    ) =
        input[tile.first][tile.second] in "|-" && visited.any { it.first == tile.first && it.second == tile.second }


    fun part2(input: List<String>): Int {
        var max = 0

        for (row in 0..input.lastIndex) {
            max = maxOf(max, calculateEnergizedTiles(input, Triple(row, 0, 'R')))
            max = maxOf(max, calculateEnergizedTiles(input, Triple(row, input.lastIndex, 'L')))
        }

        for (col in 0..input.first().lastIndex) {
            max = maxOf(max, calculateEnergizedTiles(input, Triple(0, col, 'D')))
            max = maxOf(max, calculateEnergizedTiles(input, Triple(input.lastIndex, col, 'U')))
        }

        return max
    }
}