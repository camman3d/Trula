package com.joshmonson.trula.reducer

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.parser.ast.lh.{Parentage, Identifier}
import com.joshmonson.trula.reducer.wrapping.Wrapper
import com.joshmonson.trula.reducer.support.{B, A}
import com.joshmonson.trula.parser.RuleParser

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
}
