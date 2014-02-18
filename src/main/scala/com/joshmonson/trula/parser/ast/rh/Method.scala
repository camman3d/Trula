package com.joshmonson.trula.parser.ast.rh


/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/17/14
 * Time: 7:46 AM
 * To change this template use File | Settings | File Templates.
 */
case class Method(name: String, args: List[Reference] = Nil) extends Target
