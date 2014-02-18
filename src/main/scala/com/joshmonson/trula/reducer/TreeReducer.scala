package com.joshmonson.trula.reducer

import com.joshmonson.trula.parser.RuleParser

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/17/14
 * Time: 7:15 AM
 * To change this template use File | Settings | File Templates.
 */
class TreeReducer(ruleText: String) {

  // Parse the rules
  val rules = {
    val r = RuleParser.parse(ruleText)
    if (r.isDefined)
      r.get
    else
      throw new InvalidSyntaxException()
  }




}
