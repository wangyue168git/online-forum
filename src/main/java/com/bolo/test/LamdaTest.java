//package com.bolo.test;
//
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.FileSystemXmlApplicationContext;
//import sun.plugin.javascript.navig.Array;
//
//import javax.xml.bind.Marshaller;
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Stream;
//
///**
// * @Author wangyue
// * @Date 13:58
// */
//public class LamdaTest {
//
////    public static void filter(List name, Predicate condition){
////
////        name.stream().filter((name) -> (condition.test(name)))
////                .forEach((name) -> {System.out.println(name + "");
////                });
////
////        name
////    }
//
//    static int i = 1;
//    public static void add(int i) {
//        System.out.println(++i);
//    }
//    static String s = "12";
//    public static void set(String s) {//String 传的是引用
//        s = s + "3";//值改变时，相当于new了一个新的String在堆中，拷贝的引用s指向了新的对象
//        System.out.println(s);
//    }
//
//    public static void main(String[] args) {
//        List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9);
//        Stream<Integer> stream = numbers.stream();
//        stream.filter((x) ->{
//            return x % 2 == 0;
//        }).map((x)->{
//            return x * x;
//        }).forEach(System.out::println);
//
//        ApplicationContext ctx = new FileSystemXmlApplicationContext("");
//        ctx.getBean("");
//
//
//
//        add(i);
//        System.out.println(i);
//
//        set(s);
//        System.out.println(s);
//    }
//
//    static void writeTo(List<? super Client> agg){
//        agg.add(new Client());
//    }
//
//    int gcd(int x,int y){
//        return y > 0 ? gcd(y,x%y) : x;
//    }
//
//
//}
