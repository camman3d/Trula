package examples.html

import com.joshmonson.trula.reducer.TreeReducer
import scala.xml.Elem
import com.joshmonson.trula.lib.{Save, Web}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/20/14
 * Time: 10:55 AM
 * To change this template use File | Settings | File Templates.
 */
object LinkExtractor {

  val html =
    <html>
      <body>
        <div id="container">
          <h1>This is a test</h1>
          <p>
            This is the first text.
            <a href="foo">Go somewhere</a>
          </p>
          <p>
            This is the first text.
            <a href="a">A Link</a> <a href="a">B Link</a>
          </p>
        </div>
      </body>
    </html>

  def main(args: Array[String]) {
    val treeReducer = new TreeReducer("a -> done = save(a)")
    treeReducer.use(Save)
    treeReducer.reduce(html)
    println(Save.saves)
  }

}
