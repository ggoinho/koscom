package kr.co.koscom.omp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.scsoft.boribori.data.viewmodel.ChatViewModel;
import com.sendbird.syncmanager.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kr.co.koscom.omp.data.Injection;
import kr.co.koscom.omp.data.ViewModelFactory;
import kr.co.koscom.omp.data.model.Channel;
import kr.co.koscom.omp.data.model.Response;
import kr.co.koscom.omp.view.ViewUtils;

/**
 * 협상채팅
 */

public class GroupChannelActivity extends AppCompatActivity {

    public static GroupChannelActivity groupChannelActivity;

    private Handler mHandler = new Handler();

    private ViewModelFactory viewModelFactory = null;

    public ChatViewModel chatViewModel = null;

    private CompositeDisposable disposable = new CompositeDisposable();

    private DrawerLayout drawer_layout;
    private ConstraintLayout defaultToolbar;
    private AppCompatImageView btnBack;
    private AppCompatTextView tvTitle;
    private ImageView btnSetting;
    private ImageView btnSearch;
    private LinearLayoutCompat searchToolbar;
    private AppCompatEditText inputSearch;
    private AppCompatImageView backSearch;
    private AppCompatImageView clearSearch;
    private AppCompatImageView search;


    public String channelTitle = null;
    public String channelUrl = null;
    public String orderNo = null;
    public Channel channel = null;
    public String myDealTp = "";
    public List<Runnable> apiListeners = new ArrayList();

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupChannelActivity = this;

        setContentView(R.layout.activity_drawer_chat);

        channelTitle = getIntent().getStringExtra("groupChannelTitle");
        channelUrl = getIntent().getStringExtra("groupChannelUrl");
        orderNo = getIntent().getStringExtra("orderNo");

        viewModelFactory = Injection.INSTANCE.provideViewModelFactory(this);
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel.class);

        drawer_layout = findViewById(R.id.drawer_layout);
         defaultToolbar = findViewById(R.id.defaultToolbar);
         btnBack = findViewById(R.id.btnBack);
         tvTitle = findViewById(R.id.tvTitle);
         btnSetting = findViewById(R.id.btnSetting);
         btnSearch = findViewById(R.id.btnSearch);
         searchToolbar = findViewById(R.id.searchToolbar);
         backSearch = findViewById(R.id.backSearch);
         inputSearch = findViewById(R.id.inputSearch);
         clearSearch = findViewById(R.id.clearSearch);
         search = findViewById(R.id.search);

         btnSetting.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 drawer_layout.openDrawer(GravityCompat.END);
             }
         });
         btnBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 onBackPressed();
             }
         });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(defaultToolbar.getVisibility() == View.VISIBLE){
                    defaultToolbar.setVisibility(View.GONE);
                    searchToolbar.setVisibility(View.VISIBLE);
                }
                else{
                    defaultToolbar.setVisibility(View.VISIBLE);
                    searchToolbar.setVisibility(View.GONE);
                }
            }
        });
        backSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                defaultToolbar.setVisibility(View.VISIBLE);
                searchToolbar.setVisibility(View.GONE);
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearch.setText("");
            }
        });

        if (savedInstanceState == null) {

            if(channelUrl != null) {
                // If started from notification
                Fragment fragment = GroupChatFragment.newInstance(channelUrl);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.container_group_channel, fragment)
                        .commit();
            }
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SettingChatFragment fragment = new SettingChatFragment();
            transaction.replace(R.id.nav_view, fragment);
            transaction.commitAllowingStateLoss();
        }

        findViewById(R.id.nav_view).postDelayed(new Runnable() {
            @Override
            public void run() {
                disposable.add(chatViewModel.openChannel(PreferenceUtils.getUserId(),orderNo,channelUrl,channelTitle)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Channel>() {
                            @Override
                            public void accept(Channel channel) throws Exception {
                                Log.d(GroupChannelActivity.class.getSimpleName(), "channel : " + channel.toString());

                                if("0000".equals(channel.getRCode())){

                                    tvTitle.setText(channel.getCHANNEL_TITLE());

                                    GroupChannelActivity.this.channel = channel;

                                    for (Runnable listener: apiListeners) {
                                        listener.run();
                                    }
                                }
                                else{

                                    ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, channel.getRCode(), channel.getRMsg());

                                }

                                apiListeners.clear();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                apiListeners.clear();
                                throwable.printStackTrace();
                                ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                                    @Override
                                    public Unit invoke() {
                                        return null;
                                    }
                                });
                            }
                        }));
            }
        }, 500);


    }

    public void saveNegotiation(String count, String price, String payEndDate, final Runnable listener){

        disposable.add(chatViewModel.saveNegotiation(PreferenceUtils.getUserId(),orderNo,channelUrl,count, price, payEndDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                        if("0000".equals(response.getRCode())){

                            if(listener != null){
                                listener.run();
                            }
                        }
                        else{

                            ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                        }
                        apiListeners.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        apiListeners.clear();
                        throwable.printStackTrace();
                        ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        });
                    }
                }));
    }

    public void acceptNegotiation(String number, final Runnable listener){

        disposable.add(chatViewModel.acceptNegotiation(PreferenceUtils.getUserId(),orderNo,channelUrl,number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                        if("0000".equals(response.getRCode())){

                            if(listener != null){
                                listener.run();
                            }
                        }
                        else{

                            ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                        }
                        apiListeners.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        apiListeners.clear();
                        throwable.printStackTrace();
                        ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        });
                    }
                }));
    }

    public void denyNegotiation(String number, final Runnable listener){

        disposable.add(chatViewModel.denyNegotiation(PreferenceUtils.getUserId(),orderNo,channelUrl,number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                        if("0000".equals(response.getRCode())){

                            if(listener != null){
                                listener.run();
                            }
                        }
                        else{

                            ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                        }
                        apiListeners.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        apiListeners.clear();
                        throwable.printStackTrace();
                        ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        });
                    }
                }));
    }

    public void requestPaper(final Runnable listener){

        Log.d("GroupChannelActivity", "myDealTp : " + myDealTp);

        if("10".equals(myDealTp)){
            disposable.add(chatViewModel.requestPaperOfSeller(PreferenceUtils.getUserId(),orderNo,channelUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response>() {
                        @Override
                        public void accept(Response response) throws Exception {
                            Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                            if("0000".equals(response.getRCode())){

                                if(listener != null){
                                    listener.run();
                                }
                            }
                            else{

                                ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                            }
                            apiListeners.clear();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            apiListeners.clear();
                            throwable.printStackTrace();
                            ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                                @Override
                                public Unit invoke() {
                                    return null;
                                }
                            });
                        }
                    }));
        }
        else{
            disposable.add(chatViewModel.requestPaperOfBuyer(PreferenceUtils.getUserId(),orderNo,channelUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Response>() {
                        @Override
                        public void accept(Response response) throws Exception {
                            Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                            if("0000".equals(response.getRCode())){

                                if(listener != null){
                                    listener.run();
                                }
                            }
                            else{

                                ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                            ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                                @Override
                                public Unit invoke() {
                                    return null;
                                }
                            });
                        }
                    }));
        }
    }

    public void exitChannel(final Runnable listener){

        disposable.add(chatViewModel.exitChannel(PreferenceUtils.getUserId(),orderNo,channelUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response response) throws Exception {
                        Log.d(GroupChannelActivity.class.getSimpleName(), "response : " + response.toString());

                        if("0000".equals(response.getRCode())){

                            if(listener != null){
                                listener.run();
                            }
                        }
                        else{

                            ViewUtils.Companion.showErrorMsg(GroupChannelActivity.this, response.getRCode(), response.getRMsg());
                        }
                        apiListeners.clear();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        apiListeners.clear();
                        throwable.printStackTrace();
                        ViewUtils.Companion.alertDialog(GroupChannelActivity.this, "네트워크상태를 확인해주세요.", new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                return null;
                            }
                        });
                    }
                }));
    }

    public void hideNavigation(){
        drawer_layout.closeDrawer(GravityCompat.END);
    }

    interface onBackPressedListener {
        boolean onBack();
    }
    private onBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(onBackPressedListener listener) {
        mOnBackPressedListener = listener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        disposable.clear();
    }

    @Override
    public void onBackPressed() {
        if (mOnBackPressedListener != null && mOnBackPressedListener.onBack()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void setActionBarTitle(String title) {
        /*if (tvTitle != null) {
            tvTitle.setText(title);
        }*/
    }
}
