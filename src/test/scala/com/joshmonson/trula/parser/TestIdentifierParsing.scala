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
    assertTrue(result.get.parentage.get.parent)
  }

  @Test
  def testIdAncestor() {
    val result = RuleParser.parseRule(RuleParser.id, "A >> B")
    assertTrue(result.get.kind.get == "B")
    assertTrue(result.get.parentage.get.id.kind.get == "A")
    assertFalse(result.get.parentage.get.parent)
  }

  @Test
  def testIdAncestors() {
    val result = RuleParser.parseRule(RuleParser.id, "A > B >> C")
    assertTrue(result.get.kind.get == "C")
    assertTrue(result.get.parentage.get.id.kind.get == "B")
    assertFalse(result.get.parentage.get.parent)
    assertTrue(result.get.parentage.get.id.parentage.get.id.kind.get == "A")
    assertTrue(result.get.parentage.get.id.parentage.get.parent)
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
