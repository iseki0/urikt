package space.iseki.urikt

val goJsonText = """
        [
            {
                "input": "http://www.google.com",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/file%20one%26two",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/file one&two",
                    "RawPath": "/file%20one%26two"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/#file%20one%26two",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "Fragment": "file one&two",
                    "RawFragment": "file%20one%26two"
                },
                "expected_string": ""
            },
            {
                "input": "ftp://webmaster@www.google.com/",
                "expected_output": {
                    "Scheme": "ftp",
                    "User": "webmaster",
                    "Host": "www.google.com",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "ftp://john%20doe@www.google.com/",
                "expected_output": {
                    "Scheme": "ftp",
                    "User": "john doe",
                    "Host": "www.google.com",
                    "Path": "/"
                },
                "expected_string": "ftp://john%20doe@www.google.com/"
            },
            {
                "input": "http://www.google.com/?",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "ForceQuery": true
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/?foo=bar?",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "foo=bar?"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/?q=go+language",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "q=go+language"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/?q=go%20language",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "q=go%20language"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/a%20b?q=c+d",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/a b",
                    "RawQuery": "q=c+d"
                },
                "expected_string": ""
            },
            {
                "input": "http:www.google.com/?q=go+language",
                "expected_output": {
                    "Scheme": "http",
                    "Opaque": "www.google.com/",
                    "RawQuery": "q=go+language"
                },
                "expected_string": "http:www.google.com/?q=go+language"
            },
            {
                "input": "http:%2f%2fwww.google.com/?q=go+language",
                "expected_output": {
                    "Scheme": "http",
                    "Opaque": "%2f%2fwww.google.com/",
                    "RawQuery": "q=go+language"
                },
                "expected_string": "http:%2f%2fwww.google.com/?q=go+language"
            },
            {
                "input": "mailto:/webmaster@golang.org",
                "expected_output": {
                    "Scheme": "mailto",
                    "Path": "/webmaster@golang.org"
                },
                "expected_string": ""
            },
            {
                "input": "mailto:webmaster@golang.org",
                "expected_output": {
                    "Scheme": "mailto",
                    "Opaque": "webmaster@golang.org"
                },
                "expected_string": ""
            },
            {
                "input": "/foo?query=http://bad",
                "expected_output": {
                    "Path": "/foo",
                    "RawQuery": "query=http://bad"
                },
                "expected_string": ""
            },
            {
                "input": "//foo",
                "expected_output": {
                    "Host": "foo"
                },
                "expected_string": ""
            },
            {
                "input": "//user@foo/path?a=b",
                "expected_output": {
                    "User": "user",
                    "Host": "foo",
                    "Path": "/path",
                    "RawQuery": "a=b"
                },
                "expected_string": ""
            },
            {
                "input": "///threeslashes",
                "expected_output": {
                    "Path": "/threeslashes"
                },
                "expected_string": ""
            },
            {
                "input": "http://user:password@google.com",
                "expected_output": {
                    "Scheme": "http",
                    "User": "user:password",
                    "Host": "google.com"
                },
                "expected_string": "http://user:password@google.com"
            },
            {
                "input": "http://j@ne:password@google.com",
                "expected_output": {
                    "Scheme": "http",
                    "User": "j@ne:password",
                    "Host": "google.com"
                },
                "expected_string": "http://j%40ne:password@google.com"
            },
            {
                "input": "http://jane:p@ssword@google.com",
                "expected_output": {
                    "Scheme": "http",
                    "User": "jane:p@ssword",
                    "Host": "google.com"
                },
                "expected_string": "http://jane:p%40ssword@google.com"
            },
            {
                "input": "http://j@ne:password@google.com/p@th?q=@go",
                "expected_output": {
                    "Scheme": "http",
                    "User": "j@ne:password",
                    "Host": "google.com",
                    "Path": "/p@th",
                    "RawQuery": "q=@go"
                },
                "expected_string": "http://j%40ne:password@google.com/p@th?q=@go"
            },
            {
                "input": "http://www.google.com/?q=go+language#foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "q=go+language",
                    "Fragment": "foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://www.google.com/?q=go+language#foo&bar",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "q=go+language",
                    "Fragment": "foo&bar"
                },
                "expected_string": "http://www.google.com/?q=go+language#foo&bar"
            },
            {
                "input": "http://www.google.com/?q=go+language#foo%26bar",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "www.google.com",
                    "Path": "/",
                    "RawQuery": "q=go+language",
                    "Fragment": "foo&bar",
                    "RawFragment": "foo%26bar"
                },
                "expected_string": "http://www.google.com/?q=go+language#foo%26bar"
            },
            {
                "input": "file:///home/adg/rabbits",
                "expected_output": {
                    "Scheme": "file",
                    "Host": "",
                    "Path": "/home/adg/rabbits"
                },
                "expected_string": "file:///home/adg/rabbits"
            },
            {
                "input": "file:///C:/FooBar/Baz.txt",
                "expected_output": {
                    "Scheme": "file",
                    "Host": "",
                    "Path": "/C:/FooBar/Baz.txt"
                },
                "expected_string": "file:///C:/FooBar/Baz.txt"
            },
            {
                "input": "MaIlTo:webmaster@golang.org",
                "expected_output": {
                    "Scheme": "mailto",
                    "Opaque": "webmaster@golang.org"
                },
                "expected_string": "mailto:webmaster@golang.org"
            },
            {
                "input": "a/b/c",
                "expected_output": {
                    "Path": "a/b/c"
                },
                "expected_string": "a/b/c"
            },
            {
                "input": "http://%3Fam:pa%3Fsword@google.com",
                "expected_output": {
                    "Scheme": "http",
                    "User": "?am:pa?sword",
                    "Host": "google.com"
                },
                "expected_string": ""
            },
            {
                "input": "http://192.168.0.1/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "192.168.0.1",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://192.168.0.1:8080/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "192.168.0.1:8080",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://[fe80::1]/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1]",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://[fe80::1]:8080/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1]:8080",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://[fe80::1%25en0]/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1%en0]",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://[fe80::1%25en0]:8080/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1%en0]:8080",
                    "Path": "/"
                },
                "expected_string": ""
            },
            {
                "input": "http://[fe80::1%25%65%6e%301-._~]/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1%en01-._~]",
                    "Path": "/"
                },
                "expected_string": "http://[fe80::1%25en01-._~]/"
            },
            {
                "input": "http://[fe80::1%25%65%6e%301-._~]:8080/",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[fe80::1%en01-._~]:8080",
                    "Path": "/"
                },
                "expected_string": "http://[fe80::1%25en01-._~]:8080/"
            },
            {
                "input": "http://rest.rsc.io/foo%2fbar/baz%2Fquux?alt=media",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "rest.rsc.io",
                    "Path": "/foo/bar/baz/quux",
                    "RawPath": "/foo%2fbar/baz%2Fquux",
                    "RawQuery": "alt=media"
                },
                "expected_string": ""
            },
            {
                "input": "mysql://a,b,c/bar",
                "expected_output": {
                    "Scheme": "mysql",
                    "Host": "a,b,c",
                    "Path": "/bar"
                },
                "expected_string": ""
            },
            {
                "input": "scheme://!$&'()*+,;=hello!:1/path",
                "expected_output": {
                    "Scheme": "scheme",
                    "Host": "!$&'()*+,;=hello!:1",
                    "Path": "/path"
                },
                "expected_string": ""
            },
            {
                "input": "http://host/!$&'()*+,;=:@[hello]",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "host",
                    "Path": "/!$&'()*+,;=:@[hello]",
                    "RawPath": "/!$&'()*+,;=:@[hello]"
                },
                "expected_string": ""
            },
            {
                "input": "http://example.com/oid/[order_id]",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "example.com",
                    "Path": "/oid/[order_id]",
                    "RawPath": "/oid/[order_id]"
                },
                "expected_string": ""
            },
            {
                "input": "http://192.168.0.2:8080/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "192.168.0.2:8080",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://192.168.0.2:/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "192.168.0.2:",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://2b01:e34:ef40:7730:8e70:5aff:fefe:edac:8080/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "2b01:e34:ef40:7730:8e70:5aff:fefe:edac:8080",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://2b01:e34:ef40:7730:8e70:5aff:fefe:edac:/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "2b01:e34:ef40:7730:8e70:5aff:fefe:edac:",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://[2b01:e34:ef40:7730:8e70:5aff:fefe:edac]:8080/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[2b01:e34:ef40:7730:8e70:5aff:fefe:edac]:8080",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://[2b01:e34:ef40:7730:8e70:5aff:fefe:edac]:/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "[2b01:e34:ef40:7730:8e70:5aff:fefe:edac]:",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://hello.世界.com/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "hello.世界.com",
                    "Path": "/foo"
                },
                "expected_string": "http://hello.%E4%B8%96%E7%95%8C.com/foo"
            },
            {
                "input": "http://hello.%e4%b8%96%e7%95%8c.com/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "hello.世界.com",
                    "Path": "/foo"
                },
                "expected_string": "http://hello.%E4%B8%96%E7%95%8C.com/foo"
            },
            {
                "input": "http://hello.%E4%B8%96%E7%95%8C.com/foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "hello.世界.com",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "http://example.com//foo",
                "expected_output": {
                    "Scheme": "http",
                    "Host": "example.com",
                    "Path": "//foo"
                },
                "expected_string": ""
            },
            {
                "input": "myscheme://authority<\"hi\">/foo",
                "expected_output": {
                    "Scheme": "myscheme",
                    "Host": "authority<\"hi\">",
                    "Path": "/foo"
                },
                "expected_string": ""
            },
            {
                "input": "tcp://[2020::2020:20:2020:2020%25Windows%20Loves%20Spaces]:2020",
                "expected_output": {
                    "Scheme": "tcp",
                    "Host": "[2020::2020:20:2020:2020%Windows Loves Spaces]:2020"
                },
                "expected_string": ""
            },
            {
                "input": "magnet:?xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a&dn",
                "expected_output": {
                    "Scheme": "magnet",
                    "Host": "",
                    "Path": "",
                    "RawQuery": "xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a&dn"
                },
                "expected_string": "magnet:?xt=urn:btih:c12fe1c06bba254a9dc9f519b335aa7c1367a88a&dn"
            },
            {
                "input": "mailto:?subject=hi",
                "expected_output": {
                    "Scheme": "mailto",
                    "Host": "",
                    "Path": "",
                    "RawQuery": "subject=hi"
                },
                "expected_string": "mailto:?subject=hi"
            }
        ]
    """.trimIndent()
