/*
 *  * EaseMob CONFIDENTIAL
 * __________________
 * Copyright (C) 2017 EaseMob Technologies. All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of EaseMob Technologies.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from EaseMob Technologies.
 */
package io.agora.agoravoice.utils;

import android.content.Context;

public class DensityUtil {
    /** 
     * dp to px
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * px to dip
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    /**
     * sp to px
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue){
    	 final float scale = context.getResources().getDisplayMetrics().scaledDensity;  
    	 return (int) (spValue * scale + 0.5f);  
    }
    
    /**
     * px to sp
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    

}
