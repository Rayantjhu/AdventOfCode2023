import Day03.part1
import Day03.part2

fun main() {
    val input = readInputLines("Day03")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day03 {
    /*
    Each symbol adjacent to a number (also diagonally) is a number needed for the solution. Instead of looking for
    each number, we can look for each symbol (not a dot nor digit) and then find all adjacent numbers. The only
    problem with this: if a number was adjacent to more than 1 symbol, that number would be counted twice, to
    combat this, each number should be found instead and check if it has any adjacent symbols.
 */
    fun part1(input: List<String>): Int {
        var result = 0

        input.forEachIndexed { i, line ->
            // Find the next number in the current line with its starting and ending indices
            var startIndex: Int? = null
            var endIndex: Int? = null

            for (j in line.indices) {
                // If the current character is a digit and previous was not, set the starting index of this number
                if (line[j].isDigit() && line.getOrNull(j - 1)?.isDigit() != true) startIndex = j
                // If the current character is a digit and next is not, set the ending index of this number
                if (line[j].isDigit() && line.getOrNull(j + 1)?.isDigit() != true) endIndex = j

                // A number has been found, looking for any adjacent symbols
                if (startIndex != null && endIndex != null) {
                    // If the starting index is at not at the start of the line, we can get the adjacent starting index
                    val adjacentStart = if (startIndex == 0) 0 else startIndex - 1
                    // If the ending index is not at the end of the line, we can get the adjacent ending index
                    val adjacentEnd = if (endIndex == line.lastIndex) endIndex else endIndex + 1

                    val previousLineHasSymbol = input
                        // Get the previous line
                        .getOrNull(i - 1)
                        // Take only the part we need to check if it has a symbol
                        ?.substring(adjacentStart, adjacentEnd + 1)
                        // Check if the substring has any symbol
                        ?.any { !it.isDigitOrDot() }
                        // Default to false if the line does not exist
                        ?: false

                    val currentLineHasSymbol = line.getOrNull(adjacentStart)?.isDigitOrDot() != true ||
                            line.getOrNull(adjacentEnd)?.isDigitOrDot() != true

                    val nextLineHasSymbol = input
                        // Get the next line
                        .getOrNull(i + 1)
                        // Take only the part we need to check if it has a symbol
                        ?.substring(adjacentStart, adjacentEnd + 1)
                        // Check if the substring has any symbol
                        ?.any { !it.isDigitOrDot() }
                        // Default to false if the line does not exist
                        ?: false

                    // Add number to the result if any symbol has been found
                    if (previousLineHasSymbol || currentLineHasSymbol || nextLineHasSymbol)
                        result += line.substring(startIndex, endIndex + 1).toInt()

                    // Reset indices
                    startIndex = null
                    endIndex = null
                }
            }
        }

        return result
    }

    /*
        As opposed to the first puzzle, we now need to find the numbers for each gear that is adjacent to exactly 2
        numbers. Instead of looking at all numbers, finding all gears and then checking if it neighbours 2 numbers is
        easier.
     */
    fun part2(input: List<String>): Int {
        var result = 0

        input.forEachIndexed { i, line ->
            // Get all indices for each gear in the current line
            val gearIndices = line.mapIndexedNotNull { index, char -> if (char == '*') index else null }

            gearIndices.forEach { gearIndex ->
                val adjStart = if (gearIndex == 0) 0 else gearIndex - 1
                val adjEnd = if (gearIndex == line.lastIndex) gearIndex else gearIndex + 1

                val previousLineNumberIndices = input
                    // Get the previous line if it exists
                    .getOrNull(i - 1)
                    // Get all indices of characters that are digits and are within the range with its line index
                    ?.mapIndexedNotNull { index, char -> if (char.isDigit() && index >= adjStart && index <= adjEnd) index to i - 1 else null }
                    // Default to an empty list if the line does not exist
                    ?: listOf()

                val currentLineNumberIndices = line
                    // Get all indices of characters that are digits and are within the range with its line index
                    .mapIndexedNotNull { index, char -> if (char.isDigit() && index >= adjStart && index <= adjEnd) index to i else null }

                val nextLineNumberIndices = input
                    // Get the next line if it exists
                    .getOrNull(i + 1)
                    // Get all indices of characters that are digits and are within the range with its line index
                    ?.mapIndexedNotNull { index, char -> if (char.isDigit() && index >= adjStart && index <= adjEnd) index to i + 1 else null }
                    // Default to an empty list if the line does not exist
                    ?: listOf()

                // Find all adjacent numbers with its starting and ending index
                val numbers = (previousLineNumberIndices + currentLineNumberIndices + nextLineNumberIndices).map {
                    var startIndex = it.first
                    var endIndex = it.first

                    // Update the starting index until the previous character is not a digit anymore
                    while (input[it.second].getOrNull(startIndex - 1)?.isDigit() == true) startIndex--

                    // Update the ending index until the next character is not a digit anymore
                    while (input[it.second].getOrNull(endIndex + 1)?.isDigit() == true) endIndex++

                    // Create a range from starting to ending index and get the number according to the range
                    val range = IntRange(startIndex, endIndex)
                    input[it.second].substring(range).toInt() to (range to it.second)
                }
                    // Remove all duplicates with the same range (indices) on the same line
                    .distinctBy { it.second }

                // Only add to the result when there are exactly 2 numbers
                if (numbers.size == 2) result += numbers.fold(1) { acc, pair -> acc * pair.first }
            }
        }

        return result
    }

    /** Helper function to determine whether a character is a digit or a dot. */
    fun Char.isDigitOrDot(): Boolean = this.isDigit() || this == '.'
}
