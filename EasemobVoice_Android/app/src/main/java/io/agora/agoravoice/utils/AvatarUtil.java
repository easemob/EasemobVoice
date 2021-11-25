package io.agora.agoravoice.utils;

import android.os.Environment;

import java.io.File;

import io.agora.agoravoice.R;

public class AvatarUtil {
    private static final int[] AVATAR_RES = {
            R.drawable.portrait01,
            R.drawable.portrait02,
            R.drawable.portrait03,
            R.drawable.portrait04,
            R.drawable.portrait05,
            R.drawable.portrait06,
            R.drawable.portrait07,
            R.drawable.portrait08,
            R.drawable.portrait09,
            R.drawable.portrait10,
            R.drawable.portrait11,
            R.drawable.portrait12,
            R.drawable.portrait13,
            R.drawable.portrait14,
            R.drawable.portrait15,
            R.drawable.portrait16,
            R.drawable.portrait17,
            R.drawable.portrait18,
            R.drawable.portrait19,
            R.drawable.portrait20,
            R.drawable.portrait21,
            R.drawable.portrait22,
            R.drawable.portrait23,
            R.drawable.portrait24,
            R.drawable.portrait25,
            R.drawable.portrait26,
            R.drawable.portrait27,
            R.drawable.portrait28,
            R.drawable.portrait29,
            R.drawable.portrait30,
            R.drawable.portrait31,
    };

    public static int getAvatarResByIndex(int index) {
        int idx = index;
        if (index < 0 || index >= AVATAR_RES.length) {
            idx = 0;
        }

        return AVATAR_RES[idx];
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    /**
     * Return the path of /storage/emulated/0/Pictures.
     *
     * @return the path of /storage/emulated/0/Pictures
     */
    public static String getExternalPicturesPath() {
        if (!isSdcardExist()) {
            return "";
        }
        return getAbsolutePath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }


    private static String getAbsolutePath(final File file) {
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }

    public static int getAvatarCount() {
        return AVATAR_RES.length;
    }
}
