import Day15.part1
import Day15.part2

fun main() {
    val input = readInput("Day15").replace("\n", "").split(',')
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day15 {
    fun part1(input: List<String>) = input.sumOf { calculateHash(it).toInt() }

    fun part2(input: List<String>): Int {
        val map = hashMapOf<UByte, LinkedHashMap<String, Int>>()

        input.forEach { step ->
            val tokens = step.split('-', '=')
            val label = tokens.first()
            val box = calculateHash(label)

            // Remove specified lens
            if (step.contains('-')) map[box]?.remove(label)
            // Add or update specified lens
            else map.getOrPut(box) { linkedMapOf() }[label] = tokens.last().toInt()

        }

        return map.entries.sumOf { (box, lenses) ->
            lenses.entries.withIndex().sumOf { (index, lens) ->
                (box.toInt() + 1) * (index + 1) * lens.value
            }
        }
    }

    private fun calculateHash(label: String): UByte {
        var result = 0

        label.forEach { char ->
            result += char.code
            result *= 17
            result %= 256
        }

        return result.toUByte()
    }
}