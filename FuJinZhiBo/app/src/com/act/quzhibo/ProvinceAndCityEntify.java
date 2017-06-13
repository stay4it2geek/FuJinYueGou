package com.act.quzhibo;

import java.util.ArrayList;

/**
 * Created by asus-pc on 2017/6/13.
 */

public class ProvinceAndCityEntify {
    public int proId;// 7,
    public String name;//河北省
    public int ProSort; ///5,
    public String ProRemark;//省份
    public ArrayList<CitySub> citySub;

    public class CitySub {
        public int cityId;
        public String name;
        public int CitySort;
    }
}
