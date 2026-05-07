package com.team14.sportsmanager.logic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NameGenerator {

    private static List<String> maleFirstNames = new ArrayList<>();
    private static List<String> femaleFirstNames = new ArrayList<>();
    private static List<String> lastNames = new ArrayList<>();
    private static boolean loaded = false;
    private static final Random rand = new Random();

    private static void load() {
        if (loaded) return;
        try {
            InputStream is = NameGenerator.class.getModule().getResourceAsStream("/names.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            maleFirstNames = parseArray(json, "maleFirstNames");
            femaleFirstNames = parseArray(json, "femaleFirstNames");
            lastNames = parseArray(json, "lastNames");
            loaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> parseArray(String json, String key) {
        List<String> result = new ArrayList<>();
        String marker = "\"" + key + "\"";
        int start = json.indexOf(marker);
        if (start == -1) return result;

        start = json.indexOf('[', start);
        int end = json.indexOf(']', start);
        String array = json.substring(start + 1, end);

        for (String item : array.split(",")) {
            String clean = item.trim().replace("\"", "");
            if (!clean.isEmpty()) {
                result.add(clean);
            }
        }
        return result;
    }

    public static String randomMaleName() {
        load();
        String first = maleFirstNames.get(rand.nextInt(maleFirstNames.size()));
        String last = lastNames.get(rand.nextInt(lastNames.size()));
        return first + " " + last;
    }

    public static String randomFemaleName() {
        load();
        String first = femaleFirstNames.get(rand.nextInt(femaleFirstNames.size()));
        String last = lastNames.get(rand.nextInt(lastNames.size()));
        return first + " " + last;
    }

    public static String randomName() {
        load();
        return rand.nextBoolean() ? randomMaleName() : randomFemaleName();
    }

    public static List<String> getTeamNames(String sportType) {
        load();
        List<String> result = new ArrayList<>();
        try {
            InputStream is = NameGenerator.class.getModule().getResourceAsStream("/names.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            String marker = "\"" + sportType + "\"";
            int start = json.indexOf("teamNames");
            start = json.indexOf(marker, start);
            start = json.indexOf('[', start);
            int end = json.indexOf(']', start);
            String array = json.substring(start + 1, end);

            for (String item : array.split(",")) {
                String clean = item.trim().replace("\"", "");
                if (!clean.isEmpty()) {
                    result.add(clean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}