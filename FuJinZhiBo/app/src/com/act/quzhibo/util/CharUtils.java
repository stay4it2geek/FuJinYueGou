package com.act.quzhibo.util;

import java.util.Random;

public class CharUtils {
    public static String getRandomString(int length){
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(3);
            long result=0;
            switch(number){
                case 0:
                    result= Math.round(Math.random()*25+65);
                    sb.append(String.valueOf((char)result));
                    break;
                case 1:
                    result= Math.round(Math.random()*25+97);
                    sb.append(String.valueOf((char)result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }

        }
        return sb.toString();
    }

    /**
     * time util
     * <br/>
     * 时间工具类
     *
     * @author wlf(Andy)
     * @email 411086563@qq.com
     */
    public static class TimeUtil {

        /**
         * format seconds to HH:mm:ss String
         *
         * @param seconds seconds
         * @return String of formatted in HH:mm:ss
         */
        public static String seconds2HH_mm_ss(long seconds) {

            long h = 0;
            long m = 0;
            long s = 0;
            long temp = seconds % 3600;

            if (seconds > 3600) {
                h = seconds / 3600;
                if (temp != 0) {
                    if (temp > 60) {
                        m = temp / 60;
                        if (temp % 60 != 0) {
                            s = temp % 60;
                        }
                    } else {
                        s = temp;
                    }
                }
            } else {
                m = seconds / 60;
                if (seconds % 60 != 0) {
                    s = seconds % 60;
                }
            }

            String dh = h < 10 ? "0" + h : h + "";
            String dm = m < 10 ? "0" + m : m + "";
            String ds = s < 10 ? "0" + s : s + "";

            return dh + ":" + dm + ":" + ds;
        }
    }
}
