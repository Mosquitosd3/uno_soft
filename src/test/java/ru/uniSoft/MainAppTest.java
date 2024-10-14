package ru.uniSoft;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainAppTest {
    @Test
    void testIsValidLine() {
        // Тестирование валидных строк
        assertTrue(MainApp.isValidLine("\"123\";\"456\";\"789\""));
        assertTrue(MainApp.isValidLine("123;456;789"));

        // Тестирование невалидных строк
        assertFalse(MainApp.isValidLine("abc;def;ghi"));
    }

    @Test
    void testUnionFind() {
        MainApp.UnionFind uf = new MainApp.UnionFind();

        // Тестирование простого объединения
        uf.union(1, 2);
        uf.union(2, 3);
        assertEquals(uf.find(1), uf.find(2));
        assertEquals(uf.find(2), uf.find(3));
        assertNotEquals(uf.find(1), uf.find(4));  // 4 не в группе с 1, 2, 3
    }

    @Test
    void testGroupFormation() {
        List<String[]> rows = Arrays.asList(
                new String[]{"1", "2", "3"},
                new String[]{"4", "5", "6"},
                new String[]{"1", "7", "8"},
                new String[]{"9", "10", "11"}
        );

        Map<String, Integer> valueToIndex = new HashMap<>();
        MainApp.UnionFind unionFind = new MainApp.UnionFind();

        // Пример простой логики объединения
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            String[] row = rows.get(rowIndex);
            for (String value : row) {
                if (!valueToIndex.containsKey(value)) {
                    valueToIndex.put(value, rowIndex);
                } else {
                    unionFind.union(rowIndex, valueToIndex.get(value));
                }
            }
        }

        // Проверка, что строки с общим значением сгруппированы
        Map<Integer, Set<String[]>> groups = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            int root = unionFind.find(i);
            groups.computeIfAbsent(root, k -> new HashSet<>()).add(rows.get(i));
        }

        // Проверка количества групп
        assertEquals(3, groups.size());  // Должно быть 3 группы

        // Проверка, что строки с общим значением объединены в одну группу
        Set<String[]> groupWith1 = groups.get(unionFind.find(0)); // Группа, содержащая строку с "1"
        assertEquals(2, groupWith1.size());  // Группа с "1" должна содержать 2 строки
    }
}