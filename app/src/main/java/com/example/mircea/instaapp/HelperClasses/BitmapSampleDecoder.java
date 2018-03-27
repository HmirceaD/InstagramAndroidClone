package com.example.mircea.instaapp.HelperClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapSampleDecoder {

    private int height;
    private int width;
    private byte[] byteArray;
    private final BitmapFactory.Options options;

    public BitmapSampleDecoder(byte[] b, int h, int w){

        /*Get the desired height and width of the imageview*/
        this.byteArray = b;
        this.height = h;
        this.width = w;

        options = new BitmapFactory.Options();
    }

    public Bitmap decodeBitmap(){

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        options.inSampleSize = calculateSampeOfByteArray(options, height, width);

        options.inSampleSize = 16;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
    }

    private int calculateSampeOfByteArray(BitmapFactory.Options options, int height, int width) {

        //get the amount of times the image can be sampled
        final int bitHeight = options.outHeight;
        final int bitWidth = options.outWidth;
        int sampleSize = 1;

        if(bitHeight > height || bitWidth > width){

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / sampleSize) > height && (halfWidth / sampleSize) > width) {
                sampleSize *= 2;
            }
        }

        return sampleSize;
    }
}
