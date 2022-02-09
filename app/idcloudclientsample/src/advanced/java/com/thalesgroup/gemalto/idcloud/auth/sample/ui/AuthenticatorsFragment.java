package com.thalesgroup.gemalto.idcloud.auth.sample.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.thales.dis.mobile.idcloud.auth.Authenticator;
import com.thales.dis.mobile.idcloud.auth.exception.IdCloudClientException;
import com.thalesgroup.gemalto.idcloud.auth.sample.R;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.ActivatedAuthenticators;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.AddAuthenticator;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.ChangePin;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.OnExecuteFinishListener;
import com.thalesgroup.gemalto.idcloud.auth.sample.idcloudclient.RemoveAuthenticator;
import com.thalesgroup.gemalto.idcloud.auth.sample.util.DialogUtil;


public class AuthenticatorsFragment extends Fragment {

    private SwipeMenuListView listView;
    private CustomAdapter adapter;
    private Authenticator[] authenticators = new Authenticator[0];

    private ChangePin changPinObj;
    private AddAuthenticator addAuthenticatorObj;
    private RemoveAuthenticator removeAuthenticatorObj;
    private ActivatedAuthenticators activatedAuthenticatorsObj;
    private Button deleteButton;
    private Button editButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authenticators, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        OnExecuteFinishListener<Void> addListener = new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                DialogUtil.showToastMessage(getActivity(), getString(R.string.addauthenticator_message));
                //refresh ui after add auth
                refreshAuthenticators();
            }

            @Override
            public void onError(IdCloudClientException e) {
                if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                    DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        };

        Button addButton = (Button) view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Execute the add authenticator use-case.
                executeAddAuthenticator(addListener);
            }
        });

        deleteButton = (Button) view.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.GONE);

        listView = (SwipeMenuListView) view.findViewById(R.id.listView_authenticatos);

        registerForContextMenu(listView);

        adapter = new CustomAdapter(authenticators);
        listView.setAdapter(adapter);
        initChangePinClickListener();
        refreshAuthenticators();

        // create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);


        OnExecuteFinishListener<Void> removeListener = new OnExecuteFinishListener<Void>() {
            @Override
            public void onSuccess(Void ignored) {
                initChangePinClickListener();
                DialogUtil.showToastMessage(getActivity(), getString(R.string.removeauthenticator_alert_message));
                refreshAuthenticators();
            }

            @Override
            public void onError(IdCloudClientException e) {
                initChangePinClickListener();
                if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                    DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        };

        // listener item click event on swipe delete button
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //Execute the Remove authenticator use-case
                executeRemoveAuthenticator(authenticators[position], removeListener);
                return false;
            }
        });

        editButton = (Button) view.findViewById(R.id.button_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                        for (int i = 0; i < listView.getChildCount(); i++) {
                            if (i == position) {
                                listView.getChildAt(i).setBackgroundColor(Color.LTGRAY);
                            } else  {
                                listView.getChildAt(i).setBackgroundColor(Color.WHITE);
                            }
                        }

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                executeRemoveAuthenticator(authenticators[position], removeListener);
                            }
                        });
                    }
                });
            }
        });
    }

    private void initChangePinClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Check if on click on Pin Authenticator, provide the dialog to use to change pin.
                if (authenticators[position].getType() == Authenticator.Type.PIN) {
                    DialogUtil.showAlertDialogWithCancel(getActivity(), getString(R.string.title_changepin), null,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    executeChangePin(new OnExecuteFinishListener<Void>() {
                                        @Override
                                        public void onSuccess(Void ignored) {
                                            DialogUtil.showToastMessage(getActivity(), getString(R.string.changepin_alert_message));
                                        }

                                        @Override
                                        public void onError(IdCloudClientException e) {
                                            if (e.getError() != IdCloudClientException.ErrorCode.USER_CANCELLED) {
                                                DialogUtil.showAlertDialog(getActivity(), getString(R.string.alert_error_title), e.getLocalizedMessage(), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }, null);
                }
            }
        });
    }

    private void refreshAuthenticators() {
        if (activatedAuthenticatorsObj == null) {
            activatedAuthenticatorsObj = new ActivatedAuthenticators(getActivity());
        }
        activatedAuthenticatorsObj.execute(new OnExecuteFinishListener<Authenticator[]>() {
            @Override
            public void onSuccess(Authenticator[] result) {
                authenticators = result;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.refresh(result);
                    }
                });
            }

            @Override
            public void onError(IdCloudClientException ignored) { }
        });
    }

    private void executeChangePin(final OnExecuteFinishListener listener) {
        // Initialize an instance of the Add Authenticate use-case
        changPinObj = new ChangePin(getActivity());
        changPinObj.execute(listener);
    }

    private void executeAddAuthenticator(final OnExecuteFinishListener listener) {
        // Initialize an instance of the Add Authenticate use-case
        addAuthenticatorObj = new AddAuthenticator(getActivity());
        addAuthenticatorObj.execute(listener);
    }

    private void executeRemoveAuthenticator(Authenticator authenticator, final OnExecuteFinishListener listener) {
        // Initialize an instance of the Remove Authenticate use-case, providing
        // (1) the authenticator to be removed
        removeAuthenticatorObj = new RemoveAuthenticator(getActivity(), authenticator);
        removeAuthenticatorObj.execute(listener);
    }

    // CustomAdapter class for list authenticators view
    public class CustomAdapter extends BaseAdapter {

        private Authenticator[] authenticators;

        public CustomAdapter(Authenticator[] authenticators) {
            this.authenticators = authenticators;
        }

        public void refresh(Authenticator[] authenticators) {
            this.authenticators = authenticators;
            notifyDataSetChanged();
        }

        public int getCount() {
            return authenticators.length;
        }

        public Object getItem(int position) {
            return authenticators[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.list_authenticator, parent, false);
            }

            TextView title = (TextView) view.findViewById(R.id.txtTitle);
            ImageView icon = (ImageView) view.findViewById(R.id.imgIcon);

            Authenticator authenticator = authenticators[position];
            switch (authenticator.getType()) {
                case PIN:
                    title.setText(getString(R.string.title_pin_authenticator));
                    icon.setImageResource(R.drawable.ic_dialpad);
                    break;
                case BIOMETRIC:
                    title.setText(getString(R.string.title_biometric_authenticator));
                    icon.setImageResource(R.drawable.ic_fingerprint);
                    break;
                case EMBEDDED:
                    title.setText(getString(R.string.title_system_embeded_authenticator));
                    icon.setImageResource(R.drawable.ic_fingerprint);
                    break;
                default:
                    break;
            }
            return view;
        }
    }
}
