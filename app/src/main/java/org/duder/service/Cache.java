//package org.duder.service;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//public class Cache {
//    private final Set<String> hobbies;
//    private Cache instance;
//
//    private Cache() {
//        this.hobbies = Collections.unmodifiableSet(new HashSet<>());
//    }
//
//    public Cache getInstance() {
//        if (instance == null) {
//            instance = new Cache();
//        }
//        return instance;
//    }
//
//    public void addHobby(String hobby) {
//        hobbies.add(hobby);
//    }
//
//    public Set<String> getHobbies() {
//        return hobbies;
//    }
//}
