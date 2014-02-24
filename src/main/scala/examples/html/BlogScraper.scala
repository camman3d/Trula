package examples.html

import com.joshmonson.trula.reducer.TreeReducer
import com.joshmonson.trula.lib.{Save, Web}
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/23/14
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
object BlogScraper {

  def main(args: Array[String]) {

    val rule = """a[rel="bookmark"] { @title String } -> Done = save(@title)"""
    val treeReducer = new TreeReducer(rule).use(Web, Save)
    treeReducer.reduce(new URL("http://joshmonson.com/blog/"))
    println(Save.saves)

  }

}
