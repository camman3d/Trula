package com.joshmonson.trula.parser.ast.rh

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/18/14
 * Time: 7:01 AM
 * To change this template use File | Settings | File Templates.
 */
case class RHStructure(id: Reference, children: List[Definition]) extends Definition with Target {

}
