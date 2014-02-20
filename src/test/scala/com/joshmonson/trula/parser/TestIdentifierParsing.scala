package com.joshmonson.trula.parser

import org.junit.Test
import org.junit.Assert._

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/15/14
 * Time: 7:41 AM
 * To change this template use File | Settings | File Templates.
 */
class TestIdentifierParsing {

  @Test
  def testIdKind() {
    val result = RuleParser.parseRule(RuleParser.id, "A")
    assertTrue(result.get.kind.get == "A")
  }

  @Test
  def testIdName() {
    val result = RuleParser.parseRule(RuleParser.id, ":foo")
    assertTrue(result.get.name.get == "foo")
  }

  @Test
  def testIdKindName() {
    val result = RuleParser.parseRule(RuleParser.id, "A:foo")
    assertTrue(result.get.kind.get == "A")
    assertTrue(result.get.name.get == "foo")
  }

  @Test
  def testIdLabelKind() {
    val result = RuleParser.parseRule(RuleParser.id, "@foo A")
    assertTrue(result.get.kind.get == "A")
    assertTrue(result.get.label.get == "foo")
  }

  @Test
  def testIdLabelName() {
    val result = RuleParser.parseRule(RuleParser.id, "@foo :bar")
    assertTrue(result.get.name.get == "bar")
    assertTrue(result.get.label.get == "foo")
  }

  @Test
  def testIdLabelKindName() {
    val result = RuleParser.parseRule(RuleParser.id, "@foo A:bar")
    assertTrue(result.get.kind.get == "A")
    assertTrue(result.get.name.get == "bar")
    assertTrue(result.get.label.get == "foo")
  }

  @Test
  def testIdLabel() {
    val result = RuleParser.parseRule(RuleParser.id, "@foo")
    assertTrue(result.isEmpty)
  }

  @Test
  def testIdNot1() {
    val result = RuleParser.parseRule(RuleParser.id, "! That")
    assertTrue(result.get.not)
  }

  @Test
  def testIdNot2() {
    val result = RuleParser.parseRule(RuleParser.id, "!@foo:bar")
    assertTrue(result.get.not)
  }

  @Test
  def testIdParent() {
    val result = RuleParser.parseRule(RuleParser.id, "A > B")
    assertTrue(result.get.kind.get == "B")
    assertTrue(result.get.parentage.get.id.kind.get == "A")
    assertTrue(result.get.parentage.get.isParent)
  }

  @Test
  def testIdAncestor() {
    val result = RuleParser.parseRule(RuleParser.id, "A >> B")
    assertTrue(result.get.kind.get == "B")
    assertTrue(result.get.parentage.get.id.kind.get == "A")
    assertFalse(result.get.parentage.get.isParent)
  }

  @Test
  def testIdAncestors() {
    val result = RuleParser.parseRule(RuleParser.id, "A > B >> C")
    assertTrue(result.get.kind.get == "C")
    assertTrue(result.get.parentage.get.id.kind.get == "B")
    assertFalse(result.get.parentage.get.isParent)
    assertTrue(result.get.parentage.get.id.parentage.get.id.kind.get == "A")
    assertTrue(result.get.parentage.get.id.parentage.get.isParent)
  }

  @Test
  def testIdWildcard1() {
    val result = RuleParser.parseRule(RuleParser.id, "%")
    assertTrue(result.get.label.isEmpty)
    assertTrue(result.get.kind.isEmpty)
    assertTrue(result.get.name.isEmpty)
  }

  @Test
  def testIdWildcard2() {
    val result = RuleParser.parseRule(RuleParser.id, "@foo %")
    assertEquals("foo", result.get.label.get)
    assertTrue(result.get.kind.isEmpty)
    assertTrue(result.get.name.isEmpty)
  }

  @Test
  def testIdWildcard3() {
    val result = RuleParser.parseRule(RuleParser.id, "%(4)")
    assertTrue(result.get.index.get == 4)
  }

  @Test
  def testIdProperties1() {
    val result = RuleParser.parseRule(RuleParser.id, """Foo["this" = "that"]""")
    assertEquals("that", result.get.properties("this"))
  }

  @Test
  def testIdProperties2() {
    val result = RuleParser.parseRule(RuleParser.id, """Foo["this" = "that", "one" = "two"]""")
    assertEquals("that", result.get.properties("this"))
    assertEquals("two", result.get.properties("one"))
  }

  @Test
  def testIdProperties3() {
    val result = RuleParser.parseRule(RuleParser.id, """Foo["this" = "th\"at"]""")
    assertEquals("th\"at", result.get.properties("this"))
  }

  @Test
  def testIdProperties4() {
    val result = RuleParser.parseRule(RuleParser.id, """Foo[this = "that"]""")
    assertEquals("that", result.get.properties("this"))
  }

  @Test
  def testIdPropertiesWithIndex() {
    val result = RuleParser.parseRule(RuleParser.id, """Foo(8)["this" = "that"]""")
    assertEquals("that", result.get.properties("this"))
    assertTrue(result.get.index.get == 8)
  }

  @Test
  def testRefKind() {
    val result = RuleParser.parseRule(RuleParser.ref, "A")
    assertTrue(result.get.kind.get == "A")
  }

  @Test
  def testRefName() {
    val result = RuleParser.parseRule(RuleParser.ref, ":foo")
    assertTrue(result.get.name.get == "foo")
  }

  @Test
  def testRefKindName() {
    val result = RuleParser.parseRule(RuleParser.ref, "A:foo")
    assertTrue(result.get.kind.get == "A")
    assertTrue(result.get.name.get == "foo")
  }

  @Test
  def testRefLabel() {
    val result = RuleParser.parseRule(RuleParser.ref, "@foo")
    assertTrue(result.get.label.get == "foo")
  }

  @Test
  def testRefKindLabel() {
    val result = RuleParser.parseRule(RuleParser.ref, "@foo A")
    assertTrue(result.isEmpty)
  }

  @Test
  def testRefNameLabel() {
    val result = RuleParser.parseRule(RuleParser.ref, "@foo :bar")
    assertTrue(result.isEmpty)
  }


}
