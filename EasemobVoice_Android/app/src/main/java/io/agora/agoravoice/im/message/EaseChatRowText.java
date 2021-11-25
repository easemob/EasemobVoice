package io.agora.agoravoice.im.message;

import android.content.Context;
import android.text.Spannable;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;


import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;

import io.agora.agoravoice.AgoraApplication;
import io.agora.agoravoice.R;
import io.agora.agoravoice.manager.LeanCloudManager;
import io.agora.agoravoice.bean.UserBean;
import io.agora.agoravoice.ui.views.EaseSmileUtils;

public class EaseChatRowText extends EaseChatRow{

	private TextView contentView;

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				R.layout.ease_row_received_message : R.layout.ease_row_received_message, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        if (message.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
            usernickView.setText(AgoraApplication.getInstance().config().getNickname() + ":");
        } else {
            LeanCloudManager.getInstance().getUserInfo(message.getFrom(), new EMValueCallBack<UserBean>() {
                @Override
                public void onSuccess(UserBean userBean) {
                    usernickView.setText(userBean.uName + ":");
                }

                @Override
                public void onError(int i, String s) {
                    EMLog.e("EaseChatRowText","onError  code :"+i+"  msg :"+s);
                }
            });

        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
//            String attribute = message.getStringAttribute(IDataConnector.VoipMsgAttrConstants.KEY_TYPE, "");
//            if (!CMCommonUtil.isNullOrNil(attribute) && IDataConnector.VoipMsgAttrConstants.VALUE_TYPE.equals(attribute)) {
//                avCallReceive(contentView);
//            } else {
                contentView.setText(span, BufferType.SPANNABLE);
//            }

//            ImageView imageView = findViewById(R.id.ding_msg_tips);
//            if (EaseDingMessageHelper.get().isDingMessage(message)) {
//                imageView.setVisibility(VISIBLE);
//                imageView.setBackgroundResource(R.drawable.em_chat_receipt_normal);
//            } else {
//                imageView.setVisibility(GONE);
//            }
        } else if (message.direct() == EMMessage.Direct.SEND) {
//            String attribute = message.getStringAttribute(IDataConnector.VoipMsgAttrConstants.KEY_TYPE, "");
//            if (!CMCommonUtil.isNullOrNil(attribute) && IDataConnector.VoipMsgAttrConstants.VALUE_TYPE.equals(attribute)) {
//                avCallSend(contentView);
//            } else {
                contentView.setText(span, BufferType.SPANNABLE);
//            }
        }

    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
            default:
                break;
        }
    }

    public void onAckUserUpdate(final int count) {
//        if (ackedView != null) {
//            ackedView.post(new Runnable() {
//                @Override
//                public void run() {
//                    ackedView.setVisibility(INVISIBLE);
////                    ackedView.setVisibility(VISIBLE);
////                    ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
//                }
//            });
//        }
    }

    private void onMessageCreate() {
//        progressBar.setVisibility(View.VISIBLE);
//        statusView.setVisibility(View.GONE);
    }

    private void onMessageSuccess() {
//        progressBar.setVisibility(View.GONE);
//        statusView.setVisibility(View.GONE);

        // Show "1 Read" if this msg is a ding-type msg.
//        if (EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
//            ackedView.setVisibility(VISIBLE);
//            List<String> userList = EaseDingMessageHelper.get().getAckUsers(message);
//            int count = userList == null ? 0 : userList.size();
//            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
//        }
//
//        // Set ack-user list change listener.
//        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    private void onMessageError() {
//        progressBar.setVisibility(View.GONE);
//        statusView.setVisibility(View.VISIBLE);
    }

    private void onMessageInProgress() {
//        progressBar.setVisibility(View.VISIBLE);
//        statusView.setVisibility(View.GONE);
    }

//    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
//            new EaseDingMessageHelper.IAckUserUpdateListener() {
//                @Override
//                public void onUpdate(List<String> list) {
//                    onAckUserUpdate(list.size());
//                }
//            };


//    private void avCallReceive(TextView contentView){
//        int callType = message.getIntAttribute(IDataConnector.VoipMsgAttrConstants.KEY_CALL_TYPE, -1);
//        if (IDataConnector.VoipCallType.VIDEO.intValue() == callType) {
//            Drawable drawableLeft = ContextCompat.getDrawable(context, R.drawable.chat_call_video_right_read);
//            drawableLeft.setBounds(0, 0, 56, 56);
//            contentView.setCompoundDrawables(drawableLeft, null, null, null);
//            contentView.setCompoundDrawablePadding(10);
//            String showContent = getShowContent(message);
//            contentView.setText(showContent);
//        } else if(IDataConnector.VoipCallType.VOICE.intValue() == callType){
//            Drawable drawableLeft = ContextCompat.getDrawable(context, R.drawable.chat_call_phone_read);
//            drawableLeft.setBounds(0, 0, 56, 56);
//            contentView.setCompoundDrawables(drawableLeft, null, null, null);
//            contentView.setCompoundDrawablePadding(10);
//            String showContent = getShowContent(message);
//            contentView.setText(showContent);
//        }
//
//    }

//    private void avCallSend(TextView contentView){
//        int callType = message.getIntAttribute(IDataConnector.VoipMsgAttrConstants.KEY_CALL_TYPE, -1);
//        if (IDataConnector.VoipCallType.VIDEO.intValue() == callType) {
//            Drawable drawableRight = ContextCompat.getDrawable(context, R.drawable.chat_call_video_left_read);
//            drawableRight.setBounds(0, 0, 56, 56);
//            contentView.setCompoundDrawables(null, null, drawableRight, null);
//            contentView.setCompoundDrawablePadding(10);
//            String showContent = getShowContent(message);
//            contentView.setText(showContent);
//        } else if(IDataConnector.VoipCallType.VOICE.intValue() == callType){
//            Drawable drawableRight = ContextCompat.getDrawable(context, R.drawable.chat_call_phone_read);
//            drawableRight.setBounds(0, 0, 56, 56);
//            contentView.setCompoundDrawables(null, null, drawableRight, null);
//            contentView.setCompoundDrawablePadding(10);
//            String showContent = getShowContent(message);
//            contentView.setText(showContent);
//        }
//
//    }


//    private String getShowContent(EMMessage emMessage){
//        String result = "";
//        int callStatus = emMessage.getIntAttribute(IDataConnector.VoipMsgAttrConstants.KEY_STATUS, -1);
//        if(callStatus == IDataConnector.VoipCallStatus.CALL_CANCEL.intValue()){
//            result = (message.direct() == EMMessage.Direct.RECEIVE ? "对方已取消" : "已取消");
//        } else if (callStatus == IDataConnector.VoipCallStatus.NO_ANSWER.intValue()){
//            result = "未接通";
//        } else if (callStatus == IDataConnector.VoipCallStatus.CALL_REFUSED.intValue()){
//            result = (message.direct() == EMMessage.Direct.RECEIVE ? "已拒绝" : "对方已拒绝");
//        } else if (callStatus == IDataConnector.VoipCallStatus.CALL_FINISH.intValue()){
//            long length = emMessage.getLongAttribute(IDataConnector.VoipMsgAttrConstants.KEY_CALL_LENGTH, -1);
//            result = "通话时长 "+ CmosDateUtils.getTimeFormat(length);
//        }
//        return result;
//    }

}
