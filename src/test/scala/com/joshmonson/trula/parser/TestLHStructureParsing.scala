package com.joshmonson.trula.parser

import org.junit.Test
import org.junit.Assert._

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/17/14
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
class TestLHStructureParsing {

  @Test
  def testBasicNoBrace() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A")
    assertEquals(result.get.id.kind.get, "A")
    assertTrue(result.get.children.isEmpty)
  }

  @Test
  def testBasicBrace() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A {}")
    assertEquals(result.get.id.kind.get, "A")
    assertTrue(result.get.children.isEmpty)
  }

  @Test
  def testChildren() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A { B C }")
    assertTrue(result.get.children.size == 2)
    assertTrue(result.get.children(0).id.kind.get == "B")
    assertTrue(result.get.children(1).id.kind.get == "C")
  }

  @Test
  def testNestedChildren() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A { B C { D E } }")
    assertTrue(result.get.children(1).children.size == 2)
    assertTrue(result.get.children(1).children(0).id.kind.get == "D")
    assertTrue(result.get.children(1).children(1).id.kind.get == "E")
  }

  @Test
  def testDescendants() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A { [ B C ] }")
    assertTrue(result.get.children.size == 0)
    assertTrue(result.get.descendants.size == 2)
    assertTrue(result.get.descendants(0).id.kind.get == "B")
    assertTrue(result.get.descendants(1).id.kind.get == "C")
  }

  @Test
  def testNestedDescendants() {
    val result = RuleParser.parseRule(RuleParser.lhStructureDef, "A { [ B C {[ D E ]} ] }")
    assertTrue(result.get.descendants(1).descendants.size == 2)
    assertTrue(result.get.descendants(1).descendants(0).id.kind.get == "D")
    assertTrue(result.get.descendants(1).descendants(1).id.kind.get == "E")
  }

}
