package com.joshmonson.trula.reducer.core

import com.joshmonson.trula.parser.ast.lh.Identifier
import com.joshmonson.trula.reducer.wrapping.Wrapper

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
case class StoreEntry(id: Identifier, parent: Option[Wrapper], obj: Wrapper) {
  def restore() {
    if (parent.isDefined)
      parent.get.add(obj)
  }
}

class ObjectStore {
  var data: List[StoreEntry] = Nil
  var parent: Option[Wrapper] = None

  def restore() {
    data.foreach(_.restore())
  }

  def add(entry: StoreEntry) {
    data = data :+ entry
    if (parent.isEmpty)
      parent = entry.parent
  }
}
