package com.vinaysshenoy.mite

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class CanaryUnitTest {

	@Test
	fun `test framework must work as expected`() {
		expectThat(2 + 2).isEqualTo(4)
	}

	@Test
	fun `test framework must report errors`() {
		var thrown: Throwable? = null
		try {
			expectThat(2 + 2).isEqualTo(0)
		} catch (e: Throwable) {
			thrown = e
		}
		expectThat(thrown).isNotNull()
	}
}
