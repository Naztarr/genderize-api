package com.naz.profiler.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryResolver {
    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();

    static {
        for(String code : Locale.getISOCountries()){
            String name = new Locale("", code)
                    .getDisplayCountry(Locale.ENGLISH).toLowerCase();

            COUNTRY_MAP.put(name, code);
        }
    }

    public static String resolveCode(String text){
        if (text == null || text.isBlank()) return null;

        String lower = text.toLowerCase();

        return COUNTRY_MAP.entrySet().stream()
                .sorted((a,b) -> b.getKey().length() - a.getKey().length())
                .filter(e -> lower.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
