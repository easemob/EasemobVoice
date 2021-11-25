package io.agora.agoravoice.im;

import androidx.annotation.NonNull;

import io.agora.agoravoice.business.BusinessProxy;

public class IMManager {

    private BusinessProxy mProxy;

    public IMManager(@NonNull BusinessProxy proxy) {
        mProxy = proxy;
    }


}
