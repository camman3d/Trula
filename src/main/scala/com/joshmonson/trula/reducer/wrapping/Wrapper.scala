package com.joshmonson.trula.reducer.wrapping

import com.joshmonson.trula.parser.ast.lh.Identifier
import scala.collection.Seq
import scala.collection.JavaConversions._
import scala.xml.{Text, Node, Elem}
import scala.runtime.BoxedUnit

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
  private val primitives = List("int", "double", "char", "double", "float", "long", "byte", "boolean")

  def deriveKind(obj: Any) = {
    val name = obj.getClass.getSimpleName
    if (name == null)
      obj.getClass.getCanonicalName
    name
  }
  
  //  def wrap(obj: Any) = new Wrapper(new Identifier(kind = Some(obj.getClass.getSimpleName)), obj)
  def wrap(obj: Any, name: Option[String] = None): Wrapper = obj match {
    case e: Node => wrapXml(e).get

    // Collections
    case l: List[_] => wrapList(l, deriveKind(l), name)
    case s: Set[_] => wrapList(s.toList, deriveKind(s), name)
    case l: java.util.List[_] => wrap(l.toList, name)
    case s: java.util.Set[_] => wrap(s.toSet, name)

    // Basic objects/primitives
    case o: String  => wrapBasic(o, name)
    case o: Byte    => wrapBasic(o, name)
    case o: Char    => wrapBasic(o, name)
    case o: Short   => wrapBasic(o, name)
    case o: Int     => wrapBasic(o, name)
    case o: Long    => wrapBasic(o, name)
    case o: Float   => wrapBasic(o, name)
    case o: Double  => wrapBasic(o, name)
    case o: Boolean => wrapBasic(o, name)

    // Other special things
    case Unit           => new Wrapper(new Identifier(kind = "Unit"))
    case BoxedUnit.UNIT => new Wrapper(new Identifier(kind = "Unit"))

    // General object
    case _ => wrapObj(obj, name)
  }
  
  private def wrapList(obj: List[_], kind: String, name: Option[String]) = {
    val wrapper = new Wrapper(new Identifier(kind = Some(kind), name = name), obj)
    wrapper.fields = obj.toList.map(v => wrap(v, Some("value")))
    wrapper.updateIndices()
    wrapper
  }
  
  private def wrapBasic(obj: Any, name: Option[String]) = {
    val kind = if (obj.getClass.getSimpleName == null) obj.getClass.getName else obj.getClass.getSimpleName
    new Wrapper(new Identifier(kind = Some(kind), name = name), obj)
  }

  private def wrapObj(obj: Any, name: Option[String]): Wrapper = {

    // Check for null
    if (obj == null)
      return new Wrapper(new Identifier(kind = "null"))

    // Wrap the object
    val kind = if (obj.getClass.getSimpleName == null) obj.getClass.getName else obj.getClass.getSimpleName
    val wrapper = new Wrapper(new Identifier(kind = Some(kind), name = name), obj)

    // Wrap the fields
    def wrapFields(_class: Class[_]): List[Wrapper] = {
      if (_class == classOf[Object] || _class == classOf[Any] || _class == classOf[AnyRef])
        Nil
      else {
        val fields = if (_class.getDeclaredFields == null) Nil else _class.getDeclaredFields.toList
        fields.map(f => {
          f.setAccessible(true)
          val fieldObj = f.get(obj)
          wrap(fieldObj, Some(f.getName))
        }) ++ wrapFields(_class.getSuperclass)
      }
    }
    wrapper.fields = wrapFields(obj.getClass)
    wrapper.updateIndices()
    wrapper
  }

    private def wrapXml(node: Node): Option[Wrapper] = node match {
      case t: Text => if (t.text.trim.isEmpty) None else Some(wrap(t.text.trim))
      case _ => {
        // Wrap the element
        val wrapper = new Wrapper(new Identifier(kind = Some(node.label)), node)

        // Wrap the children
        wrapper.fields = node.child.toList.map(wrapXml).filter(_.isDefined).map(_.get)
        wrapper.updateIndices()
        Some(wrapper)
      }
    }
}