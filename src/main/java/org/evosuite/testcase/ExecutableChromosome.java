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

import org.evosuite.ga.Chromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
//可执行的染色体继承染色体抽象类，多了一个执行结果成员和按测试套件适应度执行的函数
//删除了变异测试相关
public abstract class ExecutableChromosome<E extends ExecutableChromosome<E>> extends Chromosome<E> {

    private static final long serialVersionUID = 1L;

    protected transient ExecutionResult lastExecutionResult = null;

    /**
     * <p>Constructor for ExecutableChromosome.</p>
     */
    public ExecutableChromosome() {
        super();
    }

    /**
     * <p>Setter for the field <code>lastExecutionResult</code>.</p>
     *
     * @param lastExecutionResult a {@link org.evosuite.testcase.execution.ExecutionResult} object.
     */
    public void setLastExecutionResult(ExecutionResult lastExecutionResult) {
        this.lastExecutionResult = lastExecutionResult;
    }

    /**
     * <p>Getter for the field <code>lastExecutionResult</code>.</p>
     *
     * @return a {@link org.evosuite.testcase.execution.ExecutionResult} object.
     */
    public ExecutionResult getLastExecutionResult() {
        return lastExecutionResult;
    }

    /**
     * <p>clearCachedResults</p>
     */
    public void clearCachedResults() {
        this.lastExecutionResult = null;
    }

    /**
     * <p>copyCachedResults</p>
     *
     * @param other a {@link ExecutableChromosome} object.
     */
    protected abstract void copyCachedResults(E other);

    /**
     * <p>executeForFitnessFunction</p>
     *
     * @param testSuiteFitnessFunction a {@link org.evosuite.testsuite.TestSuiteFitnessFunction} object.
     * @return a {@link org.evosuite.testcase.execution.ExecutionResult} object.
     */
    abstract public ExecutionResult executeForFitnessFunction(
            TestSuiteFitnessFunction testSuiteFitnessFunction);

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException,
            IOException {
        ois.defaultReadObject();
        lastExecutionResult = null;
    }
}
