package com.joshmonson.trula.lib

import com.joshmonson.trula.reducer.TreeReducer
import com.joshmonson.trula.reducer.wrapping.Wrapper
import java.net.URL
import org.xml.sax.InputSource
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import scala.xml.parsing.NoBindingFactoryAdapter

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/23/14
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
object Web extends Library {


  def init(treeReducer: TreeReducer) {
    // Add in web-based wrapper handlers
    Wrapper.handlers += webHandler
  }

  def webHandler(obj: Any): Option[Wrapper] = obj match {
    case url: URL => Some({
      val factory = new SAXFactoryImpl
      val parser = factory.newSAXParser
      val source = new InputSource(url.toString)
      val adapter = new NoBindingFactoryAdapter
      val node = adapter.loadXML(source, parser)
      Wrapper.wrap(node)
    })
  }

}
