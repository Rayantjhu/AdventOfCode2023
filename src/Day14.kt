import Day14.part1
import Day14.part2

fun main() {
    val input = readInputLines("Day14")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day14 {

    /*
        We can simulate how the rounded rocks (O) move by iterating over each row and all of its rounded rocks. We skip
        the first row because we know that these rocks cannot move any further north. Then we iterate over each row and
        its rounded rocks. For each of these rounded rocks, we find its next position by checking whether the northern
        spot is already occupied, if it is not, keep iterating until we can't go further or it is occupied.

        This solution is in no way the most optimal, but easy to understand and implement, and still quick for this part.
     */
    fun part1(input: List<String>): Int {
        // Add the first row to a mutable list
        val tilted = mutableListOf(input.first().toCharArray())

        // Start at the second row
        input.drop(1).forEachIndexed { rowIndex, row ->
            // Add the current row
            tilted.add(row.toCharArray())
            row.forEachIndexed { charIndex, char ->
                // If the character is a rounded rock, we need to find where it will end up
                if (char == 'O') {
                    // Get a new index and increment by one, because we skipped the first row
                    var i = rowIndex + 1
                    // Stop iterating if the rock cannot move further
                    while (tilted.getOrNull(i - 1)?.get(charIndex) == '.') i--

                    // The rocks new location
                    tilted[i][charIndex] = 'O'
                    // Update its previous location to an empty space
                    if (i != rowIndex + 1) tilted[rowIndex + 1][charIndex] = '.'
                }
            }
        }

        return calculateWeight(tilted)
    }

    private fun calculateWeight(input: List<CharArray>) =
        input.withIndex().fold(0) { acc, (index, row) -> acc + row.count { it == 'O' } * (input.size - index) }

    /*
        The previous approach will not really work for the second part, it will take way too long. Not only do we have
        to check also west, south and east, but we have to do this 1 billion times. In fact, we can assume that there's
        a cycle within the billion of cycles (north, west, south, east), meaning we do not have to iterate a billion
        times. We can try to find a cycle which repeats itself, from there on we try to find on which part the cycle
        will reach the billionth iteration and use that result.
     */
    fun part2(input: List<String>): Int {
        var currentPosition = input.map { it.toCharArray() }.rollAllDirections()
        val cache = hashMapOf(currentPosition.joinToString("\n") { it.joinToString("") } to 1L)

        val max = 1_000_000_000L
        var i = 1L
        while (i < max) {
            currentPosition = currentPosition.rollAllDirections()

            val posAsString = currentPosition.joinToString("\n") { it.joinToString("") }
            if (posAsString in cache && i < 1_000) i = max - (max - i) % (i - (cache[posAsString] ?: continue))

            cache[posAsString] = i
            i++
        }

        return calculateWeight(currentPosition)
    }

    private fun List<CharArray>.rollAllDirections() = rollNorth().rollWest().rollSouth().rollEast()

    private fun List<CharArray>.rollNorth(): List<CharArray> {
        val result = this.toMutableList()

        for (row in 1..result.lastIndex) {
            for (char in result[row].indices) {
                if (result[row][char] == 'O') {
                    var i = row
                    while (result.getOrNull(i - 1)?.get(char) == '.') i--

                    if (i != row) {
                        result[i][char] = 'O'
                        result[row][char] = '.'
                    }
                }
            }
        }


        return result
    }

    private fun List<CharArray>.rollWest(): List<CharArray> {
        val result = this.toMutableList()

        for (row in result.indices) {
            for (char in 1..result[row].lastIndex) {
                if (result[row][char] == 'O') {
                    var i = char
                    while (result[row].getOrNull(i - 1) == '.') i--

                    if (i != char) {
                        result[row][i] = 'O'
                        result[row][char] = '.'
                    }
                }
            }
        }

        return result
    }

    private fun List<CharArray>.rollSouth(): List<CharArray> {
        val result = this.toMutableList()

        for (row in result.lastIndex - 1 downTo 0) {
            for (char in result[row].indices) {
                if (result[row][char] == 'O') {
                    var i = row
                    while (result.getOrNull(i + 1)?.get(char) == '.') i++

                    if (i != row) {
                        result[i][char] = 'O'
                        result[row][char] = '.'
                    }
                }
            }
        }

        return result
    }

    private fun List<CharArray>.rollEast(): List<CharArray> {
        val result = this.toMutableList()

        for (row in result.indices) {
            for (char in result[row].lastIndex - 1 downTo 0) {
                if (result[row][char] == 'O') {
                    var i = char
                    while (result[row].getOrNull(i + 1) == '.') i++

                    if (i != char) {
                        result[row][i] = 'O'
                        result[row][char] = '.'
                    }
                }
            }
        }

        return result
    }
}
