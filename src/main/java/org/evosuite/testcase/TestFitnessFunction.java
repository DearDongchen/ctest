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

import org.evosuite.ga.FitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testsuite.TestSuiteChromosome;

import java.util.List;

/**
 * Abstract base class for fitness functions for test case chromosomes
 *
 * @author Gordon Fraser
 */

//封装了染色体适应度计算的类
public abstract class TestFitnessFunction
        extends FitnessFunction<TestChromosome>
        implements Comparable<TestFitnessFunction> {

    private static final long serialVersionUID = 5602125855207061901L;
    static boolean warnedAboutIsSimilarTo = false;

    /**
     * <p>
     * getFitness
     * </p>
     *
     * @param individual a {@link TestChromosome} object.
     * @param result     a {@link ExecutionResult} object.
     * @return a double.
     */
    //抽象方法：根据执行结果，计算适应度，需要在不同覆盖准则的适应度函数子类里实现
    public abstract double getFitness(TestChromosome individual, ExecutionResult result);

    /**
     * {@inheritDoc}
     */
    //计算染色体的适应度（根据不同的覆盖准则）
    @Override
    public double getFitness(TestChromosome individual) {
        logger.trace("Executing test case on original");
        ExecutionResult origResult = individual.getLastExecutionResult();
        if (origResult == null || individual.isChanged()) {//需重新计算执行结果
            origResult = runTest(individual.test);
            individual.setLastExecutionResult(origResult);
            individual.setChanged(false);
        }

        double fitness = getFitness(individual, origResult);
        updateIndividual(individual, fitness);

        return fitness;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Used to preorder goals by difficulty
     */
    //看注解是按覆盖目标的难度排序，这个很值得看一下
    //抽象方法：适应度函数比较
    @Override
    public abstract int compareTo(TestFitnessFunction other);

    protected final int compareClassName(TestFitnessFunction other) {
        return this.getClass().getName().compareTo(other.getClass().getName());
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object other);

    /**
     * {@inheritDoc}
     */
    public ExecutionResult runTest(TestCase test) {
        return TestCaseExecutor.runTest(test);
    }

    /**
     * Determine if there is an existing test case covering this goal
     *
     * @param tests a {@link List} object.
     * @return a boolean.
     */
    //下面为多态的判断，判断目标是否被测试用例集中的某个用例、测试用例、染色体、执行结果覆盖到
    //目标是在哪里指出？
    public boolean isCovered(List<TestCase> tests) {
        return tests.stream().anyMatch(this::isCovered);
    }

    /**
     * Determine if there is an existing test case covering this goal
     *
     * @param tests a {@link List} object.
     * @return a boolean.
     */
    public boolean isCoveredByResults(List<ExecutionResult> tests) {
        return tests.stream().anyMatch(this::isCovered);
    }

    public boolean isCoveredBy(TestSuiteChromosome testSuite) {
        int num = 1;
        for (TestChromosome test : testSuite.getTestChromosomes()) {
            logger.debug("Checking goal against test " + num + "/" + testSuite.size());
            num++;
            if (isCovered(test))
                return true;
        }
        return false;
    }

    /**
     * <p>
     * isCovered
     * </p>
     *
     * @param test a {@link TestCase} object.
     * @return a boolean.
     */
    public boolean isCovered(TestCase test) {
        TestChromosome c = new TestChromosome();
        c.test = test;
        return isCovered(c);
    }

    /**
     * <p>
     * isCovered
     * </p>
     *
     * @param tc a {@link TestChromosome} object.
     * @return a boolean.
     */
    public boolean isCovered(TestChromosome tc) {
        if (tc.getTestCase().isGoalCovered(this)) {
            return true;
        }

        ExecutionResult result = tc.getLastExecutionResult();
        if (result == null || tc.isChanged()) {
            result = runTest(tc.test);
            tc.setLastExecutionResult(result);
            tc.setChanged(false);
        }

        return isCovered(tc, result);
    }

    /**
     * <p>
     * isCovered
     * </p>
     *
     * @param individual a {@link TestChromosome} object.
     * @param result     a {@link ExecutionResult} object.
     * @return a boolean.
     */
    public boolean isCovered(TestChromosome individual, ExecutionResult result) {
        boolean covered = getFitness(individual, result) == 0.0;
        if (covered) {
            individual.test.addCoveredGoal(this);
        }
        return covered;
    }

    /**
     * Helper function if this is used without a chromosome
     *
     * @param result
     * @return
     */
    public boolean isCovered(ExecutionResult result) {
        TestChromosome chromosome = new TestChromosome();
        chromosome.setTestCase(result.test);
        chromosome.setLastExecutionResult(result);
        chromosome.setChanged(false);
        return isCovered(chromosome, result);
    }

    /* (non-Javadoc)
     * @see org.evosuite.ga.FitnessFunction#isMaximizationFunction()
     */

    /**
     * {@inheritDoc}
     */
    //复写：是最大化还是最小化的适应度函数
    @Override
    public boolean isMaximizationFunction() {
        return false;
    }

    public abstract String getTargetClass();

    public abstract String getTargetMethod();
}
