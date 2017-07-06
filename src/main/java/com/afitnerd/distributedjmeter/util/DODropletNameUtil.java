package com.afitnerd.distributedjmeter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class DODropletNameUtil {

    public static final int CREATE_MULTIPLE_DROPLETS_MAX = 10;

    public static List<List<String>> dropletNumbers(String base, int numDroplets) {
        List<List<String>> ret = new ArrayList<>();
        for (int i = 0; i < numDroplets/CREATE_MULTIPLE_DROPLETS_MAX; i++) {
            final int j = i;
            List<String> nameGroup = IntStream.range(0, 10)
                .mapToObj(k -> base + "-" + ((k + 1)+(j*CREATE_MULTIPLE_DROPLETS_MAX)))
                .collect(toList());
            ret.add(nameGroup);
        }
        if (numDroplets%CREATE_MULTIPLE_DROPLETS_MAX != 0) {
            List<String> nameGroup = IntStream.range(0, numDroplets%CREATE_MULTIPLE_DROPLETS_MAX)
                .mapToObj(k -> base + "-" + ((k + 1)+(numDroplets/CREATE_MULTIPLE_DROPLETS_MAX*CREATE_MULTIPLE_DROPLETS_MAX)))
                .collect(toList());
            ret.add(nameGroup);
        }
        return ret;
    }
}
