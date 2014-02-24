package examples.interpreter

import scala.util.parsing.combinator.RegexParsers
import examples.interpreter.AST._
import examples.interpreter.AST.IntExpr
import examples.interpreter.AST.DoubleExpr
import examples.interpreter.AST.VarExpr
import examples.interpreter.AST.StringExpr
import com.joshmonson.trula.reducer.wrapping.Wrapper

object LanguageParser extends RegexParsers {

  val name = "[a-zA-Z_][a-zA-Z0-9_-]*".r
  val string = "\"" ~> "[^\"]+".r <~ "\""
  val int = "\\d+".r ^^ {_.toInt}
  val double = "\\d+\\.\\d+".r ^^ {_.toDouble}
  val boolean = ("true" | "false") ^^ {_.toBoolean}

  val intExpr = int ^^ IntExpr.apply
  val doubleExpr = double ^^ DoubleExpr.apply
  val booleanExpr = boolean ^^ BooleanExpr.apply
  val stringExpr = string ^^ StringExpr.apply
  val varExpr = name ^^ VarExpr.apply
  val callExpr = name ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
    case callName ~ args => CallExpr(callName, args : _*)
  }

  def expr: Parser[Expr] = callExpr | intExpr | doubleExpr | booleanExpr | stringExpr | varExpr

  val _type = ("int" | "boolean" | "string" | "double") ^^ _Type.withName
  val varDeclStmt = _type ~ name ~ ("=" ~> expr) ^^ {
    case varType ~ varName ~ value => VarDeclStmt(varType, varName, value)
  }
  val varAssignStmt = name ~ ("=" ~> expr) ^^ {
    case varName ~ value => VarAssignStmt(varName, value)
  }
  val callStmt = name ~ ("(" ~> repsep(expr, ",") <~ ")") ^^ {
    case callName ~ args => CallStmt(callName, args : _*)
  }
  val stmt = varDeclStmt | varAssignStmt | callStmt

  val program = phrase(rep(stmt)) ^^ {d => Program(d: _*)}

  def parse(str: String): Option[Program] = parse(program, str) match {
    case Success(result, _) => Some(result)
    case NoSuccess(error, _) => {
      println(error)
      None
    }
  }

  def main(args: Array[String]) {

    val code =
      """
        |int foobar = 1
        |boolean cheeseball = true
      """.stripMargin

    val ast = parse(code)
    println(Wrapper.wrap(ast))
  }

}
