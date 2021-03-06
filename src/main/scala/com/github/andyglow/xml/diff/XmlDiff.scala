/**
 * scala-xml-diff
 * Copyright (c) 2014, Andrey Onistchuk, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.github.andyglow.xml.diff

sealed trait XmlDiffResult

case object XmlEqual extends XmlDiffResult
case class XmlDifferent(diff: XmlDiff) extends XmlDiffResult

sealed trait XmlDiff {
  def path: List[xml.Node]
}

private[diff] object XmlDiff {

  implicit class NamedNode(val n: xml.Node) extends AnyVal {
    def name: String = n.nameToString(new StringBuilder).toString()
  }

  case class RedundantNode(path: List[xml.Node], node: xml.Node) extends XmlDiff {
    override def toString = s"""RedundantNode(
                                |   $node
                                |)""".stripMargin
  }

  case class AbsentNode(path: List[xml.Node], node: xml.Node) extends XmlDiff {
    override def toString = s"""AbsentNode(
                                |   $node
                                |)""".stripMargin
  }

  case class NodeDiff(path: List[xml.Node], expected: xml.Node, actual: xml.Node) extends XmlDiff {
    override def toString = s"""NodeDiff(
                                |   Expected: ${expected}
                                |   Actual: ${actual}
                                |)""".stripMargin
  }

  case class AttributesDiff(path: List[xml.Node], expected: xml.MetaData, actual: xml.MetaData) extends XmlDiff {
    override def toString = s"""AttributesDiff(
                                |   Expected: ${expected.asAttrMap}
                                |   Actual: ${actual.asAttrMap}
                                |)""".stripMargin
  }

  case class ChildrenDiff(path: List[xml.Node], element: xml.Node, list: List[XmlDiff]) extends XmlDiff {
    override def toString = {
      val wrongElementsReport = list.mkString(",\n").lines.map("   " + _).mkString("\n")
      s"""ChildrenDiff(
          |   None of the elements found fully matched $element
          |$wrongElementsReport
          |)""".stripMargin
    }
  }

}
