package io.agora.agoravoice.ui.views;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.agora.agoravoice.R;

public class PopWinBottom extends PopupWindow {
    private View mPopView;
    private OnItemClickListener mListener;
    private EaseChatInputMenu easeChatInputMenu;
    private String title, btnStr;

    public PopWinBottom(Context context, String txtTitle, String txtSave, OnItemClickListener mListener) {
        super(context);
        this.title = txtTitle;
        this.btnStr = txtSave;
        this.mListener = mListener;
        init(context);
        setPopupWindow();

    }

    public PopWinBottom(Context context,OnItemClickListener mListener) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mListener = mListener;
        init(context);
        setPopupWindow();
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = LayoutInflater.from(context);
        //绑定布局
        mPopView = inflater.inflate(R.layout.cm_weight_pop_window_bottom, null);
        easeChatInputMenu = mPopView.findViewById(R.id.message_edit_text);

        easeChatInputMenu.init();
//        easeChatInputMenu.showSoftKeyboard(easeChatInputMenu.getPrimaryMenu().getEditText());
        easeChatInputMenu.setChatInputMenuListener(new ChatInputMenuListener() {
            @Override
            public void onTyping(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onSendMessage(String content) {
                if (mListener != null) {
                    mListener.onSendMsg(content);
                }
                dismiss();
            }

            @Override
            public void onExpressionClicked(Object emojicon) {

            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return false;
            }

            @Override
            public void onChatExtendMenuItemClick(int itemId, View view) {
                if (mListener != null) {
                    mListener.onSelectPic();
                    dismiss();
                }

            }
        });
    }

    /**
     * 设置窗口的相关属性
     */
    @SuppressLint({"InlinedApi", "WrongConstant"})
    private void setPopupWindow() {
        this.setContentView(mPopView);// 设置View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setTouchable(true);

        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        EditText editText = easeChatInputMenu.getPrimaryMenu().getEditText();
        editText.setFocusable(true);
        editText.requestFocus();
        editText.setSelection(0);

        this.setAnimationStyle(R.style.cmui_AnimBottom);// 设置动画
        this.setBackgroundDrawable(new ColorDrawable(000000000));// 设置背景灰色

        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPopView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        easeChatInputMenu.hideSoftKeyboard();
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    /**
     * 定义一个接口，公布出去 在Activity中操作按钮的单击事件
     */
    public interface OnItemClickListener {
        void onSendMsg(String msg);
        void onSelectPic();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void show(View view) {
        showAtLocation(view,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
}
