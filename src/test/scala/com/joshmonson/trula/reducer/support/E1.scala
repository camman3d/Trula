package com.joshmonson.trula.reducer.support

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/24/14
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
case class E1(var e2: E2)
case class E2(var e1: E1)
