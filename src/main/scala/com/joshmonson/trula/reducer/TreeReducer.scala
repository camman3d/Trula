package com.joshmonson.trula.reducer

import com.joshmonson.trula.parser.RuleParser
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.core.{TreeBuilder, SubTreeFinder}
import com.joshmonson.trula.lib.Library

/**
 * The entry point for using Trula for tree reduction.
 *
 * @param ruleText The Trula rules
 */
class TreeReducer(ruleText: String) {

  // Parse the rules
  private val rules = {
    val r = RuleParser.parse(ruleText)
    if (r.isDefined)
      r.get
    else
      throw new InvalidSyntaxException()
  }

  private val treeBuilder = new TreeBuilder

  /**
   * Reduces an tree-structured object based on the defined rules.
   * @param obj The object to reduce. If it is not already wrapped, then a new [[com.joshmonson.trula.reducer.wrapping.Wrapper]] is created.
   * @return The reduced object, contained within a [[com.joshmonson.trula.reducer.wrapping.Wrapper]]
   */
  def reduce(obj: Any): Wrapper = {
    var tree = obj match {
      case w: Wrapper => w
      case a: Any => Wrapper.wrap(a)
    }

    var reduceCount = 0
    var size = tree.size
    var done = false
    while (!done) {
      done = true
      tree.updateIndices()

      // Try each rule
      for (rule <- rules) {
        if (done) {
          val subTree = SubTreeFinder.find(tree, rule.lh)
          if (subTree.isDefined) {

            // Replace with changed structure
            done = false
            val reduced = treeBuilder.build(rule.rh, subTree.get)
            if (reduced != null) {
              if (subTree.get.parent.isDefined) {
                reduced.id = reduced.id.copy(index = subTree.get.parent.get.id.index)
                subTree.get.parent.get.add(reduced)
              } else
                tree = reduced
            }
          }
        }
      }

      // Let's add a check to safeguard against non-reducibility
      reduceCount += 1
      if (reduceCount >= TreeReducer.reduceThreshold) {
        val treeSize = tree.size
        if (size < treeSize) {
          reduceCount = 0
          size = treeSize
        } else
          throw new NotReducibleException("Infinite loop while reducing.")
      }
    }
    tree
  }

  // Methods

  def use(libs: Library *) = {
    for (lib <- libs)
      lib.init(this)
    this
  }

  def addMethod(n: String, f: () => Any) = treeBuilder.add(n, f)

  def addMethod[T1](n: String, f: (T1) => Any) = treeBuilder.add(n, f)

  def addMethod[T1, T2](n: String, f: (T1, T2) => Any) = treeBuilder.add(n, f)

  def addMethod[T1, T2, T3](n: String, f: (T1, T2, T3) => Any) = treeBuilder.add(n, f)

  def addMethod[T1, T2, T3, T4](n: String, f: (T1, T2, T3, T4) => Any) = treeBuilder.add(n, f)

  def addMethod[T1, T2, T3, T4, T5](n: String, f: (T1, T2, T3, T4, T5) => Any) = treeBuilder.add(n, f)

  def addMethod[T1, T2, T3, T4, T5, T6](n: String, f: (T1, T2, T3, T4, T5, T6) => Any) = treeBuilder.add(n, f)

}

object TreeReducer {
  var reduceThreshold = 100
}
