package br.ufal.statistics;

import java.util.List;

public interface Entropy {

    public <T> Double calculate(List<T> values);

    public <T> Double variation(List<T> values);
}
