package com.joshmonson.trula.lib

import com.joshmonson.trula.reducer.TreeReducer

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/23/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
trait Library {
  def init(treeReducer: TreeReducer)
}
