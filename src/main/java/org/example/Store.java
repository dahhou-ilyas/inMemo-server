package org.example;

import java.util.HashMap;

public class Store {
    private static class Node {
        private String value;
        private Long exp;

        Node(String value, Long exp) {
            this.value = value;
            this.exp = exp;
        }

        Node(String value) {
            this.value = value;
        }

    }

    private static HashMap<String , Node> db=new HashMap<>();

    public static String set(String key,String value,Long exp){
        db.put(key,new Node(value,exp));
        return "OK";
    }


}
