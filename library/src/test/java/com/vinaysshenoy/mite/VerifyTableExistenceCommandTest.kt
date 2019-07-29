package com.vinaysshenoy.mite

import io.mockk.every
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(JUnitParamsRunner::class)
class VerifyTableExistenceCommandTest {

	@Test
	fun `when all the tables being checked for existence actually exist, the verification should pass`() {
		// given
		val nameOfTable1 = "Table1"
		val nameOfTable2 = "Table2"
		val tablesToCheckForExistence = setOf(nameOfTable1, nameOfTable2)

		val database = mockk<MiteDb>()
		every { database.verifyTableExists(nameOfTable1) } returns true
		every { database.verifyTableExists(nameOfTable2) } returns true

		val command = VerifyTableExistenceCommand.doesExist(tablesToCheckForExistence)

		// when
		val result = command.execute(database)

		// then
		expectThat(result).isEqualTo(CommandResult.pass(setOf(nameOfTable1, nameOfTable2)))
	}

	@Test
	@Parameters(method = "params for tables that do not exist when checking for existence")
	fun `when any of the tables being checked for existence do not exist, the verification should hard fail`(
			tablesThatDoExist: Set<String>,
			tablesThatDoNotExist: Set<String>
	) {
		// given
		val database = mockk<MiteDb>()
		tablesThatDoExist.forEach { tableThatDoesExist ->
			every { database.verifyTableExists(tableThatDoesExist) } returns true
		}
		tablesThatDoNotExist.forEach { tableThatDoesNotExist ->
			every { database.verifyTableExists(tableThatDoesNotExist) } returns false
		}

		val tablesToCheckForExistence = tablesThatDoExist + tablesThatDoNotExist
		val command = VerifyTableExistenceCommand.doesExist(tablesToCheckForExistence)

		// when
		val result = command.execute(database)

		// then
		expectThat(result).isEqualTo(CommandResult.hardFail(tablesThatDoNotExist))
	}

	@Suppress("Unused")
	private fun `params for tables that do not exist when checking for existence`(): List<List<Any>> {
		return listOf(
				listOf(
						setOf("Table1", "Table2"),
						setOf("Table3")
				),
				listOf(
						setOf("Table1"),
						setOf("Table3", "Table2")
				),
				listOf(
						setOf("Table2", "Table3"),
						setOf("Table1", "Table4")
				)
		)
	}
}
