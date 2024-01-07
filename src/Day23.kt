import Day23.part1
import Day23.part2

fun main() {
    val input = readInputLines("Day23")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day23 {
    val DIRECTIONS = mapOf(
        '^' to listOf(-1 to 0),
        'v' to listOf(1 to 0),
        '<' to listOf(0 to -1),
        '>' to listOf(0 to 1),
        '.' to listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
    )

    fun part1(input: List<String>): Int {
        val start = input.first().indexOfFirst { it == '.' }.let { 0 to it }
        val end = input.last().indexOfFirst { it == '.' }.let { input.lastIndex to it }
        val maxRow = input.lastIndex
        val maxCol = input.first().lastIndex

        val points = mutableListOf(start, end)

        for ((r, row) in input.withIndex()) {
            for ((c, char) in row.withIndex()) {
                if (char == '#') continue

                listOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)
                    .filter { (nr, nc) -> nr in 0..maxRow && nc in 0..maxCol && input[nr][nc] != '#' }
                    .let { if (it.size >= 3) points.add(r to c) }
            }
        }

        val graph = points.associateWith { mutableMapOf<Pair<Int, Int>, Int>() }
        for ((startRow, startCol) in points) {
            val stack = mutableListOf(Triple(0, startRow, startCol))
            val visited = mutableListOf(startRow to startCol)

            while (stack.isNotEmpty()) {
                val (distance, row, col) = stack.removeLast()

                val pos = row to col
                if (distance != 0 && pos in points) {
                    graph[startRow to startCol]?.set(pos, distance)
                    continue
                }

                for ((deltaRow, deltaCol) in DIRECTIONS[input[row][col]]!!) {
                    val newRow = row + deltaRow
                    val newCol = col + deltaCol
                    if (newRow in 0..maxRow && newCol in 0..maxCol && input[newRow][newCol] != '#' && newRow to newCol !in visited) {
                        stack.add(Triple(distance + 1, newRow, newCol))
                        visited.add(newRow to newCol)
                    }
                }
            }
        }

        val visited = mutableSetOf<Pair<Int, Int>>()
        fun dfs(point: Pair<Int, Int>): Int {
            if (point == end) return 0

            var max = Int.MIN_VALUE
            visited.add(point)
            for (nextPoint in graph[point]?.keys ?: listOf())
                if (nextPoint !in visited) max = maxOf(max, dfs(nextPoint) + graph[point]!![nextPoint]!!)
            visited.remove(point)

            return max
        }

        return dfs(start)
    }

    fun part2(input: List<String>): Int {
        val start = input.first().indexOfFirst { it == '.' }.let { 0 to it }
        val end = input.last().indexOfFirst { it == '.' }.let { input.lastIndex to it }
        val maxRow = input.lastIndex
        val maxCol = input.first().lastIndex

        val points = mutableListOf(start, end)

        for ((r, row) in input.withIndex()) {
            for ((c, char) in row.withIndex()) {
                if (char == '#') continue

                listOf(r - 1 to c, r + 1 to c, r to c - 1, r to c + 1)
                    .filter { (nr, nc) -> nr in 0..maxRow && nc in 0..maxCol && input[nr][nc] != '#' }
                    .let { if (it.size >= 3) points.add(r to c) }
            }
        }

        val graph = points.associateWith { mutableMapOf<Pair<Int, Int>, Int>() }
        for ((startRow, startCol) in points) {
            val stack = mutableListOf(Triple(0, startRow, startCol))
            val visited = mutableListOf(startRow to startCol)

            while (stack.isNotEmpty()) {
                val (distance, row, col) = stack.removeLast()

                val pos = row to col
                if (distance != 0 && pos in points) {
                    graph[startRow to startCol]?.set(pos, distance)
                    continue
                }

                for ((deltaRow, deltaCol) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) {
                    val newRow = row + deltaRow
                    val newCol = col + deltaCol
                    if (newRow in 0..maxRow && newCol in 0..maxCol && input[newRow][newCol] != '#' && newRow to newCol !in visited) {
                        stack.add(Triple(distance + 1, newRow, newCol))
                        visited.add(newRow to newCol)
                    }
                }
            }
        }

        val visited = mutableSetOf<Pair<Int, Int>>()
        fun dfs(point: Pair<Int, Int>): Int {
            if (point == end) return 0

            var max = Int.MIN_VALUE
            visited.add(point)
            for (nextPoint in graph[point]?.keys ?: listOf())
                if (nextPoint !in visited) max = maxOf(max, dfs(nextPoint) + graph[point]!![nextPoint]!!)
            visited.remove(point)

            return max
        }

        return dfs(start)
    }
}