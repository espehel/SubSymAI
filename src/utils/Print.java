package utils;

/**
 * Created by espen on 06/04/15.
 */
public class Print {

    public static void array(String tag, int[] array){
        System.out.print(tag + ": [");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+",");
        }
        System.out.println("]");
    }

    public static void array(String tag, double[] array){
        System.out.print(tag + ": [");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+",");
        }
        System.out.println("]");
    }
}
