package io.agora.agoravoice.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import java.util.List;

import io.agora.agoravoice.R;
import io.agora.agoravoice.manager.LeanCloudManager;
import io.agora.agoravoice.bean.RoomBean;
import io.agora.agoravoice.business.definition.struct.BusinessType;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.ServerClient;
import io.agora.agoravoice.business.server.retrofit.JsonCallback;
import io.agora.agoravoice.business.server.retrofit.model.responses.RoomListResp;
import io.agora.agoravoice.im.IMUtils;
import io.agora.agoravoice.im.body.IMCreateChatRoomBody;
import io.agora.agoravoice.im.net.CmosRequest;
import io.agora.agoravoice.im.net.CmosRequestOkImpl;
import io.agora.agoravoice.im.resp.CreateChatRoomResp;
import io.agora.agoravoice.manager.ProxyManager;
import io.agora.agoravoice.ui.views.CropBackgroundRelativeLayout;
import io.agora.agoravoice.ui.views.actionsheets.BackgroundActionSheet;
import io.agora.agoravoice.utils.Const;
import io.agora.agoravoice.utils.RandomUtil;
import io.agora.agoravoice.utils.RoomBgUtil;
import io.agora.agoravoice.utils.ToastUtil;
import okhttp3.internal.http2.Header;

public class PrepareActivity extends AbsLiveActivity{
    private static final int POLICY_MAX_DURATION = 10000;
    private static final int MAX_NAME_LENGTH = 25;

    private CropBackgroundRelativeLayout mBackgroundLayout;
    private int mBackgroundSelected;
    private AppCompatEditText mNameEdit;
    private AppCompatTextView mGoLiveBtn;

    private Handler mHandler;
    private final Runnable mRemovePolicyRunnable = this::closePolicyNotification;

    private final ProxyManager.RoomServiceListener mRoomListener = new ProxyManager.RoomServiceListener() {
        @Override
        public void onRoomCreated(String roomId, String roomName) {

            //TODO 创建IM聊天室
//            String currentUser = EMClient.getInstance().getCurrentUser();
//            List<String> members = new ArrayList<>();
//            members.add(currentUser);
//            proxy().createChatRoom(roomName,roomName,"",5000,members);
             createChatRoom(roomName, this, new JsonCallback<CreateChatRoomResp>(CreateChatRoomResp.class) {
                 @Override
                 public void onError(int status, String errorMsg) {
                     EMLog.e("createChatRoom","IM createChatRoom  onError   errorMsg: "+errorMsg);
                 }

                 @Override
                 public void onSuccess(int statusCode, Header[] headers, CreateChatRoomResp response) {

                     EMLog.i("createChatRoom","IM    onCreateChatRoomSuccess  roomID "+ response.getData().getId());
//                     roomId = response.getData().getId();
//                     Intent intent = new Intent(PrepareActivity.this, ChatRoomActivity.class);
                     Intent intent = new Intent(PrepareActivity.this, ImChatRoomActivity.class);
                     intent.putExtra(Const.KEY_BACKGROUND, String.valueOf(mBackgroundSelected));
                     intent.putExtra(Const.KEY_ROOM_NAME, roomName);
                     intent.putExtra(Const.KEY_ROOM_ID, roomId);
                     intent.putExtra(Const.KEY_IM_ROOM_ID, response.getData().getId());
                     intent.putExtra(Const.KEY_USER_NAME, config().getNickname());
                     intent.putExtra(Const.KEY_USER_ROLE, Const.Role.owner.ordinal());
                     intent.putExtra(Const.KEY_USER_ID, config().getUserId());
                     intent.putExtras(getIntent());
                     EMLog.e("wyz","roomId ："+roomId+" ：： "+response.getData().getId());
                     LeanCloudManager.getInstance().saveRoomInfo(new RoomBean(roomId,response.getData().getId(),roomName));
                     startActivity(intent);
                     finish();
                 }
             });

        }

        @Override
        public void onGetRoomList(String nextId, int total, List<RoomListResp.RoomListItem> list) {
            // nothing needs to be done here
        }

        @Override
        public void onLeaveRoom() {
            // nothing needs to be done here
        }

        @Override
        public void onRoomServiceFailed(int type, int code, String msg) {
            if (code == ServerClient.ERROR_CONNECTION) {
                runOnUiThread(() -> ToastUtil.showShortToast(application(), R.string.error_no_connection));
                return;
            }

            if (type == BusinessType.CREATE_ROOM) {
                Logging.e("create room fail:" + code + " " + msg);
                runOnUiThread(() -> {
                    mGoLiveBtn.setEnabled(true);
                    String format = getApplicationContext().getString(R.string.create_room_fail);
                    String message = String.format(format, code, msg);
                    ToastUtil.showShortToast(getApplicationContext(), message);
                });
            }
        }
    };

    public void createChatRoom(String roomName,Object tag , JsonCallback<CreateChatRoomResp> callback) {
        String currentUser = EMClient.getInstance().getCurrentUser();
        String[] members = {currentUser};
        EMLog.e("wyz","members : "+members);
        CmosRequest request = new CmosRequestOkImpl();
        String chatRoomUrl = IMUtils.createChatRoomUrl();
        EMLog.e("wyz", "chatRoomUrl:  = "+chatRoomUrl );
        IMCreateChatRoomBody createChatRoomBody = new IMCreateChatRoomBody(roomName,roomName,500,currentUser, members);
        EMLog.e("wyz","chatRoomUrl:  createChatRoomBody.toString() = "+createChatRoomBody.toString());
        request.requestAsyncWithAdminToken(chatRoomUrl , createChatRoomBody.toString(), tag, null, 1, callback);
    }



    @Override
    protected void onHeadsetWithMicPlugged(boolean plugged) {
        // nothing needs to be done here
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);

        init();
        postPolicyCloseDelayed();
    }

    private void init() {
        mBackgroundLayout = findViewById(R.id.activity_prepare_background);
        setBackgroundPicture();

        mNameEdit = findViewById(R.id.room_name_edit);
        setRandomRoomName();
        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_NAME_LENGTH) {
                    String format = getString(R.string.room_name_too_long_toast_format);
                    String message = String.format(format, MAX_NAME_LENGTH);
                    ToastUtil.showShortToast(PrepareActivity.this, message);
                    mNameEdit.setText(s.subSequence(0, MAX_NAME_LENGTH));
                    mNameEdit.setSelection(MAX_NAME_LENGTH);
                }
            }
        });

        mGoLiveBtn = findViewById(R.id.prepare_go_live);
    }

    private void setBackgroundPicture() {
        if (mBackgroundLayout != null) {
            mBackgroundLayout.setCropBackground(RoomBgUtil.getRoomBgPicRes(mBackgroundSelected));
        }
    }

    private void setRandomRoomName() {
        mNameEdit.setText(RandomUtil.randomLiveRoomName(this));
    }

    private void postPolicyCloseDelayed() {
        mHandler = new Handler(getMainLooper());
        mHandler.postDelayed(mRemovePolicyRunnable, POLICY_MAX_DURATION);
    }

    private void removePolicyCloseRunnable() {
        if (mHandler != null) mHandler.removeCallbacks(mRemovePolicyRunnable);
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout layout = findViewById(R.id.prepare_top_btn_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin += systemBarHeight;
        layout.setLayoutParams(params);
    }

    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.prepare_close:
                onBackPressed();
                break;
            case R.id.prepare_choose_background:
                showBackgroundActionSheet();
                break;
            case R.id.prepare_random:
                setRandomRoomName();
                break;
            case R.id.prepare_go_live:
                checkRoomNameAndGoLive();
                break;
            case R.id.prepare_policy_close:
                closePolicyNotification();
                break;
        }
    }

    private void showBackgroundActionSheet() {
        BackgroundActionSheet bgActionSheet = new BackgroundActionSheet(this);
        bgActionSheet.setShowBackButton(false);
        showActionSheet(bgActionSheet, true);
        bgActionSheet.setOnBackgroundActionListener(new BackgroundActionSheet.BackgroundActionSheetListener() {
            @Override
            public void onBackgroundPicSelected(int index, int res) {
                mBackgroundSelected = index;
                setBackgroundPicture();
            }

            @Override
            public void onBackgroundBackClicked() {
                // Do not need to respond to this event for this activity
            }
        });
    }

    private void checkRoomNameAndGoLive() {
        if (!isRoomNameValid()) {
            ToastUtil.showShortToast(this, R.string.no_room_name_toast);
            return;
        }

        mGoLiveBtn.setEnabled(false);
        proxy().addRoomServiceListener(mRoomListener);
        proxy().createRoom(config().getUserToken(),
                mNameEdit.getText().toString(),
                RoomBgUtil.indexToString(mBackgroundSelected),
                Const.ROOM_DURATION,
                Const.ROOM_MAX_AUDIENCE);
    }

    private boolean isRoomNameValid() {
        return mNameEdit.getText() != null && !TextUtils.isEmpty(mNameEdit.getText());
    }

    private void closePolicyNotification() {
        RelativeLayout layout = findViewById(R.id.live_prepare_policy_caution_layout);
        if (layout != null && layout.getParent() != null) {
            ((ViewGroup)layout.getParent()).removeView(layout);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removePolicyCloseRunnable();
        proxy().removeRoomServiceListener(mRoomListener);
    }
}
