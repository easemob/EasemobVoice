package io.agora.agoravoice.im;

import io.agora.agoravoice.ui.views.EaseEmojiconInfoProvider;

public class EaseIM {
    private static final String TAG = EaseIM.class.getSimpleName();
    private static EaseIM instance;

    private EaseEmojiconInfoProvider mEmojiconInfoProvider;

    private EaseIM() {}

    public static EaseIM getInstance() {
        if(instance == null) {
            synchronized (EaseIM.class) {
                if(instance == null) {
                    instance = new EaseIM();
                }
            }
        }
        return instance;
    }
    /**
     * get emojicon provider
     * @return
     */
    public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
        return mEmojiconInfoProvider;
    }

    /**
     * set emojicon provider
     * @param emojiconInfoProvider
     * @return
     */
    public EaseIM setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
        mEmojiconInfoProvider = emojiconInfoProvider;
        return this;
    }

}
