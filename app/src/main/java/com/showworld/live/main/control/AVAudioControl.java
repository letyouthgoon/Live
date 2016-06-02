package com.showworld.live.main.control;

import android.content.Context;
import android.content.Intent;

import com.showworld.live.SWLApplication;
import com.showworld.live.main.Constants;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVAudioCtrl.Delegate;

public class AVAudioControl {
    private Context mContext = null;

    private Delegate mDelegate = new Delegate() {
        @Override
        protected void onOutputModeChange(int outputMode) {
            super.onOutputModeChange(outputMode);
            mContext.sendBroadcast(new Intent(Constants.ACTION_OUTPUT_MODE_CHANGE));
        }
    };

    AVAudioControl(Context context) {
        mContext = context;
    }

    void initAVAudio() {
        QavsdkControl qavsdk = ((SWLApplication) mContext)
                .getQavsdkControl();
        qavsdk.getAVContext().getAudioCtrl().setDelegate(mDelegate);
    }

    boolean getHandfreeChecked() {
        QavsdkControl qavsdk = ((SWLApplication) mContext)
                .getQavsdkControl();
        return qavsdk.getAVContext().getAudioCtrl().getAudioOutputMode() == AVAudioCtrl.OUTPUT_MODE_HEADSET;
    }

    String getQualityTips() {
        QavsdkControl qavsdk = ((SWLApplication) mContext).getQavsdkControl();
        AVAudioCtrl avAudioCtrl;
        if (qavsdk != null) {
            avAudioCtrl = qavsdk.getAVContext().getAudioCtrl();
            return avAudioCtrl.getQualityTips();
        }

        return "";
    }
}