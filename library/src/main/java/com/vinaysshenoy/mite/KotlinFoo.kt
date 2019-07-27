package com.vinaysshenoy.mite

import org.commonmark.node.Node
import org.commonmark.node.Visitor

typealias DbInputRecord = Map<String, String>

fun <V : Visitor> Node.accept2(visitor: V): V {
	accept(visitor)
	return visitor
}
