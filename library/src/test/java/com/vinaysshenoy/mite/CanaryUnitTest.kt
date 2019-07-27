package com.vinaysshenoy.mite

import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
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

	@Test
	fun `markdown tables must be parsed`() {
		// given
		val parser = Parser
				.builder()
				.extensions(listOf(TablesExtension.create()))
				.build()

		val tableText = """
			|id |name                                   |dob       |email-address     |
			|---|---------------------------------------|----------|------------------|
			|1  |No orders                              |1990-02-20|testuser1@test.com|
			|2  |One order with phone number            |1985-11-13|testuser2@test.com|
			|3  |One order with no phone number         |2000-04-01|testuser3@test.com|
			|4  |Two orders, latest with no phone number|1998-01-01|testuser4@test.com|
			|5  |Two orders,both with phone number      |1997-02-02|testuser5@test.com|
		""".trimIndent()

		// when
		val node = parser.parse(tableText)

		// then
		print(node.toString())
	}
}
