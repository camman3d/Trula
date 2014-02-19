package com.joshmonson.trula.parser.ast.rh

import com.joshmonson.trula.parser.ast.lh.Identifier

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 7:00 AM
 * To change this template use File | Settings | File Templates.
 */
case class Reference(
                      label: Option[String] = None,
                      kind: Option[String] = None,
                      name: Option[String] = None
                      ) {

  def references(id: Identifier) = {
    if (label.isDefined)
      label == id.label
    else
      kind.map(_ == id.kind.getOrElse("")).getOrElse(true) && name.map(_ == id.name.getOrElse("")).getOrElse(true)
  }

  def toId = Identifier(label, kind, name)

  override def toString: String =
    label.map("@" + _ + " ").getOrElse("") +
      kind.getOrElse("") +
      name.map(":" + _).getOrElse("")
}
