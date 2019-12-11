package br.ufal.statistics;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * https://whaticode.wordpress.com/2010/05/24/a-java-implementation-for-shannon-entropy/
 * */
public class ShannonEntropy implements Entropy {

     @Override
     public <T> Double calculate(List<T> values) {
         // count the occurrences of each value
         Map<T, Long> mapClustering = values.stream().collect(
                Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )
        );

        Set<T> keys = mapClustering.keySet();

        //new logarithm base
        int k = (keys.size() <= 1) ? 2 : keys.size();

        Double entropy = 0.0; //shannon entropy value
        for (T key : keys) {
            Double probability = (double) mapClustering.get(key) / values.size();
            double entropyRate = probability * (Math.log(probability) / Math.log(k));
            entropy -= entropyRate;
        }

        return entropy;
    }

    @Override
    public <T> Double variation(List<T> values) {
        // count the occurrences of each value
        Map<T, Long> mapClustering = values.stream().collect(
                Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )
        );

        Set<T> keys = mapClustering.keySet();

        return keys.size() / (double) values.size();
    }
}
