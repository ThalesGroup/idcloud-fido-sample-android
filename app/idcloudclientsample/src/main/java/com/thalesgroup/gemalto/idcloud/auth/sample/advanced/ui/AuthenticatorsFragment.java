package com.thalesgroup.gemalto.idcloud.auth.sample.advanced.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thales.dis.mobile.idcloud.auth.ui.UiCallbacks;
import com.thales.dis.mobile.idcloud.authui.callback.SampleCommonUiCallback;
import com.thales.dis.mobile.idcloud.authui.callback.SampleSecurePinUiCallback;
import com.thalesgroup.gemalto.idcloud.auth.sample.Configuration;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient.ActivatedAuthenticators;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient.AddAuthenticator;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient.ChangePin;
import com.thalesgroup.gemalto.idcloud.auth.sample.advanced.idcloudclient.RemoveAuthenticator;

import java.util.List;


public class AuthenticatorsFragment extends Fragment {

    private SwipeMenuListView mListView;
    private CustomAdapter adapter;
    private List<Authenticator> authenticators;

    private ChangePin changPinObj;
    private AddAuthenticator addAuthenticatorObj;
    private RemoveAuthenticator removeAuthenticatorObj;
    private static ActivatedAuthenticators activatedAuthenticators = null;
    private Button deleteButton;
    private Button editButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authenticators, container, false);

        Button addButton = (Button)view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Execute the add authenticator use-case.
                executeAddAuthenticator(new OnExecuteFinishListener() {
                    @Override
                    public void onSuccess() {
                        showAlertDialog(getString(R.string.addauthenticator_alert_title),getString(R.string.addauthenticator_message));
                    }
                    @Override
                    public void onError(IdCloudClientException e) {
                        showAlertDialog(getString(R.string.alert_error_title),e.getLocalizedMessage());
                    }
                });
            }
        });

        deleteButton = (Button)view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mListView = (SwipeMenuListView) view.findViewById(R.id.listView_authenticatos);

        // Set up an instance of the ActivatedAuthenticators use-case to retreive a list of registered authenticators.
        if (activatedAuthenticators == null) {
            activatedAuthenticators = new ActivatedAuthenticators(getActivity(), Configuration.url);
        }
        authenticators = activatedAuthenticators.execute();

        String[] textString = new String[authenticators.size()];
        int[] drawableIds = new int[authenticators.size()]; ;

        for (int i=0; i<authenticators.size(); i++) {
            switch (authenticators.get(i).getType()) {
                case PIN:
                    textString[i] = getString(R.string.title_pin_authenticator);
                    drawableIds[i] = R.drawable.ic_dialpad;
                    break;
                case BIOMETIRC:
                    textString[i] = getString(R.string.title_biometric_authenticator);
                    drawableIds[i] = R.drawable.ic_fingerprint;
                    break;
                case EMBEDDED:
                    textString[i] = getString(R.string.title_system_embeded_authenticator);
                    drawableIds[i] = R.drawable.ic_fingerprint;
                    break;
                default:
                    break;
            }
        }

        adapter = new CustomAdapter(getActivity(), textString, drawableIds);
        mListView.setAdapter(adapter);

        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Check if on click on Pin Authenticator, provide the dialog to use to change pin.
                if (authenticators.get((int)l).getType()== Authenticator.Type.PIN) {
                    BottomSheetDialog mBottomDialogNotificationAction;
                    View sheetView = getActivity().getLayoutInflater().inflate(R.layout.dialog_changepin, null);
                    mBottomDialogNotificationAction = new BottomSheetDialog(getActivity());
                    mBottomDialogNotificationAction.setContentView(sheetView);
                    mBottomDialogNotificationAction.show();

                    View button_changpin = sheetView.findViewById(R.id.button_changpin);
                    button_changpin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mBottomDialogNotificationAction.dismiss();
                            //ChangePin
                            executeChangePin(new OnExecuteFinishListener() {
                                @Override
                                public void onSuccess() {
                                    showAlertDialog(getString(R.string.changepin_alert_title), getString(R.string.changepin_alert_message));
                                }
                                @Override
                                public void onError(IdCloudClientException e) {
                                    showAlertDialog(getString(R.string.alert_error_title),e.getLocalizedMessage());
                                }
                            });
                        }
                    });
                    View button_cancal = sheetView.findViewById(R.id.button_cancel);
                    button_cancal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mBottomDialogNotificationAction.dismiss();
                        }
                    });

                    // Remove default white color background
                    FrameLayout bottomSheet = (FrameLayout) mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                    bottomSheet.setBackground(null);
                }
            }
        });

        // create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }

        };
        // set creator
        mListView.setMenuCreator(creator);

        // listener item click event on swipe delete button
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //Execute the Remove authenticator use-case
                executeRemoveAuthenticator(authenticators.get(position), new OnExecuteFinishListener() {
                    @Override
                    public void onSuccess() {
                        showAlertDialog(getString(R.string.removeauthenticator_alert_title), getString(R.string.removeauthenticator_alert_message));
                    }
                    @Override
                    public void onError(IdCloudClientException e) {
                        showAlertDialog(getString(R.string.alert_error_title),e.getLocalizedMessage());
                    }
                });
                return false;
            }
        });

        editButton = (Button)view.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        mListView.getChildAt(i).setBackgroundColor(Color.LTGRAY);

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                executeRemoveAuthenticator(authenticators.get((int) l), new OnExecuteFinishListener() {
                                    @Override
                                    public void onSuccess() {
                                        showAlertDialog(getString(R.string.removeauthenticator_alert_title), getString(R.string.removeauthenticator_alert_message));
                                    }
                                    @Override
                                    public void onError(IdCloudClientException e) {
                                        showAlertDialog(getString(R.string.alert_error_title), e.getLocalizedMessage());
                                    }
                                });
                            }
                       });
                    }
                });
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void executeChangePin(final AuthenticatorsFragment.OnExecuteFinishListener listener) {
        // Initialize an instance of the Add Authenticate use-case, providing
        // (1) the pre-configured URL
        // (2) the pinPadUiDelegate

        SampleSecurePinUiCallback securePinPadUiCallback = new SampleSecurePinUiCallback(
                getActivity().getSupportFragmentManager(), getString(R.string.usecase_enrollment)
        );

        changPinObj = new ChangePin(getActivity(), Configuration.url, securePinPadUiCallback);
        changPinObj.execute(listener);
    }

    private void executeAddAuthenticator(final AuthenticatorsFragment.OnExecuteFinishListener listener) {
        // Initialize an instance of the Add Authenticate use-case, providing
        // (1) the pre-configured URL
        // (2) the uiCallbacks

        UiCallbacks uiCallbacks = new UiCallbacks();
        SampleSecurePinUiCallback securePinUiCallback = new SampleSecurePinUiCallback(
                getActivity().getSupportFragmentManager(), getString(R.string.usecase_addauthenticator)
        );
        uiCallbacks.securePinPadUiCallback = securePinUiCallback;
        uiCallbacks.commonUiCallback = new SampleCommonUiCallback(
                getActivity().getSupportFragmentManager()
        );

        addAuthenticatorObj = new AddAuthenticator(getActivity(), Configuration.url, uiCallbacks);
        addAuthenticatorObj.execute(listener);
    }

    private void executeRemoveAuthenticator(Authenticator authenticator, final AuthenticatorsFragment.OnExecuteFinishListener listener) {
        // Initialize an instance of the Remove Authenticate use-case, providing
        // (1) the pre-configured URL
        // (2) the authenticator to be removed
        removeAuthenticatorObj = new RemoveAuthenticator(getActivity(), Configuration.url, authenticator);
        removeAuthenticatorObj.execute(listener);
    }

    public interface OnExecuteFinishListener {
        void onSuccess();
        void onError(IdCloudClientException e);
    }

    protected void showAlertDialog(final String title, final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AuthenticatorsFragment.this.getContext())
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Reload fragment to refresh authenticator list
                                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.detach(AuthenticatorsFragment.this).attach(AuthenticatorsFragment.this).commit();

                                if(title == getString(R.string.removeauthenticator_alert_title)) {
                                    deleteButton.setVisibility(View.GONE);
                                    editButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }
        });

    }

    // CustomAdapter class for list authenticators view
    public class CustomAdapter extends BaseAdapter {

        private Context mContext;
        private String[]  Title;
        private int[] imge;

        public CustomAdapter(Context context, String[] text1,int[] imageIds) {
            mContext = context;
            Title = text1;
            imge = imageIds;
        }

        public int getCount() {
            return Title.length;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_authenticator, parent, false);
            TextView title;
            ImageView i1;
            i1 = (ImageView) row.findViewById(R.id.imgIcon);
            title = (TextView) row.findViewById(R.id.txtTitle);
            title.setText(Title[position]);
            i1.setImageResource(imge[position]);

            return (row);
        }
    }
}