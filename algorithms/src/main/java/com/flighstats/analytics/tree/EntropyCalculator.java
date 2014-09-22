package com.flighstats.analytics.tree;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class EntropyCalculator {
    public static final double LOG_2 = Math.log(2);

    double entropy(List<LabeledItem> items, Object attribute, Integer attributeValue) {
        List<LabeledItem> matchingItems = items.stream().filter(i -> attributeValue.equals(i.evaluate(attribute))).collect(toList());

        double numberPositive = matchingItems.stream().filter(LabeledItem::positive).count();
        double numberNegative = matchingItems.stream().filter(LabeledItem::negative).count();

        double totalItems = matchingItems.size();
        return entropy(totalItems, numberPositive, numberNegative);
    }

    private double entropy(double totalItems, double numberPositive, double numberNegative) {
        double fractionPositive = numberPositive / totalItems;
        double fractionNegative = numberNegative / totalItems;

        return -fractionPositive * log2(fractionPositive) - fractionNegative * log2(fractionNegative);
    }

    double labelEntropy(List<LabeledItem> items) {
        double numberPositive = items.stream().filter(LabeledItem::positive).count();
        double numberNegative = items.stream().filter(LabeledItem::negative).count();
        return entropy(items.size(), numberPositive, numberNegative);
    }

    Double entropyGain(List<LabeledItem> items, Object attribute) {
        Set<Integer> values = items.stream().map(li -> li.evaluate(attribute)).collect(toSet());

        double labelEntropy = labelEntropy(items);
        int totalItems = items.size();
        for (Integer value : values) {
            double numberMatching = items.stream().filter(i -> value.equals(i.evaluate(attribute))).count();
            double fractionMatching = numberMatching / totalItems;
            labelEntropy = labelEntropy - entropy(items, attribute, value) * fractionMatching;
        }
        return labelEntropy;
    }

    private double log2(double number) {
        //not actually true, but for this algorithm, it works fine.
        if (number == 0) {
            return 0;
        }
        return Math.log(number) / LOG_2;
    }


}
