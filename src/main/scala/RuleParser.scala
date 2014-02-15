import ast.{Structure, Parentage, Reference, Identifier}
import scala.util.parsing.combinator.RegexParsers

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/14/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
object RuleParser extends RegexParsers {

  val text = "[a-zA-Z0-9-_]+".r
  val label = "@" ~ text ^^ {_._2}
  val name = ":" ~ text ^^ {_._2}
  val index = "(" ~ "\\d+".r ~ ")" ^^ {_._1._2.toInt}

  // Different ways to identify on the left-hand
  val kindId = (label ?) ~ text ~ (index ?) ^^ {d =>
    Identifier(d._1._1, Some(d._1._2), None, d._2)
  }
  val nameId = (label ?) ~ name ~ (index ?) ^^ {d =>
    Identifier(d._1._1, None, Some(d._1._2), d._2)
  }
  val kindNameId = (label ?) ~ text ~ name ~ (index ?) ^^ {d =>
    Identifier(d._1._1._1, Some(d._1._1._2), Some(d._1._2), d._2)
  }
  val basicId = kindId | nameId | kindNameId

  // Now add in ancestor identification
  val ancestor = basicId ~ ">>" ^^ {d =>
    Parentage(d._1, parent = false)
  }
  val parent = basicId ~ ">" ^^ {d =>
    Parentage(d._1, parent = true)
  }
  val history = rep(ancestor | parent) ^^ {d =>
    d.foldLeft(None: Option[Parentage])((p1, p2) => Some(p2.copy(p2.id, p2.parent, p1)))
  }
  val id = history ~ basicId ^^ {d => d._2.copy(parentage = d._1)}

  // Define how the left-hand structure looks
//  def basicStructure: Parser[Structure] = id ~ (("{" ~ rep(basicStructure) ~ "}") ?) ^^ {d =>
//    new Structure(d._1, d._2.map(_._1._2).getOrElse(Nil))
//  }
  
  // Different ways to reference on the right-hand
  val labelRef = label ^^ {d => Reference(Some(d))}
  val kindRef = text ^^ {d => Reference(kind = Some(d))}
  val nameRef = name ^^ {d => Reference(name = Some(d))}
  val kindNameRef = text ~ name ^^ {d => new Reference(None, Some(d._1), Some(d._2))}
  val ref = kindNameRef | labelRef | kindRef | nameRef








  def parse(str: String): Unit = parse(id, str) match {
    case Success(result, _) => {
      println(result)
    }
    case failure: NoSuccess => println("Failure: " + failure)
  }

  def main(args: Array[String]) {
    RuleParser.parse("Foo")
    RuleParser.parse("Foo > Bar")
    RuleParser.parse("Foo >> Bar > Baz")
    RuleParser.parse("Bar")
    RuleParser.parse("Baz")
  }

}
