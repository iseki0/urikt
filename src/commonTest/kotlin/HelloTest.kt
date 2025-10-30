/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2025 iseki zero and all contributors
 * Licensed under the MIT License. See LICENSE file for details.
 */

import kotlin.test.Test
import kotlin.test.assertEquals

class HelloTest {
    @Test
    fun testHello() {
        println("Hello, World!")
        assertEquals("Hello, World!", "Hello, World!")
    }
}