package com.vomont.vlink.manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.img.ImagDetailActivity;
import com.vomont.vlink.manager.adapter.FileManageAdapter;
import com.vomont.vlink.manager.bean.ItemFile;
import com.vomont.vlink.manager.bean.ManageFile;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.AnimationUtil;
import com.vomont.vlink.util.DensityUtil;
import com.vomont.vlink.util.VlinkRealPlayUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class FileManageActivity extends BaseActivity {


    @BindView(R.id.manage_file_list)
    RecyclerView manage_file_list;

    @BindView(R.id.top_name)
    TextView top_name;

    @BindView(R.id.top_right_name)
    TextView top_right_name;

    @BindView(R.id.top_back)
    ImageView top_back;

    @BindView(R.id.manage_file_buttom)
    LinearLayout manage_file_buttom;

    @BindView(R.id.manage_file_delete)
    RelativeLayout manage_file_delete;

    @BindView(R.id.manage_file_select)
    RelativeLayout manage_file_select;

    @BindView(R.id.manage_file_share)
    RelativeLayout manage_file_share;

    @BindView(R.id.manage_file_img)
    ImageView manage_file_img;

    private ACache aCache;

    UserInfo userLogin;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlinker";
    private VlinkRealPlayUtil util;

    private List<String> files;

    private List<ManageFile> mlist;

    private FileManageAdapter adapter;

    private boolean isEdit = true;

    private boolean isChoose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        top_back.setVisibility(View.VISIBLE);
        top_right_name.setVisibility(View.VISIBLE);
        top_right_name.setText("编辑");
        top_name.setText("文件管理");
        util = new VlinkRealPlayUtil(this);
        aCache = ACache.get(this);
        userLogin = (UserInfo) aCache.getAsObject(ACaCheContrast.user);
        util.setPath(path + "/" + userLogin.getNum() + "/img/");
        if (util.getImageName() != null) {
            files = util.getImageName();
        } else {
            files = new ArrayList<>();
        }
        List<String> dates = new ArrayList<>();
        mlist = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String[] str = files.get(i).split("/");
            if (!dates.contains(str[str.length - 1].substring(0, 8))) {
                dates.add(str[str.length - 1].substring(0, 8));
            }
        }
        for (int i = 0; i < dates.size(); i++) {
            ManageFile manageF = new ManageFile();
            List<ItemFile> itemFiles = new ArrayList<>();
            for (String name : files) {
                String[] str = name.split("/");
                if (dates.get(i).equals(str[str.length - 1].substring(0, 8))) {
                    ItemFile item = new ItemFile();
                    item.setName(name);
                    item.setChoose(false);
                    itemFiles.add(item);
                }
            }

            manageF.setDate(dates.get(i));
            manageF.setItemFiles(itemFiles);
            mlist.add(manageF);
        }
        adapter = new FileManageAdapter(this);
        manage_file_list.setLayoutManager(new LinearLayoutManager(this));
        manage_file_list.setAdapter(adapter);
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
        manage_file_list.setHasFixedSize(true);
        adapter.setOnPicListener(new FileManageAdapter.OnPicListener() {
            @Override
            public void OnClick(int groupPosition, int childPosition) {
                if (!isEdit) {
                    //编辑
                    boolean result = mlist.get(groupPosition).getItemFiles().get(childPosition).isChoose();
                    mlist.get(groupPosition).getItemFiles().get(childPosition).setChoose(!result);
                    adapter.setData(mlist);
                    adapter.notifyItemChanged(groupPosition);
                    manage_file_list.setHasFixedSize(true);
                } else {
                    //预览
                    if (util.getImageName() != null) {
                        files = util.getImageName();
                    } else {
                        files = new ArrayList<>();
                    }
                for(int i=0;i<files.size();i++) {
                    if (files.get(i).equals(mlist.get(groupPosition).getItemFiles().get(childPosition).getName()))
                    {
                        Intent intent=new Intent(FileManageActivity.this, ImagDetailActivity.class);
                        intent.putExtra("pic",(Serializable) files);
                        intent.putExtra("position",i);
                        startActivity(intent);
                        break;
                    }
                }

                }
            }
        });
    }

    @OnClick({R.id.top_back, R.id.top_right_name, R.id.manage_file_delete, R.id.manage_file_select,R.id.manage_file_share})
    public void setOnClick(View v) {
        switch (v.getId()) {
            case R.id.top_back:
                //返回
                finish();
                break;
            case R.id.top_right_name:
                //编辑
                if (isEdit) {
                    top_right_name.setText("取消");
                    AnimationUtil.buttomTotop(manage_file_buttom);
                    manage_file_buttom.setVisibility(View.VISIBLE);
                } else {
                    top_right_name.setText("编辑");
                    AnimationUtil.topToButtom(manage_file_buttom);
                    manage_file_buttom.setVisibility(View.GONE);
                    for (ManageFile manageFile : mlist) {
                        for (ItemFile itemFile : manageFile.getItemFiles()) {
                            itemFile.setChoose(false);
                        }
                    }
                    adapter.setData(mlist);
                    adapter.notifyDataSetChanged();
                }
                isEdit = !isEdit;
                break;
            case R.id.manage_file_delete:
                if(mlist!=null&&mlist.size()!=0) {
                    //删除文件
                    new CircleDialog.Builder(this)
                            .setWidth(0.6f)
                            .configDialog(new ConfigDialog() {
                                @Override
                                public void onConfig(DialogParams params) {

                                }
                            })
                            .setText("确认删除文件")
                            .configText(new ConfigText() {
                                @Override
                                public void onConfig(TextParams params) {
                                    params.textSize= DensityUtil.dip2px(FileManageActivity.this,18);
                                    params.height=DensityUtil.dip2px(FileManageActivity.this,60);
                                    params.gravity= Gravity.CENTER;
                                }
                            })
                            .setNegative("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteFile();
                                }
                            })
                            .configNegative(new ConfigButton() {
                                @Override
                                public void onConfig(ButtonParams params) {
                                    params.textSize= DensityUtil.dip2px(FileManageActivity.this,16);
                                    params.height=DensityUtil.dip2px(FileManageActivity.this,60);
                                }
                            })
                            .setPositive("取消",null)
                            .configPositive(new ConfigButton() {
                                @Override
                                public void onConfig(ButtonParams params) {
                                    params.textSize= DensityUtil.dip2px(FileManageActivity.this,16);
                                    params.height=DensityUtil.dip2px(FileManageActivity.this,60);
                                }
                            })
                            .show();
                }
                break;
            case R.id.manage_file_select:
                //全选
                if(mlist!=null&&mlist.size()!=0) {
                    if (!isChoose) {
                        for(int i=0;i<mlist.size();i++)
                        {
                            for(int j=0;j<mlist.get(i).getItemFiles().size();j++)
                            {
                                mlist.get(i).getItemFiles().get(j).setChoose(true);
                            }
                            adapter.notifyItemChanged(i);
                        }
                        manage_file_img.setImageResource(R.mipmap.img_checked);
                    } else {
                        for(int i=0;i<mlist.size();i++)
                        {
                            for(int j=0;j<mlist.get(i).getItemFiles().size();j++)
                            {
                                mlist.get(i).getItemFiles().get(j).setChoose(false);
                            }
                            adapter.notifyItemChanged(i);
                        }
                        manage_file_img.setImageResource(R.mipmap.mange_select);
                    }
                    adapter.setData(mlist);
                    adapter.notifyDataSetChanged();
                    isChoose = !isChoose;
                    manage_file_list.setHasFixedSize(true);
                }
                break;
            case R.id.manage_file_share:
                shareImage();
                break;
            default:
                break;
        }
    }


    /**
     * 多图片分享
     */
    private void  shareImage()
    {
        List<File> shareFiles=new ArrayList<>();
        Iterator<ManageFile> it = mlist.iterator();
        while (it.hasNext()) {
            ManageFile x = it.next();
            Iterator<ItemFile> itemFileIterator = x.getItemFiles().iterator();
            while (itemFileIterator.hasNext()) {
                ItemFile itemFile = itemFileIterator.next();
                if (itemFile.isChoose()) {
                    File file = new File(itemFile.getName());
                    shareFiles.add(file);
                }
            }
        }
        if(shareFiles.size()!=0)
        {
            ArrayList<Uri> imageUris = new ArrayList<>();
            for(File file:shareFiles)
            {
                Uri uri = Uri.fromFile(file);
                imageUris.add(uri);
            }
            Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            mulIntent.setType("image/*");
            startActivity(Intent.createChooser(mulIntent,"分享图片"));
        }
        else
        {
            Toast.makeText(this,"请选择需要分享的图片！",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFile() {
        Iterator<ManageFile> it = mlist.iterator();
        while (it.hasNext()) {
            ManageFile x = it.next();
            Iterator<ItemFile> itemFileIterator = x.getItemFiles().iterator();
            while (itemFileIterator.hasNext()) {
                ItemFile itemFile = itemFileIterator.next();
                if (itemFile.isChoose()) {
                    File file = new File(itemFile.getName());
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    itemFileIterator.remove();
                }
            }
        }
        Iterator<ManageFile> manageFileIterator = mlist.iterator();
        while (manageFileIterator.hasNext()) {
            if (manageFileIterator.next().getItemFiles().size() == 0) {
                manageFileIterator.remove();
            }
        }
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
    }
}
