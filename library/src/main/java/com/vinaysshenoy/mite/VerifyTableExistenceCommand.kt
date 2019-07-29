package com.vinaysshenoy.mite

class VerifyTableExistenceCommand(
		private val checkThatTablesExist: Set<String>,
		private val checkThatTablesDoNotExist: Set<String>
) {
	fun execute(database: MiteDb): CommandResult<Set<String>> {
		val tablesThatExist = checkThatTablesExist
				.filter { tableToCheckExistence -> database.verifyTableExists(tableToCheckExistence) }
				.toSet()

		return when (tablesThatExist) {
			checkThatTablesExist -> CommandResult.pass(tablesThatExist)
			else -> CommandResult.hardFail(checkThatTablesExist - tablesThatExist)
		}
	}

	companion object {

		@JvmStatic
		fun doesExist(tables: Set<String>): VerifyTableExistenceCommand {
			return VerifyTableExistenceCommand(checkThatTablesExist = tables, checkThatTablesDoNotExist = emptySet())
		}
	}
}
