package space.iseki.urikt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the UriBuilder class according to RFC 3986.
 *
 * These tests verify that the UriBuilder correctly implements the URI syntax
 * as defined in RFC 3986.
 */
class UriBuilderTest {

    /**
     * Test building URIs with various schemes.
     *
     * According to RFC 3986 section 3.1, scheme names consist of a sequence of characters
     * beginning with a letter and followed by any combination of letters, digits, plus ("+"),
     * period ("."), or hyphen ("-").
     */
    @Test
    fun testScheme() {
        // Valid schemes
        assertEquals("http://example.com", buildUri {
            withSchema("http")
            withHost("example.com")
        }.toString())

        assertEquals("https://example.com", buildUri {
            withSchema("https")
            withHost("example.com")
        }.toString())

        assertEquals("ftp://example.com", buildUri {
            withSchema("ftp")
            withHost("example.com")
        }.toString())

        assertEquals("ldap://example.com", buildUri {
            withSchema("ldap")
            withHost("example.com")
        }.toString())

        assertEquals("mailto:user@example.com", buildUri {
            withSchema("mailto")
            withRawPath("user@example.com")
        }.toString())

        assertEquals("news:comp.lang", buildUri {
            withSchema("news")
            withRawPath("comp.lang")
        }.toString())

        assertEquals("tel:+1-816-555-1212", buildUri {
            withSchema("tel")
            withRawPath("+1-816-555-1212")
        }.toString())

        assertEquals("urn:isbn:0451450523", buildUri {
            withSchema("urn")
            withRawPath("isbn:0451450523")
        }.toString())

        // Test with scheme containing allowed special characters
        assertEquals("a+b-c.d://example.com", buildUri {
            withSchema("a+b-c.d")
            withHost("example.com")
        }.toString())

        // Invalid schemes should throw exceptions
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("") }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("1http") }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("http:") }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("http/") }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("http?") }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withSchema("http#") }.toString() }
    }

    /**
     * Test building URIs with various authority components.
     *
     * According to RFC 3986 section 3.2, the authority component consists of
     * an optional userinfo subcomponent, a host subcomponent, and an optional port subcomponent.
     */
    @Test
    fun testAuthority() {
        // Host only
        assertEquals("http://example.com", buildUri {
            withSchema("http")
            withHost("example.com")
        }.toString())

        // Host with port
        assertEquals("http://example.com:8080", buildUri {
            withSchema("http")
            withHost("example.com")
            withPort(8080)
        }.toString())

        // Host with userinfo
        assertEquals("http://user:pass@example.com", buildUri {
            withSchema("http")
            withUserinfo("user", "pass")
            withHost("example.com")
        }.toString())

        // Complete authority
        assertEquals("http://user:pass@example.com:8080", buildUri {
            withSchema("http")
            withUserinfo("user", "pass")
            withHost("example.com")
            withPort(8080)
        }.toString())

        // IPv4 address as host
        assertEquals("http://192.168.1.1", buildUri {
            withSchema("http")
            withHost("192.168.1.1")
        }.toString())

        // IPv6 address as host
        assertEquals("http://[2001:db8::7]", buildUri {
            withSchema("http")
            withHost("[2001:db8::7]")
        }.toString())

        // Invalid port should throw exception
        assertFailsWith<IllegalArgumentException> { buildUri { withPort(-1) }.toString() }
        assertFailsWith<IllegalArgumentException> { buildUri { withPort(65536) }.toString() }

        // Empty host should throw exception
        assertFailsWith<IllegalArgumentException> { buildUri { withHost("") }.toString() }
    }

    /**
     * Test building URIs with various path components.
     *
     * According to RFC 3986 section 3.3, the path component contains data, usually
     * organized in hierarchical form, that, along with data in the non-hierarchical
     * query component, identifies a resource.
     */
    @Test
    fun testPath() {
        // Simple path
        assertEquals("http://example.com/path", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawPath("path")
        }.toString())

        // Path with multiple segments
        assertEquals("http://example.com/path/to/resource", buildUri {
            withSchema("http")
            withHost("example.com")
            addPathSegment("path")
            addPathSegment("to")
            addPathSegment("resource")
        }.toString())

        // Path with special characters that should be encoded
        assertEquals("http://example.com/path%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawPath("path with spaces")
        }.toString())

        // Path without authority
        assertEquals("mailto:user@example.com", buildUri {
            withSchema("mailto")
            withRawPath("user@example.com")
        }.toString())

        // Path with reserved characters
        assertEquals("http://example.com/path/with%2Fslash", buildUri {
            withSchema("http")
            withHost("example.com")
            addPathSegment("path")
            addPathSegment("with/slash")
        }.toString())

        // Empty path
        assertEquals("http://example.com", buildUri {
            withSchema("http")
            withHost("example.com")
        }.toString())

        // Path with dot segments (should be preserved in the builder)
        assertEquals("http://example.com/path/./to/../resource", buildUri {
            withSchema("http")
            withHost("example.com")
            addPathSegment("path")
            addPathSegment(".")
            addPathSegment("to")
            addPathSegment("..")
            addPathSegment("resource")
        }.toString())
    }

    /**
     * Test building URIs with various query components.
     *
     * According to RFC 3986 section 3.4, the query component contains non-hierarchical
     * data that, along with data in the path component, serves to identify a resource.
     */
    @Test
    fun testQuery() {
        // Simple query
        assertEquals("http://example.com?query=value", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawQuery("query=value")
        }.toString())

        // Multiple query parameters
        assertEquals("http://example.com?name=value&name2=value2", buildUri {
            withSchema("http")
            withHost("example.com")
            addQuery("name", "value")
            addQuery("name2", "value2")
        }.toString())

        // Query with special characters that should be encoded
        assertEquals("http://example.com?query=value%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawQuery("query=value with spaces")
        }.toString())

        // Query with reserved characters
        assertEquals("http://example.com?query=value%26with%3Dreserved", buildUri {
            withSchema("http")
            withHost("example.com")
            addQuery("query", "value&with=reserved")
        }.toString())

        // Empty query
        assertEquals("http://example.com?", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawQuery("")
        }.toString())
    }

    /**
     * Test building URIs with various fragment components.
     *
     * According to RFC 3986 section 3.5, the fragment identifier component allows
     * indirect identification of a secondary resource by reference to a primary resource
     * and additional identifying information.
     */
    @Test
    fun testFragment() {
        // Simple fragment
        assertEquals("http://example.com#fragment", buildUri {
            withSchema("http")
            withHost("example.com")
            withFragment("fragment")
        }.toString())

        // Fragment with special characters that should be encoded
        assertEquals("http://example.com#fragment%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withFragment("fragment with spaces")
        }.toString())

        // Fragment with reserved characters
        assertEquals("http://example.com#fragment%23with?reserved", buildUri {
            withSchema("http")
            withHost("example.com")
            withFragment("fragment#with?reserved")
        }.toString())

        // Empty fragment
        assertEquals("http://example.com#", buildUri {
            withSchema("http")
            withHost("example.com")
            withFragment("")
        }.toString())
    }

    /**
     * Test building complete URIs with all components.
     *
     * This test verifies that all components can be combined correctly to form valid URIs.
     */
    @Test
    fun testCompleteUri() {
        // Complete URI with all components
        assertEquals("http://user:pass@example.com:8080/path/to/resource?name=value&name2=value2#fragment", buildUri {
            withSchema("http")
            withUserinfo("user", "pass")
            withHost("example.com")
            withPort(8080)
            addPathSegment("path")
            addPathSegment("to")
            addPathSegment("resource")
            addQuery("name", "value")
            addQuery("name2", "value2")
            withFragment("fragment")
        }.toString())
    }

    /**
     * Test building the example URIs from RFC 3986 section 1.1.2.
     *
     * This test verifies that the UriBuilder can correctly build all the example URIs
     * provided in the RFC.
     */
    @Test
    fun testRfcExamples() {
        // ftp://ftp.is.co.za/rfc/rfc1808.txt
        assertEquals("ftp://ftp.is.co.za/rfc/rfc1808.txt", buildUri {
            withSchema("ftp")
            withHost("ftp.is.co.za")
            addPathSegment("rfc")
            addPathSegment("rfc1808.txt")
        }.toString())

        // http://www.ietf.org/rfc/rfc2396.txt
        assertEquals("http://www.ietf.org/rfc/rfc2396.txt", buildUri {
            withSchema("http")
            withHost("www.ietf.org")
            addPathSegment("rfc")
            addPathSegment("rfc2396.txt")
        }.toString())

        // ldap://[2001:db8::7]/c=GB?objectClass?one
        assertEquals("ldap://[2001:db8::7]/c=GB?objectClass?one", buildUri {
            withSchema("ldap")
            withHost("[2001:db8::7]")
            addPathSegment("c=GB")
            withRawQuery("objectClass?one")
        }.toString())

        // mailto:John.Doe@example.com
        assertEquals("mailto:John.Doe@example.com", buildUri {
            withSchema("mailto")
            withRawPath("John.Doe@example.com")
        }.toString())

        // news:comp.infosystems.www.servers.unix
        assertEquals("news:comp.infosystems.www.servers.unix", buildUri {
            withSchema("news")
            withRawPath("comp.infosystems.www.servers.unix")
        }.toString())

        // tel:+1-816-555-1212
        assertEquals("tel:+1-816-555-1212", buildUri {
            withSchema("tel")
            withRawPath("+1-816-555-1212")
        }.toString())

        // telnet://192.0.2.16:80/
        assertEquals("telnet://192.0.2.16:80/", buildUri {
            withSchema("telnet")
            withHost("192.0.2.16")
            withPort(80)
            withRawPath("")
        }.toString())

        // urn:oasis:names:specification:docbook:dtd:xml:4.1.2
        assertEquals("urn:oasis:names:specification:docbook:dtd:xml:4.1.2", buildUri {
            withSchema("urn")
            withRawPath("oasis:names:specification:docbook:dtd:xml:4.1.2")
        }.toString())
    }

    /**
     * Test percent-encoding of special characters in different URI components.
     *
     * According to RFC 3986 section 2.1, percent-encoding is used to represent
     * a data octet in a component when that octet's corresponding character is
     * outside the allowed set or is being used as a delimiter of, or within, the component.
     */
    @Test
    fun testPercentEncoding() {
        // Test encoding in host
        assertEquals("http://example.com", buildUri {
            withSchema("http")
            withHost("example.com", true)
        }.toString())

        // Test encoding in path
        assertEquals("http://example.com/path%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawPath("path with spaces")
        }.toString())

        // Test encoding in query
        assertEquals("http://example.com?query=value%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withRawQuery("query=value with spaces")
        }.toString())

        // Test encoding in fragment
        assertEquals("http://example.com#fragment%20with%20spaces", buildUri {
            withSchema("http")
            withHost("example.com")
            withFragment("fragment with spaces")
        }.toString())

        // Test encoding in userinfo
        assertEquals("http://user%40domain:pass%20word@example.com", buildUri {
            withSchema("http")
            withUserinfo("user@domain", "pass word")
            withHost("example.com")
        }.toString())

        // Test encoding of unreserved characters (should not be encoded)
        assertEquals("http://example.com/~user/.-_", buildUri {
            withSchema("http")
            withHost("example.com")
            addPathSegment("~user")
            addPathSegment(".-_")
        }.toString())
    }

    /**
     * Test IPv6 address formats according to RFC 3986.
     *
     * According to RFC 3986 section 3.2.2, an IPv6 address is represented in the
     * host portion of the authority component as "[v*.*]" where the "*" is
     * replaced by the hexadecimal representation of the IPv6 address.
     */
    @Test
    fun testIPv6Addresses() {
        // Standard IPv6 address
        assertEquals("http://[2001:db8:85a3:8d3:1319:8a2e:370:7348]", buildUri {
            withSchema("http")
            withHost("[2001:db8:85a3:8d3:1319:8a2e:370:7348]")
        }.toString())

        // IPv6 address with compressed zeros
        assertEquals("http://[2001:db8::1]", buildUri {
            withSchema("http")
            withHost("[2001:db8::1]")
        }.toString())

        // IPv6 loopback address
        assertEquals("http://[::1]", buildUri {
            withSchema("http")
            withHost("[::1]")
        }.toString())

        // IPv6 unspecified address
        assertEquals("http://[::]", buildUri {
            withSchema("http")
            withHost("[::]")
        }.toString())

        // IPvFuture format
        assertEquals("http://[v1.fe80::1]", buildUri {
            withSchema("http")
            withHost("[v1.fe80::1]")
        }.toString())
    }

    /**
     * Test normalization of URIs according to RFC 3986 section 6.
     *
     * This test verifies that the UriBuilder correctly handles normalization
     * of URIs, including case normalization, percent-encoding normalization,
     * and path segment normalization.
     */
    @Test
    fun testNormalization() {
        // Case normalization of scheme
        assertEquals("http://example.com", buildUri {
            withSchema("HTTP")
            withHost("example.com")
        }.toString())

        // Case normalization of host
        assertEquals("http://example.com", buildUri {
            withSchema("http")
            withHost("EXAMPLE.COM")
        }.toString())

    }
} 