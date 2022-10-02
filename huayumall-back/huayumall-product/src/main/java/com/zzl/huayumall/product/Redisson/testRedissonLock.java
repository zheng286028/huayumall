package com.zzl.huayumall.product.Redisson;

import cn.hutool.core.lang.UUID;
import org.redisson.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述
 *
 * @author 郑子浪
 * @date 2022/08/05  13:58
 */
@Controller
public class testRedissonLock {
    @Resource
    private RedissonClient client;
    @Resource
    private StringRedisTemplate redis;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        /**
         * 1、分布式情况下能不能锁住：能
         * 2、业务异常会不会造成死锁 ：不会，redis的锁有时间
         */
        //创建一把锁
        RLock lock = client.getLock("my-lock");
        //阻塞线程锁，其他线程只能等待锁的释放
        //要是设置锁时间，且业务执行的时间超过了锁的时间，那么锁的功能将会失效(其他业务将会抢到锁)且到时候删锁时把别人的锁删掉，包括看门狗

        /**
         * 1、设置锁的超时时间：占锁成功后，return this.evalWriteAsync(this.getRawName(), LongCodec.INSTANCE, command,
         *              "if (redis.call('exists', KEYS[1]) == 0) then redis.call('hincrby', KEYS[1], ARGV[2], 1);
         *              redis.call('pexpire', KEYS[1], ARGV[1]); return nil; end; if (redis.call('hexists', KEYS[1], ARGV[2]) == 1)
         *              then redis.call('hincrby', KEYS[1], ARGV[2], 1); redis.call('pexpire', KEYS[1], ARGV[1]); return nil;
         *              end; return redis.call('pttl', KEYS[1]);", Collections.singletonList(this.getRawName()),
         *              new Object[]{unit.toMillis(leaseTime), this.getLockName(threadId)});
         *               设置锁时间且占锁后，超时后会向redis发送lug脚本，删除锁
         *
         * 2、未指定锁时间：this.lockWatchdogTimeout = 30000L; watch dog默认时间为30秒
         *   什么时候续期：this.internalLockLeaseTime【看门狗时间】 / 3L, TimeUnit.MILLISECONDS);  每隔三分之一(十秒)续期到30秒
         *
         * 3、不过还是建议自定义时间，毕竟续期操作也是很浪费效率的，而且设置超时时间为30秒，也没有那个业务能执行30秒
         *
         */
        /*lock.lock(10, TimeUnit.SECONDS);*/
        lock.lock();
        System.out.println("加锁成功");
        try {
            System.out.println(Thread.currentThread().getId());
            System.out.println("等待业务的执行");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
        } finally {
            //解锁，redis拥有watch deg机制，要是业务没执行完毕，自动对锁进行续期
            System.out.println("开锁");
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 读写锁，作用：读的数据一定是最新的
     * 1、写锁(排它锁)只能有一把锁
     * 写时只能有一个线程进行写操作
     * 不加时间也默认有watch dog机制
     * 2、读锁(共享锁)
     * 多个线程之间可以同时进行读操作redis只会记录多个读锁，要是在进行写操作，那么读锁要等待写锁完成锁的释放
     * 即使是正在进行读操作，那么写也要进行等待
     */
    @ResponseBody
    @GetMapping("/write")
    public String writeLock() {
        String uuid = UUID.randomUUID().toString();
        RReadWriteLock lock = client.getReadWriteLock("wr-lock");
        RLock rLock = lock.writeLock();
        rLock.lock();
        try {
            Thread.sleep(20000);
            redis.opsForValue().set("write", uuid);
        } catch (InterruptedException e) {
        } finally {
            rLock.unlock();
        }
        return uuid;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readLock() {
        RReadWriteLock lock = client.getReadWriteLock("wr-lock");
        RLock rLock = lock.readLock();
        rLock.lock();
        String write = redis.opsForValue().get("write");
        rLock.unlock();
        return write;
    }

    /**
     * 信号量：默认信号量为0
     *   只有release调用之后，坑位才会增加，semaphore才能执行业务
     *   release(5):表示release每调用一次增加5个坑位
     *   acquire(5)：表示一次使用的坑位，没坑会阻塞线程
     *   tryAcquire()：尝试占坑，如果没有返回false,不会阻塞线程
     *   且该key不会自动删除
     */
    @GetMapping("/semaphore")
    @ResponseBody
    public String semaphoreAcquire() throws InterruptedException {
        //设置信号量
        RSemaphore semaphore = client.getSemaphore("semaphore");
        boolean b = semaphore.tryAcquire();
        return "ok=>"+b;
    }

    @GetMapping("/release")
    @ResponseBody
    public String semaphoreRelease() throws InterruptedException {
        //设置信号量
        RSemaphore semaphore = client.getSemaphore("semaphore");
        semaphore.release();
        return "ok";
    }

    /**
     * countDownLatch：闭锁
     *  countDownLatch.trySetCount(3):表示还要三个学生没走，如果设置了超时，即使学生没走完，一样关门
     *  countDownLatch.await()：学生没走完，只能等待学生走完才能执行业务
     *  countDownLatch.countDown()：执行一次走一位学生
     *  且当学生走完执行了await()才会删除锁
     */
    @GetMapping("/await")
    @ResponseBody
    public String await() throws InterruptedException {
        RCountDownLatch countDownLatch = client.getCountDownLatch("countDownLatch");
        countDownLatch.trySetCount(3);
        countDownLatch.await(10, TimeUnit.SECONDS);
        return "关门了";
    }

    @GetMapping("/countDown")
    @ResponseBody
    public String countDown(){
        RCountDownLatch countDownLatch = client.getCountDownLatch("countDownLatch");
        countDownLatch.countDown();
        return "gogogo";
    }
}
