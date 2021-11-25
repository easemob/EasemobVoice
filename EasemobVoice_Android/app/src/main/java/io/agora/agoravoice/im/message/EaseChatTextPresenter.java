package io.agora.agoravoice.im.message;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;


import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatTextPresenter extends EaseChatRowPresenter {
    private static final String TAG = "EaseChatTextPresenter";

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowText(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);

//        if (!EaseDingMessageHelper.get().isDingMessage(message)) {
//            return;
//        }
//
//        // If this msg is a ding-type msg, click to show a list who has already read this message.
//        if (message.direct() == EMMessage.Direct.SEND) {
//            List<String> list = EaseDingMessageHelper.get().getAckUsers(message);
//            if (list != null && list.size() > 0) {
//                Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
//                i.putExtra("msg", message);
//                getContext().startActivity(i);
//            } else {
//              CustomToast.getInstance(getContext()).show("群成员未读",1000);
//            }
//        }
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
//        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
//            try {
//                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
//            } catch (HyphenateException e) {
//                e.printStackTrace();
//            }
//            return;
//        }
//
//        // Send the group-ack cmd type msg if this msg is a ding-type msg.
//        EaseDingMessageHelper.get().sendAckMessage(message);
    }
}
