package com.joshmonson.trula.parser.ast.lh

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/14/14
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
case class Parentage(id: Identifier, isParent: Boolean) {
  override def toString: String = id + (if (isParent) " > " else " >> ")
}
