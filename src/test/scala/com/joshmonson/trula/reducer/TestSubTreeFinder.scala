package com.joshmonson.trula.reducer

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.parser.RuleParser
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.support.{B, A}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
class TestSubTreeFinder {

  @Test
  def testFindSimple() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 1)
    assertEquals("A", store.get.data(0).id.kind.get)
  }

  @Test
  def testFindChild() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("B -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 1)
    assertEquals("B", store.get.data(0).id.kind.get)
  }

  @Test
  def testFindParentAndChild() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A { B } -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 2)
    assertEquals("A", store.get.data(0).id.kind.get)
    assertEquals("B", store.get.data(1).id.kind.get)
  }

  @Test
  def testFindAncestryParent() {
    val b1 = new Wrapper("B")
    b1.fields = List(new Wrapper("C"))
    val b2 = new Wrapper("B")
    b2.fields = List(new Wrapper("D"))
    val a = new Wrapper("A")
    a.fields = List(b1, b2, new Wrapper("D"))
    
    val structure = RuleParser.parse("B > D -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 1)
    assertEquals("D", store.get.data(0).id.kind.get)
    assertEquals(b2, store.get.data(0).parent.get)
  }

  @Test
  def testFindAncestryAncestor() {
    val b1 = new Wrapper("B")
    b1.fields = List(new Wrapper("C"))
    val b2 = new Wrapper("B")
    b2.fields = List(new Wrapper("D"))
    val a = new Wrapper("A")
    a.fields = List(b1, b2, new Wrapper("D"))

    val structure = RuleParser.parse("C >> A -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 1)
    assertEquals("A", store.get.data(0).id.kind.get)
  }

  @Test
  def testFindDescendant1() {
    val b1 = new Wrapper("B")
    b1.fields = List(new Wrapper("C"))
    val b2 = new Wrapper("B")
    b2.fields = List(new Wrapper("D"))
    val a = new Wrapper("A")
    a.fields = List(b1, b2, new Wrapper("D"))

    val structure = RuleParser.parse("A {[ D ]} -> X").get(0).lh
    val store = SubTreeFinder.find(a, structure)
    assertTrue(store.get.data.size == 2)
    assertEquals("A", store.get.data(0).id.kind.get)
    assertEquals("D", store.get.data(1).id.kind.get)
    assertEquals(b2, store.get.data(1).parent.get)
  }

}
