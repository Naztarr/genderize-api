package com.naz.profiler.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryResolver {

    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();

    static {
        for (String code : Locale.getISOCountries()) {
            String name = new Locale("", code)
                    .getDisplayCountry(Locale.ENGLISH)
                    .toLowerCase();

            COUNTRY_MAP.put(name, code);
        }
    }

    public static String resolveCode(String text) {

        if (text == null || text.isBlank()) {
            return null;
        }

        String normalized = text.toLowerCase()
                .replaceAll("[^a-z ]", " ")
                .replaceAll("\\s+", " ");

        for (Map.Entry<String, String> entry : COUNTRY_MAP.entrySet()) {
            String country = entry.getKey();

            if (normalized.contains(" " + country + " ")
                    || normalized.startsWith(country + " ")
                    || normalized.endsWith(" " + country)
                    || normalized.equals(country)) {
                return entry.getValue();
            }
        }

        return null;
    }
}























//package com.naz.profiler.util;
//
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//public class CountryResolver {
//    private static final Map<String, String> COUNTRY_MAP = new HashMap<>();
//
//    static {
//        for(String code : Locale.getISOCountries()){
//            String name = new Locale("", code)
//                    .getDisplayCountry(Locale.ENGLISH).toLowerCase();
//
//            COUNTRY_MAP.put(name, code);
//        }
//    }
//
//    public static String resolveCode(String text){
//        if (text == null || text.isBlank()) {
//            return null;
//        }
//
//        String lower = text.toLowerCase();
//
//        for (Map.Entry<String, String> entry : COUNTRY_MAP.entrySet()) {
//            if (lower.contains(entry.getKey())) {
//                return entry.getValue();
//            }
//        }
//        return null;
//    }
//}
