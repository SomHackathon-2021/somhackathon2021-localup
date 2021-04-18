package com.hakathon.localapp;

import java.util.Random;

public class CodeGenerator {
    Random random = new Random();

    public String genCode(int times, int chars) {
        String code = "";
        for(int i = times; i >= 1; i--){
            for(int j = chars; j >= 1; j--){
                code = code + (char) (random.nextInt(26) + 'a');
            }
            code = code + "-";
        }
        return new StringBuilder(code).deleteCharAt(code.length()-1).toString().toUpperCase();
    }

}
