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
package org.evosuite.ga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Abstract base class of fitness functions
 *
 * @author Gordon Fraser
 */

    //封装了适应度操作的类，适应度函数类的泛型是任何继承了染色体类的类
    //给定一个染色体就可以求出标量适应度的值，暗指其是对于一个确定的覆盖目标的（如某个分支），目标在哪？
public abstract class FitnessFunction<T extends Chromosome<T>> implements Serializable {

    private static final long serialVersionUID = -8876797554111396910L;
    protected static final Logger logger = LoggerFactory.getLogger(FitnessFunction.class);

    /**
     * Make sure that the individual gets to know about its fitness
     *
     * @param individual a {@link Chromosome} object.
     * @param fitness    a double.
     */
    //给定染色体individual，赋与适应值fitness
    protected void updateIndividual(T individual, double fitness) {
        individual.setFitness(this, fitness);
        // the following assumes updateIndividual is called from a 'getFitness' method,
        // which seems to be case for all classes that extends 'FitnessFunction'
        individual.increaseNumberOfEvaluations();
    }

    //获取最好的个体
    public T getBestStoredIndividual() {
        return null;
    }

    /**
     * Calculate and set fitness function #TODO the 'set fitness' part should be
     * done by some abstract super class of all FitnessFunctions
     *
     * @param individual a {@link Chromosome} object.
     * @return new fitness
     */
    //抽象方法：给染色体，返回适应度
    public abstract double getFitness(T individual);

    /**
     * Normalize a value using Andrea's normalization function
     *
     * @param value a double.
     * @return a double.
     * @throws java.lang.IllegalArgumentException if any.
     */
    //给一个BD值，返回正则化的BD值
    public static double normalize(double value) throws IllegalArgumentException {
        if (value < 0d) {
            throw new IllegalArgumentException("Values to normalize cannot be negative");
        }
        if (Double.isInfinite(value)) {
            return 1.0;
        }
        return value / (1.0 + value);
    }

    /**
     * Do we need to maximize, or minimize this function?
     *
     * @return a boolean.
     */
    //判断适应度函数需最小化还是最大化
    public abstract boolean isMaximizationFunction();

    /**
     * if the fitness function contains an archive, updates the archive and the fitness values of the population, and returns true.
     * if the fitness function doesn't contain an archive, return false.
     * <p>
     * This method has to be invoked after each generation.
     *
     * @return
     */
    //存疑
    public boolean updateCoveredGoals() {
        return false;
    }
}
