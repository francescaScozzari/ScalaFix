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

package it.unich.scalafix.finite

import scala.collection.mutable
import it.unich.scalafix.Assignment
import it.unich.scalafix.FixpointSolverListener

/**
  * A fixpoint solver based on a worklist.
  */
object WorkListSolver extends FiniteFixpointSolver {

  /**
    * Parameters needed for the round robin solver
    *
    * @param start    the initial assignment.
    * @param listener the listener whose callbacks are invoked for debugging and tracing.
    */
  case class Params[U, V](
                           start: Assignment[U, V],
                           listener: FixpointSolverListener[U, V] = FixpointSolverListener.EmptyListener
                         ) extends BaseParams[U, V]

  /**
    * @inheritdoc
    * This solver only works with finite equation systems.
    */
  type EQS[U, V] = FiniteEquationSystem[U, V]

  def solve[U, V](eqs: EQS[U, V], params: Params[U, V]) = {
    import params._

    val current = mutable.HashMap.empty[U, V].withDefault(start)
    listener.initialized(current)
    // is it better to use a Queue for a worklist ?
    var workList = collection.mutable.LinkedHashSet.empty[U]
    workList ++= eqs.unknowns
    while (!workList.isEmpty) {
      val x = workList.head
      workList.remove(x)
      val newval = eqs.body(current)(x)
      listener.evaluated(current, x, newval)
      if (newval != current(x)) {
        current(x) = newval
        // variant with Queue
        // for (y <- eqs.infl(x); if !(workList contains y)) workList += y
        workList ++= eqs.infl(x)
      }
    }
    listener.completed(current)
    current
  }

  def apply[U, V](
                   eqs: EQS[U, V],
                   start: Assignment[U, V],
                   listener: FixpointSolverListener[U, V] = FixpointSolverListener.EmptyListener
                 ) =
    solve(eqs, Params(start, listener))
}
