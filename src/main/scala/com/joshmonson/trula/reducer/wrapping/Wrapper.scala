package com.joshmonson.trula.reducer.wrapping

import com.joshmonson.trula.parser.ast.lh.Identifier
import scala.collection.Seq
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
class Wrapper(var id: Identifier) {
  var obj: Option[Any] = None
  var fields: List[Wrapper] = Nil
  var used = false

  def this(id: Identifier, obj: Any) {
    this(id)
    this.obj = Some(obj)
    wrapFields()
  }

  def this(kind: String) {
    this(new Identifier(kind = kind))
  }

  def clear() {
    used = false
    fields.map(_.clear())
  }

  def add(field: Wrapper) {
    if (field.id.index.isDefined) {
      val index = math.min(fields.size, field.id.index.get)
      fields = fields.slice(0, index) ::: List(field) ::: fields.slice(index, fields.size)
    } else
      fields :::= List(field)
  }

  def remove(field: Wrapper) {
    fields = fields.filterNot(_ == field)
  }



  private def wrapFields(_class: Class[_]): List[Wrapper] = {
    val typeName = if (_class.getCanonicalName == null) _class.getName else _class.getCanonicalName
    if (_class == classOf[Any] || _class == classOf[AnyRef] || _class == classOf[Object] ||
      typeName.startsWith("java.lang") || Wrapper.primitives.contains(typeName))

      // Don't pull out fields
      Nil
    else if (classOf[Seq[_]].isAssignableFrom(_class)) {

      // Handle scala lists
      obj.get.asInstanceOf[Seq[_]].toList.map(d => {
        new Wrapper(new Identifier(kind = d.getClass.getSimpleName, name = "value"), d)
      })
    } else if (classOf[Set[_]].isAssignableFrom(_class)) {

      // Handle scala sets
      obj.get.asInstanceOf[Set[_]].toList.map(d => {
        new Wrapper(new Identifier(kind = d.getClass.getSimpleName, name = "value"), d)
      })
    } else if (classOf[java.util.List[_]].isAssignableFrom(_class)) {

      // Handle Java lists by converting them to scala
      val javaList = obj.get.asInstanceOf[java.util.List[_]]
      val scalaList = javaList.toList
      obj = Some(scalaList)
      wrapFields(scalaList.getClass)
    } else if (classOf[java.util.Set[_]].isAssignableFrom(_class)) {

      // Handle Java sets by converting them to scala
      val javaSet = obj.get.asInstanceOf[java.util.Set[_]]
      val scalaSet = javaSet.toSet
      obj = Some(scalaSet)
      wrapFields(scalaSet.getClass)
    } else {

      // Generic object. Recursively pull out fields
      val declaredFields = if (_class.getDeclaredFields == null) Nil else _class.getDeclaredFields.toList
      declaredFields.map(field => {
        field.setAccessible(true)
        new Wrapper(new Identifier(kind = field.getType.getSimpleName, name = field.getName), field.get(obj.get))
      }) ::: wrapFields(_class.getSuperclass)
    }
  }

  private def wrapFields() {
    fields = wrapFields(obj.get.getClass)
    updateIndices()
  }

  def updateIndices() {
    for (i <- 0 until fields.size)
      fields(i).id = fields(i).id.copy(index = Some(i))
  }

}

object Wrapper {
  private val primitives = List("int", "double", "char", "double", "float", "long", "byte", "boolean")
  def wrap(obj: Any) = new Wrapper(new Identifier(kind = Some(obj.getClass.getSimpleName)), obj)
}