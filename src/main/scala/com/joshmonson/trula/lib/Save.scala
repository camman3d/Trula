package com.joshmonson.trula.lib

import com.joshmonson.trula.reducer.TreeReducer

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/23/14
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
object Save extends Library {

  // This is where stuff gets saved
  var saves = Set[Any]()
  
  // This is how stuff gets saved. You can change this
  var save: Any => Any = (n: Any) => {
    saves += n
    n
  }
  
  def init(treeReducer: TreeReducer) {
    treeReducer.addMethod("save", save)
  }
}
