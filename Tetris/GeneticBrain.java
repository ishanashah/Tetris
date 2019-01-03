package Tetris;

import java.util.Arrays;
import java.util.Collections;

public class GeneticBrain {
    public static final int GENERATIONS = 10;
    public static final int MAX_SCORE = 10000000;
    public static final int SIZE = 45;
    public static final double MUTATION = 0.05;
    public static final double MUTATION_DIFFERENCE = 0.02;

    //Index 0 is Rows Cleared
    //Index 1 is Holes Created
    //Index 2 is Ruggedness

    private Species[] population;

    public GeneticBrain(){
        population = new Species[SIZE];
        for(int i = 0; i < SIZE; i++){
            population[i] = new Species();
        }

    }

    public void setFitness(int i, int fitnessValue){
        //fitness[i] = fitnessValue;

        population[i].setFitness(fitnessValue);
    }



    public double[] getPopulation(int i){
        return population[i].getWeight();
    }


    public void generateNewPopulation(){
        Arrays.sort(population, Collections.reverseOrder());

        Species[] newPopulation = new Species[SIZE];
        int size = 0;

        for(int i = 0; i < 10 - 1; i++){
            for(int j = i + 1; j < 10; j++){
                newPopulation[size] = new Species(population[i], population[j]);
                if(size < SIZE - 1){
                    size++;
                }
            }
        }
        population = newPopulation;
    }


    public static class Species implements Comparable{
        private double[] weights = new double[3];;
        private int fitness = 1;

        public Species(){
            weights[0] = Math.random();
            weights[1] = Math.random() * (-1);
            weights[2] = Math.random() * (-1);
            normalize();
        }

        public Species(Species lhs, Species rhs){
            for(int i = 0; i < weights.length; i++){
                weights[i] = (lhs.getWeight(i) * lhs.fitness + rhs.getWeight(i) * rhs.fitness) / 2;
            }
            normalize();
            for(int i = 0; i < weights.length; i++){
                if(Math.random() < MUTATION){
                    weights[i] += (Math.random() - 0.5) / (0.5 / MUTATION_DIFFERENCE);
                }
                normalize();
            }
        }

        public void setFitness(int fitness){
            this.fitness = fitness;
        }

        public int getFitness(){
            return fitness;
        }

        public double[] getWeight(){
            double[] output = new double[weights.length];
            for(int i = 0; i < weights.length; i++){
                output[i] = weights[i];
            }
            return output;
        }

        public double getWeight(int index){
            return weights[index];
        }

        private void normalize(){
            double length = 0;
            for(int i = 0; i < weights.length; i++){
                length += Math.abs(weights[i]) * Math.abs(weights[i]);
            }
            length = Math.sqrt(length);
            for(int i = 0; i < weights.length; i++){
                weights[i] = weights[i] / length;
            }
        }

        public int compareTo(Object other){
            if(!(other instanceof Species)) return 0;
            Species otherSpecies = (Species) other;
            return fitness - otherSpecies.fitness;
        }

    }
}
