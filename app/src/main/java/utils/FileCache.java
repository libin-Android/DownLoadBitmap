package utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/29.
 */
public class FileCache {
    private File cacheDir;

    public FileCache(Context context) {
        // 找一个用来缓存图片的路径
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                    "文件夹名称");
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    /**
     * "http://img04.tooopen.com/images/20130712/tooopen_17270713.jpg"
     * http://pic.beifabook.com/Book/B\7533\28\B753328850.GIF
     * 创建文件
     *
     * @param url
     * @return
     */
    public File getFile(String url) {
        //截取/
        String[] split = url.split("/");
        int length1 = split.length;
        String filename1 = split[length1 - 1];
        //截取\
        String[] split1 = filename1.split("\\\\");
        int length2 = split1.length;
        String filename2 = split1[length2 - 1];
        //截取.
        String[] split2 = filename2.split("\\.");
        String folderName = split2[0];
        //给每个图片创建一个文件夹
        File folder = new File(cacheDir, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        //创建图片文件
        File f = new File(folder, filename2);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }

    /**
     * 根据uri获取文件的路径,(当文件为txt类型的返回路径，其他类型文件返回null)
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    if (cursor.getString(column_index).endsWith(".txt")) {
                        return cursor.getString(column_index);
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            if (uri.getPath().endsWith(".txt")) {
                return uri.getPath();
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 读取文本文件中的内容（内容为url）
     *
     * @param strFilePath
     * @return
     */
    public static List<String> ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        ArrayList<String> list = new ArrayList<>();//文件内容的集合
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        if (line.endsWith(".jpg") || line.endsWith(".jepg") || line.endsWith(".png") || line.endsWith(".gif") || line.endsWith(".GIF")) {
                            list.add(line);
                        } else {
                            Log.e("libin: ", "不是图片");
                        }
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return list;
    }
}
