package com.joshmonson.trula.parser.ast.rh

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

  override def toString: String =
    label.map("@" + _ + " ").getOrElse("") +
      kind.getOrElse("") +
      name.map(":" + _).getOrElse("")
}
