package com.joshmonson.trula.reducer.wrapping

import com.joshmonson.trula.parser.ast.lh.Identifier
import scala.collection.JavaConversions._
import scala.xml.{Text, Node}
import scala.runtime.BoxedUnit
import scala.collection.mutable

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
    //    wrapFields()
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

  def size: Int = 1 + fields.map(_.size).sum

  def updateIndices() {
    for (i <- 0 until fields.size)
      fields(i).id = fields(i).id.copy(index = Some(i))
  }

  def toString(indent: String): String =
    indent + id + ": " + obj.map(o => Wrapper.deriveKind(o)) + "\n" +
      fields.map(_.toString(indent + "+  ")).mkString("")

  override def toString: String = toString("")
}

object Wrapper {

  /**
   * A wrapper map which looks at object references
   */
  private class WrapperCache {
    private var data = Set[(AnyRef, Wrapper)]()

    def contains(obj: AnyRef) = data.exists(d => d._1.eq(obj))
    def += (item: (AnyRef, Wrapper)) {
      data += item
    }
    def apply(obj: AnyRef) = data.find(d => d._1.eq(obj)).get._2

  }

  // This is where special wrapper functions are saved
  var handlers = Set[AnyRef => Option[Wrapper]]()

  def deriveKind(obj: Any) = {
    val name = obj.getClass.getSimpleName
    if (name == null)
      obj.getClass.getCanonicalName
    name
  }

  def isBasic(obj: Any) = obj match {
    case o: String => true
    case o: Byte => true
    case o: Char => true
    case o: Short => true
    case o: Int => true
    case o: Long => true
    case o: Float => true
    case o: Double => true
    case o: Boolean => true
    case _ => false
  }

  def wrap(obj: Any, name: Option[String] = None): Wrapper = {
    wrap(obj, name, new WrapperCache)
  }

  /**
   * Transforms objects to Wrappers
   */
  private def wrap(obj: Any, name: Option[String], cache: WrapperCache): Wrapper = obj match {
      // Basic objects/primitives
      case o if isBasic(o) => wrapBasic(o, name)

      // Check the cache to handle cycles
      case o: AnyRef if cache.contains(o) => cache(o)

      // XML
      case e: Node => wrapXml(e, cache).get

      // Collections
      case l: List[_] => wrapList(l, deriveKind(l), name, cache)
      case s: Set[_] => wrapList(s.toList, deriveKind(s), name, cache)
      case l: java.util.List[_] => wrap(l.toList, name, cache)
      case s: java.util.Set[_] => wrap(s.toSet, name, cache)

      // Other special things
      case Unit => new Wrapper(new Identifier(kind = "Unit"))
      case BoxedUnit.UNIT => new Wrapper(new Identifier(kind = "Unit"))
      case n if n == null => new Wrapper(new Identifier(kind = "null"))

      // General object
      case o: AnyRef => wrapObj(o, name, cache)
    }

  /**
   * Handles lists
   */
  private def wrapList(obj: List[_], kind: String, name: Option[String], cache: WrapperCache) = {
    val wrapper = new Wrapper(new Identifier(kind = Some(kind), name = name), obj)
    wrapper.fields = obj.toList.map(v => wrap(v, Some("value"), cache))
    wrapper.updateIndices()
    cache += obj -> wrapper
    wrapper
  }

  /**
   * Transforms basics/primitives to Wrappers
   */
  private def wrapBasic(obj: Any, name: Option[String]) = {
    val kind = if (obj.getClass.getSimpleName == null) obj.getClass.getName else obj.getClass.getSimpleName
    val wrapper = new Wrapper(new Identifier(kind = Some(kind), name = name), obj)
    wrapper
  }

  /**
   * Handles transforming generic objects to Wrappers
   */
  private def wrapObj(obj: AnyRef, name: Option[String], cache: WrapperCache): Wrapper = {

    // First, try the custom handlers
    for (handler <- handlers) {
      val result = handler(obj)
      if (result.isDefined) {
        cache += obj -> result.get
        return result.get
      }
    }

    // Wrap the object
    val kind = if (obj.getClass.getSimpleName == null) obj.getClass.getName else obj.getClass.getSimpleName
    val wrapper = new Wrapper(new Identifier(kind = Some(kind), name = name), obj)
    cache += obj -> wrapper

    // Wrap the fields
    def wrapFields(_class: Class[_]): List[Either[(String, String), Wrapper]] = {
      if (_class == classOf[Object] || _class == classOf[Any] || _class == classOf[AnyRef])
        Nil
      else {
        val fields = if (_class.getDeclaredFields == null) Nil else _class.getDeclaredFields.toList
        fields.map(f => {
          f.setAccessible(true)
          val fieldObj = f.get(obj)
          if (isBasic(fieldObj))
            Left(f.getName -> fieldObj.toString)
          else
            Right(wrap(fieldObj, Some(f.getName), cache))
        }) ++ wrapFields(_class.getSuperclass)
      }
    }
    val wrappedFields = wrapFields(obj.getClass)
    wrapper.fields = wrappedFields.filter(_.isRight).map(_.right.get)
    wrapper.id = wrapper.id.copy(properties = wrappedFields.filter(_.isLeft).map(_.left.get).toMap)
    wrapper.updateIndices()
    wrapper
  }

  /**
   * Handles transforming XML to Wrapper
   */
  private def wrapXml(node: Node, cache: WrapperCache): Option[Wrapper] = node match {
    case t: Text => {
      val wrapper = if (t.text.trim.isEmpty) None else Some(wrap(t.text.trim))
      wrapper.foreach(w => cache += node -> w)
      wrapper
    }
    case _ => {
      // Wrap the element
      val wrapper = new Wrapper(new Identifier(kind = Some(node.label)), node)

      // Wrap the properties and fields
      wrapper.id = wrapper.id.copy(properties = node.attributes.asAttrMap)
      wrapper.fields = node.child.toList.map(n => wrapXml(n, cache)).filter(_.isDefined).map(_.get)
      wrapper.updateIndices()
      cache += node -> wrapper
      Some(wrapper)
    }
  }
}