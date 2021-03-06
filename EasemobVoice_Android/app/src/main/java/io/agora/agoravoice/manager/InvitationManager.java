package io.agora.agoravoice.manager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.agora.agoravoice.business.BusinessProxy;
import io.agora.agoravoice.business.definition.struct.ErrorCode;
import io.agora.agoravoice.business.definition.struct.RoomUserInfo;
import io.agora.agoravoice.business.log.Logging;
import io.agora.agoravoice.business.server.retrofit.model.requests.SeatBehavior;
import io.agora.agoravoice.utils.Const;

public class InvitationManager {
    private static final String TAG = InvitationManager.class.getSimpleName();

    public interface InvitationManagerListener {
        void onInvitationTimeout(String userId);

        void onApplicationTimeout(String userId);

        void onListChanged();
    }

    private static final int TIMEOUT = 30 * 1000;

    private final BusinessProxy mProxy;
    private final String mRoomId;

    private final List<InvitationManagerListener> mListener = new ArrayList<>();

    private final List<RoomUserInfo> mFullUserList = new ArrayList<>();

    private final List<RoomUserInfo> mInviteList = new ArrayList<>();
    private final Map<String, RoomUserInfo> mInviteUserInfoMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> mInviteSeatMap = new ConcurrentHashMap<>();
    private final Map<String, Long> mInviteTimeMap = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> mMultiInviteForSeatMap = new ConcurrentHashMap<>();

    private final List<RoomUserInfo> mApplyList = new ArrayList<>();
    private final Map<String, RoomUserInfo> mApplyUserInfoMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> mApplySeatMap = new ConcurrentHashMap<>();
    private final Map<String, Long> mApplyTimeMap = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> mMultiApplyForSeatMap = new ConcurrentHashMap<>();

    private final Object mInviteLock = new Object();
    private boolean mInviteTimerStop;
    private final Object mApplyLock = new Object();
    private boolean mApplyTimerStop;

    private final Runnable mInviteTimer = () -> {
        synchronized (mInviteLock) {
            while (!mInviteList.isEmpty() && !mInviteTimerStop) {
                Iterator<Map.Entry<String, Long>> iterator
                        = mInviteTimeMap.entrySet().iterator();
                List<String> timeoutUserList = new ArrayList<>();
                while (iterator.hasNext()) {
                    Map.Entry<String, Long> entry = iterator.next();
                    String userId = entry.getKey();
                    long timestamp = entry.getValue();
                    long now = System.currentTimeMillis();
                    if (now - timestamp > TIMEOUT) {
                        timeoutUserList.add(userId);
                    }
                }

                for (String userId : timeoutUserList) {
                    removeInvitationInfo(userId);
                    for (InvitationManagerListener listener : mListener) {
                        listener.onInvitationTimeout(userId);
                    }
                }

                try {
                    mInviteLock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        Logging.i("invitation timer stops");
    };

    private final Runnable mApplyTimer = () -> {
        synchronized (mApplyLock) {
            while (!mApplyList.isEmpty() && !mApplyTimerStop) {
                Iterator<Map.Entry<String, Long>> iterator =
                        mApplyTimeMap.entrySet().iterator();
                List<String> timeoutUserList = new ArrayList<>();
                while (iterator.hasNext()) {
                    Map.Entry<String, Long> entry = iterator.next();
                    String userId = entry.getKey();
                    long timestamp = entry.getValue();
                    long now = System.currentTimeMillis();
                    if (now - timestamp > TIMEOUT) {
                        timeoutUserList.add(userId);
                    }
                }

                for (String userId : timeoutUserList) {
                    removeApplicationInfo(userId, false);
                    for (InvitationManagerListener listener : mListener) {
                        listener.onApplicationTimeout(userId);
                    }
                }

                try {
                    mApplyLock.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        Logging.i("application timer stops");
    };

    private void startInviteTimer() {
        mInviteTimerStop = false;
        new Thread(mInviteTimer).start();
    }

    private void startApplyTimer() {
        mApplyTimerStop = false;
        new Thread(mApplyTimer).start();
    }

    private void stopInviteTimer() {
        mInviteTimerStop = true;
    }

    private void stopApplyTimer() {
        mApplyTimerStop = true;
    }

    public void addInvitationListener(InvitationManagerListener listener) {
        mListener.add(listener);
    }

    public void removeInvitationListener(InvitationManagerListener listener) {
        mListener.remove(listener);
    }

    public InvitationManager(@NonNull String roomId, @NonNull BusinessProxy proxy) {
        mRoomId = roomId;
        mProxy = proxy;
    }

    public void updateUserList(List<RoomUserInfo> list) {
        if (list == null || list.size() <= 0) return;
        mFullUserList.clear();
        mFullUserList.addAll(list);
    }

    public List<RoomUserInfo> getFullUserList() {
        return mFullUserList;
    }

    public List<RoomUserInfo> getInvitedList() {
        return mInviteList;
    }

    public boolean hasInvited(String userId) {
        return mInviteUserInfoMap.containsKey(userId);
    }

    public List<RoomUserInfo> getApplicationList() {
        return mApplyList;
    }

    public boolean hasApplication() {
        return !mApplyList.isEmpty();
    }

    public boolean hasUserApplied(String userId) {
        return mApplyUserInfoMap.containsKey(userId);
    }

    private void removeInvitationInfo(String userId) {
        mInviteSeatMap.remove(userId);
        mInviteTimeMap.remove(userId);
        RoomUserInfo info = mInviteUserInfoMap.remove(userId);
        if (info != null) mInviteList.remove(info);
    }

    private void removeApplyForASeatMapForAUser(int no, String userId) {
        if (!mMultiApplyForSeatMap.containsKey(no)) return;
        Set<String> set = mMultiApplyForSeatMap.get(no);
        if (set != null) set.remove(userId);
    }

    // We may invite multiple users for a seat at the same time
    private void addMultiInviteRecord(int no, String userId) {
        Set<String> seatInvitations = mMultiInviteForSeatMap.get(no);
        if (seatInvitations == null) {
            mMultiInviteForSeatMap.put(no, new HashSet<>());
        }
        seatInvitations = mMultiInviteForSeatMap.get(no);
        seatInvitations.add(userId);
    }

    // Once a user accepts the invitation for a seat, all
    // users' invitations should be removed from the
    // invitation map for this seat.
    private void removeMultiInviteRecord(int no) {
        Set<String> seatInvitations = mMultiInviteForSeatMap.get(no);
        if (seatInvitations != null) {
            for (String userId : seatInvitations) {
                removeInvitationInfo(userId);
            }
        }
    }

    // Remove a single user's record, maybe because the
    // user has left, or the invitation fails
    private void removeMultiInviteRecord(int no, String userId) {
        Set<String> seatInvitations = mMultiInviteForSeatMap.get(no);
        if (seatInvitations != null) {
            seatInvitations.remove(userId);
        }
    }

    private void removeApplicationInfo(String userId, boolean allSeat) {
        if (allSeat) {
            if (mApplySeatMap.containsKey(userId)) {
                int no = mApplySeatMap.get(userId);
                Set<String> userSet = mMultiApplyForSeatMap.get(no);
                if (userSet != null) {
                    Set<String> newSet = new HashSet<>(userSet);
                    for (String user : newSet) {
                        removeApplyForAUser(user);
                    }
                }
            }
        } else {
            removeApplyForAUser(userId);
        }
    }

    private void removeApplyForAUser(String userId) {
        if (mApplySeatMap.containsKey(userId)) {
            int no = mApplySeatMap.get(userId);
            RoomUserInfo userInfo = mApplyUserInfoMap.remove(userId);
            if (userInfo != null) mApplyList.remove(userInfo);
            removeApplyForASeatMapForAUser(no, userId);
            mApplyTimeMap.remove(userId);
        }
    }

    public int requestSeatBehavior(@NonNull String token, @NonNull String userId, String userName, int no, int behavior) {
        int seatNo = no;
        if (behavior == SeatBehavior.INVITE) {
            if (mInviteUserInfoMap.containsKey(userId)) {
                return Const.ERR_REPEAT_INVITE;
            } else {
                RoomUserInfo info = getUserInfoByUserId(userId);
                if (info == null) return Const.ERR_USER_UNKNOWN;
                if (!mInviteList.contains(info)) {
                    if (mInviteList.isEmpty()) {
                        // When this is the first in list, start a timer
                        // to check invitation timeout
                        Logging.i("start invitation timer");
                        startInviteTimer();
                    }

                    mInviteList.add(info);
                    mInviteUserInfoMap.put(userId, info);
                    mInviteSeatMap.put(userId, no);
                    mInviteTimeMap.put(userId, System.currentTimeMillis());
                    addMultiInviteRecord(no, userId);
                }
            }
        } else if (behavior == SeatBehavior.APPLY_ACCEPT ||
                behavior == SeatBehavior.APPLY_REJECT) {
            Integer seatNum = mApplySeatMap.get(userId);
            seatNo = seatNum != null ? seatNum : 0;
            removeApplicationInfo(userId, true);
            for (InvitationManagerListener listener : mListener) {
                listener.onListChanged();
            }
        }

        mProxy.requestSeatBehavior(token, mRoomId, userId, userName, seatNo, behavior);
        return Const.ERR_OK;
    }

    public void handleSeatBehaviorRequestFail(String userId, String userName, int no, int behavior, int errorCode) {
        switch (behavior) {
            case SeatBehavior.INVITE:
                removeInvitationInfo(userId);
                break;
            case SeatBehavior.APPLY_ACCEPT:
                if (errorCode == ErrorCode.ERROR_SEAT_TAKEN) {
                    // Room owner has accepted the application of
                    // audience for a seat, but the seat has been
                    // taken by earlier operations.
                    // We remove this application from list.
                    removeApplicationInfo(userId, false);
                }
                break;
        }
    }

    private void addApplyMap(int no, String userId) {
        RoomUserInfo info = mApplyUserInfoMap.remove(userId);
        if (info != null) {
            mApplyList.remove(info);
            mApplyList.add(info);
            int oldApplyNo = mApplySeatMap.remove(userId);
            removeApplyForASeatMapForAUser(oldApplyNo, userId);
            addApplyForSeat(no, userId);
            mApplySeatMap.put(userId, no);
        } else {
            info = getUserInfoByUserId(userId);
            if (info != null) {
                if (mApplyList.isEmpty()) {
                    Logging.i("start application timer");
                    startApplyTimer();
                }

                mApplyList.add(info);
                mApplyUserInfoMap.put(userId, info);
                mApplySeatMap.put(userId, no);
                mApplyTimeMap.put(userId, System.currentTimeMillis());
                addApplyForSeat(no, userId);
            }
        }
    }

    private void addApplyForSeat(int no, String userId) {
        if (mMultiApplyForSeatMap.get(no) == null) {
            mMultiApplyForSeatMap.put(no, new HashSet<>());
        }
        Set<String> set = mMultiApplyForSeatMap.get(no);
        set.add(userId);
    }

    public void receiveSeatBehaviorResponse(String userId, String userName, int no, int behavior) {
        switch (behavior) {
            case SeatBehavior.INVITE_ACCEPT:
                // Remove all invitation information related
                // to this seat
                removeMultiInviteRecord(no);
                for (InvitationManagerListener listener : mListener) {
                    listener.onListChanged();
                }
                break;
            case SeatBehavior.INVITE_REJECT:
                removeInvitationInfo(userId);
                removeMultiInviteRecord(no, userId);
                break;
            case SeatBehavior.APPLY:
                addApplyMap(no, userId);
                for (InvitationManagerListener listener : mListener) {
                    listener.onListChanged();
                }
                break;
            case SeatBehavior.APPLY_ACCEPT:
                removeApplicationInfo(userId, true);
                break;
            case SeatBehavior.APPLY_REJECT:
                removeApplicationInfo(userId, false);
                break;
        }
    }

    public void userLeft(String userId) {
        if (mInviteSeatMap.containsKey(userId)) {
            int no = mInviteSeatMap.get(userId);
            removeMultiInviteRecord(no, userId);
        }

        removeInvitationInfo(userId);
        removeApplicationInfo(userId, false);
        RoomUserInfo info = getUserInfoByUserId(userId);
        if (info != null) mFullUserList.remove(info);
    }

    public void requestSeatStateChange(@NonNull String token, @NonNull String roomId, int no, int state) {
        mProxy.changeSeatState(token, roomId, no, state);
    }

    private RoomUserInfo getUserInfoByUserId(String userId) {
        RoomUserInfo ret = null;
        for (RoomUserInfo info : mFullUserList) {
            if (info.userId.equals(userId)) {
                ret = info;
            }
        }

        return ret;
    }

    public void stopTimer() {
        stopInviteTimer();
        stopApplyTimer();
    }
}
