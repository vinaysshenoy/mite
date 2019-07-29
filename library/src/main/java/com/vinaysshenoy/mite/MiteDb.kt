package com.vinaysshenoy.mite

interface MiteDb {
	fun verifyTableExists(table: String): Boolean
}
