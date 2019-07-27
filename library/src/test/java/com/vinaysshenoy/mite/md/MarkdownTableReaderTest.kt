package com.vinaysshenoy.mite.md

import com.vinaysshenoy.mite.DbInputRecord
import com.vinaysshenoy.mite.md.MarkdownTableReader.Result.InvalidTable
import com.vinaysshenoy.mite.md.MarkdownTableReader.Result.Success
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo

class MarkdownTableReaderTest {

	private val tableReader: MarkdownTableReader = CommonmarkMarkdownTableReader()

	@Test
	fun `empty block should be read as an empty list`() {
		// given
		val text = ""

		// when
		val result = tableReader.read(text) as Success

		// then
		expectThat(result.tableData).isEmpty()
	}

	@Test
	fun `block with only header should be read as an empty list`() {
		// given
		val text = """
			|id |name|
			|---|----|
		""".trimIndent()

		// when
		val result = tableReader.read(text) as Success

		// then
		expectThat(result.tableData).isEmpty()
	}

	@Test
	fun `block with one row should be read`() {
		// given
		val text = """
			|id |name       |
			|---|-----------|
			|1  |Test Name 1|
		""".trimIndent()

		// when
		val result = tableReader.read(text) as Success

		// then
		val expectedTableData: List<DbInputRecord> = listOf(
				mapOf("id" to "1", "name" to "Test Name 1")
		)
		expectThat(result.tableData).isEqualTo(expectedTableData)
	}

	@Test
	fun `block with multiple rows should be read`() {
		// given
		val text = """
			|id    |name             |
			|------|-----------------|
			|1     |Test Name 100    |
			|2     |Test Name 45     |
			|5     |Test Name 1987656|
			|67    |Test Name 145    |
			|1001  |Test Name 678    |
		""".trimIndent()

		// when
		val result = tableReader.read(text) as Success

		// then
		val expectedTableData: List<DbInputRecord> = listOf(
				mapOf("id" to "1", "name" to "Test Name 100"),
				mapOf("id" to "2", "name" to "Test Name 45"),
				mapOf("id" to "5", "name" to "Test Name 1987656"),
				mapOf("id" to "67", "name" to "Test Name 145"),
				mapOf("id" to "1001", "name" to "Test Name 678")
		)
		expectThat(result.tableData).isEqualTo(expectedTableData)
	}

	@Test
	fun `block with multiple rows and columns should be read`() {
		// given
		val text = """
			|id |name                                   |dob       |email-address     |phone-number|
			|---|---------------------------------------|----------|------------------|------------|
			|1  |No orders                              |1990-02-20|testuser1@test.com|NULL        |
			|2  |One order with phone number            |1985-11-13|testuser2@test.com|number-1    |
			|3  |One order with no phone number         |2000-04-01|testuser3@test.com|NULL        |
			|4  |Two orders, latest with no phone number|1998-01-01|testuser4@test.com|NULL        |
			|5  |Two orders,both with phone number      |1997-02-02|testuser5@test.com|number-3    |
		""".trimIndent()

		// when
		val result = tableReader.read(text) as Success

		// then
		val expectedTableData: List<DbInputRecord> = listOf(
				mapOf(
						"id" to "1",
						"name" to "No orders",
						"dob" to "1990-02-20",
						"email-address" to "testuser1@test.com",
						"phone-number" to "NULL"
				),
				mapOf(
						"id" to "2",
						"name" to "One order with phone number",
						"dob" to "1985-11-13",
						"email-address" to "testuser2@test.com",
						"phone-number" to "number-1"
				),
				mapOf(
						"id" to "3",
						"name" to "One order with no phone number",
						"dob" to "2000-04-01",
						"email-address" to "testuser3@test.com",
						"phone-number" to "NULL"
				),
				mapOf(
						"id" to "4",
						"name" to "Two orders, latest with no phone number",
						"dob" to "1998-01-01",
						"email-address" to "testuser4@test.com",
						"phone-number" to "NULL"
				),
				mapOf(
						"id" to "5",
						"name" to "Two orders,both with phone number",
						"dob" to "1997-02-02",
						"email-address" to "testuser5@test.com",
						"phone-number" to "number-3"
				)
		)
		expectThat(result.tableData).isEqualTo(expectedTableData)
	}

	@Test
	fun `invalid table block must return the invalid table result`() {
		// given
		val text = """
			|id |name|
		""".trimIndent()

		// when
		val result = tableReader.read(text)

		// then
		expectThat(result).isEqualTo(InvalidTable(text))
	}
}
