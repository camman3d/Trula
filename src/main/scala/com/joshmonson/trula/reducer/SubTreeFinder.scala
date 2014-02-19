package com.joshmonson.trula.reducer

import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.parser.ast.lh.LHStructure
import com.joshmonson.trula.reducer.StructureMatcher.matches

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
object SubTreeFinder {

  def find(obj: Wrapper, structure: LHStructure, history: List[Wrapper] = Nil): Option[ObjectStore] = {
    if (matches(obj, structure, history)) {
      val store = flatten(obj, structure, history)

      // Look for and flatten descendants
      for (descendant <- structure.descendants) {
        var dStore: Option[ObjectStore] = None
        for (field <- obj.fields)
          if (dStore.isEmpty)
            dStore = find(field, descendant, obj :: history)

        // If we found the descendant tree, add it to the store
        if (dStore.isDefined)
          store.data = store.data ++ dStore.get.data
        else {

          // Otherwise revert everything back to how it was
          store.restore()
          return None
        }
      }
      Some(store)
    } else {

      // Recursively look for the structure in the children fields
      for (field <- obj.fields) {
        val fStore = find(field, structure, obj :: history)
        if (fStore.isDefined)
          return fStore
      }
      None
    }
  }

  def flatten(obj: Wrapper, structure: LHStructure, history: List[Wrapper]): ObjectStore = {

    // Remove the object from its parent
    obj.clear()
    val parent = if (history.isEmpty) None else Some(history(0))
    if (parent.isDefined)
      parent.get.remove(obj)

    // Add the object to the store
    val store = new ObjectStore
    store.add(StoreEntry(structure.id, parent, obj))

    // Remove the children and them flattened
    structure.children.filterNot(_.id.not).foreach(child => {
      val field = obj.fields.find(w => child.id.identifies(w.id) && matches(w, child, obj :: history))
      store.data = store.data ++ flatten(field.get, child, obj :: history).data
    })

    store
  }

}
