/**
  * Copyright 2015, 2016 Gianluca Amato <gianluca.amato@unich.it>
  *
  * This file is part of ScalaFix.
  * ScalaFix is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * ScalaFix is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty ofa
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with ScalaFix.  If not, see <http://www.gnu.org/licenses/>.
  */

package it.unich.scalafix

import scala.collection.mutable
import it.unich.scalafix.lattice.Magma

/**
  * This is the abstract class for a generic equation system.
  *
  * @tparam U the type for the unknowns of this equation system.
  * @tparam V the type for the values assumed by the unknowns of this equation system.
  */
abstract class EquationSystem[U, V] {
  /**
    * The body of the equation system, i.e., a map `Assignment[U,V] => Assignment[U,V]`.
    */
  val body: Body[U, V]

  /**
    * Given an assignment `rho` and unknown `u`, returns the pair `(body(rho)(x), uks)`. `uks` is a set of unknowns
    * with the property that if `rho'` differs from `rho` only for variables which are not in `uks`, then
    * `body(rho)(u)==body(rho')(u)`.
    */
  def bodyWithDependencies(rho: Assignment[U, V])(u: U): (V, Iterable[U])

  /**
    * Add boxes to the equation system
    *
    * @param boxes a box assignment
    */
  def withBoxes(boxes: BoxAssignment[U, V]): EquationSystem[U, V]

  /**
    * Combine a base assignment with the equation system. The type `V` should be endowed with a magma.
    *
    * @param init the assignment to add to the equation system
    */
  def withBaseAssignment(init: PartialFunction[U, V])(implicit magma: Magma[V]): EquationSystem[U, V]
}

object EquationSystem {

  /**
    * This trait is a mixin for an EquationSystem and implements the method `bodyWithDependecies` by instrumenting
    * the assignment rho during evaluation of body(rho)(x) and keeping track of all the evaluated unknowns.
    */
  trait BodyWithDependenciesFromBody[U, V] {
    this: EquationSystem[U, V] =>

    def bodyWithDependencies(rho: Assignment[U, V])(x: U) = {
      val queried = mutable.Buffer.empty[U]
      val trackrho = { y: U =>
        queried.append(y)
        rho(y)
      }
      val newval = body(trackrho)(x)
      (newval, queried.toSeq)
    }
  }

  /**
    * A trait which provides the withBaseAssignment method to equation systems.
    */
  trait WithBaseAssignment[U, V] {
    this: EquationSystem[U, V] =>

    def withBaseAssignment(init: PartialFunction[U, V])(implicit magma: Magma[V]): EquationSystem[U, V] =
      new SimpleEquationSystem(body.withBaseAssignment(init))
  }

  /**
    * A trait which provides the withBoxes method to equation systems.
    */
  trait WithBoxes[U, V] {
    this: EquationSystem[U, V] =>

    def withBoxes(boxes: BoxAssignment[U, V]): EquationSystem[U, V] =
      new SimpleEquationSystem(body.withBoxAssignment(boxes))
  }

  /**
    * This is a simple implementation of an equation systems, obtained by a body.
    */
  final class SimpleEquationSystem[U, V](
                                          val body: Body[U, V]
                                        )
    extends EquationSystem[U, V] with BodyWithDependenciesFromBody[U, V] with WithBaseAssignment[U, V] with WithBoxes[U, V]

  /**
    * An equation system determined by a given body.
    */
  def apply[U, V](body: Body[U, V]): EquationSystem[U, V] = new SimpleEquationSystem(body)

  /**
    * An equation system whose body is given by a function between assignments
    */
  def apply[U, V](f: Assignment[U, V] => Assignment[U, V]): EquationSystem[U, V] = new SimpleEquationSystem(Body(f))
}