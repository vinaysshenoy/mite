package com.vinaysshenoy.mite.md

import com.vinaysshenoy.mite.DbInputRecord
import com.vinaysshenoy.mite.accept2
import com.vinaysshenoy.mite.md.MarkdownTableReader.Result.InvalidTable
import com.vinaysshenoy.mite.md.MarkdownTableReader.Result.Success
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Node
import org.commonmark.node.Text
import org.commonmark.parser.Parser

class CommonmarkMarkdownTableReader : MarkdownTableReader {

	private val parser by lazy {
		Parser.Builder()
				.extensions(listOf(TablesExtension.create()))
				.build()
	}

	override fun read(text: String): MarkdownTableReader.Result {
		return try {
			Success(readMarkdownTableNodeAsDbInputRecords(parser.parse(text)))
		} catch (e: NotATableException) {
			InvalidTable(text)
		}
	}

	private fun readMarkdownTableNodeAsDbInputRecords(node: Node): List<DbInputRecord> {
		return when (node.firstChild) {
			null -> emptyList()
			!is TableBlock -> throw NotATableException()
			else -> readTableBlockAsDbRecords(node.firstChild as TableBlock)
		}
	}

	private fun readTableBlockAsDbRecords(tableBlock: TableBlock): List<DbInputRecord> {
		return when {
			tableBlock.hasOnlyHeader() -> emptyList()
			else -> readDbInputRecordsFromTableHeadAndBody(tableBlock.firstChild as TableHead, tableBlock.lastChild as TableBody)
		}
	}

	private fun readDbInputRecordsFromTableHeadAndBody(tableHead: TableHead, tableBody: TableBody): List<DbInputRecord> {
		val columnNames = readTexts(tableHead.firstChild as TableRow)
		val rows = generateSequence(tableBody.firstChild as TableRow) { node -> node.next as TableRow? }
				.map(this::readTexts)
				.toList()

		return generateDbInputRecords(columnNames, rows)
	}

	private fun readTexts(tableRow: TableRow): List<String> {
		return tableRow
				.accept2(TextCollector())
				.let { visitor -> visitor.allTexts }
	}

	private fun generateDbInputRecords(columnNames: List<String>, rows: List<List<String>>): List<DbInputRecord> {
		return rows.map { row -> mapRowAndColumnNamesToDbInputRecord(row, columnNames) }
	}

	private fun mapRowAndColumnNamesToDbInputRecord(
			row: List<String>,
			columnNames: List<String>
	): DbInputRecord {
		return columnNames
				.zip(row)
				.toMap()
	}

	private class TextCollector : AbstractVisitor() {

		private val textHolder = mutableListOf<String>()

		val allTexts: List<String>
			get() = textHolder.toList()

		override fun visit(text: Text) {
			textHolder.add(text.literal)
		}
	}

	private class NotATableException : IllegalArgumentException()
}

private fun TableBlock.hasOnlyHeader() = firstChild === lastChild
