package com.joshmonson.trula.parser.ast.lh

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/15/14
 * Time: 7:13 AM
 * To change this template use File | Settings | File Templates.
 */
case class LHStructure(id: Identifier, children: List[LHStructure] = Nil, descendants: List[LHStructure] = Nil) {
}


