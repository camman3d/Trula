package com.joshmonson.trula.parser.ast.lh

import com.joshmonson.trula.reducer.wrapping.Wrapper

case class Identifier(
                       label: Option[String] = None,
                       kind: Option[String] = None,
                       name: Option[String] = None,
                       index: Option[Int] = None,
                       parentage: Option[Parentage] = None,
                       not: Boolean = false
                       ) {

  def this(label: String = null, kind: String = null, name: String = null) {
    this(
      if (label == null) None else Some(label),
      if (kind == null) None else Some(kind),
      if (name == null) None else Some(name)
    )
  }

  def identifies(target: Identifier) = {
    kind.map(_ == target.kind.getOrElse("")).getOrElse(true) &&
      name.map(_ == target.name.getOrElse("")).getOrElse(true) &&
      index.map(_ == target.index.getOrElse(-1)).getOrElse(true)
  }

  override def toString: String =
    parentage.getOrElse("") +
      label.map("@" + _ + " ").getOrElse("") +
      kind.getOrElse("") +
      name.map(":" + _).getOrElse("") +
      index.map("(" + _ + ")").getOrElse("")

}


