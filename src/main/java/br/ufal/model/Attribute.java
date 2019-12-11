package br.ufal.model;

import br.ufal.statistics.Correlation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
public class Attribute {

    private String name;
    private Double entropyValue;
    private Double variation;
    private Set<Correlation> correlation = new HashSet<>();
    private Double generalValue;
    private Boolean isRelevant = false;
    private double[] sample;

    @Override
    public boolean equals(Object obj) {
        return name.equals(((Attribute)obj).getName());
    }

    @Override
    public String toString() {
        return String.format("%s(%.2f); Corr: %d; Variation: %.2f; General Value: %.2f\n", name, entropyValue, correlation.size(), variation, generalValue);
    }
}
