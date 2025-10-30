/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2025 iseki zero and all contributors
 * Licensed under the MIT License. See LICENSE file for details.
 */

package space.iseki.urikt

/**
 * Utility function to generate bit masks for character sets used in URI parsing.
 *
 * This function generates bit masks for various character sets defined in RFC 3986,
 * such as unreserved characters, delimiters, and allowed characters for different URI components.
 * The bit masks are used for efficient character matching in the URI parser.
 *
 * This is a development utility and not intended for production use.
 */
fun main() {
    /**
     * Generates bit masks for a character set defined by the given predicate.
     *
     * @param name The name of the bit mask
     * @param block The predicate that defines the character set
     */
    fun f(name: String, block: (Char) -> Boolean) {
        (63 downTo 0).joinToString(
            separator = "", prefix = "private val ${name}_L = 0b", postfix = "uL"
        ) { if (block(it.toChar())) "1" else "0" }.let(::println)
        (127 downTo 64).joinToString(
            separator = "", prefix = "private val ${name}_H = 0b", postfix = "uL"
        ) { if (block(it.toChar())) "1" else "0" }.let(::println)
    }

    /**
     * Checks if a character is a generic delimiter as defined in RFC 3986.
     *
     * @param c The character to check
     * @return true if the character is a generic delimiter, false otherwise
     */
    fun isGenDelims(c: Char) = c in charArrayOf(':', '/', '?', '#', '[', ']', '@')

    /**
     * Checks if a character is a subcomponent delimiter as defined in RFC 3986.
     *
     * @param c The character to check
     * @return true if the character is a subcomponent delimiter, false otherwise
     */
    fun isSubDelims(c: Char) = c in charArrayOf('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=')

    /**
     * Checks if a character is an unreserved character as defined in RFC 3986.
     *
     * @param c The character to check
     * @return true if the character is an unreserved character, false otherwise
     */
    fun isUnreserved(c: Char) = c.isLetterOrDigit() || c == '-' || c == '.' || c == '_' || c == '~'

    /**
     * Checks if a character is a reserved character as defined in RFC 3986.
     *
     * @param c The character to check
     * @return true if the character is a reserved character, false otherwise
     */
    fun isReserved(c: Char) = isGenDelims(c) || isSubDelims(c)

    // Generate bit masks for various character sets
    f("UNRESERVED") { isUnreserved(it) }
    f("GEN_DELIMS") { isGenDelims(it) }
    f("SUB_DELIMS") { isSubDelims(it) }
    f("RESERVED") { isReserved(it) }
    f("SCHEMA_SUFFIX") { it.isLetterOrDigit() || it == '+' || it == '-' || it == '.' }
    f("AFTER_AUTHORITY") { it in charArrayOf('#', '?', '/') }
    f("AFTER_PATH") { it in charArrayOf('#', '?') }
    f("HOST_ALLOWED_CHARS") { isUnreserved(it) || isSubDelims(it) }
    f("USERINFO_ALLOWED_CHARS") { isUnreserved(it) || isSubDelims(it) }
    f("USERNAME_ALLOWED_CHARS") { isUnreserved(it) || isSubDelims(it) || it == ':' }
    f("QUERY_ALLOWED_CHARS") { isUnreserved(it) || it == ':' || isSubDelims(it) || it == '/' || it == '?' || it == '@' }
    f("QUERY_ARG_ALLOWED_CHARS") {
        val c = charArrayOf('=', '&')
        it !in c && (isUnreserved(it) || it == ':' || isSubDelims(it) || it == '/' || it == '?' || it == '@')
    }
    f("FRAGMENT_ALLOWED_CHARS") { isUnreserved(it) || it == ':' || isSubDelims(it) || it == '/' || it == '?' || it == '@' }
    f("IPVFUTURE_ALLOWED_CHARS") { isUnreserved(it) || it == ':' || isSubDelims(it) }
    f("SEGMENT_ALLOWED_CHARS") { isUnreserved(it) || isSubDelims(it) || it == '@' || it == ':' }
    f("PATH_ALLOWED_CHARS") { isUnreserved(it) || isSubDelims(it) || it == '@' || it == ':' || it == '/' }
}
