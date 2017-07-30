/**
 * StackBlur v1.0 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * Author of the original algorithm: Mario Klingemann <mario.quasimondo.com>
 *
 * This is a compromise between Gaussian Blur and Box blur
 * It creates much better looking blurs than Box Blur, but is
 * 7x faster than my Gaussian Blur implementation.
 *
 * I called it Stack Blur because this describes best how this
 * filter works internally: it creates a kind of moving stack
 * of colors whilst scanning through the image. Thereby it
 * just has to add one new block of color to the right side
 * of the stack and remove the leftmost color. The remaining
 * colors on the topmost layer of the stack are either added on
 * or reduced by one, depending on if they are on the right or
 * on the left side of the stack.
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package com.act.quzhibo.stackblur;

import android.content.Context;
import android.graphics.Bitmap;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackBlurManager {
	static Context _context;

	static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
	static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);


	/**
	 * 原始图像
	 */
	private final Bitmap _image;

	/**
	 * 最近的结果的模糊
	 */
	private Bitmap _result;

	/**
	 * 模糊方式
	 */
	private BlurProcess _blurProcess;

	/**
	 * 构造函数方法（基本初始化和像素数组的构造）
	 * @param context 上下文
	 * @param image 图像将被分析
	 */
	public StackBlurManager(Context context, Bitmap image) {
		_context = context.getApplicationContext();
		_image = image;
	}

	/**
	 * 处理给定半径上的图像。半径必须至少1
	 * @param radius
	 */
	public Bitmap process(int radius) {
		_blurProcess = new JavaBlurProcess();
		_result = _blurProcess.blur(_image, radius);
		return _result;
	}

	/**
	 * 返回作为位图的模糊图像
	 * @return 模糊图像
	 */
	public Bitmap returnBlurredImage() {
		return _result;
	}

	/**
	 * 将图像保存到文件系统中
	 * @param path 保存图像的路径
	 */
	public void saveIntoFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(path);
			_result.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回原始图像作为位图
	 * @return 原始位图图像
	 */
	public Bitmap getImage() {
		return this._image;
	}


	/**
	 *
	 * @param radius 最大模糊度(在0.0到25.0之间)
     * @return
     */
	public Bitmap processRenderScript(float radius) {
		_blurProcess = new RenderScriptBlur(_context);
		_result = _blurProcess.blur(_image,radius);
		return _result;
	}

	public void onDestory(){
		if (_blurProcess != null)
			_blurProcess.onDestory();
	}
}
