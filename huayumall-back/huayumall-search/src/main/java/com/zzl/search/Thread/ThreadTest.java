package com.zzl.search.Thread;

import net.sf.jsqlparser.statement.execute.Execute;

import java.util.concurrent.*;

/**
 * 1、继承Thread类
 * 2、实现Runnable接口
 * 3、callable接口 + futureTask(可以拿到返回结果，处理异常)
 * FutureTask<Integer> futureTask = new FutureTask<>(new myCallabe());
 * new Thread(futureTask).start();
 * Integer integer = futureTask.get(); //获取myCallable()的返回值
 * 当调用get()方式时会阻塞线程等待方法的执行
 * <p>
 * 4、线程池
 */
public class ThreadTest {
    public static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 1、开启线程池线程会成阻塞状态，直到线程的销毁
         * CompletableFuture.runAsync()  无法获取返回值
         * whenComplete(返回结果，异常)  //程序执行成功：获取结果，异常信息
         * exceptionally(异常)  //程序发生异常，可以给个返回值
         */
        System.out.println("thread  start.......");
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, service);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).whenComplete((resp,ex)->{
//            System.out.println("程序执行成功。。。结果为："+resp+";异常为："+ex);
//        }).exceptionally((ex)->{
//            return 0;
//        });
//        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).handle((resp, ex) -> {
//            if (resp != null) {
//                System.out.println("程序执行成功，结果为："+resp);
//                return resp * 2;
//            }
//            if (ex != null) {
//                //出了异常
//                System.out.println("异常信息为："+ex.getMessage());
//                return 10;
//            }
//            return 0;
//        });

        /**
         * 2、线程串行化方法，就好比两个线程方法
         *  1）、thenRun()  无法获取上一个线程的结果，无返回值
         *  2）、thenAccept() 能获取上一个线程的结果，无返回值
         *  3）、thenApply()  能获取上一个线程的结果，有返回值
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        },service).thenApplyAsync(resp -> {
//            System.out.println("线程2启动了。。上一个任务结果为：" + resp);
//            return 10;
//        }, service);
//
//        System.out.println("thread  end.......线程2结果为："+future.get());

        /**
         * 3、两种组合都要完成
         */
//        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程1：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("线程1结果为：" + i);
//            return i;
//        }, service);
//        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("线程2：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("线程2结果为：" + i);
//            return i;
//        }, service).thenCombineAsync(future01, (resp01, resp02) -> {
//            System.out.println("线程1结果为：" + resp01 + "====>" + "线程2结果为：" + resp02);
//            String msg = "线程3";
//            return msg;
//        }, service);
//        System.out.println("线程3的返回结果为："+future02.get());

        /**
         * 两个组合只要有一个完成
         */
        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前01线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, service);

        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("当前02线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }, service);

        CompletableFuture<String> future = future02.applyToEitherAsync(future01, (resp) -> {
            System.out.println("当前03线程：" + Thread.currentThread().getId());
            System.out.println("上一个线程的结果为：" + resp);
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return resp + "哈哈";
        }, service);

        System.out.println("thread  end........."+future.get());
    }

    //1、Thread
    public static class myThread extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    //2、Runnable
    public static class myRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程id为" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    //3、callable+FutureTask
    public static class myCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程id为：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
    //4、线程池
    //1)、ExecutorService service = Executors.newFixedThreadPool(10);
}
