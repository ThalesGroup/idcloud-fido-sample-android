package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.thales.dis.mobile.idcloud.auth.ui.UserConfirmationCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.model.TransactionContextData;

public class ConsumerAppTransactionSigningFragment extends DialogFragment {

    private TransactionContextData transactionContextData;
    private UserConfirmationCallback userConfirmationCallback;

    private Button cancelButton;
    private Button signTransactionButton;
    TextView txnContextTextView;
    TextView txnDateTextView;

    public static ConsumerAppTransactionSigningFragment newInstance(TransactionContextData transactionContextData,
                                                                    UserConfirmationCallback userConfirmationCallback) {
        ConsumerAppTransactionSigningFragment fragment = new ConsumerAppTransactionSigningFragment();
        fragment.transactionContextData = transactionContextData;
        fragment.userConfirmationCallback = userConfirmationCallback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dialog_transaction_signing, container, false);

        if (transactionContextData != null) {
            txnContextTextView = view.findViewById(R.id.txt_transaction_content);
            txnDateTextView = view.findViewById(R.id.txt_transaction_date);
            txnDateTextView.setText(getString(R.string.sign_alert_date, transactionContextData.getDate()));
            txnContextTextView.setText(transactionContextData.getMessage(getContext()));
        }
        cancelButton = view.findViewById(R.id.btn_transaction_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userConfirmationCallback != null) {
                    userConfirmationCallback.cancel();
                    dismiss();
                }
            }
        });

        signTransactionButton = view.findViewById(R.id.btn_sign_transaction_proceed);
        signTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userConfirmationCallback != null) {
                    userConfirmationCallback.onProceed();
                    dismiss();
                }
            }
        });

        return view;
    }
}
