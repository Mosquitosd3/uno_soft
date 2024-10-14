package ru.uniSoft;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;


public class MainApp {
    public static void main(String[] args) {
        String urlStr = "https://github.com/PeacockTeam/new-job/releases/download/v1.0/lng-4.txt.gz";
        List<String[]> rows = new ArrayList<>();
        Map<String, Integer> valueToIndex = new HashMap<>();  // Map to store value -> row index
        UnionFind unionFind = new UnionFind();

        try (GZIPInputStream gzip = new GZIPInputStream(new URL(urlStr).openStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzip))) {

            String line;
            int rowIndex = 0;
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    String[] columns = line.split(";");
                    rows.add(columns);

                    for (String value : columns) {
                        value = value.replace("\"", "").trim();  // Clean up value
                        if (!value.isEmpty()) {
                            if (!valueToIndex.containsKey(value)) {
                                valueToIndex.put(value, rowIndex);
                            } else {
                                unionFind.union(rowIndex, valueToIndex.get(value));
                            }
                        }
                    }
                    rowIndex++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Map<Integer, Set<String[]>> groups = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            int root = unionFind.find(i);
            groups.computeIfAbsent(root, k -> new HashSet<>()).add(rows.get(i));
        }

        List<Set<String[]>> groupList = new ArrayList<>(groups.values());
        groupList.sort((g1, g2) -> g2.size() - g1.size());

        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            int numGroups = 0;
            for (Set<String[]> group : groupList) {
                if (group.size() > 1) {
                    numGroups++;
                }
            }
            writer.println("Количество групп, содержащих более одного элемента: " + numGroups);

            int groupNumber = 1;
            for (Set<String[]> group : groupList) {
                writer.println("Group " + groupNumber++);
                for (String[] row : group) {
                    writer.println(String.join(";", row));
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidLine(String line) {
        return line.matches("(\"?[0-9]*\"?;)*\"?[0-9]*\"?");
    }

    static class UnionFind {
        private final Map<Integer, Integer> parent = new HashMap<>();
        private final Map<Integer, Integer> rank = new HashMap<>();

        public int find(int x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                rank.put(x, 1);
            }
            if (x != parent.get(x)) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX != rootY) {
                if (rank.get(rootX) > rank.get(rootY)) {
                    parent.put(rootY, rootX);
                } else if (rank.get(rootX) < rank.get(rootY)) {
                    parent.put(rootX, rootY);
                } else {
                    parent.put(rootY, rootX);
                    rank.put(rootX, rank.get(rootX) + 1);
                }
            }
        }
    }
}

