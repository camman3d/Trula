package com.joshmonson.trula.reducer

import org.junit.Test
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.support.{B, A}
import com.joshmonson.trula.parser.RuleParser
import org.junit.Assert._
import com.joshmonson.trula.reducer.core.{TreeBuilder, SubTreeFinder}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
class TestTreeBuilder {

  @Test
  def testDelete() {
    val a = Wrapper.wrap(new A(new B()))
    val rule = RuleParser.parse("B -> ~").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val rebuilt = new TreeBuilder().build(rule.rh, store.get)
    assertNull(rebuilt)
    assertTrue(a.fields.isEmpty)
  }

  @Test
  def testBuildStructure() {
    val a = Wrapper.wrap(new A(new B()))
    val rule = RuleParser.parse("A { B } -> B { A }").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val rebuilt = new TreeBuilder().build(rule.rh, store.get)
    assertEquals("B", rebuilt.id.kind.get)
    assertEquals(a, rebuilt.fields(0))
  }

  @Test
  def testBuildStructureAssignment() {
    val a = Wrapper.wrap(new A(new B()))
    val rule = RuleParser.parse("A -> C = A").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val rebuilt = new TreeBuilder().build(rule.rh, store.get)
    assertEquals("C", rebuilt.id.kind.get)
    assertEquals("B", rebuilt.fields(0).id.kind.get)
  }

  @Test
  def testBuildMethodAssignment() {
    val a = Wrapper.wrap(new A(new B()))
    val rule = RuleParser.parse("A -> :c = foo(A)").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val treeBuilder = new TreeBuilder
    treeBuilder.add("foo", (a: A) => a.b)
    val rebuilt = treeBuilder.build(rule.rh, store.get)
    assertEquals("B", rebuilt.id.kind.get)
    assertEquals("c", rebuilt.id.name.get)
  }

  @Test
  def testBuildNewProperty() {
    val a = Wrapper.wrap(<A name="a"></A>)
    val rule = RuleParser.parse("A -> A[foo = \"bar\"]").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val rebuilt = new TreeBuilder().build(rule.rh, store.get)
    assertTrue(rebuilt.id.properties.size == 2)
    assertEquals("bar", rebuilt.id.properties("foo"))
  }

  @Test
  def testBuildOverwriteProperty() {
    val a = Wrapper.wrap(<A name="a"></A>)
    val rule = RuleParser.parse("A -> A[name = \"bar\"]").get(0)
    val store = SubTreeFinder.find(a, rule.lh)
    val rebuilt = new TreeBuilder().build(rule.rh, store.get)
    assertTrue(rebuilt.id.properties.size == 1)
    assertEquals("bar", rebuilt.id.properties("name"))
  }

}
