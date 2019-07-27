package com.vinaysshenoy.mite.md

import com.vinaysshenoy.mite.DbInputRecord

interface MarkdownTableReader {
	fun read(text: String): Result

	sealed class Result {
		data class Success(val tableData: List<DbInputRecord>) : Result()
		data class InvalidTable(val text: String) : Result()
	}
}
