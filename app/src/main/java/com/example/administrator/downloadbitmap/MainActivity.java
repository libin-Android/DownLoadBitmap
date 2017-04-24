package com.example.administrator.downloadbitmap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import utils.FileCache;
import utils.HttpUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Button openResource = (Button) findViewById(R.id.openResource);
        Button createFolder = (Button) findViewById(R.id.createFolder);
        openResource.setOnClickListener(this);
        createFolder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openResource:
                openResourceMethod();
                break;
            case R.id.createFolder:
                createFolderMethod();
                break;
        }
    }

    private void createFolderMethod() {
        // 找一个用来缓存图片的路径
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                    "文件夹AAA");
        } else {
            cacheDir = this.getCacheDir();
        }
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        createFolderMethod2(cacheDir);
    }

    private void createFolderMethod2(File cacheDir) {
        //给每个图片创建一个文件夹
        File folder = new File(cacheDir, "内部文件夹");
        if (!folder.exists()) {
            folder.mkdirs();//此方法必须有，没有文件夹创建不成功
        }
        //创建文件
        File f = new File(folder, "aaa.txt");
        if (!f.exists()) {
            try {
                f.createNewFile();//此方法必须有，没有文件创建不成功
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载图片
     *
     * @param data
     */
    private void downLoadBitmapMethod(final List<String> data) {
        FileCache fileCache = new FileCache(this);
        for (int i = 0; i < data.size(); i++) {
            final String s = data.get(i);
            final File file = fileCache.getFile(s);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtils.CopyStream(s, file);
                }
            }).start();
        }
    }

    /**
     * 打开文件管理器
     */
    private void openResourceMethod() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择下载文件"), 2);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    /**
     * 文件返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileCache.getPath(this, uri);
                    if (path != null) {
                        Log.e("onActivityResult: ", path.toString());
                        Toast.makeText(this, "获取路径成功", Toast.LENGTH_SHORT).show();
                        List<String> list = FileCache.ReadTxtFile(path);//读取文件内容
                        downLoadBitmapMethod(list);
                    } else {
                        Log.e("onActivityResult: ", "不是txt的文件");
                        Toast.makeText(this, "不是txt的文件，无法下载", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
