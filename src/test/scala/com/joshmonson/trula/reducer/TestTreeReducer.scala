package com.joshmonson.trula.reducer

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.reducer.support.{A, B}
import com.joshmonson.trula.reducer.wrapping.Wrapper

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 9:54 PM
 * To change this template use File | Settings | File Templates.
 */
class TestTreeReducer {

  @Test
  def testNormalReduce1() {
    val a = Wrapper.wrap(new A(new B()))
    val treeReducer = new TreeReducer("A -> B = A")
    val reduced = treeReducer.reduce(a)
    assertTrue(reduced.fields.size == 1)
    assertEquals("B", reduced.id.kind.get)
  }

  @Test
  def testNormalReduce2() {
    val a = Wrapper.wrap(new A(new B()))
    val treeReducer = new TreeReducer("A { B } -> B { A }")
    val reduced = treeReducer.reduce(a)
    assertTrue(reduced.fields.size == 1)
    assertEquals("B", reduced.id.kind.get)
    assertEquals("A", reduced.fields(0).id.kind.get)
  }

  @Test
  def testNormalReduce3() {
    val a = Wrapper.wrap(new A(new B()))
    val treeReducer = new TreeReducer("A -> :result = foo(A)")
    treeReducer.treeBuilder.add("foo", (a: A) => "Hello")
    val reduced = treeReducer.reduce(a)
    assertTrue(reduced.fields.size == 0)
    assertEquals("String", reduced.id.kind.get)
    assertEquals("result", reduced.id.name.get)
    assertEquals("Hello", reduced.obj.get)
  }

  @Test
  def testInfiniteReduce() {
    val a = Wrapper.wrap(new A(new B()))
    val treeReducer = new TreeReducer("A -> A")
    try {
      treeReducer.reduce(a)
      fail()
    } catch {
      case e: NotReducibleException => assertTrue(true)
      case _: Throwable => fail()
    }
  }
}
