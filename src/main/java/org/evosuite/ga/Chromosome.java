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

//import org.evosuite.Properties;
import org.evosuite.ga.metaheuristics.localsearch.LocalSearchObjective;
import org.evosuite.utils.PublicCloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.averagingDouble;

/**
 * Abstract base class of chromosomes
 *
 * @author Gordon Fraser, Jose Miguel Rojas
 */
//染色体类中为啥没出现染色体的编码存储变量啊？？
public abstract class Chromosome<T extends Chromosome<T>>
        implements Comparable<T>, Serializable, PublicCloneable<T>, SelfTyped<T> {
//自限定类型声明，要求T为Class或者Class的子类
    private static final long serialVersionUID = -6921897301005213358L;

    /**
     * General Class Related Constants
     */
    public static final int MIN_REACHABLE_COVERAGE = 0;
    public static final int MAX_REACHABLE_COVERAGE = 100;

    /**
     * Constant <code>logger</code>
     */
    private static final Logger logger = LoggerFactory.getLogger(Chromosome.class);

    /**
     * only used for testing/debugging
     */
    //应该是指染色体的构造只在debug时候用到
    protected Chromosome() {
        // empty
    }

    /**
     * Last recorded fitness value
     */
    //最后一次记录的适应度函数<染色体> --》适应度值，用map是为了同时使用多个适应度函数
    private final LinkedHashMap<FitnessFunction<T>, Double> fitnessValues = new LinkedHashMap<>();

    /**
     * Previous fitness, to see if there was an improvement
     */
    //上一次的适应度函数<染色体> --》适应度值
    private final LinkedHashMap<FitnessFunction<T>, Double> previousFitnessValues = new LinkedHashMap<>();

    /**
     * Has this chromosome changed since its fitness was last evaluated?
     */
    //染色体自从适应度评估之后更改过吗
    private boolean changed = true;

    /**
     * Has local search been applied to this individual since it was last changed?
     */
    //染色体上次改变之后是否应用过局部搜索
    private boolean localSearchApplied = false;
    //？染色体&适应度--》覆盖值，可能是覆盖的目标数之类的
    private final LinkedHashMap<FitnessFunction<T>, Double> coverageValues = new LinkedHashMap<>();

    /**
     * The number of uncovered goals with regard to the fitness function given as key
     */
    //？？染色体&适应度--》未盖目标数，但是对一个染色体来说，未改目标数有杀意义吗？对于测试套件染色体来说是有意义，不过测试套件染色体仍然待阅读
    private final LinkedHashMap<FitnessFunction<T>, Integer> numsNotCoveredGoals = new LinkedHashMap<>();

    /**
     * The number of covered goals with regard to the fitness function given as key
     */
    //染色体&适应度--》已盖目标数
    private final LinkedHashMap<FitnessFunction<T>, Integer> numsCoveredGoals = new LinkedHashMap<>();

    // protected double coverage = 0.0;

    // protected int numOfCoveredGoals = 0;

    /**
     * Generation in which this chromosome was created
     */
    //染色体经历的代数
    protected int age = 0;

    /**
     * The Pareto front this chromosome belongs to. The first non-dominated front is assigned rank
     * 0, the next front rank 1 and so on. A rank of -1 means undefined.
     */
    //染色体所属的pareto前沿，0开始计数，-1表示未定义
    protected int rank = -1;

    /**
     *
     */
    //？？染色体的distance是什么
    protected double distance = 0.0;

    /**
     * Keep track of how many times this Chromosome has been mutated
     */
    //跟踪染色体的变异次数
    private int numberOfMutations = 0;

    /**
     * Keep track of how many times this Chromosome has been evaluated
     */
    //跟踪染色体的被计算适应度次数
    private int numberOfEvaluations = 0;

    // It is a non-negative number and it quantifies the tolerance of the system accepting a worse
    // solution than the existing one. (field used by Chemical Reaction Optimization algorithms)
    //protected double kineticEnergy = Properties.INITIAL_KINETIC_ENERGY;//化学反应优化。无用

    // When a molecule undergoes a collision, one of the elementary reactions will be triggered and it
    // may experience a change in its molecular structure. It is a record of the total number of collisions
    // a molecule has taken. (field used by Chemical Reaction Optimization algorithms)
    protected int numCollisions = 0;

    /**
     * Return current fitness value
     *
     * @return a double.
     */
    //？？适应度值是所有适应度函数下适应度值的加和，这是为什么？
    public double getFitness() {
        return fitnessValues.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Returns the fitness of this chromosome as computed by the given fitness function {@code ff}.
     *
     * @param ff the fitness function
     * @return the fitness of this chromosome
     */
    //返回染色体对于给定适应度函数的适应值，从染色体的map中取，没有则向适应度函数传入染色体，计算适应度
    public double getFitness(FitnessFunction<T> ff) {
        return fitnessValues.containsKey(ff)
                ? fitnessValues.get(ff)
                : ff.getFitness(self()); // Calculate new value if non is cached
    }
    //curd
    public Map<FitnessFunction<T>, Double> getFitnessValues() {
        return this.fitnessValues;
    }
    //curd
    public Map<FitnessFunction<T>, Double> getPreviousFitnessValues() {
        return this.previousFitnessValues;
    }

    /**
     * Tells whether the fitness of this chromosome has already been computed before using the
     * given fitness function.
     *
     * @param ff the fitness function
     * @return
     */
    //curd
    public boolean hasExecutedFitness(FitnessFunction<T> ff) {
        return this.previousFitnessValues.containsKey(ff);
    }
    //curd
    public void setFitnessValues(Map<? extends FitnessFunction<T>, Double> fits) {
        //TODO mainfitness?
        this.fitnessValues.clear();
        this.fitnessValues.putAll(fits);
    }
    //curd
    public void setPreviousFitnessValues(Map<FitnessFunction<T>, Double> lastFits) {
        this.previousFitnessValues.clear();
        this.previousFitnessValues.putAll(lastFits);
    }

    /**
     * Adds a fitness function and sets fitness, coverage, and numCoveredGoal
     * default.
     *
     * @param ff a fitness function
     */
    //增加一个适应度函数，以及一个最小适应的初值
    public void addFitness(FitnessFunction<T> ff) {
        final double fitnessValue = ff.isMaximizationFunction() ? 0 : Double.MAX_VALUE;
        this.addFitness(ff, fitnessValue);
    }

    /**
     * Adds a fitness function with an associated fitness value
     *
     * @param ff           a fitness function
     * @param fitnessValue the fitness value for {@code ff}
     */
    //增加适应值函数，给定初值
    public void addFitness(FitnessFunction<T> ff, double fitnessValue) {
        this.addFitness(ff, fitnessValue, 0.0, 0);
    }

    /**
     * Adds a fitness function with an associated fitness value and coverage
     * value
     *
     * @param ff           a fitness function
     * @param fitnessValue the fitness value for {@code ff}
     * @param coverage     the coverage value for {@code ff}
     */
    //增加适应值函数，给定初值和覆盖值
    public void addFitness(FitnessFunction<T> ff, double fitnessValue, double coverage) {
        this.addFitness(ff, fitnessValue, coverage, 0);
    }

    /**
     * Adds a fitness function with an associated fitness value, coverage value,
     * and number of covered goals.
     *
     * @param ff              a fitness function
     * @param fitnessValue    the fitness value for {@code ff}
     * @param coverage        the coverage value for {@code ff}
     * @param numCoveredGoals the number of covered goals for {@code ff}
     */
    //增加适应度函数，给定初值，覆盖值，和覆盖目标数
    public void addFitness(FitnessFunction<T> ff, double fitnessValue, double coverage,
                           int numCoveredGoals) {
        this.fitnessValues.put(ff, fitnessValue);
        this.previousFitnessValues.put(ff, fitnessValue);
        this.coverageValues.put(ff, coverage);
        this.numsCoveredGoals.put(ff, numCoveredGoals);
        this.numsNotCoveredGoals.put(ff, -1);
    }

    /**
     * Set new fitness value
     *
     * @param value a double.
     */
    //更新适应度，会把当前的适应度给到上次适应度，再写入当前适应度为新值
    public void setFitness(FitnessFunction<T> ff, double value) throws IllegalArgumentException {
        if (Double.isNaN(value) || (Double.isInfinite(value))) {
//				 || ( value < 0 ) || ( ff == null ))
            throw new IllegalArgumentException("Invalid value of Fitness: " + value + ", Fitness: "
                    + ff.getClass().getName());
        }

        previousFitnessValues.put(ff, fitnessValues.getOrDefault(ff, value));
        fitnessValues.put(ff, value);
    }

    /**
     * Tells whether the fitness of this chromosome has changed from the previous to the current
     * generation.
     *
     * @return
     */
    public boolean hasFitnessChanged() {
        return fitnessValues.keySet().stream()
                .anyMatch(ff -> {
                    final double currentValue = fitnessValues.get(ff);
                    final double previousValue = previousFitnessValues.get(ff);
                    return currentValue != previousValue;
                });
    }

    /**
     * {@inheritDoc}
     * <p>
     * Create a deep copy of the chromosome
     */
    @Override
    public abstract T clone();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

    /**
     * {@inheritDoc}
     * <p>
     * Determine relative ordering of this chromosome to another chromosome. If
     * the fitness values are equal, go through all secondary objectives and try
     * to find one where the two are not equal.
     */
    @Override
    public int compareTo(T c) {
        int i = (int) Math.signum(this.getFitness() - c.getFitness());
        if (i == 0) {
            return compareSecondaryObjective(c.self());
        } else
            return i;
    }

    /**
     * Secondary Objectives are specific to chromosome types
     *
     * @param o a {@link Chromosome} object.
     * @return a int.
     */
    public abstract int compareSecondaryObjective(T o);

    /**
     * Apply mutation
     */
    public abstract void mutate();

    /**
     * Fixed single point cross over
     *
     * @param other    a {@link Chromosome} object.
     * @param position a int.
     * @throws ConstructionFailedException if any.
     */
    public void crossOver(T other, int position) throws ConstructionFailedException {
        crossOver(other, position, position);
    }

    /**
     * Single point cross over
     *
     * @param other     a {@link Chromosome} object.
     * @param position1 a int.
     * @param position2 a int.
     * @throws ConstructionFailedException if any.
     */
    public abstract void crossOver(T other, int position1, int position2)
            throws ConstructionFailedException;

    /**
     * Apply the local search
     *
     * @param objective a {@link LocalSearchObjective}
     *                  object.
     */
    public abstract boolean localSearch(LocalSearchObjective<T> objective);

    /**
     * Apply the local search
     *
     * @param objective
     *            a {@link org.evosuite.ga.LocalSearchObjective} object.
     */
    // public void applyAdaptiveLocalSearch(LocalSearchObjective<? extends
    // Chromosome> objective) {
    // // No-op
    // }

    /**
     * Apply DSE
     *
     * @param algorithm
     *            a {@link org.evosuite.ga.GeneticAlgorithm} object.
     */
    // public abstract boolean applyDSE(GeneticAlgorithm<?> algorithm);

    /**
     * Return length of individual
     *
     * @return a int.
     */
    public abstract int size();

    /**
     * Return whether the chromosome has changed since the fitness value was
     * computed last
     *
     * @return a boolean.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Set changed status to @param changed
     *
     * @param changed a boolean.
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
        // If it's changed, then that also implies LS is possible again
        localSearchApplied = false;
    }

    public boolean hasLocalSearchBeenApplied() {
        return localSearchApplied;
    }

    public void setLocalSearchApplied(boolean localSearchApplied) {
        this.localSearchApplied = localSearchApplied;
    }

    /**
     * <p>
     * Getter for the field <code>coverage</code>.
     * </p>
     * <p>
     * Returns a single coverage value calculated as the average of
     * coverage values for all fitness functions.
     *
     * @return a double.
     */
    public double getCoverage() {
        final double cov = coverageValues.values().stream().collect(averagingDouble(Double::doubleValue));
        assert (cov >= 0.0 && cov <= 1.0) : "Incorrect coverage value " + cov + ". Expected value between 0 and 1";
        return cov;
    }

    /**
     * Computes the total number of goals covered by this chromosome taking into account all the
     * fitness functions known to this chromosome.
     *
     * @return
     */
    public int getNumOfCoveredGoals() {
        return numsCoveredGoals.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Computes the total number of goals not covered by this chromosome taking into account all the
     * fitness functions known to this chromosome.
     *
     * @return
     */
    public int getNumOfNotCoveredGoals() {
        return numsNotCoveredGoals.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    public void setNumsOfCoveredGoals(Map<FitnessFunction<T>, Integer> fits) {
        this.numsCoveredGoals.clear();
        this.numsCoveredGoals.putAll(fits);
    }

    public void setNumsOfNotCoveredGoals(Map<FitnessFunction<T>, Integer> fits) {
        this.numsNotCoveredGoals.clear();
        this.numsNotCoveredGoals.putAll(fits);
    }

    public void setNumOfNotCoveredGoals(FitnessFunction<T> ff, int numCoveredGoals) {
        this.numsNotCoveredGoals.put(ff, numCoveredGoals);
    }

    public Map<FitnessFunction<T>, Integer> getNumsOfCoveredGoals() {
        return this.numsCoveredGoals;
    }

    public LinkedHashMap<FitnessFunction<T>, Integer> getNumsNotCoveredGoals() {
        return numsNotCoveredGoals;
    }

    public Map<FitnessFunction<T>, Double> getCoverageValues() {
        return this.coverageValues;
    }

    public void setCoverageValues(Map<? extends FitnessFunction<T>, Double> coverages) {
        this.coverageValues.clear();
        this.coverageValues.putAll(coverages);
    }

    // public void setNumOfCoveredGoals(int numOfCoveredGoals) {
    // this.numOfCoveredGoals = numOfCoveredGoals;
    // }

    /**
     * Gets the coverage value for a given fitness function
     *
     * @param ff a fitness function
     * @return the number of covered goals for {@code ff}
     */
    public double getCoverage(FitnessFunction<T> ff) {
        return coverageValues.getOrDefault(ff, 0.0);
    }

    /**
     * Sets the coverage value for a given fitness function
     *
     * @param ff       a fitness function
     * @param coverage the coverage value
     */
    public void setCoverage(FitnessFunction<T> ff, double coverage) {
        this.coverageValues.put(ff, coverage);
    }

    /**
     * Gets the number of covered goals for a given fitness function
     *
     * @param ff a fitness function
     * @return the number of covered goals for {@code ff}
     */
    public int getNumOfCoveredGoals(FitnessFunction<?> ff) {
        return numsCoveredGoals.getOrDefault(ff, 0);
    }

    /**
     * Gets the number of not covered goals for a given fitness function
     *
     * @param ff a fitness function
     * @return the number of covered goals for {@code ff}
     */
    public int getNumOfNotCoveredGoals(FitnessFunction<?> ff) {
        return numsNotCoveredGoals.getOrDefault(ff, 0);
    }

    /**
     * Sets the number of covered goals for a given fitness function
     *
     * @param ff              a fitness function
     * @param numCoveredGoals the number of covered goals
     */
    public void setNumOfCoveredGoals(FitnessFunction<T> ff, int numCoveredGoals) {
        this.numsCoveredGoals.put(ff, numCoveredGoals);
    }

    public void updateAge(int generation) {
        this.age = generation;
    }

    public int getAge() {
        return age;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int r) {
        this.rank = r;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double d) {
        this.distance = d;
    }

    /**
     * Computes the fitness value of this chromosome, trying to use the given <code>Class</code>
     * instance <code>clazz</code> as the fitness function. Returns <code>0.0</code> if none of the
     * fitness functions known to this chromosome is assignment compatible with <code>clazz</code>.
     *
     * @param clazz
     * @return
     */
    public double getFitnessInstanceOf(Class<?> clazz) {
        Optional<FitnessFunction<T>> off = fitnessValues.keySet().stream()
                .filter(clazz::isInstance)
                .findFirst();
        return off.map(fitnessValues::get).orElse(0.0);
    }

    /**
     * Computes the coverage value of this chromosome, trying to use the given <code>Class</code>
     * instance <code>clazz</code> as the fitness function. Returns <code>0.0</code> if none of the
     * fitness functions known to this chromosome is assignment compatible with <code>clazz</code>.
     *
     * @param clazz
     * @return
     */
    public double getCoverageInstanceOf(Class<?> clazz) {
        Optional<FitnessFunction<T>> off = coverageValues.keySet().stream()
                .filter(clazz::isInstance)
                .findFirst();
        return off.map(coverageValues::get).orElse(0.0);
    }

    /**
     * Increases by one the number of times this chromosome has been mutated
     */
    public void increaseNumberOfMutations() {
        this.numberOfMutations++;
    }

    /**
     * Return number of times this chromosome has been mutated
     */
    public int getNumberOfMutations() {
        return this.numberOfMutations;
    }

    /**
     * Set number of times this chromosome has been mutated
     */
    public void setNumberOfMutations(int numberOfMutations) {
        this.numberOfMutations = numberOfMutations;
    }

    /**
     * Increases by one the number of times this chromosome has been evaluated
     */
    public void increaseNumberOfEvaluations() {
        this.numberOfEvaluations++;
    }

    /**
     * Return number of times this chromosome has been evaluated
     */
    public int getNumberOfEvaluations() {
        return this.numberOfEvaluations;
    }

    /**
     * Set number of times this chromosome has been evaluated
     */
    public void setNumberOfEvaluations(int numberOfEvaluations) {
        this.numberOfEvaluations = numberOfEvaluations;
    }

//    /**
//     * Returns the tolerance of the system accepting a worse solution than the existing one. (Note:
//     * method used by Chemical Reaction Optimization algorithms)
//     *
//     * @return a double value
//     */
//    public double getKineticEnergy() {
//        return this.kineticEnergy;
//    } //化学反应优化，无用

//    /**
//     * Sets the tolerance of the system accepting a worse solution than the existing one. (Note:
//     * method used by Chemical Reaction Optimization algorithms)
//     *
//     * @param kineticEnergy a double value
//     */
//    public void setKineticEnergy(double kineticEnergy) {
//        this.kineticEnergy = kineticEnergy;
//    }//化学反应优化

    /**
     * Returns the total number of collisions a chromosome (i.e., a molecule in a CRO scenario) has
     * taken. (Note: method used by Chemical Reaction Optimization algorithms)
     *
     * @return a integer value
     */
    public int getNumCollisions() {
        return this.numCollisions;
    }

    /**
     * Sets the total number of collisions of a chromosome (i.e., a molecule in a CRO scenario).
     * (Note: method used by Chemical Reaction Optimization algorithms)
     *
     * @param numCollisions a integer value
     */
    public void setNumCollisions(int numCollisions) {
        this.numCollisions = numCollisions;
    }

    /**
     * Sets the total number of collisions of a chromosome (i.e., a molecule in a CRO scenario) to
     * zero. (Note: method used by Chemical Reaction Optimization algorithms)
     */
    public void resetNumCollisions() {
        this.numCollisions = 0;
    }

    /**
     * Increases the total number of collisions of a chromosome (i.e., a molecule in a CRO scenario)
     * by one. (Note: method used by Chemical Reaction Optimization algorithms)
     */
    public void increaseNumCollisionsByOne() {
        this.numCollisions++;
    }
}
