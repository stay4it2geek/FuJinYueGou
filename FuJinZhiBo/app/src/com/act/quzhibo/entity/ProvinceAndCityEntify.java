package com.act.quzhibo.entity;

import java.util.ArrayList;

public class ProvinceAndCityEntify {
    public int proId;// 7,
    public String name;//
    public int ProSort; ///5,
    public String ProRemark;//
    public ArrayList<CitySub> citySub;

    public class CitySub {
        public int cityId;
        public String name;
        public int CitySort;
    }
}
