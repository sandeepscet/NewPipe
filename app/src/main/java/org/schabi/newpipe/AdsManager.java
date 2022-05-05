package org.schabi.newpipe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Map;


public class AdsManager {

    InterstitialAd mInterstitialAdMobId = null;

    public void loadAd(Activity context, String adUnitId) {
        if (mInterstitialAdMobId == null) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        Log.d("MyApp", String.format(
                                "Adapter name: %s, Description: %s, Latency: %d",
                                adapterClass, status.getDescription(), status.getLatency()));
                    }

                    InterstitialAd.load(context, adUnitId, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            AdsManager.this.mInterstitialAdMobId = interstitialAd;
                            //    Log.d("ads1", "load" + context.getClass().getSimpleName());
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            AdsManager.this.mInterstitialAdMobId = null;
                            //    Log.d("ads1", "onAdFailedToLoad" + context.getClass().getSimpleName());
                        }
                    });
                }
            });
        }
    }

    public void showInterstitial(Activity context, String intent, String adUnitId, OnAdsClickListener onAdsClickListener) {
        if (AdsManager.this.mInterstitialAdMobId != null) {
            AdsManager.this.mInterstitialAdMobId.show(context);
            AdsManager.this.mInterstitialAdMobId.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            AdsManager.this.mInterstitialAdMobId = null;
                            loadAd(context, adUnitId);
//                            startActivity(context, intent);
                            onAdsClickListener.onClick(intent);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            AdsManager.this.mInterstitialAdMobId = null;
                            loadAd(context, adUnitId);
                            onAdsClickListener.onClick(intent);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            AdsManager.this.mInterstitialAdMobId = null;
                            onAdsClickListener.onClick(intent);
                        }
                    });
        } else if (AdsManager.this.mInterstitialAdMobId == null) {
            loadAd(context, adUnitId);
            onAdsClickListener.onClick(intent);
        }
    }


}
