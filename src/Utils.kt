import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()
fun readInputAndChunkByBlankLines(name: String) = sequence {
    val lines = Path("src/$name.txt").readLines()
    var start = 0
    for (i in 1..lines.size){
        if(lines.getOrNull(i).isNullOrEmpty()){
            yield(lines.slice(start until i))
            start = i.inc()
        }
    }
}


/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
