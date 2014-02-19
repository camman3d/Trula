package com.joshmonson.trula.parser

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.parser.ast.rh.{Deletion, Assignment}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 9:23 AM
 * To change this template use File | Settings | File Templates.
 */
class TestRuleParsing {

  @Test
  def testRule1() {
    val result = RuleParser.parse("A -> B")
    assertTrue(result.get.size == 1)
    assertEquals(result.get(0).lh.id.kind.get, "A")
    assertEquals(result.get(0).rh.id.kind.get, "B")
  }

  @Test
  def testRule2() {
    val result = RuleParser.parse("A -> B = A")
    assertTrue(result.get.size == 1)
    assertEquals(result.get(0).lh.id.kind.get, "A")
    assertEquals(result.get(0).rh.id.kind.get, "B")
  }

  @Test
  def testRule3() {
    val result = RuleParser.parse("A > B -> :c = d(e, f)")
    assertTrue(result.get.size == 1)
    assertEquals(result.get(0).lh.id.kind.get, "B")
    assertEquals(result.get(0).rh.id.name.get, "c")
  }

  @Test
  def testMultipleRules() {
    val result = RuleParser.parse("A -> B C -> D = E")
    assertTrue(result.get.size == 2)
    assertEquals(result.get(0).lh.id.kind.get, "A")
    assertEquals(result.get(0).rh.id.kind.get, "B")
    assertEquals(result.get(1).lh.id.kind.get, "C")
    assertEquals(result.get(1).rh.id.kind.get, "D")
  }

  @Test
  def testDeletion() {
    val result = RuleParser.parse("A -> ~")
    assertTrue(result.get(0).rh.isInstanceOf[Deletion])
  }

}
