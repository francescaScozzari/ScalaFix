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

package it.unich.scalafix.drivers

/**
  * A driver class has two aims: (1) it makes  easier to use fixpoint solvers when the parameters for
  * the algorithms are obtained by an user interface, and therefore may not be statically known; (2) combines
  * together different classes to implement standard behaviors. A driver is still a fixpoint solver,
  * but it does not implement any algorithm and just delegates to other fixpoint solvers by passing them suitable
  * parameters.
  *
  * This is only a marker trait.
  */
trait Driver

/**
  * The driver companion object collects all parameters which may be passed to drivers.
  */
object Driver {

  /**
    * This exception is thrown when the parameters provided to the `Driver` are not correct.
    */
  class DriverBadParameters(msg: String) extends Exception(msg)

  /**
    * An enumeration with the solvers supported by this driver.
    */
  object Solver extends Enumeration {
    type Solver = Value

    val KleeneSolver = Value
    val RoundRobinSolver = Value
    val PriorityWorkListSolver = Value
    val WorkListSolver = Value
    val HierarchicalOrderingSolver = Value
  }

  object BoxStrategy extends Enumeration {
    type BoxStrategy = Value

    /**
      * Only apply widening.
      */
    val OnlyWidening = Value

    /**
      * Standard two pass widening/narrowing iteration.
      */
    val TwoPhases = Value

    /**
      * Single pass with a warrowing.
      */
    val Warrowing = Value
  }

  object BoxScope extends Enumeration {
    type BoxScope = Value

    /**
      * Use standard widening.
      */
    val Standard = Value

    /**
      * Use localized widening.
      */
    val Localized = Value
  }

  object BoxLocation extends Enumeration {
    type BoxLocation = Value

    /**
      * Put widening/narrowing points nowhere
      */
    val None = Value

    /**
      * Put widening/narrowing points at each unknown.
      */
    val All = Value

    /**
      * Put widening/narrowing points at each loop head.
      */
    val Loop = Value
  }
}
