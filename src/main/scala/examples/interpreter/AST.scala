package examples.interpreter

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/23/14
 * Time: 7:32 PM
 * To change this template use File | Settings | File Templates.
 */
object AST {
  abstract class Expr
  case class IntExpr(i: Int) extends Expr
  case class BooleanExpr(b: Boolean) extends Expr
  case class DoubleExpr(d: Double) extends Expr
  case class StringExpr(s: String) extends Expr
  case class VarExpr(name: String) extends Expr
  case class CallExpr(name: String, args: Expr *) extends Expr

  object _Type extends Enumeration {
    type _Type = Value
    val _int = Value("int")
    val _bool = Value("boolean")
    val _double = Value("double")
    val _string = Value("string")
  }
  import _Type._

  abstract class Stmt
  case class VarDeclStmt(_type: _Type, name: String, value: Expr) extends Stmt
  case class VarAssignStmt(name: String, value: Expr) extends Stmt
  case class CallStmt(name: String, args: Expr *) extends Stmt

  case class Program(statements: Stmt *)
}
