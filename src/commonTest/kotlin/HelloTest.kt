import kotlin.test.Test
import kotlin.test.assertEquals

class HelloTest {
    @Test
    fun testHello() {
        println("Hello, World!")
        assertEquals("Hello, World!", "Hello, World!")
    }
}