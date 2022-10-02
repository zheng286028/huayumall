package com.zzl.common;

import java.util.Random;

/**
 * ¹¦ÄÜÃèÊö
 *
 * @author Ö£×ÓÀË
 * @date 2022/08/12  0:51
 */
public class test {
    public int randomTest01(){
        int min = 30;
        int max = 100;
        int result = new Random().nextInt(max-min) + min;
        return result;
    }

    public int randomTest02(){
        int min = 30;
        int max = 100;
        int result = (int) (Math.random() * (max - min) + min);
        return result;
    }

    public static void main(String[] args) {
        for (int i = 3; i <=1000 ; i++) {
            boolean flag = true;
            for (int j = 3; j <i ; j++) {
                if(i%j==0){
                    flag = false;
                    break;
                }
            }
            if(flag){
                System.out.println(i);
            }
        }
    }
}
