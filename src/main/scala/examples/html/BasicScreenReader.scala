package examples.html

import com.joshmonson.trula.reducer.TreeReducer

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/20/14
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
object BasicScreenReader {

  val html =
    <html foo="bar">
      <body>
        <div id="container">
          <h1>This is a test</h1>
          <p>
            This is the first text.
            <a href="foo">Go somewhere</a>
          </p>
          <p>
            This is the second text.
            <a href="a">A Link</a> <a href="a">B Link</a>
            One other thought
          </p>
        </div>
      </body>
    </html>

  def main(args: Array[String]) {
    val treeReducer = new TreeReducer("p { @s String } -> p { :done = print(@s) }")
    treeReducer.addMethod("print", (s: String) => println(s))
    treeReducer.reduce(html)
  }

}
