package player.music.sorting;



public class SortingStringIntArray {

    private String[] strings;
    private int[] ints;
    private String tempString;
    private int tempInt, sortingLength = 0;

    public SortingStringIntArray(String[] strings, int[] ints) {

        this.strings = strings;
        this.ints = ints;

        sortingLength = strings.length;
        for (int i = 0; i < sortingLength; i++) {

            for (int j = 0; j < sortingLength-1; j++) {

                int a = strings[i].compareToIgnoreCase(strings[j]);
                if (a < 0) {
                    tempString = strings[i];
                    strings[i] = strings[j];
                    strings[j] = tempString;

                    tempInt = ints[i];
                    ints[i] = ints[j];
                    ints[j] = tempInt;
                }
            }
        }
    }

    public String[] getSortedStrings() {
        return strings;
    }

    public int[] getSortedInts() {
        return ints;
    }
}
