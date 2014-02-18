package com.joshmonson.trula.reducer

import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.parser.ast.lh.{LHStructure, Parentage}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
object StructureMatcher {

  /**
   * This matches a parentage declaration to a history list. The history list is ordered children left of parents.
   */
  def matchParentage(parentage: Option[Parentage], history: List[Wrapper]): Boolean = {
    if (parentage.isEmpty)
      true
    else if (history.isEmpty)
      false
    else if (parentage.get.isParent)
      parentage.get.id.identifies(history(0).id) && matchParentage(parentage.get.id.parentage, history.drop(1))
    else if (parentage.get.id.identifies(history(0).id))
      matchParentage(parentage.get.id.parentage, history.drop(1))
    else
      matchParentage(parentage, history.drop(1))
  }

  /**
   * This figures out if the given object matches the provided structure.
   */
  def matches(obj: Wrapper, structure: LHStructure, history: List[Wrapper] = Nil): Boolean = {
    // If the object & its parentage matches
    if (structure.id.identifies(obj.id) && matchParentage(obj.id.parentage, history)) {

      // Clear previous marks
      obj.clear()

      // Match the children
      val allFound = structure.children.forall(child => {

        // Look for the first matching field
        val field = obj.fields.find(w => !w.used && child.id.identifies(w.id) && matches(w, child, obj :: history))
        field.map(_.used = true)

        // If nothing is found the do a hard return, because we don't need to look further
        if (field.isDefined == child.id.not) {
          obj.clear()
          return false
        }
        true
      })
      obj.used = allFound
      allFound
    } else
      false
  }
}
