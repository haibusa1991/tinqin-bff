//package com.tinqin.bff.rest;
//
//import java.lang.reflect.Method;
//
//public class TinqinRestExportRunner {
//    public static void main(String[] args) {
//        try {
//            TinqinRestExportRunner.class.getClassLoader();
//
//            Class<?> restExport = Class.forName("com.tinqin.restexport.RestExport");
//
//            Object o = restExport.getDeclaredConstructor().newInstance();
//            Method main = restExport.getMethod("main", String[].class);
//            main.invoke(o, (Object) args);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
