package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.thales.dis.mobile.idcloud.auth.ui.TransactionContext;
import com.thales.dis.mobile.idcloud.auth.ui.UserConfirmationCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.model.TransactionContextData;

import java.util.HashMap;

public class CustomAppClientConformerCallback extends SampleCommonUiCallback {
    FragmentActivity activity;
    FragmentManager fragmentManager;

    public CustomAppClientConformerCallback(FragmentActivity activity, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onTransactionConfirmation(TransactionContext transactionContext, UserConfirmationCallback userConfirmationCallback) {

        HashMap<String, String> txnDetailMap = (HashMap<String, String>) transactionContext.getTransactionDetails();
        TransactionContextData transactionContextData = TransactionContextData.newInstance(txnDetailMap);
        if (fragmentManager != null) {
            ConsumerAppTransactionSigningFragment transactionSigningFragment =
                    ConsumerAppTransactionSigningFragment.newInstance(transactionContextData, userConfirmationCallback);
            transactionSigningFragment.setCancelable(false);
            transactionSigningFragment.show(fragmentManager, "fragment_transaction_signing");
        }
    }
}
