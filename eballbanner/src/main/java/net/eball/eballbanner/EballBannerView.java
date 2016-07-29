package net.eball.eballbanner;

/**
 * Created by yukatou on 2016/07/13.
 */
import net.adcrops.sdk.AdcController;
import net.adcrops.sdk.AdcController.OptionalPropertiesType;
import net.adcrops.sdk.banner.AdcBanner;
import net.adcrops.sdk.banner.AdcBanner.BannerFailResult;
import net.adcrops.sdk.banner.AdcBanner.BannerType;
import net.adcrops.sdk.data.AdcBannerData;
import net.adcrops.sdk.exception.AdcInitNotReachableNextworkExcepsion;
import net.adcrops.sdk.exception.AdcSecurityException;
import net.adcrops.sdk.listener.AdcBannerListener;
import net.adcrops.sdk.listener.AdcNetworkNotifyListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;





public class EballBannerView extends FrameLayout implements AdcBannerListener, AdcNetworkNotifyListener{

    private Context mContext;
    private static AdcController ctrl = null;
    private WebView bannerWeb;
    private DisplayMetrics metrics;
    private boolean mIsAdjustSize;
    private AdcBanner b;
    private ProgressBar progressBar;
    private EballBannerPosition mPosition;
    private AdcController.OptionalPropertiesType mType;

    public enum EballBannerPosition{
        EballBannerPositionHeader,
        EballBannerPositionFooter,
        EballBannerPositionFree
    }

    public EballBannerView(Context context, EballBannerPosition position, AdcController.OptionalPropertiesType type) {
        super(context);
        mContext = context;
        mPosition = position;
        mType = type;
        initBannerView();
    }


    public EballBannerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EballBannerView);
        mIsAdjustSize = array.getBoolean(R.styleable.EballBannerView_isAdjustSize, true);

        int type = array.getInt(R.styleable.EballBannerView_OptionalPropertiesType, 0);
        String typeName = null;
        if (type == 0) {
            typeName = "DEFAULT";
        } else {
            typeName = "OPTIONAL"+String.valueOf(type);
        }
        mType = AdcController.OptionalPropertiesType.valueOf(typeName);

        initBannerView();
        isAdjustAdSize(mIsAdjustSize);

    }

    private void initBannerView() {
        //Log.d("", "==initBannerView");
        try {
            // TODO:adcropsControllerのイニシャライズ
            AdcController.setup(mContext);
        } catch (AdcSecurityException e) {
            e.printStackTrace();
        }
        AdcController.OptionalPropertiesType type= mType;


        try {
            ctrl = new AdcController(type);
            ctrl.setActivity(this);
        } catch (AdcInitNotReachableNextworkExcepsion e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            //Log.d("", "==AdcBanner");
            b = new AdcBanner(this, BannerType.BANNER);
        } catch (AdcSecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        WindowManager wm = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();

        // ディスプレイ情報の取得
        metrics = new DisplayMetrics();
        disp.getMetrics(metrics);

        bannerWeb = new WebView(mContext);
        bannerWeb.getSettings().setJavaScriptEnabled(true);
        bannerWeb.setBackgroundColor(Color.rgb(240, 241, 242));
        //addView(bannerWeb, new FrameLayout.LayoutParams((int)(320*metrics.scaledDensity), (int)(50*metrics.scaledDensity)));
        addView(bannerWeb, setParams());

        progressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(100, 100, Gravity.CENTER);
        addView(progressBar,params2);


    }

    public void load(){
        if (b != null) {
            b.load();
        }
    }

    public void stop(){
        if (b != null) {
            b.stop();
        }
    }

    public void pause(){
        if (b != null) {
            b.pause();
        }
    }

    public void isAdjustAdSize(boolean isAdjustAdSize){
        mIsAdjustSize = isAdjustAdSize;
        bannerWeb.setLayoutParams(setParams());
    }

    private FrameLayout.LayoutParams setParams() {
        FrameLayout.LayoutParams params;
        if (mIsAdjustSize) {
            float density = metrics.scaledDensity;
            float ratio = 1080.0f / (320.0f * density);
            params = new FrameLayout.LayoutParams((int)((320*metrics.scaledDensity) * ratio), (int)((50*metrics.scaledDensity) * ratio));
        } else {
            params = new FrameLayout.LayoutParams((int)(320*metrics.scaledDensity), (int)(50*metrics.scaledDensity));
        }
        if (mPosition == EballBannerPosition.EballBannerPositionHeader) {
            params.gravity = Gravity.CENTER | Gravity.TOP;
            return params;
        } else if (mPosition == EballBannerPosition.EballBannerPositionFooter){
            params.gravity = Gravity.CENTER | Gravity.BOTTOM;
            return params;
        } else if (mPosition == EballBannerPosition.EballBannerPositionFree){
            return params;
        } else {
            return params;
        }

    }

    public FrameLayout.LayoutParams getParams() {
        return (LayoutParams) bannerWeb.getLayoutParams();
    }

    @Override
    public void onAdcBannerDidFailReceiveAd(BannerFailResult result) {
        Log.d("", "==onAdcBannerDidFailReceiveAd");

    }


    @Override
    public void onAdcBannerDidFinishLoad(AdcBannerData data) {
        Log.d("", "==onAdcBannerDidFinishLoad:"+data);
        bannerWeb.loadDataWithBaseURL(data.getBaseURL(), data.getHtml(), "text/html", "UTF-8", null);
        progressBar.setVisibility(View.GONE);
        bannerWeb.setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    public void onAdcBannerDidReceiveAd(AdcBannerData data) {
        Log.d("", "==onAdcBannerDidReceiveAd");
        bannerWeb.loadDataWithBaseURL(data.getBaseURL(), data.getHtml(), "text/html", "UTF-8", null);
    }


    @Override
    public void onAdcRequestNotReachableStatusError() {
        // TODO Auto-generated method stub

    }

}
