/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agora.agoravoice.im.message;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;


import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;

import java.util.List;

import io.agora.agoravoice.R;

public class EaseCommonUtils {
	private static final String TAG = "CommonUtils";
	/**
	 * check if network avalable
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
			}
		}

		return false;
	}

	/**
	 * check if sdcard exist
	 * 
	 * @return
	 */
	public static boolean isSdcardExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode){
        String replace = expressioName.replace("表情", "em100");

	    EMMessage message = EMMessage.createTxtSendMessage("["+replace+"]", toChatUsername);
        if(identityCode != null){
            message.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
	}

	/**
     * Get digest according message type and content
     * 
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
        case IMAGE:
            digest = getString(context, R.string.picture);
            break;
        case TXT:
            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                if (!TextUtils.isEmpty(txtBody.getMessage())) {
                    digest = txtBody.getMessage();
                } else {
                    digest = getString(context, R.string.dynamic_expression);
                }
            } else {
                digest = txtBody.getMessage();
            }
            break;
        case FILE:
            digest = getString(context, R.string.file);
            break;
        default:
            EMLog.e(TAG, "error, unknow type");
            return "";
        }

        return digest;
    }
    
    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }


    /**
	 * get top activity
	 * @param context
	 * @return
	 */
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null){
            return runningTaskInfos.get(0).topActivity.getClassName();
        } else {
            return "";
        }
	}
	
	/**
     * set initial letter of according user's nickname( username if no nickname)
     * 
     * @param user
     */
//    public static void setUserInitialLetter(EaseUser user) {
//        final String DefaultLetter = "#";
//        String letter = DefaultLetter;
//        if (user == null){
//            return;
//        }
//        final class GetInitialLetter {
//            String getLetter(String name) {
//                if (TextUtils.isEmpty(name)) {
//                    return DefaultLetter;
//                }
//                char char0 = name.toLowerCase().charAt(0);
//                if (Character.isDigit(char0)) {
//                    return DefaultLetter;
//                }
//                ArrayList<Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
//                if (l != null && l.size() > 0 && l.get(0).target.length() > 0)
//                {
//                    Token token = l.get(0);
//                    String letter = token.target.substring(0, 1).toUpperCase();
//                    char c = letter.charAt(0);
//                    if (c < 'A' || c > 'Z') {
//                        return DefaultLetter;
//                    }
//                    return letter;
//                }
//                return DefaultLetter;
//            }
//        }
//
//        if (!TextUtils.isEmpty(user.getRemark()) ) {
//            letter = new GetInitialLetter().getLetter(user.getRemark());
//            user.setInitialLetter(letter);
//            return;
//        }
//
//        if (!TextUtils.isEmpty(user.getNickname()) ) {
//            letter = new GetInitialLetter().getLetter(user.getNickname());
//            user.setInitialLetter(letter);
//            return;
//        }
//        if (letter.equals(DefaultLetter) && !TextUtils.isEmpty(user.getUsername())) {
//            letter = new GetInitialLetter().getLetter(user.getUsername());
//        }
//        user.setInitialLetter(letter);
//    }

//    public static String getPinYin(String hanzi) {
//        ArrayList<MyHanziToPinyin.Token> tokens = MyHanziToPinyin.getInstance().get(hanzi);
//        StringBuilder sb = new StringBuilder();
//        if (tokens != null && tokens.size() > 0) {
//            for (MyHanziToPinyin.Token token : tokens) {
//                if (Token.PINYIN == token.type) {
//                    sb.append(token.target);
//                } else {
//                    sb.append(token.source);
//                }
//            }
//        }
//
//        return sb.toString().toUpperCase();
//    }

    /**
     * 提取汉字的首字母，如果里面含有费中文字符则忽略之；如果全为非中文则返回""。
     *
     * @param caseType 当为1时获取的首字母为小写，否则为大写。
     */
//    public static String getPinYinHeadChar(String zn_str, int caseType) {
//        if (zn_str != null && !zn_str.trim().equalsIgnoreCase("")) {
//            char[] strChar = zn_str.toCharArray();
//            // 汉语拼音格式输出类
//            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
//            // 输出设置，大小写，音标方式等
//            if (1 == caseType) {
//                hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//            } else {
//                hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
//            }
//            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
//            StringBuffer pyStringBuffer = new StringBuffer();
//            char c = strChar[0];
//            char pyc;
//            if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {//是中文或者a-z或者A-Z转换拼音
//                try {
//                    String[] pyStirngArray = PinyinHelper.toHanyuPinyinStringArray(strChar[0], hanYuPinOutputFormat);
//                    if (null != pyStirngArray && pyStirngArray[0] != null) {
//                        pyc = pyStirngArray[0].charAt(0);
//                        pyStringBuffer.append(pyc);
//                    }
//                } catch (BadHanyuPinyinOutputFormatCombination e) {
//                    e.printStackTrace();
//                }
//            }
//            return pyStringBuffer.toString();
//        }
//        return "#";
//    }

    
    /**
     * change the chat type to EMConversationType
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     * @param message
     * return
     *
     * \~english
     * check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     *
     * @param message
     * @return
     */
    public static boolean isSilentMessage(EMMessage message){
        return message.getBooleanAttribute("em_ignore_notification", false);
    }

}
