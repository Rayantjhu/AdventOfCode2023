import Day11.part1
import Day11.part2
import kotlin.math.abs

fun main() {
    val input = readInputLines("Day11")

    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day11 {
    /*
        It is a pretty easy problem where we need to find the distance for each pair of galaxies. One small edge-case:
        each row/column that does not contain any galaxies should be counted twice.

        One way to fix this is to simply go through each row and column and add another row or column if it does not
        contain a galaxy. However, as we need to first check for each row if it contains any galaxy, if not add it and
        do the same for each column, which is even more complicated, which seems very inefficient.

        Instead, we will keep track of all rows and columns that do not contain galaxies. Then when we find the shortest
        distance for the pair of galaxies and check if within the galaxies is one or more empty rows and count it up to
        the distance.

        Calculating the distance itself is done using the Manhattan distance. It takes the absolute value of difference
        between x1 and x2 and y1 and y2 and adds these up. The formula is as such: distance = |x1 - x2| + |y1 - y2|
     */
    fun part1(input: List<String>): Int {
        // Find all empty rows
        val emptyRowIndices = input.mapIndexedNotNull { i, row -> if (row.contains('#')) null else i }

        val lastRow = input.lastIndex
        val lastColumn = input.first().lastIndex
        // Find all empty columns
        val emptyColIndices = (0..lastColumn).mapNotNull { columnIndex ->
            // Check for each row if the current column contains a galaxy
            for (rowIndex in 0..lastRow) if (input[rowIndex][columnIndex] == '#') return@mapNotNull null
            // Return the column index, because it does not contain a galaxy
            columnIndex
        }

        return input
            // Find all the points for each galaxy
            .mapIndexedNotNull { rowIndex, row ->
                if (!row.contains('#')) return@mapIndexedNotNull null

                row.mapIndexedNotNull { columnIndex, char -> if (char == '.') null else Pair(rowIndex, columnIndex) }
            }
            // Flatten to 1 list and not a list containing lists containing pairs
            .flatten()
            // Get the combinations of each galaxy
            .combinations()
            // Calculate the sum of the distances for each pair of galaxies
            .sumOf { (f, s) ->
                // Calculate the number of empty rows and columns that need to be included
                val emptyRows = emptyRowIndices.count { it in minOf(f.first, s.first)..maxOf(f.first, s.first) }
                val emptyCols = emptyColIndices.count { it in minOf(f.second, s.second)..maxOf(f.second, s.second) }
                // Calculate the Manhattan distance (with the empty rows/cols):
                // |x1 - x2| + emptyRows + |y1 - y2| + emptyCols
                abs(f.first - s.first) + emptyRows + abs(f.second - s.second) + emptyCols
            }
    }

    fun <T> List<T>.combinations() = flatMapIndexed { i, outer -> drop(i + 1).map { inner -> Pair(outer, inner) } }

    /*
        This time an empty row or column is not just one more larger, but 1 million times rows/columns larger.
     */
    fun part2(input: List<String>): Long {
        val emptyRowIndices = input.mapIndexedNotNull { i, row -> if (row.contains('#')) null else i }

        val lastRow = input.lastIndex
        val lastColumn = input.first().lastIndex
        val emptyColIndices = (0..lastColumn).mapNotNull { columnIndex ->
            for (rowIndex in 0..lastRow) if (input[rowIndex][columnIndex] == '#') return@mapNotNull null
            columnIndex
        }

        return input
            .mapIndexedNotNull { rowIndex, row ->
                if (!row.contains('#')) return@mapIndexedNotNull null
                row.mapIndexedNotNull { columnIndex, char -> if (char == '.') null else Pair(rowIndex, columnIndex) }
            }
            .flatten()
            .combinations()
            .sumOf { (f, s) ->
                val emptyRows = emptyRowIndices.count { it in minOf(f.first, s.first)..maxOf(f.first, s.first) }.toLong()
                val emptyCols = emptyColIndices.count { it in minOf(f.second, s.second)..maxOf(f.second, s.second) }.toLong()
                // Here is the key difference, in that we multiply the rows/cols by 999.999. Not by 1 million because we
                // don't need to multiply the empty row/col itself.
                abs(f.first - s.first) + emptyRows * 999_999 + abs(f.second - s.second) + emptyCols * 999_999
            }
    }
}