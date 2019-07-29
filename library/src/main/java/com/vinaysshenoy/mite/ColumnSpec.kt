package com.vinaysshenoy.mite

data class ColumnSpec(
		val name: String,
		val type: ColumnType,
		val isPrimaryKey: Boolean,
		val isNullable: Boolean
) {

	enum class ColumnType {
		Integer,
		Real,
		Text
	}
}
