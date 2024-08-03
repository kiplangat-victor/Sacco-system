package com.emtechhouse.usersservice.utils.code_generator;

import java.util.Comparator;

public class CodeComparator implements Comparator<String> {
    private int index;

    public CodeComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(String code1, String code2) {
        int numericPart1 = Integer.parseInt(code1.substring(index));
        int numericPart2 = Integer.parseInt(code2.substring(index));
        return Integer.compare(numericPart2, numericPart1);
    }
}