package com.hy.tvmeidaview;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.hy.tvmeidaview.utils.FileServer;
import com.hy.tvmeidaview.utils.SPUtils;
import com.hy.tvmeidaview.view.Movie;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import it.sauronsoftware.ftp4j.FTPClient;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbSession;

public class MainFragment extends BrowseSupportFragment {


//    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayObjectAdapter mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    private static SimpleBackgroundManager simpleBackgroundManager = null;


    private String HOST = "192.168.0.118";
    private String PORT = "2121";
    private String USERNAME = "user";
    private String PASSWORD = "123";
    private String Folder = "123";

    private FTPClient client;

    List<Movie> list_movie = new ArrayList<>();

    String str_folder;

    private Map<String, SmbFile[]> filesArray = new HashMap<>();

    private List<SmbFile[]> list_all = new ArrayList<>();

    public FileServer fileServer = new FileServer();


//    HeaderItem cardPresenterHeader = new HeaderItem(0, "分类 1");
//    CardPresenter cardPresenter = new CardPresenter();
//    ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        HOST = SPUtils.getInstance().getString("HOST", "");
        PORT = SPUtils.getInstance().getString("PORT", "");
        USERNAME = SPUtils.getInstance().getString("USER", "");
        PASSWORD = SPUtils.getInstance().getString("PWD", "");
        Folder = SPUtils.getInstance().getString("FOLDER", "");

        setupUIElements();

        loadRows();

        setupEventListeners();

        simpleBackgroundManager = new SimpleBackgroundManager(getActivity());

        fileServer.start();
        getMovie();
    }


    private void getMovie() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                SmbFile mRootFolder = smbLogin();
                if (mRootFolder == null) {
//                    Log.e("内容为空", "=+++");
                    return;
                }
                SmbFile[] files = null;
                try {
                    files = mRootFolder.listFiles();
                } catch (SmbException e) {
                    e.printStackTrace();
                }

                filesArray.clear();

                if (files != null) {
                    for (SmbFile smbfile : files) {
                        boolean isDirectory = false;
                        try {
                            isDirectory = smbfile.isDirectory();
                        } catch (SmbException e) {
                            e.printStackTrace();
                        }
                        if (isDirectory) {
                            SmbFile[] fs = null;
                            try {
                                fs = smbfile.listFiles();
                            } catch (SmbException e) {
                                e.printStackTrace();
                            }
                            if (fs != null && fs.length != 0) {
                                Log.e("run: ", smbfile.getName());
                                if (smbfile.getName().contains(Folder)) {
                                    str_folder = smbfile.getName();
                                    filesArray.put(str_folder, fs);
                                }
                            }
                        }


                    }

                }

                SmbFile[] smbFiles = filesArray.get(str_folder);
                List<String> list_title = new ArrayList<>();

                for (SmbFile file : smbFiles) {
//                    Log.e("子列表: ", file.getName());
                    try {
                        list_all.add(file.listFiles());
                        list_title.add(file.getName());
                    } catch (SmbException e) {
                        e.printStackTrace();
                    }

                }
                for (int i = 0; i < list_all.size(); i++) {
                    SmbFile[] smbFiles1 = list_all.get(i);
//                    Log.e("标题: ", list_title.get(i).replace("/", ""));
                    cardHeard = "cardPresenterHeader1" + i;
                    cardPresenters = "cardPresenter1" + i;
                    cardRowAAdatper = "xxxx" + i;
                    final HeaderItem cardHeard = new HeaderItem(i, list_title.get(i).replace("/", ""));
                    CardPresenter cardPresenters = new CardPresenter();
                    final ArrayObjectAdapter cardRowAAdatper = new ArrayObjectAdapter(cardPresenters);
                    for (SmbFile smbFile : smbFiles1) {
//                        Log.e("run: 子子列表", smbFile.getName());
                        Movie movie = new Movie();
                        if (smbFile.getName().endsWith("mp4") | smbFile.getName().endsWith("avi")) {
                            movie.setTitle(smbFile.getName());
                            for (SmbFile smbFile2 : smbFiles1) {
                                Log.e("zizizizi", smbFile2.getName());
                                if ((smbFile2.getName().endsWith("png") | smbFile2.getName().endsWith("jpg")) & smbFile2.getName().substring(0, smbFile2.getName().indexOf(".")).equals(movie.getTitle().substring(0, smbFile2.getName().indexOf(".")))) {
                                    movie.setCardImageUrl(fileServer.getURL(smbFile2));
                                    Log.e("run: 子子列表", movie.getCardImageUrl());
                                }
                            }
                        }
                        movie.setStudio("studio");
                        movie.setUrl(fileServer.getURL(smbFile));
                        cardRowAAdatper.add(movie);
                    }
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRowsAdapter.add(new ListRow(cardHeard, cardRowAAdatper));
                        }
                    });
                }
            }
        }.start();
        setAdapter(mRowsAdapter);
    }

    String cardHeard, cardPresenters, cardRowAAdatper;

    private void setupEventListeners() {
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {


//                Intent intent = new Intent(getContext(), MoviePlayerActivity.class);
//                intent.putExtra("xx", row.getHeaderItem().getName());
//                startActivity(intent);
                if (item instanceof Movie) {
                    Intent intent = new Intent(getContext(), MoviePlayerActivity.class);
                    intent.putExtra("xx", (Serializable) item);
                    startActivity(intent);
                }

            }
        });
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });


    }

    private SmbFile smbLogin() {

        if (HOST == null)
            return null;
        String rootPath = "smb://" + HOST + "/";
        SmbFile mRootFolder = null;
        Log.e("smbLogin: ", HOST);
        try {
            UniAddress mDomain = UniAddress.getByName(HOST);
            NtlmPasswordAuthentication mAuthentication = new NtlmPasswordAuthentication(HOST, USERNAME, PASSWORD);
            SmbSession.logon(mDomain, mAuthentication);
            mRootFolder = new SmbFile(rootPath, mAuthentication);
        } catch (UnknownHostException e) {
            Log.d("=============", "异常");
            e.printStackTrace();
        } catch (SmbException e) {
            Log.d("=============", "异常");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Log.d("=============", "异常");
            e.printStackTrace();
        }
        return mRootFolder;
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof String) {                    // GridItemPresenter
                simpleBackgroundManager.clearBackground();
            } else if (item instanceof Movie) {              // CardPresenter
                simpleBackgroundManager.updateBackground(getActivity().getDrawable(R.drawable.movie));
            }
        }
    }

    private void setupUIElements() {
//         setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.bg_circle_red));
//        setTitle("Hello Android TV!"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

    }


    private void loadRows() {


//        /* GridItemPresenter */
//        HeaderItem gridItemPresenterHeader = new HeaderItem(0, "GridItemPresenter");
//
//        GridItemPresenter mGridPresenter = new GridItemPresenter();
//        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
////        gridRowAdapter.add("ITEM 1");
////        gridRowAdapter.add("ITEM 2");
////        gridRowAdapter.add("ITEM 3");
//        mRowsAdapter.add(new ListRow(gridItemPresenterHeader, gridRowAdapter));

        /* CardPresenter */

        /* CardPresenter */
//        HeaderItem cardPresenterHeader1 = new HeaderItem(1, "分类 2");
//        CardPresenter cardPresenter1 = new CardPresenter();
//        ArrayObjectAdapter cardRowAdapter1 = new ArrayObjectAdapter(cardPresenter1);


//        for (int i = 0; i < 10; i++) {
//            Movie movie = new Movie();
//            movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//            movie.setTitle("title" + i);
//            movie.setStudio("studio" + i);
//            cardRowAdapter1.add(movie);
//        }
//
//        mRowsAdapter.add(new ListRow(cardPresenterHeader1, cardRowAdapter1));
//
//
//        /* CardPresenter */
//        HeaderItem cardPresenterHeader2 = new HeaderItem(2, "分类 3");
//        CardPresenter cardPresenter2 = new CardPresenter();
//        ArrayObjectAdapter cardRowAdapter2 = new ArrayObjectAdapter(cardPresenter2);
//
//
//        for (int i = 0; i < 10; i++) {
//            Movie movie = new Movie();
//            movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//            movie.setTitle("title" + i);
//            movie.setStudio("studio" + i);
//            cardRowAdapter2.add(movie);
//        }
//
//        mRowsAdapter.add(new ListRow(cardPresenterHeader2, cardRowAdapter2));
//
//        /* CardPresenter */
//        HeaderItem cardPresenterHeader3 = new HeaderItem(3, "分类 4");
//        CardPresenter cardPresenter3 = new CardPresenter();
//        ArrayObjectAdapter cardRowAdapter3 = new ArrayObjectAdapter(cardPresenter3);
//
//
//        for (int i = 0; i < 10; i++) {
//            Movie movie = new Movie();
//            movie.setCardImageUrl("http://heimkehrend.raindrop.jp/kl-hacker/wp-content/uploads/2014/08/DSC02580.jpg");
//            movie.setTitle("title" + i);
//            movie.setStudio("studio" + i);
//            cardRowAdapter3.add(movie);
//        }
//
//        mRowsAdapter.add(new ListRow(cardPresenterHeader3, cardRowAdapter3));

//        setAdapter(mRowsAdapter);
        /* Set */

    }


    /**
     * from AOSP sample source code AOSP
     * GridItemPresenter class. Show TextView with item type String.
     */
    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }

    }


    protected void showAddDialog() {

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.layout_dialog_edit, null);
        final EditText edit_ip = textEntryView.findViewById(R.id.edit_ip);
        final EditText edit_port = textEntryView.findViewById(R.id.edit_port);
        final EditText edit_user = textEntryView.findViewById(R.id.edit_user);
        final EditText edit_pwd = textEntryView.findViewById(R.id.edit_pwd);
        final EditText edit_folder = textEntryView.findViewById(R.id.edit_folder);
        edit_ip.setText(HOST);
        edit_port.setText(PORT);
        edit_user.setText(USERNAME);
        edit_pwd.setText(PASSWORD);
        edit_folder.setText(Folder);


        AlertDialog.Builder ad1 = new AlertDialog.Builder(getContext());
        ad1.setTitle("输入地址:");
        ad1.setIcon(android.R.drawable.ic_dialog_info);
        ad1.setView(textEntryView);

        ad1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                SPUtils.getInstance().put("HOST", edit_ip.getText().toString().trim());
                SPUtils.getInstance().put("PORT", edit_port.getText().toString().trim());
                SPUtils.getInstance().put("USER", edit_user.getText().toString().trim());
                SPUtils.getInstance().put("PWD", edit_pwd.getText().toString().trim());
                SPUtils.getInstance().put("FOLDER", edit_folder.getText().toString().trim());
                HOST = edit_ip.getText().toString().trim();
                PORT = edit_port.getText().toString().trim();
                USERNAME = edit_user.getText().toString().trim();
                PASSWORD = edit_pwd.getText().toString().trim();
                Folder = edit_folder.getText().toString().trim();


                getMovie();
            }
        });
        ad1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad1.show();// 显示对话框

    }

    @Override
    public void onDestroy() {
        fileServer.release();
        super.onDestroy();
    }
}