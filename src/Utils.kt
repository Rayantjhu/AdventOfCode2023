import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

/** Reads lines from the given input txt file. */
fun readInputLines(name: String) = Path("src/$name.txt").readLines()

fun readInput(name: String) = Path("src/$name.txt").readText()

/** Helper function to print the solution for the first part of the puzzle */
fun Number.printFirstPart() = println("The solution for the first part is: $this")

/** Helper function to print the solution for the second part of the puzzle */
fun Number.printSecondPart() = println("The solution for second part is: $this")

data class Quadruple<out T1, out T2, out T3, out T4>(val first: T1, val second: T2, val third: T3, val fourth: T4)

data class Quintuple<out T1, out T2, out T3, out T4, out T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5
)

fun <A, B> A.then(other: (A) -> B): B {
    return other(this)
}

fun <T> Collection<T>.distinctPairs(): Collection<Pair<T, T>> {
    return flatMapIndexed { i: Int, item1: T ->
        drop(i + 1).map { item2 -> item1 to item2 }
    }
}