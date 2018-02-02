import org.junit.Test
import pl.droidsonroids.jspoon.example.java.Example
import pl.droidsonroids.jspoon.example.kotlin.main

class IntellijHelper {
    @Test
    fun runJava() {
        Example.main(arrayOf())
    }

    @Test
    fun runKotlin() {
        main(arrayOf())
    }
}