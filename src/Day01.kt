import Day01.part1
import Day01.part2

fun main() {
    val input = readInputLines("Day01")
    val digits = (1..9).associateBy { it.toString() } + mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )

    part1(input).printFirstPart()
    part2(input, digits).printSecondPart()
}

private object Day01 {
    /*
        To find the first and last digit, two pointers can be used.
        The first pointer starts at the beginning and stops once it finds the first digit.
        The other pointer starts from the end and goes back until it finds a digit.

        If there is only one digit in the line, the pointers will simply point to the same digit.
     */
    fun part1(input: List<String>): Int {
        var result = 0

        input.forEach { line ->
            var start = 0
            var end = line.length - 1

            // Move the start pointer until a digit is found
            while (start < end && !line[start].isDigit()) start++

            // Move the end pointer until a digit is found
            while (start < end && !line[end].isDigit()) end--

            // If both pointers found digits (or the same digit), convert and add to the result
            if (start <= end) {
                val firstDigit = line[start] - '0'
                val lastDigit = line[end] - '0'
                result += 10 * firstDigit + lastDigit
            }
        }

        return result
    }

    fun part2(input: List<String>, digits: Map<String, Int>): Int {
        return input.sumOf { line ->
            val first = digits
                .map { line.indexOf(it.key) to it.value }
                .filter { it.first != -1 }
                .minBy { it.first }
                .second

            val last = digits
                .map { line.lastIndexOf(it.key) to it.value }
                .filter { it.first != -1 }
                .maxBy { it.first }
                .second

            10 * first + last
        }
    }

}
