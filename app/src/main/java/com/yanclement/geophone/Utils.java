package com.yanclement.geophone;

import java.util.ArrayList;

/**
 * Created by YPierru on 18/11/2016.
 */

public class Utils {


    public static boolean isInteger(String str){
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String[] listToArray(ArrayList<String> list){
        String[] array = new String[list.size()];

        for(int i=0;i<list.size();i++){
            array[i]=list.get(i);
        }
        return array;
    }
}
