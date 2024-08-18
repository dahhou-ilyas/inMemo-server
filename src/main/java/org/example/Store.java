package org.example;

import java.util.Date;
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

        @Override
        public String toString() {
            if(exp != null){
                return "("+value +" , "+new Date(exp) +")";
            }
            return "("+value+")";

        }
    }

    private static HashMap<String , Node> db=new HashMap<>();

    public static synchronized String set(String key,String value,Long exp){
        db.put(key,new Node(value,exp));
        System.out.println(db);
        return "OK";
    }

    public static synchronized String get(String key){
        Node result = db.get(key);
        if (result == null) {
            return null;
        }
        if (result.exp == null || result.exp > new Date().getTime()) {
            return result.value;
        }
        db.remove(key);  // Clean up expired key
        return null;
    }

}