package space.iseki.urikt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UriTest {
    @Test
    fun testParse() {
        val uri = Uri("http://git:123@www.ics.uci.edu:936/pub/ietf/uri/?foo=12345#Related")
        assertEquals("http", uri.schema)
        assertEquals("git:123", uri.rawUserinfo)
        assertEquals("git", uri.rawUsername)
        assertEquals("www.ics.uci.edu", uri.rawHost)
        assertEquals("936", uri.rawPort)
        assertEquals("git:123@www.ics.uci.edu:936", uri.rawAuthority)
        assertEquals("/pub/ietf/uri/", uri.rawPath)
        assertEquals("foo=12345", uri.rawQuery)
        assertEquals("Related", uri.rawFragment)
    }

    @Test
    fun testWithoutSchema() {
        val uri = Uri("//www.ics.uci.edu:936/pub/ietf/uri/?foo=12345#Related")
        assertEquals(null, uri.schema)
        assertEquals(null, uri.rawUserinfo)
        assertEquals(null, uri.rawUsername)
        assertEquals("www.ics.uci.edu", uri.rawHost)
        assertEquals("936", uri.rawPort)
        assertEquals("www.ics.uci.edu:936", uri.rawAuthority)
        assertEquals("/pub/ietf/uri/", uri.rawPath)
        assertEquals("foo=12345", uri.rawQuery)
        assertEquals("Related", uri.rawFragment)
    }

    @Test
    fun testPathSlash() {
        val uri = Uri("http://www.google.com/")
        assertEquals("/", uri.rawPath)
    }

    @Test
    fun testPathMultiAt() {
        val uri = Uri("http://j@ne:password@google.com")
        assertEquals("google.com", uri.decodedHost)
        assertEquals("j@ne:password", uri.decodedUserinfo)
        assertEquals("j@ne", uri.decodedUsername)
    }

    @Test
    fun testMagnet() {
        val uri = Uri("magnet:?xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a&dn")
        assertEquals(null, uri.decodedHost)
        assertEquals(null, uri.decodedPath)
        assertEquals("xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a&dn", uri.decodedQuery)
    }

    @Test
    fun testUnescape() {
        val input = "file%20one%26two"
        assertEquals(" one&two", input.unescape(4 until input.length))
        assertFailsWith<UriSyntaxException> { "%%".unescape(0..1) }
    }


    @Test
    fun testValidIPvFuture() {
        listOf(
            // Basic valid cases:
            "v1.abc",               // minimal HEXDIG and unreserved letters
            "vF.hello",             // uppercase hex digit "F" and letters after dot
            "v123.ABC",             // multiple HEXDIG followed by uppercase letters
            // Including sub-delims and colon:
            "vA.!$&'()*+,;=:",       // allowed sub-delims and colon after the dot
            "v0.~:_",               // mix of unreserved (~, _) and colon
            // Mix of allowed characters: digits, unreserved and sub-delims
            "v1.0-9_~abc"           // valid: "0-9_~abc" are all in allowed sets
        ).forEach { address ->
            assertTrue(isValidVFuture(address, address.indices), "Expected valid IPvFuture: $address")
        }
    }

    @Test
    fun testInvalidIPvFuture() {
        listOf(
            "1v.abc",       // Does not start with 'v'
            "v.abc",        // Missing HEXDIG between 'v' and dot
            "vG.abc",       // 'G' is not a valid hex digit (only 0-9, A-F, a-f allowed)
            "v1",           // Missing the dot and trailing part
            "v1.",          // Dot present but no following characters
            "v1.abc def",   // Contains a space (not allowed)
            "v1.abc[",      // Contains '[' which is not allowed
            "V1.abc",       // Uppercase "V" instead of required lowercase 'v'
            "v1.abc/",      // '/' is not in unreserved, sub-delims, or colon
            "v1.abc@",      // '@' is not allowed
            "v1.abc?",      // '?' is not allowed
            "v1.abc#",      // '#' is not allowed
            "v1.abc\\",     // '\' is not allowed
            ""              // Empty string is invalid
        ).forEach { address ->
            assertFalse(isValidVFuture(address, address.indices), "Expected invalid IPvFuture: $address")
        }
    }

    @Test
    fun testValidIPv4() {
        listOf(
            "192.168.1.1",
            "0.0.0.0",
            "255.255.255.255",
            "127.0.0.1",
            "10.0.0.1",
            "172.16.0.1",
            "8.8.8.8"
        ).forEach { address ->
            assertTrue(isValidIPv4(address, address.indices), "Expected valid IPv4: $address")
        }
    }

    @Test
    fun testInvalidIPv4() {
        listOf(
            "256.256.256.256",
            "192.168.1",
            "192.168.1.1.1",
            "192.168.1.-1",
            "abc.def.ghi.jkl",
            "192.168.1.300",
            "192.168.1.1/24",
            "123.456.789.0",
            "1.1.1.1.1",
            "1.1.1.1.",
            "1.1.1.01",
            "1.1.01.1",
            "01.1.1.1",
            "",
            "...."
        ).forEach { address ->
            assertFalse(isValidIPv4(address, address.indices), "Expected invalid IPv4: $address")
        }
    }

    @Test
    fun testValidIPv6() {
        listOf(
            "aA::",
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "fe80::1",
            "2001:db8::ff00:42:8329",
            "fd12:3456:789a:1::1",
            "::1",
            "2001:0db8:abcd:0012:0000:0000:0000:0001",
            "ff02::1",
            "2607:f8b0:4005:805::200e",
            "2001:0db8:1234:5678:9abc:def0:1234:5678",
            "fd00::1",
            "2001:db8:abcd::1",
            "fe80::abcd:1234:5678:90ab",
            "2001:0db8:aaaa:bbbb:cccc:dddd:eeee:ffff",
            "fd12:3456:789a:1:2:3:4:5",
            "ff02::2",
            "2001:4860:4860::8888",
            "2606:4700:4700::1111",
            "2404:6800:4003:c00::101",
            "2620:119:35::35",
            "2001:470:1f0b:15e5::1",
        ).forEach { assertTrue(isValidIPv6(it, it.indices), it) }
    }

    @Test
    fun testInvalidIPv6() {
        listOf(
            "1:1",
            "1:",
            "2001:db8:85a3::8a2e:37023:7334",
            "fe80:::1",
            "2001:db8:zzzz::ff00:42:8329",
            "fd12:3456:789a:1:::1",
            "::g1",
            "2001:0db8:abcd:0012:0000:0000:0000:00001",
            "ff02::1::1",
            "2607:f8b0:4005:805::200e::",
            "2001:db8:1234:5678:9abc:def0:1234:56789",
            "fd00::1::1",
            "2001:db8:abcd:xyz::1",
            "fe80::abcd:12345:5678:90ab",
            "2001:0db8:aaaa:bbbb:cccc:dddd:eeee:ffff:1234",
            "fd12:3456:789a:1:2:3:4:5:6",
            "ff02:gg::2",
            "2001:4860:4860:::8888",
            "2606:4700:4700::11111",
            "2404:6800:4003:c00::101::",
            "2620:119:35:::35",
            "2001:470:1f0b:15e5::g",
        ).forEach { assertFalse(isValidIPv6(it, it.indices), it) }
    }

    @Test
    fun testParseGo() {
        val tests = Json.decodeFromString<List<UrlTest>>(goJsonText)
        tests.forEachIndexed { index, it ->
            val uri = Uri(it.input)
            val caseText = "Case $index: ${it.input}"
            fun m(s: String) = "$s <-> $caseText"
            it.expectedOutput.scheme?.let { assertEquals(it, uri.schema, m("scheme")) }
            it.expectedOutput.host?.let {
                val portString = uri.portIndices?.let { p -> ":" + uri.toString().slice(p) }.orEmpty()
                assertEquals(it, uri.decodedHost.orEmpty() + portString, m("host"))
            }
            it.expectedOutput.path?.let { assertEquals(it, uri.decodedPath.orEmpty(), m("path")) }
            it.expectedOutput.rawPath?.let { assertEquals(it, uri.rawPath.orEmpty(), m("rawPath")) }
            it.expectedOutput.fragment?.let { assertEquals(it, uri.decodedFragment.orEmpty(), m("fragment")) }
            it.expectedOutput.rawFragment?.let { assertEquals(it, uri.rawFragment.orEmpty(), m("rawFragment")) }
            it.expectedOutput.user?.let { assertEquals(it, uri.decodedUserinfo.orEmpty(), m("user")) }
//            it.expectedOutput.opaque?.let { assertEquals(it, uri.rawAuthority, m("opaque")) }
            it.expectedOutput.rawQuery?.let { assertEquals(it, uri.rawQuery.orEmpty(), m("rawQuery")) }
//            it.expectedOutput.forceQuery?.let { assertEquals(it, uri.queryIndices != null, m("forceQuery")) }
        }
    }

    @Test
    fun testRfcExamples() {
        // FTP URI
        val ftpUri = Uri("ftp://ftp.is.co.za/rfc/rfc1808.txt")
        assertEquals("ftp", ftpUri.schema)
        assertEquals("ftp.is.co.za", ftpUri.rawHost)
        assertEquals("/rfc/rfc1808.txt", ftpUri.rawPath)
        assertEquals(null, ftpUri.rawQuery)
        assertEquals(null, ftpUri.rawFragment)

        // HTTP URI
        val httpUri = Uri("http://www.ietf.org/rfc/rfc2396.txt")
        assertEquals("http", httpUri.schema)
        assertEquals("www.ietf.org", httpUri.rawHost)
        assertEquals("/rfc/rfc2396.txt", httpUri.rawPath)
        assertEquals(null, httpUri.rawQuery)
        assertEquals(null, httpUri.rawFragment)

        // LDAP URI with IPv6 address and query
        val ldapUri = Uri("ldap://[2001:db8::7]/c=GB?objectClass?one")
        assertEquals("ldap", ldapUri.schema)
        assertEquals("[2001:db8::7]", ldapUri.rawHost)
        assertEquals("/c=GB", ldapUri.rawPath)
        assertEquals("objectClass?one", ldapUri.rawQuery)
        assertEquals(null, ldapUri.rawFragment)

        // Mailto URI
        val mailtoUri = Uri("mailto:John.Doe@example.com")
        assertEquals("mailto", mailtoUri.schema)
        assertEquals(null, mailtoUri.rawAuthority)
        assertEquals("John.Doe@example.com", mailtoUri.rawPath)
        assertEquals(null, mailtoUri.rawQuery)
        assertEquals(null, mailtoUri.rawFragment)

        // News URI
        val newsUri = Uri("news:comp.infosystems.www.servers.unix")
        assertEquals("news", newsUri.schema)
        assertEquals(null, newsUri.rawAuthority)
        assertEquals("comp.infosystems.www.servers.unix", newsUri.rawPath)
        assertEquals(null, newsUri.rawQuery)
        assertEquals(null, newsUri.rawFragment)

        // Tel URI
        val telUri = Uri("tel:+1-816-555-1212")
        assertEquals("tel", telUri.schema)
        assertEquals(null, telUri.rawAuthority)
        assertEquals("+1-816-555-1212", telUri.rawPath)
        assertEquals(null, telUri.rawQuery)
        assertEquals(null, telUri.rawFragment)

        // Telnet URI with port
        val telnetUri = Uri("telnet://192.0.2.16:80/")
        assertEquals("telnet", telnetUri.schema)
        assertEquals("192.0.2.16", telnetUri.rawHost)
        assertEquals("80", telnetUri.rawPort)
        assertEquals(80, telnetUri.port)
        assertEquals("/", telnetUri.rawPath)
        assertEquals(null, telnetUri.rawQuery)
        assertEquals(null, telnetUri.rawFragment)

        // URN
        val urnUri = Uri("urn:oasis:names:specification:docbook:dtd:xml:4.1.2")
        assertEquals("urn", urnUri.schema)
        assertEquals(null, urnUri.rawAuthority)
        assertEquals("oasis:names:specification:docbook:dtd:xml:4.1.2", urnUri.rawPath)
        assertEquals(null, urnUri.rawQuery)
        assertEquals(null, urnUri.rawFragment)
    }

    @Serializable
    data class UrlTest(
        @SerialName("input") val input: String,
        @SerialName("expected_output") val expectedOutput: UrlEntry,
        @SerialName("expected_string") val expectedString: String,
    )

    @Serializable
    data class UrlEntry(
        @SerialName("Scheme") val scheme: String? = null,
        @SerialName("Host") val host: String? = null,
        @SerialName("Path") val path: String? = null,
        @SerialName("RawPath") val rawPath: String? = null,
        @SerialName("Fragment") val fragment: String? = null,
        @SerialName("RawFragment") val rawFragment: String? = null,
        @SerialName("User") val user: String? = null,
        @SerialName("Opaque") val opaque: String? = null,
        @SerialName("RawQuery") val rawQuery: String? = null,
        @SerialName("ForceQuery") val forceQuery: Boolean? = null,
    )
}
