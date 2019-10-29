package com.bolo.test.test02;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Created by wangyue on 2018/10/15.
 */
public class RecursiveTaskDemo extends RecursiveTask<Integer> {

    /**
     *  每个"小任务"最多只打印70个数
     */
    private static final int MAX = 70;
    private int arr[];
    private int start;
    private int end;

    public RecursiveTaskDemo(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        if ((end - start) < MAX){
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        }else {
            System.err.println("=====任务分解======");
            int middle = (start + end)/2;
            RecursiveTaskDemo left = new RecursiveTaskDemo(arr,start,middle);
            RecursiveTaskDemo right = new RecursiveTaskDemo(arr,middle,end);
            left.fork();

            right.fork();
            return left.join() + right.join();
        }
    }

    interface A<T>{
        T get();
    }

    class B<T extends A> implements A<Integer>{

        @Override
        public Integer get() {
            return null;
        }

    }

    class C implements A{
        @Override
        public Object get() {
            return null;
        }
    }


    //编译时检查，运行时擦除，运行时类型为A<Number>
//    private void set(A<? extends Number> n){
//        LoadingCache employeeCache =
//                CacheBuilder.newBuilder()
//                        .maximumSize(100) // maximum 100 records can be cached
//                        .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30 minutes of access
//                        .build(new CacheLoader(){
//                            @Override
//                            public Employee load(String o) throws Exception {
//                                return getFromDatabase(o);
//                            }
//                        });
//    }
    private static Employee getFromDatabase(String empId){
        Employee e1 = new Employee("Mahesh", "Finance", "100");
        Employee e2 = new Employee("Rohan", "IT", "103");
        Employee e3 = new Employee("Sohan", "Admin", "110");

        Map<String, Employee> database = new HashMap<>();
        database.put("100", e1);
        database.put("103", e2);
        database.put("110", e3);
        System.out.println("Database hit for" + empId);
        return database.get(empId);
    }



    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        Integer i1 = 1;
        Integer i2 = 1;
        System.out.println();

        int arr[] = new int[100];
        Random random = new Random();
        int total = 0;
        // 初始化100个数字元素
        for (int i = 0; i < arr.length; i++) {
            int temp = random.nextInt(100);
            // 对数组元素赋值,并将数组元素的值添加到total总和中
            total += (arr[i] = temp);
        }
        //System.out.println("初始化时的总和=" + total);
        // 创建包含Runtime.getRuntime().availableProcessors()返回值作为个数的并行线程的ForkJoinPool
        System.out.println(Runtime.getRuntime().availableProcessors());
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();//forkjoinpool线程池内线程是并行的，普通线程池多为并发
        Integer integer = forkJoinPool.invoke( new RecursiveTaskDemo(arr, 0, arr.length));
        System.out.println("计算出来的总和=" + integer);
        System.out.println("计算出来的总和=" + integer);
        Thread.sleep(1000);
        System.out.println("计算出来的总和=" + integer);


        List<Integer> nums = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
        List<Integer> numsWithoutNull = nums.stream().
                filter(Objects::nonNull).
                collect(ArrayList::new,
                        ArrayList::add,
                        ArrayList::addAll);

        List<String> list = new ArrayList<>();
        list.stream().collect(Collectors.toList());
        list.stream().findFirst();
        list.stream().forEach(string -> {
            if (string.equals("1")){
                return;
            }
        });

        list.stream().map(string -> {
            Integer i = 1;
            return i;
        });
        Instant start = Instant.now();
        LongStream.rangeClosed(0,110)
                //并行流
                .parallel()
                .reduce( 0,Long::sum );
        LongStream.rangeClosed(0,110)
                //顺序流
                .sequential()
                .reduce( 0,Long::max);
        Instant end = Instant.now();
        System.out.println("耗费时间"+ Duration.between( start,end ).toMillis());
    }
}
