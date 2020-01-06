package kr.co.koscom.omp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.syncmanager.ConnectionManager;
import com.sendbird.syncmanager.FailedMessageEventActionReason;
import com.sendbird.syncmanager.MessageCollection;
import com.sendbird.syncmanager.MessageEventAction;
import com.sendbird.syncmanager.MessageFilter;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.sendbird.syncmanager.handler.MessageCollectionCreateHandler;
import com.sendbird.syncmanager.handler.MessageCollectionHandler;
import com.sendbird.syncmanager.utils.FileUtils;
import com.sendbird.syncmanager.utils.MediaPlayerActivity;
import com.sendbird.syncmanager.utils.PhotoViewerActivity;
import com.sendbird.syncmanager.utils.PreferenceUtils;
import com.sendbird.syncmanager.utils.SyncManagerUtils;
import com.sendbird.syncmanager.utils.TextUtils;
import com.sendbird.syncmanager.utils.UrlPreviewInfo;
import com.sendbird.syncmanager.utils.WebUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONException;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kr.co.koscom.omp.view.NumberEditText;
import kr.co.koscom.omp.view.NumberTextWatcher;
import kr.co.koscom.omp.view.ViewUtils;
import kr.co.koscom.omp.view.WebUtil;


public class GroupChatFragment extends Fragment {

    private static final String LOG_TAG = GroupChatFragment.class.getSimpleName();

    private Handler mHandler = new Handler();

    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";

    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;

    private static final String STATE_CHANNEL_URL = "STATE_CHANNEL_URL";
    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    static final String EXTRA_CHANNEL = "EXTRA_CHANNEL";

    private InputMethodManager mIMM;
    private HashMap<BaseChannel.SendFileMessageWithProgressHandler, FileMessage> mFileProgressHandlerMap;

    private LinearLayout btnShowPrice;
    private TextView showPriceTitle;
    private AppCompatImageView showPriceArrow;
    private LinearLayoutCompat currentPriceZone;

    private RelativeLayout mRootLayout;
    private RecyclerView mRecyclerView;
    private GroupChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private AppCompatImageButton mMessageSendButton;
    private AppCompatImageButton mUploadFileButton;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText;
    private ConstraintLayout btnShowContact;
    private AppCompatImageView ivShowContact;
    private ConstraintLayout contactZone;
    private AppCompatImageView btnTooltip;
    private RelativeLayout tooltip;
    private ImageView btnCloseTooltip;
    private TextView payEndDate;
    private ImageView payEndDatePicker;
    private TextView btnCancelContact;
    private LinearLayoutCompat btnConfirmContact;
    private EditText requestCount;
    private EditText requestPrice;

    private TextView prePrice;
    private TextView count;
    private TextView price;
    private TextView totalAmount;

    private GroupChannel mChannel;
    private String mChannelUrl;

    private int mCurrentState = STATE_NORMAL;
    private BaseMessage mEditingMessage = null;

    final MessageFilter mMessageFilter = new MessageFilter(BaseChannel.MessageTypeFilter.ALL, null, null);
    private MessageCollection mMessageCollection;

    private long mLastRead;

    private Date today = new Date();
    private DecimalFormat numberFormat = new DecimalFormat("#,###");

    /**
     * To create an instance of this fragment, a Channel URL should be required.
     */
    public static GroupChatFragment newInstance(@NonNull String channelUrl) {
        GroupChatFragment fragment = new GroupChatFragment();

        Bundle args = new Bundle();
        args.putString("groupChannelUrl", channelUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIMM = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mFileProgressHandlerMap = new HashMap<>();

        if (savedInstanceState != null) {
            mChannelUrl = savedInstanceState.getString(STATE_CHANNEL_URL);
        } else {
            mChannelUrl = getArguments().getString("groupChannelUrl");
        }

        Log.d(LOG_TAG, mChannelUrl);

        mLastRead = PreferenceUtils.getLastRead(mChannelUrl);
        mChatAdapter = new GroupChatAdapter(getActivity());
        setUpChatListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        setRetainInstance(true);


        btnShowPrice = rootView.findViewById(R.id.btnShowPrice);
        showPriceTitle = rootView.findViewById(R.id.showPriceTitle);
        showPriceArrow = rootView.findViewById(R.id.showPriceArrow);
        currentPriceZone = rootView.findViewById(R.id.currentPriceZone);
        btnShowContact = rootView.findViewById(R.id.btnShowContact);
        ivShowContact = rootView.findViewById(R.id.ivShowContact);
        contactZone = rootView.findViewById(R.id.contactZone);
        btnTooltip = rootView.findViewById(R.id.btnTooltip);
        tooltip = rootView.findViewById(R.id.tooltip);
        btnCloseTooltip = rootView.findViewById(R.id.btnCloseTooltip);

        requestCount = rootView.findViewById(R.id.requestCount);
        requestPrice = rootView.findViewById(R.id.requestPrice);

        payEndDate = rootView.findViewById(R.id.payEndDate);
        payEndDatePicker = rootView.findViewById(R.id.payEndDatePicker);
        btnCancelContact = rootView.findViewById(R.id.btnCancelContact);
        btnConfirmContact = rootView.findViewById(R.id.btnConfirmContact);

        mRootLayout = (RelativeLayout) rootView.findViewById(R.id.layout_group_chat_root);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_group_chat);

        mCurrentEventLayout = rootView.findViewById(R.id.layout_group_chat_current_event);
        mCurrentEventText = (TextView) rootView.findViewById(R.id.text_group_chat_current_event);

        mMessageEditText = (EditText) rootView.findViewById(R.id.edittext_group_chat_message);
        mMessageSendButton = rootView.findViewById(R.id.button_group_chat_send);
        mUploadFileButton = rootView.findViewById(R.id.button_group_chat_upload);

        prePrice = rootView.findViewById(R.id.prePrice);
        count = rootView.findViewById(R.id.count);
        price = rootView.findViewById(R.id.price);
        totalAmount = rootView.findViewById(R.id.totalAmount);

        payEndDate.setText(DateFormatUtils.format(new GregorianCalendar(), "yyyy-MM-dd"));
        payEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(payEndDate.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                        NumberFormat numberFormat = NumberFormat.getInstance();
                        numberFormat.setMinimumIntegerDigits(2);
                        payEndDate.setText(year + "-" + numberFormat.format(monthOfYear+1) + "-" + numberFormat.format(dayOfMonth));
                    }
                }, Integer.parseInt(StringUtils.substringBefore(payEndDate.getText().toString(), "-")),
                        Integer.parseInt(StringUtils.substringBetween(payEndDate.getText().toString(), "-", "-"))-1,
                        Integer.parseInt(StringUtils.substringAfterLast(payEndDate.getText().toString(), "-")));
                dialog.show();
            }
        });
        payEndDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payEndDate.performClick();
            }
        });
        btnShowPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPriceZone.getVisibility() != View.VISIBLE){
                    currentPriceZone.setVisibility(View.VISIBLE);
                    showPriceTitle.setVisibility(View.INVISIBLE);
                    showPriceArrow.setImageResource(R.drawable.ico_expand_less);
                }
                else{
                    currentPriceZone.setVisibility(View.GONE);
                    showPriceTitle.setVisibility(View.VISIBLE);
                    showPriceArrow.setImageResource(R.drawable.ico_expand_more);
                }
            }
        });
        btnShowContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactZone.getVisibility() == View.VISIBLE){
                    contactZone.setVisibility(View.GONE);
                    ivShowContact.setImageResource(R.drawable.ico_expand_more_w);
                }
                else{
                    contactZone.setVisibility(View.VISIBLE);
                    ivShowContact.setImageResource(R.drawable.ico_expand_less_w);
                }
            }
        });
        btnTooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooltip.setVisibility(View.VISIBLE);
            }
        });
        btnCloseTooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooltip.setVisibility(View.INVISIBLE);
            }
        });
        requestCount.addTextChangedListener(new NumberTextWatcher(requestCount));
        requestPrice.addTextChangedListener(new NumberTextWatcher(requestPrice));
        btnConfirmContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtils.isEmpty(requestCount.getText().toString().trim()) || "0".equals(requestCount.getText().toString())){
                    ViewUtils.Companion.alertDialog(getActivity(), "수량을 입력하세요.", new Function0<Unit>() {
                        @Override
                        public Unit invoke() {
                            requestCount.requestFocus();
                            return null;
                        }
                    });

                    return;
                }
                if(StringUtils.isEmpty(requestPrice.getText().toString().trim()) || "0".equals(requestPrice.getText().toString())){
                    ViewUtils.Companion.alertDialog(getActivity(), "가격을 입력하세요.", new Function0<Unit>() {
                        @Override
                        public Unit invoke() {
                            requestPrice.requestFocus();
                            return null;
                        }
                    });

                    return;
                }
                GroupChannelActivity.groupChannelActivity.saveNegotiation(StringUtils.removeAll(requestCount.getText().toString().trim(),","),
                        StringUtils.removeAll(requestPrice.getText().toString().trim(),","),
                        StringUtils.removeAll(payEndDate.getText().toString(),"-"),
                        new Runnable() {
                            @Override
                            public void run() {

                                ViewUtils.Companion.alertDialog(getActivity(), "성공적으로 저장했습니다.", new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        contactZone.setVisibility(View.GONE);
                                        ivShowContact.setImageResource(R.drawable.ico_expand_more_w);

                                        return null;
                                    }
                                });

                            }
                        });
            }
        });
        btnCancelContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactZone.setVisibility(View.GONE);
                ivShowContact.setImageResource(R.drawable.ico_expand_more_w);
            }
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mMessageSendButton.setEnabled(true);
                    mMessageSendButton.setImageResource(R.drawable.ico_chat_send_w);
                } else {
                    mMessageSendButton.setEnabled(false);
                    mMessageSendButton.setImageResource(R.drawable.ico_chat_send);
                }
            }
        });

        mMessageSendButton.setEnabled(false);
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GroupChatFragment", "send onclick");

                if (mCurrentState == STATE_EDIT) {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0) {
                        if (mEditingMessage != null) {
                            editMessage(mEditingMessage, WebUtil.blockSpecialChars(userInput));
                        }
                    }
                    setState(STATE_NORMAL, null, -1);
                } else {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0) {
                        sendUserMessage(WebUtil.blockSpecialChars(userInput));
                        mMessageEditText.setText("");
                    }
                }
            }
        });

        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMedia();
            }
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    setTypingStatus(false);
                } else {
                    setTypingStatus(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setUpRecyclerView();
        setHasOptionsMenu(true);

        String userId = PreferenceUtils.getUserId();
        SyncManagerUtils.setup(getContext(), userId, new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                if (getActivity() == null) {
                    return;
                }

                ((BaseApplication)getActivity().getApplication()).setSyncManagerSetup(true);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createMessageCollection(mChannelUrl);
                        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, new ConnectionManager.ConnectionManagementHandler() {
                            @Override
                            public void onConnected(boolean reconnect) {
                                refresh();
                            }
                        });
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((GroupChannelActivity)getActivity()).apiListeners.add(new Runnable() {
            @Override
            public void run() {
                prePrice.setText(((GroupChannelActivity)getActivity()).channel.getR_DEAL_UPRC());
                prePrice.setText(numberFormat.format(Long.parseLong(prePrice.getText().toString())));

                count.setText(((GroupChannelActivity)getActivity()).channel.getDEAL_QTY());
                requestCount.setText(count.getText().toString());
                count.setText(numberFormat.format(Long.parseLong(count.getText().toString())));

                price.setText(((GroupChannelActivity)getActivity()).channel.getDEAL_UPRC());
                requestPrice.setText(price.getText().toString());
                price.setText(numberFormat.format(Long.parseLong(price.getText().toString())));

                totalAmount.setText(String.valueOf(Long.parseLong(StringUtils.removeAll(count.getText().toString(),","))
                        * Long.parseLong(StringUtils.removeAll(price.getText().toString(),","))));
                totalAmount.setText(numberFormat.format(Long.parseLong(totalAmount.getText().toString())));

                for(kr.co.koscom.omp.data.model.Member member : ((GroupChannelActivity)getActivity()).channel.getMembersList()){
                    if(PreferenceUtils.getUserId().equals(member.getUser_id())){
                        if("order".equals(member.getOrder_req())){
                            btnShowContact.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setupUI(mRootLayout);
            }
        }, 500);


    }

    public void setupUI(View view) {

        if (!(view instanceof EditText) && view.getId() != R.id.button_group_chat_send && view.getId() != R.id.btnConfirmContact) {
            Log.d("GroupChatFragment", "setupUI view : " + view);
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(v);
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideSoftKeyboard(View view) {
        mIMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        mChatAdapter.setContext(getActivity()); // Glide bug fix (java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity)

        // Gets channel from URL user requested

        Log.d(LOG_TAG, mChannelUrl);

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    List<Member> typingUsers = channel.getTypingMembers();
                    displayTyping(typingUsers);
                }
            }
        });
    }

    @Override
    public void onPause() {
        setTypingStatus(false);

        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Save messages to cache.
        if (mMessageCollection != null) {
            mMessageCollection.setCollectionHandler(null);
            mMessageCollection.remove();
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CHANNEL_URL, mChannelUrl);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set this as true to restore background connection management.
        SendBird.setAutoBackgroundDetection(true);

        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }

            sendFileWithThumbnail(data.getData());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((GroupChannelActivity)context).setOnBackPressedListener(new GroupChannelActivity.onBackPressedListener() {
            @Override
            public boolean onBack() {
                if (mCurrentState == STATE_EDIT) {
                    setState(STATE_NORMAL, null, -1);
                    return true;
                }

                mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                return false;
            }
        });
    }


    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
                        mMessageCollection.fetch(MessageCollection.Direction.NEXT, null);
                    }

                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                        mMessageCollection.fetch(MessageCollection.Direction.PREVIOUS, null);
                    }
                }
            }
        });
    }

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message) {
                if (mChatAdapter.isFailedMessage(message) && !mChatAdapter.isResendingMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }

                if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                    try {
                        UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                        startActivity(browserIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                // Load media chooser and removeSucceededMessages the failed message from list.
                if (mChatAdapter.isFailedMessage(message)) {
                    retryFailedMessage(message);
                    return;
                }

                // Message is sending. Do nothing on click event.
                if (mChatAdapter.isTempMessage(message)) {
                    return;
                }


                onFileMessageClicked(message);
            }
        });

        mChatAdapter.setItemLongClickListener(new GroupChatAdapter.OnItemLongClickListener() {
            @Override
            public void onUserMessageItemLongClick(UserMessage message, int position) {
                showMessageOptionsDialog(message, position);
            }

            @Override
            public void onFileMessageItemLongClick(FileMessage message) {
            }

            @Override
            public void onAdminMessageItemLongClick(AdminMessage message) {
            }
        });
    }

    private void showMessageOptionsDialog(final BaseMessage message, final int position) {
        String[] options;

        if (message.getMessageId() == 0) {
            options = new String[] { "Delete message" };
        } else {
            options = new String[] { "Edit message", "Delete message" };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    setState(STATE_EDIT, message, position);
                } else if (which == 1) {
                    deleteMessage(message);
                }
            }
        });
        builder.create().show();
    }

    private void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case STATE_NORMAL:
                mCurrentState = STATE_NORMAL;
                mEditingMessage = null;

                mUploadFileButton.setVisibility(View.VISIBLE);
                //mMessageSendButton.setText("SEND");
                mMessageEditText.setText("");
                break;

            case STATE_EDIT:
                mCurrentState = STATE_EDIT;
                mEditingMessage = editingMessage;

                mUploadFileButton.setVisibility(View.GONE);
                //mMessageSendButton.setText("SAVE");
                String messageString = ((UserMessage)editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    mMessageEditText.setSelection(0, messageString.length());
                }

                mMessageEditText.requestFocus();
                mMessageEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIMM.showSoftInput(mMessageEditText, 0);

                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(position);
                            }
                        }, 500);
                    }
                }, 100);
                break;
        }
    }

    private void createMessageCollection(final String channelUrl) {
        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    MessageCollection.create(channelUrl, mMessageFilter, mLastRead, new MessageCollectionCreateHandler() {
                        @Override
                        public void onResult(MessageCollection messageCollection, SendBirdException e) {
                            if (e == null) {
                                if (mMessageCollection == null) {
                                    mMessageCollection = messageCollection;
                                    mMessageCollection.setCollectionHandler(mMessageCollectionHandler);
                                    mChannel = mMessageCollection.getChannel();

                                    if (getActivity() == null) {
                                        return;
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mChatAdapter.setChannel(mChannel);
                                            updateActionBarTitle();
                                        }
                                    });

                                    fetchInitialMessages();
                                }
                            } else {
                                e.printStackTrace();

                                Toast.makeText(getContext(), "Failed to get channel.", Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().onBackPressed();
                                    }
                                }, 1000);
                            }
                        }
                    });
                } else {
                    mChannel = groupChannel;
                    mChatAdapter.setChannel(mChannel);
                    updateActionBarTitle();

                    if (mMessageCollection == null) {
                        mMessageCollection = new MessageCollection(groupChannel, mMessageFilter, mLastRead);
                        mMessageCollection.setCollectionHandler(mMessageCollectionHandler);

                        fetchInitialMessages();
                    }
                }
            }
        });
    }

    private void refresh() {
        if (mChannel != null) {
            mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    updateActionBarTitle();
                }
            });

            // call fetch(NEXT) to get missed message when app is offline.
            if (mMessageCollection != null) {
                mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);
            }
        } else {
            createMessageCollection(mChannelUrl);
        }
    }

    private void fetchInitialMessages() {
        if (mMessageCollection == null) {
            return;
        }

        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new CompletionHandler() {
            @Override
            public void onCompleted(SendBirdException e) {
                mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, new CompletionHandler() {
                    @Override
                    public void onCompleted(SendBirdException e) {
                        mMessageCollection.fetchFailedMessages(new CompletionHandler() {
                            @Override
                            public void onCompleted(SendBirdException e) {
                                if (getActivity() == null) {
                                    return;
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatAdapter.markAllMessagesAsRead();
                                        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getLastReadPosition(mLastRead), mRecyclerView.getHeight() / 2);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Retry?")
                .setPositiveButton("resend_message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                mChannel.resendUserMessage((UserMessage) message, new BaseChannel.ResendUserMessageHandler() {
                                    @Override
                                    public void onSent(UserMessage userMessage, SendBirdException e) {
                                        mMessageCollection.handleSendMessageResponse(userMessage, e);
                                    }
                                });
                            } else if (message instanceof FileMessage) {
                                Uri uri = mChatAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                })
                .setNegativeButton("delete_message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            if (message instanceof UserMessage) {
                                mMessageCollection.deleteMessage(message);
                            } else if (message instanceof FileMessage) {
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                }).show();
    }

    /**
     * Display which users are typing.
     * If more than two users are currently typing, this will state that "multiple users" are typing.
     *
     * @param typingUsers The list of currently typing users.
     */
    private void displayTyping(List<Member> typingUsers) {

        if (typingUsers.size() > 0) {
            mCurrentEventLayout.setVisibility(View.VISIBLE);
            String string;

            if (typingUsers.size() == 1) {
                string = typingUsers.get(0).getNickname() + " is typing";
            } else if (typingUsers.size() == 2) {
                string = typingUsers.get(0).getNickname() + " " + typingUsers.get(1).getNickname() + " is typing";
            } else {
                string = "Multiple users are typing";
            }
            mCurrentEventText.setText(string);
        } else {
            mCurrentEventLayout.setVisibility(View.GONE);
        }
    }

    private void requestMedia() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();

            // Pick images or videos
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType("*/*");
                String[] mimeTypes = {"image/*", "video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            } else {
                intent.setType("image/* video/*");
            }

            intent.setAction(Intent.ACTION_GET_CONTENT);

            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);

            // Set this as false to maintain connection
            // even when an external Activity is started.
            SendBird.setAutoBackgroundDetection(false);
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, "Storage access permissions are required to upload/download files.",
                    Snackbar.LENGTH_LONG)
                    .setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", message.getUrl());
            i.putExtra("type", message.getType());
            startActivity(i);
        } else if (type.startsWith("video")) {
            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
            intent.putExtra("url", message.getUrl());
            startActivity(intent);
        } else {
            showDownloadConfirmDialog(message);
        }
    }

    private void showDownloadConfirmDialog(final FileMessage message) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Download file?")
                    .setPositiveButton("download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                FileUtils.downloadFile(getActivity(), message.getUrl(), message.getName());
                            }
                        }
                    })
                    .setNegativeButton("cancel", null).show();
        }

    }

    private void updateActionBarTitle() {
        String title = "";

        if(mChannel != null) {
            title = TextUtils.getGroupChannelTitle(mChannel);
        }

        // Set action bar title to name of channel
        if (getActivity() != null) {
            ((GroupChannelActivity) getActivity()).setActionBarTitle(title);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void sendUserMessageWithUrl(final String text, String url) {
        if (mChannel == null) {
            return;
        }

        new WebUtils.UrlPreviewAsyncTask() {
            @Override
            protected void onPostExecute(UrlPreviewInfo info) {
                if (mChannel == null) {
                    return;
                }

                UserMessage tempUserMessage = null;
                BaseChannel.SendUserMessageHandler handler = new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            Log.e(LOG_TAG, e.toString());
                            Toast.makeText(
                                    getActivity(),
                                    "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                        }

                        mMessageCollection.handleSendMessageResponse(userMessage, e);
                    }
                };

                try {
                    // Sending a message with URL preview information and custom type.
                    String jsonString = info.toJsonString();
                    tempUserMessage = mChannel.sendUserMessage(text, jsonString, GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    // Sending a message without URL preview information.
                    tempUserMessage = mChannel.sendUserMessage(text, handler);
                }


                // Display a user message to RecyclerView
                if (mMessageCollection != null) {
                    mMessageCollection.appendMessage(tempUserMessage);
                }
            }
        }.execute(url);
    }

    private void sendUserMessage(String text) {
        if (mChannel == null) {
            return;
        }

        List<String> urls = WebUtils.extractUrls(text);
        if (urls.size() > 0) {
            sendUserMessageWithUrl(text, urls.get(0));
            return;
        }

        final UserMessage pendingMessage = mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                mMessageCollection.handleSendMessageResponse(userMessage, e);
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    Toast.makeText(getActivity(),"Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        if (mMessageCollection != null) {
            mMessageCollection.appendMessage(pendingMessage);
        }
    }

    /**
     * Notify other users whether the current user is typing.
     *
     * @param typing Whether the user is currently typing.
     */
    private void setTypingStatus(boolean typing) {
        if (mChannel == null) {
            return;
        }

        if (typing) {
            mChannel.startTyping();
        } else {
            mChannel.endTyping();
        }
    }

    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendFileWithThumbnail(Uri uri) {
        if (mChannel == null) {
            return;
        }

        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri);

        if (info == null) {
            Toast.makeText(getActivity(), "Extracting file information failed.", Toast.LENGTH_LONG).show();
            return;
        }

        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            Toast.makeText(getActivity(), "File must be located in local storage.", Toast.LENGTH_LONG).show();
        } else {
            BaseChannel.SendFileMessageWithProgressHandler progressHandler = new BaseChannel.SendFileMessageWithProgressHandler() {
                @Override
                public void onProgress(int bytesSent, int totalBytesSent, int totalBytesToSend) {
                    FileMessage fileMessage = mFileProgressHandlerMap.get(this);
                    if (fileMessage != null && totalBytesToSend > 0) {
                        int percent = (totalBytesSent * 100) / totalBytesToSend;
                        mChatAdapter.setFileProgressPercent(fileMessage, percent);
                    }
                }

                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        // removeSucceededMessages preview message from MessageCollection
                        if (mMessageCollection != null) {
                            mMessageCollection.deleteMessage(fileMessage);
                        }

                        // add failed message to adapter
                        mChatAdapter.insertFailedMessages(Collections.singletonList((BaseMessage) fileMessage));
                        return;
                    }

                    // append sent message.
                    if (mMessageCollection != null) {
                        mMessageCollection.appendMessage(fileMessage);
                    }
                }
            };

            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, progressHandler);

            mFileProgressHandlerMap.put(progressHandler, tempFileMessage);

            mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);

            if (mMessageCollection != null) {
                mMessageCollection.appendMessage(tempFileMessage);
            }
        }
    }

    private void editMessage(final BaseMessage message, String editedMessage) {
        if (mChannel == null) {
            return;
        }

        mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new BaseChannel.UpdateUserMessageHandler() {
            @Override
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(getActivity(), "Error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMessageCollection != null) {
                    mMessageCollection.updateMessage(userMessage);
                }
            }
        });
    }

    /**
     * Deletes a message within the channel.
     * Note that users can only delete messages sent by oneself.
     *
     * @param message The message to delete.
     */
    private void deleteMessage(final BaseMessage message) {
        if (message.getMessageId() == 0) {
            mMessageCollection.deleteMessage(message);
        } else {
            if (mChannel == null) {
                return;
            }

            mChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        Toast.makeText(getActivity(), "Error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMessageCollection.deleteMessage(message);
                }
            });
        }
    }

    private void updateLastSeenTimestamp(List<BaseMessage> messages) {
        long lastSeenTimestamp = mLastRead == Long.MAX_VALUE ? 0 : mLastRead;
        for (BaseMessage message : messages) {
            if (lastSeenTimestamp < message.getCreatedAt()) {
                lastSeenTimestamp = message.getCreatedAt();
            }
        }

        if (lastSeenTimestamp > mLastRead) {
            PreferenceUtils.setLastRead(mChannelUrl, lastSeenTimestamp);
            mLastRead = lastSeenTimestamp;
        }
    }

    private boolean isTarget(BaseMessage message){
        Log.d(GroupChatAdapter.class.getSimpleName(), "message("+message.getMentionedUsers().size()+") : " + ToStringBuilder.reflectionToString(message));

        boolean isTarget = false;

        if(message instanceof AdminMessage){

            //isTarget = true;
            if(message.getMentionedUsers() != null && message.getMentionedUsers().size() > 0){
                for (User user : message.getMentionedUsers()) {
                    Log.d("GroupChatAdapter", "getMentionedUsers user : " + ToStringBuilder.reflectionToString(user));
                    if(PreferenceUtils.getUserId().equals(user.getUserId())){
                        isTarget = true;
                        break;
                    }
                }
            }else{
                isTarget = true;
            }
        }
        else{
            isTarget = true;
        }

        return isTarget;
    }

    private MessageCollectionHandler mMessageCollectionHandler = new MessageCollectionHandler() {
        @Override
        public void onMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
        }

        @Override
        public void onSucceededMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onSucceededMessageEvent: size = " + messages.size() + ", action = " + action);
            Log.d("SyncManager", "onSucceededMessageEvent: messages = " + messages);

            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertSucceededMessages(messages);
                            mChatAdapter.markAllMessagesAsRead();
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;

                        case UPDATE:
                            mChatAdapter.updateSucceededMessages(messages);
                            break;

                        case CLEAR:
                            mChatAdapter.clear();
                            break;
                    }
                }
            });

            updateLastSeenTimestamp(messages);
        }

        @Override
        public void onPendingMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onPendingMessageEvent: size = " + messages.size() + ", action = " + action);
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertSucceededMessages(messages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;
                    }
                }
            });
        }

        @Override
        public void onFailedMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action, final FailedMessageEventActionReason reason) {
            Log.d("SyncManager", "onFailedMessageEvent: size = " + messages.size() + ", action = " + action);
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertFailedMessages(messages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeFailedMessages(messages);
                            break;
                        case UPDATE:
                            if (reason == FailedMessageEventActionReason.UPDATE_RESEND_FAILED) {
                                mChatAdapter.updateFailedMessages(messages);
                            }
                            break;
                    }
                }
            });
        }
    };
}
