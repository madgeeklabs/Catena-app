package com.timmesserschmidt.braintreesdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class SDKActivity extends Activity {
    private static final String SERVER_BASE = "http://www.madgeeklabs.com:3000";
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk);

        getToken();
    }

    public void onStartClick(View view) {
        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("Awesome payment")
                .secondaryDescription("Using the Client SDK")
                .amount("$10.00")
                .submitButtonText("Pay")
                .build();

        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

            RequestParams requestParams = new RequestParams();
            requestParams.put("payment_method_nonce", paymentMethodNonce);
            requestParams.put("amount", "10.00");

            client.post(SERVER_BASE + "/payment", requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    Log.d("successs", String.valueOf(content));
                    Toast.makeText(SDKActivity.this, content, Toast.LENGTH_LONG).show();
                }

            });
        }
    }

    private void getToken() {
        client.get(SERVER_BASE + "/token", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                clientToken = content;
                findViewById(R.id.btn_start).setEnabled(true);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("error", String.valueOf(statusCode));
                Log.d("error", String.valueOf(errorResponse));
            }
        });
    }
}
