package com.joshmonson.trula.parser

import org.junit.Test
import org.junit.Assert._
import com.joshmonson.trula.parser.ast.rh.{Deletion, Method, RHStructure, Assignment}

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/17/14
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
class TestRHDefinitionParsing {

  @Test
  def testStructureBasicNoBrace() {
    val result = RuleParser.parseRule(RuleParser.rhStructure, "A")
    assertEquals(result.get.id.kind.get, "A")
    assertTrue(result.get.children.isEmpty)
  }

  @Test
  def testStructureBasicBrace() {
    val result = RuleParser.parseRule(RuleParser.rhStructure, "A {}")
    assertEquals(result.get.id.kind.get, "A")
    assertTrue(result.get.children.isEmpty)
  }

  @Test
  def testStructureChildren() {
    val result = RuleParser.parseRule(RuleParser.rhStructure, "A { B C }")
    assertTrue(result.get.children.size == 2)
    assertEquals(result.get.children(0).id.kind.get, "B")
    assertEquals(result.get.children(1).id.kind.get, "C")
  }

  @Test
  def testStructureNestedChildren() {
    val result = RuleParser.parseRule(RuleParser.rhStructure, "A { B C { D E } }")
    val cChild = result.get.children(1).asInstanceOf[RHStructure]
    assertTrue(cChild.children.size == 2)
    assertEquals(cChild.children(0).id.kind.get, "D")
    assertEquals(cChild.children(1).id.kind.get, "E")
  }

  @Test
  def testMethodNoArgs() {
    val result = RuleParser.parseRule(RuleParser.rhMethod, "fooBar()")
    assertEquals(result.get.name, "fooBar")
    assertTrue(result.get.args.isEmpty)
  }

  @Test
  def testMethodArgs() {
    val result = RuleParser.parseRule(RuleParser.rhMethod, "fooBar(:one, @two, Three)")
    assertEquals(result.get.name, "fooBar")
    assertEquals(result.get.args(0).name.get, "one")
    assertEquals(result.get.args(1).label.get, "two")
    assertEquals(result.get.args(2).kind.get, "Three")
  }

  @Test
  def testStructureAssignment() {
    val result = RuleParser.parseRule(RuleParser.rhAssignment, ":foo = A { B C }")
    assertTrue(result.get.id.name.get == "foo")
    val structure = result.get.target.asInstanceOf[RHStructure]
    assertEquals(structure.id.kind.get, "A")
    assertTrue(structure.children.size == 2)
    assertEquals(structure.children(0).id.kind.get, "B")
    assertEquals(structure.children(1).id.kind.get, "C")
  }

  @Test
  def testMethodAssignment() {
    val result = RuleParser.parseRule(RuleParser.rhAssignment, ":foo = bar()")
    assertTrue(result.get.id.name.get == "foo")
    val method = result.get.target.asInstanceOf[Method]
    assertEquals(method.name, "bar")
  }

  @Test
  def testDeletionAssignment() {
    val result = RuleParser.parseRule(RuleParser.rhAssignment, ":foo = ~")
    assertTrue(result.get.id.name.get == "foo")
    assertTrue(result.get.target.isInstanceOf[Deletion])
  }

  @Test
  def testDefinitionStructure() {
    val result = RuleParser.parseRule(RuleParser.rhDef, "A { B C }")
    assertTrue(result.get.isInstanceOf[RHStructure])
    assertTrue(result.get.id.kind.get == "A")
  }

  @Test
  def testDefinitionAssignment() {
    val result = RuleParser.parseRule(RuleParser.rhDef, "A = B { C D }")
    assertTrue(result.get.isInstanceOf[Assignment])
    assertTrue(result.get.id.kind.get == "A")
  }

  @Test
  def testDefinitionDeletion1() {
    val result = RuleParser.parseRule(RuleParser.rhDef, "A = ~")
    assertTrue(result.get.id.kind.get == "A")
    assertTrue(result.get.asInstanceOf[Assignment].target.isInstanceOf[Deletion])
  }

  @Test
  def testDefinitionDeletion2() {
    val result = RuleParser.parseRule(RuleParser.rhDef, "@foo { :bar = ~ }")
    assertTrue(result.get.id.label.get == "foo")
    val child = result.get.asInstanceOf[RHStructure].children(0)
    assertTrue(child.asInstanceOf[Assignment].target.isInstanceOf[Deletion])
  }

}
