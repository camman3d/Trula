package com.joshmonson.trula.reducer

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.parser.ast.lh.{Parentage, Identifier}
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.support.{B, A}
import com.joshmonson.trula.parser.RuleParser
import com.joshmonson.trula.reducer.core.StructureMatcher

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
class TestStructureMatcher {
  @Test
  def testIdentifierIdentifies() {
    val id1 = new Identifier(name = "foo")
    val id2 = new Identifier(kind = "Bar")
    val id3 = new Identifier(name = "foo", kind = "Bar")
    assertTrue(id1.identifies(id3))
    assertTrue(id2.identifies(id3))
    assertFalse(id3.identifies(id1))
    assertFalse(id3.identifies(id2))
  }

  @Test
  def testMatchParentage() {
    val history = List(new Wrapper("C"), new Wrapper("B"), new Wrapper("A"))
    val parentage1 = new Parentage(new Identifier(kind = Some("C"), parentage = Some(new Parentage(new Identifier(kind = "A"), false))), true)
    val parentage2 = new Parentage(new Identifier(kind = Some("C"), parentage = Some(new Parentage(new Identifier(kind = "A"), true))), true)
    assertTrue(StructureMatcher.matchParentage(Some(parentage1), history))
    assertFalse(StructureMatcher.matchParentage(Some(parentage2), history))
  }

  @Test
  def testMatches1() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A -> X").get(0).lh
    assertTrue(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatches2() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A { B } -> X").get(0).lh
    assertTrue(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatches3() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A { C } -> X").get(0).lh
    assertFalse(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatches4() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("A { B C } -> X").get(0).lh
    assertFalse(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatchesWildcard1() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("% { B } -> X").get(0).lh
    assertTrue(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatchesWildcard2() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("% { % } -> X").get(0).lh
    assertTrue(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatchesWildcard3() {
    val a = Wrapper.wrap(new A(new B()))
    val structure = RuleParser.parse("% { C } -> X").get(0).lh
    assertFalse(StructureMatcher.matches(a, structure))
  }

  @Test
  def testMatchesProperties1() {
    val obj = Wrapper.wrap(<b name="c"></b>)
    val structure = RuleParser.parse("""b[name="c"] -> X""").get(0).lh
    assertTrue(StructureMatcher.matches(obj, structure))
  }

  @Test
  def testMatchesProperties2() {
    val obj = Wrapper.wrap(<b name="c"></b>)
    val structure = RuleParser.parse("""b[name="d"] -> X""").get(0).lh
    assertFalse(StructureMatcher.matches(obj, structure))
  }

  @Test
  def testMatchesProperties3() {
    val obj = Wrapper.wrap(<b name="c" age="4"></b>)
    val structure = RuleParser.parse("""b[name="c", age="4"] -> X""").get(0).lh
    assertTrue(StructureMatcher.matches(obj, structure))
  }

  @Test
  def testMatchesProperties4() {
    val obj = Wrapper.wrap(<b name="c" age="4"></b>)
    val structure = RuleParser.parse("""b[name="c", age="5"] -> X""").get(0).lh
    assertFalse(StructureMatcher.matches(obj, structure))
  }

  @Test
  def testMatchesParentProperties() {
    val obj1 = Wrapper.wrap(<div class="foo"><a href="#">Foo</a></div>)
    val obj2 = Wrapper.wrap(<div class="bar"><a href="#">Bar</a></div>)
    val structure = RuleParser.parse("""div[class="foo"] > a -> X""").get(0).lh
    assertTrue(StructureMatcher.matches(obj1.fields(0), structure, List(obj1)))
    assertFalse(StructureMatcher.matches(obj2.fields(0), structure, List(obj2)))
  }
}
