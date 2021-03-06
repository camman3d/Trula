package com.joshmonson.trula.reducer

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.support._
import com.joshmonson.trula.parser.ast.lh.Identifier
import java.util
import com.joshmonson.trula.parser.ast.lh.Identifier
import scala.Some

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
class TestWrapper {

  @Test
  def testConstructor1() {
    val wrapper = new Wrapper(new Identifier(kind = "Foo", name = "Bar"))
    assertEquals("Foo", wrapper.id.kind.get)
    assertEquals("Bar", wrapper.id.name.get)
  }

  @Test
  def testConstructor2() {
    val a = new A(new B())
    val wrapper = new Wrapper(new Identifier(kind = "Foo", name = "Bar"), a)
    assertEquals("Foo", wrapper.id.kind.get)
    assertEquals("Bar", wrapper.id.name.get)
    assertTrue(wrapper.fields.size == 0)
  }

  @Test
  def testConstructor3() {
    val wrapper = new Wrapper("FooBar")
    assertEquals("FooBar", wrapper.id.kind.get)
  }

  @Test
  def testWrap() {
    val a = new A(new B())
    val wrapper = Wrapper.wrap(a)
    assertEquals("A", wrapper.id.kind.get)
    assertTrue(wrapper.fields.size == 1)
    assertEquals("B", wrapper.fields(0).id.kind.get)
    assertEquals("b", wrapper.fields(0).id.name.get)
  }

  @Test
  def testWrapProperties() {
    val c = new C("Foo", 32)
    val wrapper = Wrapper.wrap(c)
    assertTrue(wrapper.fields.size == 0)
    assertTrue(wrapper.id.properties.size == 2)
    assertEquals("Foo", wrapper.id.properties("foo"))
    assertEquals("32", wrapper.id.properties("i"))
  }

  @Test
  def testScalaObjects() {
    val d = new D(Some("This"), Right(2.1), ("One", "two", 3))
    val wrapper = Wrapper.wrap(d)
    assertTrue(wrapper.fields.size == 3)
    assertEquals("Some", wrapper.fields(0).id.kind.get)
    assertEquals("Right", wrapper.fields(1).id.kind.get)
    assertEquals("Tuple3", wrapper.fields(2).id.kind.get)
  }

  @Test
  def testWrapJavaList() {
    val list = new util.ArrayList[String]()
    list.add("One")
    list.add("Two")
    list.add("Three")
    val wrapper = Wrapper.wrap(list)
    assertTrue(wrapper.fields.size == 3)
  }

  @Test
  def testWrapScalaList() {
    val list = List("One", "Two", "Three")
    val wrapper = Wrapper.wrap(list)
    assertTrue(wrapper.fields.size == 3)
  }

  @Test
  def testWrapJavaSet() {
    val list = new util.HashSet[String]()
    list.add("One")
    list.add("Two")
    list.add("Three")
    val wrapper = Wrapper.wrap(list)
    assertTrue(wrapper.fields.size == 3)
  }

  @Test
  def testWrapScalaSet() {
    val list = Set("One", "Two", "Three")
    val wrapper = Wrapper.wrap(list)
    assertTrue(wrapper.fields.size == 3)
  }

  @Test
  def testWrapXML() {
    val xml = <one id="foo"><two></two><three></three></one>
    val wrapper = Wrapper.wrap(xml)
    assertEquals("one", wrapper.id.kind.get)
    assertTrue(wrapper.fields.size == 2)
    assertTrue(wrapper.id.properties.size == 1)
    assertEquals("foo", wrapper.id.properties("id"))
    assertEquals("two", wrapper.fields(0).id.kind.get)
    assertEquals("three", wrapper.fields(1).id.kind.get)
  }

  @Test
  def testWrapCycle() {
    val e1 = E1(null)
    val e2 = E2(e1)
    e1.e2 = e2

    val wrapper = Wrapper.wrap(e1)
    assertEquals("E1", wrapper.id.kind.get)
    assertEquals("E2", wrapper.fields(0).id.kind.get)
    assertEquals("E1", wrapper.fields(0).fields(0).id.kind.get)
  }

}
