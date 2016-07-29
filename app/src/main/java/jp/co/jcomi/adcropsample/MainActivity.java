package jp.co.jcomi.adcropsample;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import net.adcrops.sdk.AdcController;
import net.adcrops.sdk.data.AdcAdData;
import net.adcrops.sdk.exception.AdcInitNotReachableNextworkExcepsion;
import net.adcrops.sdk.exception.AdcSecurityException;
import net.adcrops.sdk.listener.AdcNetworkNotifyListener;
import net.adcrops.sdk.listener.AdcXMLRequestListener;
import net.eball.eballbanner.EballBannerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EballBannerView view = (EballBannerView)findViewById(R.id.eballBannerView1);
        view.load();
    }
}
