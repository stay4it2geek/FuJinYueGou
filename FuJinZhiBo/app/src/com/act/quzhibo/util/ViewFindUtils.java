package com.act.quzhibo.util;

import android.util.SparseArray;
import android.view.View;

@SuppressWarnings({ "unchecked" })
public class ViewFindUtils
{
	/**
	 * ViewHolder简洁写法,避免适配器中重复定义ViewHolder,减少代码量
	 */
	public static <T extends View> T hold(View view, int id)
	{
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();

		if (viewHolder == null)
		{
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}

		View childView = viewHolder.get(id);

		if (childView == null)
		{
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}

		return (T) childView;
	}

	/**
	 * 替代findviewById方法
	 */
	public static <T extends View> T find(View view, int id)
	{
		return (T) view.findViewById(id);
	}
}
