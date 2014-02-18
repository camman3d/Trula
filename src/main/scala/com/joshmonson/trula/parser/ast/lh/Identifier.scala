package com.joshmonson.trula.parser.ast.lh

case class Identifier(
                       label: Option[String] = None,
                       kind: Option[String] = None,
                       name: Option[String] = None,
                       index: Option[Int] = None,
                       parentage: Option[Parentage] = None,
                       not: Boolean = false
                       ) {

  override def toString: String =
    parentage.getOrElse("") +
      label.map("@" + _ + " ").getOrElse("") +
      kind.getOrElse("") +
      name.map(":" + _).getOrElse("") +
      index.map("(" + _ + ")").getOrElse("")

}


