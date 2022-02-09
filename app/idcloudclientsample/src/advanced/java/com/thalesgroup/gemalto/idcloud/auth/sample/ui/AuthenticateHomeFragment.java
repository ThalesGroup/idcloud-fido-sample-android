package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.BaseActivity;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.Authenticate;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.Unenroll;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;


public class AuthenticateHomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        OnExecuteFinishListener<Void> listener = new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                DialogUtil.showToastMessage(getActivity(), getString(R.string.fetch_alert_message));
            }

            @Override
            public void onError(IdCloudClientException e) {
                if (e.getError() == IdCloudClientException.ErrorCode.NO_PENDING_EVENTS) {
                    DialogUtil.showToastMessage(getActivity(), e.getLocalizedMessage());
                } else if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                    DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        };

        Button button = (Button) view.findViewById(R.id.button_authenticate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeAuthenticate(listener);
            }
        });

        ImageButton shareButton = (ImageButton) view.findViewById(R.id.imageButton_shareInAuthenticate);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).shareLogEmail();
            }
        });

        OnExecuteFinishListener<Void> unenrollListener = new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {

                DialogUtil.showToastMessage(getActivity(), getString(R.string.unenroll_alert_message));
                Intent intent = new Intent(getActivity(), EnrollActivity.class);
                getActivity().startActivity(intent);

            }

            @Override
            public void onError(IdCloudClientException e) {
                DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            }
        };

        Button unenrollButton = (Button) view.findViewById(R.id.button_unenroll);
        unenrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Execute the add Unenroll use-case.
                executeUnenroll(unenrollListener);
            }
        });

        return view;
    }

    private void executeAuthenticate(final OnExecuteFinishListener listener) {
        // Initialize an instance of the Authenticate use-case, providing
        Authenticate authenticateObj = new Authenticate(getActivity());
        authenticateObj.execute(listener);
    }

    private void executeUnenroll(final OnExecuteFinishListener listener) {
        // Initialize an instance of the Unenroll use-case, providing
        Unenroll unenrollObj = new Unenroll(getActivity());
        unenrollObj.execute(listener);
    }

}
