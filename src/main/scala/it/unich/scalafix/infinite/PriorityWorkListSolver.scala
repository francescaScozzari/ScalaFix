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

package it.unich.scalafix.infinite

import it.unich.scalafix.FixpointSolverListener.EmptyListener

import scala.collection.mutable
import it.unich.scalafix.{PartialAssignment, Assignment, FixpointSolverListener, EquationSystem}

/**
  * A local fixpoint solver based on a worklist with priorities.
  */
object PriorityWorkListSolver {

  /**
    * This is an dynamic ordering on unknowns: every time an unknown appears, it gets assigned a lower
    * priority than previous one (i.e., it comes earlier in the ordering). This is the default ordering
    * for PriorityWorkListSolver when an explicit one is not provided.
    */
  class DynamicPriority[U] extends Ordering[U] {
    val map = mutable.Map.empty[U, Int]
    var current = 0

    def compare(x: U, y: U) = {
      val xp = map getOrElseUpdate(x, {
        current -= 1;
        current
      })
      val yp = map getOrElseUpdate(y, {
        current -= 1;
        current
      })
      xp - yp
    }
  }

  /**
    * Locally solve a finite equation system.
    *
    * @tparam U type of the unknowns for the equation system
    * @tparam V type of values of the equatiom system
    * @param eqs      equation system to solve
    * @param wanted   the unknowns we want to solve
    * @param start    assignment to start the evaluation (defaults to `eqs.initial`)
    * @param ordering an ordering which specifies priorities between unknowns (defaults to a dynamical ordering
    *                 induced by evaluation)
    * @param listener a listener to track the behaviour of the solver (defaults to the empty listener)
    * @return the solution of the equation system
    */
  def apply[U, V](eqs: EquationSystem[U, V])
                 (wanted: Iterable[U], start: Assignment[U, V] = eqs.initial, ordering: Ordering[U] = new DynamicPriority[U],
                  listener: FixpointSolverListener[U, V] = EmptyListener): PartialAssignment[U, V] = {
    val infl = new mutable.HashMap[U, mutable.Set[U]] with mutable.MultiMap[U, U] {
      override def makeSet = new mutable.LinkedHashSet[U]
    }
    var workList = mutable.PriorityQueue.empty[U](ordering)
    workList ++= wanted

    val current = mutable.HashMap.empty[U, V].withDefault(start)
    listener.initialized(current)
    while (!workList.isEmpty) {
      val x = workList.dequeue()
      val (newval, dependencies) = eqs.bodyWithDependencies(current)(x)
      listener.evaluated(current, x, newval)
      for (y <- dependencies) {
        if (!current.isDefinedAt(y)) {
          current(y) = start(y)
          workList += y
        }
        infl.addBinding(y, x)
      }
      if (newval != current(x)) {
        current(x) = newval
        workList ++= infl(x)
      }
    }
    listener.completed(current)
    current
  }
}
