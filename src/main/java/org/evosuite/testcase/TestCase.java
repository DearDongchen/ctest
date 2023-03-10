/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.testcase;

import org.evosuite.ga.ConstructionFailedException;
//import org.evosuite.testcase.execution.Scope;
import org.evosuite.testcase.variable.VariableReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A test case is a sequence of Variable.
 *
 * @author TieXin Wang
 */
//旧有功能：增减覆盖目标
    //新增功能，输入变量数，各个变量的类型，各个变量的值
public interface TestCase {

    /**
     * Get a unique ID representing this test. This is mainly useful for debugging.
     *
     * @return the unique ID of this test
     */
    int getID();

    /**
     * Keep track of an additional covered goal
     *
     * @param goal a {@link org.evosuite.testcase.TestFitnessFunction} object.
     */
    //一个覆盖目标是一个TestFitnessFunction
    void addCoveredGoal(TestFitnessFunction goal);

    /**
     * Remove goal that may have been covered
     *
     * @param goal a {@link org.evosuite.testcase.TestFitnessFunction} object.
     */
    void removeCoveredGoal(TestFitnessFunction goal);


    /**
     * Remove all covered goals
     */
    void clearCoveredGoals();


    /**
     * <p>clone</p>
     *
     * @return a {@link TestCase} object.
     */
    TestCase clone();

    //环境交互，包括文件交互，url访问等，暂时先不实现了
//    /**
//     * Retrieve an object containing information about what environment components this test interacted with
//     *
//     * @return a {@link List} object.
//     */
//    AccessedEnvironment getAccessedEnvironment();

    /**
     * Retrieve all coverage goals covered by this test
     *
     * @return a {@link Set} object.
     */
    Set<TestFitnessFunction> getCoveredGoals();


    /**
     * Get the last object of the defined type
     *
     * @param type
     * @return
     * @throws ConstructionFailedException
     */
//    VariableReference getLastObject(Type type)
//            throws ConstructionFailedException;
//
//    /**
//     * Get the last object of the defined type
//     *
//     * @param type
//     * @return
//     * @throws ConstructionFailedException
//     */
//    VariableReference getLastObject(Type type, int position)
//            throws ConstructionFailedException;
//
//
//    /**
//     * Get actual object represented by a variable for a given execution scope
//     *
//     * @param reference Variable
//     * @param scope     Excution scope
//     * @return Object in scope
//     */
//    Object getObject(VariableReference reference, Scope scope);
//
//    /**
//     * Get all objects up to the given position.
//     *
//     * @param position a int.
//     * @return a {@link List} object.
//     */
//    List<VariableReference> getObjects(int position);
//
//    /**
//     * Get all objects up to position satisfying constraint
//     *
//     * @param type     a {@link Type} object.
//     * @param position a int.
//     * @return a {@link List} object.
//     */
//    List<VariableReference> getObjects(Type type, int position);
//
//    /**
//     * Get a random object matching type
//     *
//     * @param type     a {@link Type} object.
//     * @param position Upper bound in test case up to which objects are considered
//     * @return a {@link org.evosuite.testcase.variable.VariableReference} object.
//     * @throws ConstructionFailedException if no such object exists
//     */
//    VariableReference getRandomNonNullNonPrimitiveObject(Type type, int position)
//            throws ConstructionFailedException;
//
//    /**
//     * Get a random object matching type
//     *
//     * @param type     a {@link Type} object.
//     * @param position Upper bound in test case up to which objects are considered
//     * @return a {@link org.evosuite.testcase.variable.VariableReference} object.
//     * @throws ConstructionFailedException if no such object exists
//     */
//    VariableReference getRandomNonNullObject(Type type, int position)
//            throws ConstructionFailedException;
//
//    /**
//     * Get a random object matching type
//     *
//     * @return Random object
//     * @throws ConstructionFailedException if any.
//     */
//    VariableReference getRandomObject();
//
//    /**
//     * Get a random object matching type
//     *
//     * @param position Upper bound in test case up to which objects are considered
//     * @return a {@link org.evosuite.testcase.variable.VariableReference} object.
//     * @throws ConstructionFailedException if no such object exists
//     */
//    VariableReference getRandomObject(int position);
//
//    /**
//     * Get a random object matching type
//     *
//     * @param type Class we are looking for
//     * @return Random object
//     * @throws ConstructionFailedException if any.
//     */
//    VariableReference getRandomObject(Type type)
//            throws ConstructionFailedException;
//
//    /**
//     * Get a random object matching type
//     *
//     * @param type     a {@link Type} object.
//     * @param position Upper bound in test case up to which objects are considered
//     * @return a {@link org.evosuite.testcase.variable.VariableReference} object.
//     * @throws ConstructionFailedException if no such object exists
//     */
//    VariableReference getRandomObject(Type type, int position)
//            throws ConstructionFailedException;
//
//    /**
//     * Determine the set of variables that depend on var
//     *
//     * @param var Variable to check for
//     * @return Set of dependent variables
//     */
//    Set<VariableReference> getReferences(VariableReference var);
//
//    /**
//     * Get return value (variable) of statement at position
//     *
//     * @param position a int.
//     * @return a {@link org.evosuite.testcase.variable.VariableReference} object.
//     */
//    VariableReference getReturnValue(int position);

    /**
     * <p>isEmpty</p>
     *
     * @return true if size()==0
     */
    boolean isEmpty();

    boolean isFailing();

    void setFailing();

    /**
     * Check if the current test case does cover the given goal.
     *
     * @param goal
     * @return
     */
    boolean isGoalCovered(TestFitnessFunction goal);

    //判断由于环境因素造成的不稳定，暂不使用
//    /**
//     * A test can be unstable if its assertions fail, eg due to non-determinism,
//     * non-properly handled static variables and side effects on environment, etc
//     *
//     * @return
//     */
//    boolean isUnstable();

    /**
     * Check if test case is valid (executable)
     *
     * @return a boolean.
     */
    boolean isValid();

    /**
     * Replace a VariableReference with another one
     *
     * @param var1 The old variable
     * @param var2 The new variable
     */
    void replace(VariableReference var1, VariableReference var2);



    //新含义为返回程序输入的个数
    int size();

}
