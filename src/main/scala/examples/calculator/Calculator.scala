package examples.calculator

import com.joshmonson.trula.reducer.TreeReducer
import scala.util.parsing.combinator.RegexParsers

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/19/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
object Calculator {

  case class Add(lh: Any, rh: Any)
  case class Sub(lh: Any, rh: Any)
  case class Mul(lh: Any, rh: Any)
  case class Div(lh: Any, rh: Any)

  val rules =
    """
      |Add { @lh Integer  @rh Integer } -> :result = add(@lh, @rh)
      |Sub { @lh Integer  @rh Integer } -> :result = sub(@lh, @rh)
      |Mul { @lh Integer  @rh Integer } -> :result = mul(@lh, @rh)
      |Div { @lh Integer  @rh Integer } -> :result = div(@lh, @rh)
    """.stripMargin

  val treeReducer = new TreeReducer(rules)
  treeReducer.addMethod("add", (a: Int, b: Int) => a + b)
  treeReducer.addMethod("sub", (a: Int, b: Int) => a - b)
  treeReducer.addMethod("mul", (a: Int, b: Int) => a * b)
  treeReducer.addMethod("div", (a: Int, b: Int) => a / b)


  def main(args: Array[String]) {

    List(
      Add(14, -16),
      Add(Mul(2, 3), Sub(4, 2)),
      Div(Add(2, 8), 2)
    ).foreach(example => {
      val result = treeReducer.reduce(example)
      println("Result: " + result.obj.get)
    })
    
  }
  
}
