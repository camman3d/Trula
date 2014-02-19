package com.joshmonson.trula.reducer.core

import com.joshmonson.trula.parser.ast.rh._
import com.joshmonson.trula.reducer.wrapping.Wrapper
import scala.collection.mutable
import com.joshmonson.trula.parser.ast.rh.RHStructure
import com.joshmonson.trula.parser.ast.rh.Method
import com.joshmonson.trula.parser.ast.rh.Assignment

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
class TreeBuilder {

  val methods: mutable.Map[String, List[Wrapper] => Wrapper] = mutable.Map()


  // Method helpers
  private def wrap(result: Any) = result match {
    case w: Wrapper => w
    case d: Any => Wrapper.wrap(d)
  }
  private def prep[T](a: Wrapper) = a.obj.get.asInstanceOf[T]
  def add(n: String, f: () => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f()))
  }
  def add[T1](n: String, f: (T1) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)))))
  }
  def add[T1, T2](n: String, f: (T1, T2) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)), prep(a(1)))))
  }
  def add[T1, T2, T3](n: String, f: (T1, T2, T3) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)), prep(a(1)), prep(a(2)))))
  }
  def add[T1, T2, T3, T4](n: String, f: (T1, T2, T3, T4) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)), prep(a(1)), prep(a(2)), prep(a(3)))))
  }
  def add[T1, T2, T3, T4, T5](n: String, f: (T1, T2, T3, T4, T5) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)), prep(a(1)), prep(a(2)), prep(a(3)), prep(a(4)))))
  }
  def add[T1, T2, T3, T4, T5, T6](n: String, f: (T1, T2, T3, T4, T5, T6) => Any) {
    methods += n -> ((a: List[Wrapper]) => wrap(f(prep(a(0)), prep(a(1)), prep(a(2)), prep(a(3)), prep(a(4)), prep(a(5)))))
  }

  def build(definition: Definition, store: ObjectStore): Wrapper = definition match {
    case structure: RHStructure => {

      // First build the root
      val obj = store.data
        .find(e => definition.id.references(e.id)).map(_.obj)
        .getOrElse(new Wrapper(definition.id.toId))
      obj.used = true

      // Now add the children
      for (child <- structure.children)
        obj.add(build(child, store))
      obj
    }
    case assignment: Assignment => {
      val value = assignment.target match {
        case method: Method => {
          val args = method.args.map(arg => store.data.find(e => arg.references(e.id)).get.obj)
          methods(method.name)(args)
        }
        case structure: RHStructure => build(structure, store)
      }

      // Overwrite the identifier
      if (assignment.id.name.isDefined)
        value.id = value.id.copy(name = assignment.id.name)
      if (assignment.id.kind.isDefined)
        value.id = value.id.copy(kind = assignment.id.kind)
      value
    }
    case _: Deletion => null
  }

}
