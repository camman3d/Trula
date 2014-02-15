package ast

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/14/14
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
case class Parentage(id: Identifier, parent: Boolean, parentage: Option[Parentage] = None) {
  override def toString: String = id + (if (parent) " >" else " >>")
}

case class Identifier(
                       label: Option[String] = None,
                       kind: Option[String] = None,
                       name: Option[String] = None,
                       index: Option[Int] = None,
                       parentage: Option[Parentage] = None
                       ) {

  override def toString: String =
    parentage.getOrElse("") +
      label.map("@" + _ + " ").getOrElse("") +
      kind.getOrElse("") +
      name.map(":" + _).getOrElse("") +
      index.map("(" + _ + ")").getOrElse("")

}

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
