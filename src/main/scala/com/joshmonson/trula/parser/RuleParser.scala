package com.joshmonson.trula.parser

import scala.util.parsing.combinator.RegexParsers
import com.joshmonson.trula.parser.ast._
import com.joshmonson.trula.parser.ast.rh._
import com.joshmonson.trula.parser.ast.rh.RHStructure
import com.joshmonson.trula.parser.ast.rh.Reference
import scala.Some
import com.joshmonson.trula.parser.ast.rh.Method
import com.joshmonson.trula.parser.ast.lh.{Parentage, LHStructure}
import com.joshmonson.trula.parser.ast.lh.Identifier

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/14/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
object RuleParser extends RegexParsers {

  val string = "\"(\\\\\"|[^\"])+\"".r ^^ {
    d =>
      d.substring(1, d.length - 1).replaceAll("\\\\\"", "\"")
  }
  val text = "[a-zA-Z0-9-_]+".r
  val label = "@" ~ text ^^ {
    _._2
  }
  val name = ":" ~ text ^^ {
    _._2
  }
  val index = "(" ~ "\\d+".r ~ ")" ^^ {
    _._1._2.toInt
  }
  val not = ("!" ?) ^^ {
    _.isDefined
  }

  // Different ways to identify on the left-hand
  val property = (text | string) ~ "=" ~ string ^^ {
    d =>
      d._1._1 -> d._2
  }
  val properties = "[" ~ property ~ rep("," ~ property) ~ "]" ^^ {
    d =>
      d._1._2.map(_._2).toMap + d._1._1._2
  }
  val kindId = (label ?) ~ text ^^ {
    d =>
      Identifier(d._1, Some(d._2), None)
  }
  val nameId = (label ?) ~ name ^^ {
    d =>
      Identifier(d._1, None, Some(d._2))
  }
  val kindNameId = (label ?) ~ text ~ name ^^ {
    d =>
      Identifier(d._1._1, Some(d._1._2), Some(d._2))
  }
  val wildcard = (label ?) ~ "%" ^^ {
    d =>
      Identifier(d._1)
  }
  val basicId = not ~ (kindNameId | kindId | nameId | wildcard) ~ (index ?) ~ (properties ?) ^^ {
    d =>
      d._1._1._2.copy(index = d._1._2, not = d._1._1._1, properties = d._2.getOrElse(Map()))
  }

  // Now add in ancestor identification
  val ancestor = basicId ~ ">>" ^^ {
    d =>
      Parentage(d._1, isParent = false)
  }
  val parent = basicId ~ ">" ^^ {
    d =>
      Parentage(d._1, isParent = true)
  }
  val history = rep(ancestor | parent) ^^ {
    d =>
      d.foldLeft(None: Option[Parentage])((p1, p2) => Some(p2.copy(p2.id.copy(parentage = p1))))
  }
  val id = history ~ basicId ^^ {
    d => d._2.copy(parentage = d._1)
  }

  // Define how the left-hand structure looks
  val descendants = "[" ~ rep(lhStructureDef) ~ "]" ^^ {
    _._1._2
  }

  def lhStructureDef: Parser[LHStructure] = id ~ (("{" ~ rep(lhStructureDef) ~ (descendants ?) ~ "}") ?) ^^ {
    d =>
      LHStructure(d._1, d._2.map(_._1._1._2).getOrElse(Nil), d._2.map(_._1._2.getOrElse(Nil)).getOrElse(Nil))
  }

  // Different ways to reference on the right-hand
  val labelRef = label ^^ {
    d => Reference(Some(d))
  }
  val kindRef = text ^^ {
    d => Reference(kind = Some(d))
  }
  val nameRef = name ^^ {
    d => Reference(name = Some(d))
  }
  val kindNameRef = text ~ name ^^ {
    d => new Reference(None, Some(d._1), Some(d._2))
  }
  val ref = (kindNameRef | labelRef | kindRef | nameRef) ~ (properties ?) ^^ {
    d =>
      d._1.copy(properties = d._2.getOrElse(Map()))
  }

  // Define how the right-hand structure looks
  val rhDef: Parser[Definition] = rhAssignment | rhStructure

  def rhStructure = ref ~ (("{" ~ rep(rhDef) ~ "}") ?) ^^ {
    d =>
      val children = d._2.map(_._1._2).getOrElse(Nil)
      RHStructure(d._1, children)
  }

  def rhMethod = text ~ "(" ~ ((ref ~ rep("," ~ ref)) ?) ~ ")" ^^ {
    d =>
      val args = d._1._2.map(p => p._1 :: p._2.map(_._2)).getOrElse(Nil)
      Method(d._1._1._1, args)
  }

  def rhAssignment = ref ~ "=" ~ (rhMethod | rhStructure) ^^ {
    d =>
      Assignment(d._1._1, d._2)
  }

  val rhDeletion = "~" ^^ {
    d => Deletion()
  }

  // Rule definitions
  val rule = lhStructureDef ~ "->" ~ (rhDef | rhDeletion) ^^ {
    d =>
      Rule(d._1._1, d._2)
  }
  val ruleList = rep(rule)


  def parse(str: String): Option[List[Rule]] = parseRule(ruleList, str)

  def parseRule[T](rule: RuleParser.Parser[T], str: String): Option[T] = parse(rule, str.trim) match {
    case Success(result, input) => {
      if (input.atEnd)
        Some(result)
      else
        None
    }
    case _: NoSuccess => None
  }

}
