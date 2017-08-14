package com.act.quzhibo.entity;

import com.bigkoo.pickerview.model.IPickerViewData;

/**
 * Created by KyuYi on 2017/3/2.
 * E-Mail:kyu_yi@sina.com
 * 功能：
 */

public class CardBean implements IPickerViewData {
    String ageStr;

    public CardBean(String ageStr) {
        this.ageStr = ageStr;
    }


    @Override
    public String getPickerViewText() {
        return ageStr;
    }

}

