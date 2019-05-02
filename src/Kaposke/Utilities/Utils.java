package Kaposke.Utilities;

import java.io.*;
import java.util.List;

public class Utils {
    public static int[] byteMatrixToIntArray(byte[][] matrix) {
        int[] array = new int[matrix.length * matrix[0].length];

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                array[y * matrix[0].length + x] = matrix[y][x];
            }
        }
        return array;
    }

    public static String intArrayToString(int[] array, String separator) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            string.append(array[i]);
            if(i < array.length - 1)
                string.append(separator);
        }
        return string.toString();
    }

    public static String byteMatrixToString(byte[][] matrix, String separator) {
        return intArrayToString(byteMatrixToIntArray(matrix), separator);
    }

    public static String booleanArrayToString(boolean[] array, String separator) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            string.append(array[i] ? "true" :  "false");
            if(i < array.length - 1)
                string.append(separator);
        }
        return string.toString();
    }

    public static String booleanArrayToIntString(boolean[] array, String separator) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            string.append(array[i] ? "1" : "0");
            if(i < array.length - 1)
                string.append(separator);
        }
        return string.toString();
    }

    public static boolean intToBoolean(int value) {
        return value > 0;
    }

    public static String stringListToSingleString(List<String> strings, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            stringBuilder.append(strings.get(i));
            if(i < strings.size() - 1)
                stringBuilder.append(separator);
        }
        return stringBuilder.toString();
    }

    // returns highest double index
    public static int getHighestDoubleIndex(double[] list) {
        double highest = Double.NEGATIVE_INFINITY;
        int highestIndex = 0;

        for (int i = 0; i < list.length; i++) {
            if(list[i] > highest) {
                highest = list[i];
                highestIndex = i;
            }
        }
        return highestIndex;
    }

    public static void createFileWith(String fileName, List<String> lines) throws IOException {
        File file = new File(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
    }
}
