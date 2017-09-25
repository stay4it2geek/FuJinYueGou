package com.act.quzhibo.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    public static Bitmap rotateBitmap(Bitmap img, int rotation) {
        try {
            if (rotation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
                img.recycle();
                return rotatedImg;
            }
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    public static LruCache<String, Bitmap> createBitmapCache() {
        // 获取应用程序最大可用内存
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(cacheSize) {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= 12) {
                    return bitmap.getByteCount();
                } else {
                    return 0;
                }
            }
        };
        return cache;
    }

    public static Bitmap getThumbnail(Context context, Uri uri, int THUMBNAIL_SIZE) {
        try {
            InputStream input = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                return null;
            int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
            double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
            return bitmap;
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取图片库中某张图片正方形略缩的终极方法
     *
     * @param context
     * @param imgId   图片ID，不可以为NULL
     * @param ori     图片原来的旋转角度，默认是0
     * @return
     */
    public static Bitmap getThumbnailFinal(Context context, String imgId, int ori) {
        try {
            if (TextUtils.isEmpty(imgId)) {
                return null;
            }

            // 优先使用数据库中的略缩小图
            Bitmap thumbnailBm = MediaStore.Images.Thumbnails.getThumbnail(
                    context.getContentResolver(),
                    Long.valueOf(imgId),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
            );

            // 如果读不到，读大图，然后裁剪
            if( thumbnailBm==null){
                Log.w("","db thumbnailBm==null ");
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imgId);
                thumbnailBm = BitmapUtil.getThumbnail(context, uri, 180);
            }

            // 如果旋转，就转回来
            if (thumbnailBm != null && thumbnailBm.isRecycled() == false) {
                if (ori != 0) {
                    Log.w("","try ori="+ori);
                    Bitmap rotateBm = BitmapUtil.rotateBitmap(thumbnailBm, ori);
                    if (rotateBm != null && rotateBm.isRecycled() == false) {
                        return rotateBm;
                    }
                }
                return thumbnailBm;
            }

        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }/*catch(FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }*/ catch (Exception e) {
            e.printStackTrace();
        }
        /*
        if (DEBUG) {
            Log.w("", "fetch thumbnail failed!!");
        }
        */
        return null;
    }
}