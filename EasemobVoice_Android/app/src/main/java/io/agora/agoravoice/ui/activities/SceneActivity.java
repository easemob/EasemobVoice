package io.agora.agoravoice.ui.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

import io.agora.agoravoice.R;
import io.agora.agoravoice.business.server.retrofit.JsonCallback;
import io.agora.agoravoice.im.IMUtils;
import io.agora.agoravoice.im.net.CmosRequest;
import io.agora.agoravoice.im.net.CmosRequestOkImpl;
import io.agora.agoravoice.im.resp.ChatRoomInfoResp;
import io.agora.agoravoice.im.resp.CreateChatRoomResp;
import io.agora.agoravoice.manager.LeanCloudManager;
import io.agora.agoravoice.bean.RoomBean;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.retrofit.model.responses.RoomListResp;
import io.agora.agoravoice.manager.ProxyManager;
import io.agora.agoravoice.ui.views.SquareRelativeLayout;
import io.agora.agoravoice.utils.Const;
import io.agora.agoravoice.utils.UserUtil;
import io.agora.agoravoice.utils.WindowUtil;
import okhttp3.internal.http2.Header;

/**
 * Show the room list for a certain scene
 */
public class SceneActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final int SPAN_COUNT = 2;
    private static final int REFRESH_DELAY = 1000 * 60;

    // By default, the client asks for 10 more rooms to show in the list
    private static final int REQ_ROOM_COUNT = 10;

    private Handler mHandler;
    private PageRefreshRunnable mRefreshRunnable;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private RoomListAdapter mAdapter;
    private int mListItemCorner;
    private RelativeLayout mExceptionLayout;

    private boolean mRoomEntered;

    private final ProxyManager.RoomServiceListener mRoomListener = new ProxyManager.RoomServiceListener() {
        @Override
        public void onRoomCreated(String roomId, String roomName) {
            // No need to respond to this callback
        }

        @Override
        public void onGetRoomList(String nextId, int total, List<RoomListResp.RoomListItem> list) {
            runOnUiThread(() -> {
                if (total <= 0) {
                    showNoRoomInfo();
                } else {
                    showList(list, nextId);
                }
                finishRequest();
            });
        }

        @Override
        public void onLeaveRoom() {
            // No need to respond to this callback
        }

        @Override
        public void onRoomServiceFailed(int type, int code, String msg) {
            runOnUiThread(() -> {
                finishRequest();
                showNoDataInfo();
            });
        }
    };

    private void showList(List<RoomListResp.RoomListItem> list, String startId) {
        mExceptionLayout.removeAllViews();
        mExceptionLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.append(list, TextUtils.isEmpty(startId));
    }

    private void showNoRoomInfo() {
        mRecyclerView.setVisibility(View.GONE);
        mExceptionLayout.setVisibility(View.VISIBLE);
        mExceptionLayout.removeAllViews();
        LayoutInflater.from(this).inflate(
            R.layout.layout_empty_list,
            mExceptionLayout);
    }

    private void showNoDataInfo() {
        mRecyclerView.setVisibility(View.GONE);
        mExceptionLayout.setVisibility(View.VISIBLE);
        mExceptionLayout.removeAllViews();
        LayoutInflater.from(this).inflate(
                R.layout.layout_server_error,
                mExceptionLayout);
    }

    private void showNoConnectionInfo() {
        mRecyclerView.setVisibility(View.GONE);
        mExceptionLayout.setVisibility(View.VISIBLE);
        mExceptionLayout.removeAllViews();
        LayoutInflater.from(this).inflate(
                R.layout.layout_no_connection,
                mExceptionLayout);
    }

    private void finishRequest() {
        proxy().removeRoomServiceListener(mRoomListener);
        stopSwipeRefresh();
    }

    private final ProxyManager.NetworkStateChangedListener mNetworkListener = new
        ProxyManager.NetworkStateChangedListener() {
            @Override
            public void onNetworkDisconnected() {
                runOnUiThread(() -> showNoConnectionInfo());
            }

            @Override
            public void onNetworkAvailable(int type) {
                refreshPage();
            }
        };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);
        WindowUtil.hideStatusBar(getWindow(), false);
        proxy().addNetworkStateListener(mNetworkListener);
        initView();
        initRefresh();
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.activity_scene_top_layout);
        if (topLayout != null) {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
            params.topMargin += systemBarHeight;
            topLayout.setLayoutParams(params);
        }
    }

    private void initView() {
        AppCompatTextView titleView = findViewById(R.id.scene_activity_title);
        titleView.setText(getIntent().getStringExtra(Const.KEY_SCENE_TYPE_NAME));

        mSwipeLayout = findViewById(R.id.scene_activity_swipe);
        mSwipeLayout.setNestedScrollingEnabled(false);
        mSwipeLayout.setOnRefreshListener(this);

        mRecyclerView = findViewById(R.id.scene_activity_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

        mAdapter = new RoomListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RoomListItemDecoration());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    stopRefreshTimer();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mSwipeLayout.isRefreshing()) {
                        // The swipe layout is refreshing when
                        // we want to refresh the whole page.
                        // In this case, we'll let the refreshing
                        // listener to handle all the work.
                        return;
                    }

                    startRefreshTimer();
                    int lastItemPosition = recyclerView.getChildAdapterPosition(
                            recyclerView.getChildAt(recyclerView.getChildCount() - 1));
                    if (lastItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                        String nextId = mAdapter.getLast() == null
                                ? null : mAdapter.getLast().roomId;
                        getRoomList(nextId);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        findViewById(R.id.scene_create_room_btn).setOnClickListener(this);
        findViewById(R.id.scene_activity_back).setOnClickListener(this);

        mListItemCorner = getResources().getDimensionPixelOffset(R.dimen.corner_4);

        mExceptionLayout = findViewById(R.id.scene_activity_exception_layout);
    }

    private void initRefresh() {
        mHandler = new Handler();
        mRefreshRunnable = new PageRefreshRunnable();
    }

    private boolean isPageRefreshing() {
        return mSwipeLayout != null && mSwipeLayout.isRefreshing();
    }

    private void stopSwipeRefresh() {
        if (isPageRefreshing()) mSwipeLayout.setRefreshing(false);
    }

    private void stopRefreshTimer() {
        mHandler.removeCallbacks(mRefreshRunnable);
    }

    private void startRefreshTimer() {
        mHandler.postDelayed(mRefreshRunnable, REFRESH_DELAY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_create_room_btn:
                createNewRoom();
                break;
            case R.id.scene_activity_back:
                onBackPressed();
                break;
        }
    }

    private void createNewRoom() {
        Intent intent = new Intent(this, PrepareActivity.class);
        startActivity(intent);
    }

    private class PageRefreshRunnable implements Runnable {
        @Override
        public void run() {
            onPeriodicRefreshTimerTicked();
            mHandler.postDelayed(mRefreshRunnable, REFRESH_DELAY);
        }

        private void onPeriodicRefreshTimerTicked() {
            refreshPage();
        }
    }

    /**
     * Asks for more rooms
     * @param nextId null if to refresh the whole page, otherwise
     *               the last room id of current room list
     */
    private void getRoomList(@Nullable String nextId) {
        proxy().addRoomServiceListener(mRoomListener);
        proxy().getRoomList(config().getUserToken(), nextId, REQ_ROOM_COUNT, 0);
    }

    private void refreshPage() {
        getRoomList(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPage();
            }
        }, 2000);//3????????????R

//        startRefreshTimer();
        mRoomEntered = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefreshTimer();
    }

    @Override
    public void onRefresh() {
        refreshPage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        proxy().removeRoomServiceListener(mRoomListener);
    }

    private class RoomListAdapter extends RecyclerView.Adapter<RoomListItemViewHolder> {
        private List<RoomListResp.RoomListItem> mRoomList = new ArrayList<>();

        @NonNull
        @Override
        public RoomListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RoomListItemViewHolder(LayoutInflater.from(SceneActivity.this).
                    inflate(R.layout.live_room_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RoomListItemViewHolder holder, final int position) {
            if (mRoomList.size() <= position) return;

            RoomListResp.RoomListItem item = mRoomList.get(position);


            holder.name.setText(item.roomName);
            getRoomMember(item.roomId,holder.count);
//            holder.count.setText(String.valueOf(item.onlineUsers));
            RoundedBitmapDrawable d = UserUtil.getUserRoundIcon(getResources(), item.ownerUserInfo.userId);
            d.setCornerRadius(mListItemCorner);
            holder.layout.setBackground(d);
            holder.itemView.setOnClickListener((view) -> {
                if (mRoomEntered) {
                    Logging.d("too frequently call enter a room");
                    return;
                }

                if (config().isUserExisted() && position < mRoomList.size()) {
                    mRoomEntered = true;
                    RoomListResp.RoomListItem selectedInfo = mRoomList.get(position);
                    Logging.d("enter room " + selectedInfo.channelName);
                    enterRoom(selectedInfo.roomId,
                            selectedInfo.roomName,
                            selectedInfo.ownerUserInfo.userId,
                            selectedInfo.ownerUserInfo.userName,
                            selectedInfo.backgroundImage);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mRoomList.size();
        }

        void append(List<RoomListResp.RoomListItem> infoList, boolean reset) {
            if (reset) mRoomList.clear();
            mRoomList.addAll(infoList);
            notifyDataSetChanged();
        }

        void clear(boolean notifyChange) {
            mRoomList.clear();
            if (notifyChange) notifyDataSetChanged();
        }

        RoomListResp.RoomListItem getLast() {
            return mRoomList.isEmpty() ? null : mRoomList.get(mRoomList.size() - 1);
        }
    }


    private void getChatRoomInfo(String imRoomId,TextView textView){
        String chatRoomInfo = IMUtils.getChatRoomInfo(imRoomId);
        CmosRequest request = new CmosRequestOkImpl();
        EMLog.e("getChatRoomInfo", "chatRoomInfo: "+chatRoomInfo );
        request.requestGetAsyncWithHeader(chatRoomInfo, this, null, new JsonCallback<ChatRoomInfoResp>(ChatRoomInfoResp.class) {
            @Override
            public void onError(int status, String errorMsg) {
                EMLog.e("getChatRoomInfo","IM getChatRoomInfo  onError   errorMsg: "+errorMsg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, ChatRoomInfoResp response) {
                if (response != null && response.getData() != null && response.getData().size() > 0) {
                    String affiliations_count = response.getData().get(0).getAffiliations_count();
                    EMLog.e("getChatRoomInfo","IM getChatRoomInfo  affiliations_count: "+affiliations_count);
                    if (!TextUtils.isEmpty(affiliations_count)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(affiliations_count);
                            }
                        });
                    }
                }

            }
        });
    }

    private void getRoomMember(String roomId, TextView textView){
        LeanCloudManager.getInstance().getRoomInfo(roomId, new EMValueCallBack<RoomBean>() {
            @Override
            public void onSuccess(RoomBean roomBean) {
                //  Intent intent = new Intent(this, ChatRoomActivity.class);
                EMLog.e("wyz","getRoomMember : roomId ???"+roomId+" ??????imRoomId  "+roomBean.imRoomId);
                getChatRoomInfo(roomBean.imRoomId,textView);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private void enterRoom(String roomId, String roomName, String userId, String userName, String background) {
        LeanCloudManager.getInstance().getRoomInfo(roomId, new EMValueCallBack<RoomBean>() {
            @Override
            public void onSuccess(RoomBean roomBean) {
                //  Intent intent = new Intent(this, ChatRoomActivity.class);
                EMLog.e("wyz","enterRoom : roomId ???"+roomId+" ??????imRoomId  "+roomBean.imRoomId);
                //roomId????????????ID
                EMClient.getInstance().chatroomManager().joinChatRoom(roomBean.imRoomId, new EMValueCallBack<EMChatRoom>() {
                    @Override
                    public void onSuccess(EMChatRoom value) {
                        //?????????????????????
                        EMLog.e("wyz","enterRoom :  ????????????????????????");
                        Intent intent = new Intent(SceneActivity.this, ImChatRoomActivity.class);
                        intent.putExtra(Const.KEY_ROOM_ID, roomId);
                        intent.putExtra(Const.KEY_IM_ROOM_ID, roomBean.imRoomId);
                        intent.putExtra(Const.KEY_ROOM_NAME, roomName);
                        intent.putExtra(Const.KEY_BACKGROUND, background);
                        intent.putExtra(Const.KEY_USER_ID, userId);
                        intent.putExtra(Const.KEY_USER_NAME, userName);
                        if (userId != null && userId.equals(config().getUserId())) {
                            intent.putExtra(Const.KEY_USER_ROLE, Const.Role.owner.ordinal());
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onError(final int error, String errorMsg) {
                        //?????????????????????
                        EMLog.e("wyz","enterRoom :  ????????????????????????");
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }



    private static class RoomListItemViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView count;
        AppCompatTextView name;
        SquareRelativeLayout layout;

        RoomListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.live_room_list_item_count);
            name = itemView.findViewById(R.id.live_room_list_item_room_name);
            layout = itemView.findViewById(R.id.live_room_list_item_background);
        }
    }

    private class RoomListItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpacing;

        RoomListItemDecoration() {
            mSpacing = getResources().getDimensionPixelOffset(R.dimen.scene_room_list_item_spacing);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            int position = parent.getChildAdapterPosition(view);
            int total = parent.getAdapter() == null ? 0 : parent.getAdapter().getItemCount();
            int half = mSpacing / 2;

            outRect.set(half, half, half, half);

            if (position < SPAN_COUNT) {
                outRect.top = 0;
            } else if (position + SPAN_COUNT >= total) {
                outRect.bottom = 0;
            }

            int line = position % SPAN_COUNT;
            if (line == 0) {
                outRect.left = 0;
            } else if (line + 1 == SPAN_COUNT) {
                outRect.right = 0;
            }
        }
    }
}
