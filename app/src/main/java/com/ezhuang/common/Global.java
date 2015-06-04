package com.ezhuang.common;

/**
 * Created by Administrator on 2015/4/3 0003.
 */

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;

import com.ezhuang.MyApp;
import com.ezhuang.common.htmltext.GrayQuoteSpan;
import com.ezhuang.model.CurrentUser;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by cc191954 on 14-8-233.
 */
public class Global {
    public static final String HOST_91JZ = "http://121.41.117.203:80";
//    public static final String HOST_91JZ = "http://wwww.91jzw.com";
//    public static final String HOST_91JZ = "http://192.168.0.178:8080/HardcoverServer";

    public static String HOST = HOST_91JZ;

    public static int PAGE_SIZE = 15;

    public static String PROJECT_MANAGER = "5";
    public static String CEHCK = "10";
    public static String BUYER = "8";
    public static String STAFF = "9";
    public static String QUALITY = "13";

    public static String PUSH_BROADCAST = "jpush_broadcast";

    public static String[] PJ_STATE = new String[]{"","启动","进行中","竣工"};

    public static CurrentUser currentUser = null;

    public static SimpleDateFormat DateFormatTime = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat DayFormatTime = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat MonthDayFormatTime = new SimpleDateFormat("MMMdd日");

    public static SimpleDateFormat WeekFormatTime = new SimpleDateFormat("EEE");
    public static SimpleDateFormat NextWeekFormatTime = new SimpleDateFormat("下EEE");
    public static SimpleDateFormat LastWeekFormatTime = new SimpleDateFormat("上EEE");

    public static String dayFromTime(long time) {
        return DayFormatTime.format(time);
    }

    public static long longFromDay(String day) throws ParseException {
        final String format = "yyyy-MM-dd";
        final SimpleDateFormat sd = new SimpleDateFormat(format);
        return sd.parse(day).getTime();
    }

    public static String dayCount(long time) {
        return DayFormatTime.format(time);
    }

    public static void errorLog(Exception e) {
        e.printStackTrace();
        Log.e("", "" + e);
    }

    public static String encodeInput(String at, String input) {
        if (at == null || at.isEmpty()) {
            return input;
        }

        return String.format("@%s %s", at, input);
    }

    public static String encodeUtf8(String s) {
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (Exception e) {
        }

        return "";
    }

    public static boolean isImageUri(String s1) {
        s1 = s1.toLowerCase();
        return s1.endsWith(".png")
                || s1.endsWith(".jpg")
                || s1.endsWith(".jpeg")
                || s1.endsWith(".bmp")
                || s1.endsWith(".gif");
    }

    public static boolean isVideoUri(String s1) {
        s1 = s1.toLowerCase();
        return s1.endsWith(".mp4")
                || s1.endsWith(".3gp")
                || s1.endsWith(".flv")
                || s1.endsWith(".bmp")
                || s1.endsWith(".gif");
    }

    public static void syncCookie(Context context) {
        PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        List<Cookie> cookies = cookieStore.getCookies();

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie eachCookie = cookies.get(i);
            String cookieString = eachCookie.getName() + "=" + eachCookie.getValue();
            cookieManager.setCookie(Global.HOST, cookieString);
        }

        CookieSyncManager.getInstance().sync();

    }

    public static void copy(Context context, String content) {
//        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        cmb.setText(content);
    }

    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static List<Camera.Size> getResolutionList(Camera camera){
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height!=rhs.height)
                return lhs.height-rhs.height;
            else
                return lhs.width-rhs.width;
        }

    }

    public static String replaceAvatar(JSONObject json) {
        return replaceUrl(json, "avatar");
    }

    public static String getErrorMsg(JSONObject jsonObject) {
        String s = "";
        try {
            JSONObject jsonData = jsonObject.getJSONObject("msg");
            String key = (String) jsonData.keys().next();
            s = jsonData.getString(key);
        } catch (Exception e) {
        }

        return s;
    }

    public static String replaceUrl(JSONObject json, String name) {
        String s = json.optString(name);
        if (s.indexOf("/static") == 0) {
            return Global.HOST + s;
        }

        return s;
    }

    public static void popSoftkeyboard(Context ctx, View view, boolean wantPop) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (wantPop) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Html.TagHandler tagHandler = new Html.TagHandler() {
        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.toLowerCase().equals("code") && !opening) {
                output.append("\n\n");
            }
        }
    };

    private static final String IMAGE_URL_SCAL = "%s?imageMogr2/thumbnail/!%s";

    public static String makeSmallUrl(ImageView view, String url) {
        String realUrl = url.split("\\?")[0];

        if (url.indexOf("http") == 0) {
            // 头像再裁剪需要算坐标，就不改参数了
            // https://dn-coding-net-production-static.qbox.me/c28b97dd-61f2-41d4-bd7e-b04f0c634751.jpg?imageMogr2/auto-orient/format/jpeg/crop/!164x164a568a38
            if (url.contains("/crop/")) {
                return url;
            }

            ViewGroup.LayoutParams lp = view.getLayoutParams();
            String width = intToString(lp.width);
            String height = intToString(lp.height);

            // 如果初始化的时候没有长宽，默认取高度为200dp缩略图
            if (width.isEmpty() && height.isEmpty()) {
                height = String.valueOf(Global.dpToPx(200));
                width = String.valueOf(Global.dpToPx(200));

            }
            return String.format(IMAGE_URL_SCAL, realUrl, width);
        } else {
            return realUrl;
        }
    }


    // 7牛图片接口，用法如下
    // http://developer.qiniu.com/docs/v6/api/reference/fop/image/imageview2.html
//    private static final String IMAGE_URL_CROP = "%s/?imageView2/4/w/%s/h/%s";
//    public static String makeSmallUrl(ImageView view, String url, int minWidth) {
//        String realUrl = url.split("\\?")[0];
//
//        if (url.indexOf("http") == 0) {
//            // 头像再裁剪需要算坐标，就不改参数了
//            // https://dn-coding-net-production-static.qbox.me/c28b97dd-61f2-41d4-bd7e-b04f0c634751.jpg?imageMogr2/auto-orient/format/jpeg/crop/!164x164a568a38
//            if (url.contains("/crop/")) {
//                return url;
//            }
//
//            ViewGroup.LayoutParams lp = view.getLayoutParams();
//
//            String width = intToString(Math.max(lp.width, lp.height));
//            String height = intToString(Math.min(lp.width, lp.height));
//
//            // 如果初始化的时候没有长宽，默认取高度为200dp缩略图
//            if (width.isEmpty() && height.isEmpty()) {
//                height = String.valueOf(Global.dpToPx(200));
//            }
//            return String.format(IMAGE_URL_CROP, realUrl, width, height);
//        } else {
//            return realUrl;
//        }
//
//    }

    public static String makeLargeUrl(String url) {
        final int MAX = 4096; // ImageView显示的图片不能大于这个数
        return String.format(IMAGE_URL_SCAL, url, 4096, 4096);
    }

    private static String intToString(int length) {
        String width;
        if (length > 0) {
            width = String.valueOf(length);
        } else {
            width = "";
        }

        return width;
    }

    public static int dpToPx(int dpValue) {
        return (int) (dpValue * MyApp.sScale + 0.5f);
    }

    public static int pxToDp(float pxValue) {
        return (int) (pxValue / MyApp.sScale + 0.5f);
    }






    static public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
        }
        return outputStream.toString();
    }

    public static class MessageParse {
        public String text = "";
        public ArrayList<String> uris = new ArrayList<String>();

        public String toString() {
            String s = "text " + text + "\n";
            for (int i = 0; i < uris.size(); ++i) {
                s += uris.get(i) + "\n";
            }
            return s;
        }
    }

    public static String dataToNow(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
            return "未知";
        }
        return dayToNow(date.getTime());
    }

    public static String dayToNow(long time) {
        Calendar now = Calendar.getInstance();

        long minute = (now.getTimeInMillis() - time) / 60000;
        if (minute < 60) {
            if (minute == 0) {
                return "刚刚";
            } else {
                return minute + "分钟前";
            }
        }

        long hour = minute / 60;
        if (hour < 24) {
            return hour + "小时前";
        }

        long day = hour / 24;
        if (day < 30) {
            return day + "天前";
        }

        long month = day / 30;
        if (month < 11) {
            return month + "个月前";
        }

        long year = month / 12;
        return year + "年前";
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activityNetwork = mConnectivityManager.getActiveNetworkInfo();
            return activityNetwork != null && activityNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static DecimalFormat df = new java.text.DecimalFormat("#.00");

    /**
     * 显示文件大小,保留两位
     *
     * @param size
     * @return
     */
    public static String HumanReadableFilesize(double size) {
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB", "PB"};
        double mod = 1024.0;
        int i = 0;
        while (size >= mod) {
            size /= mod;
            i++;
        }
        //return Math.round(size) + units[i];
        return df.format(size) + " " + units[i];
    }

    public static boolean isMoblie(String moblie){
        Pattern p = Pattern.compile("^((1[3-9][0-9])|(15[^4,\\D])|(17[0-9])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(moblie);
        return m.matches();
    }

    public static boolean isFloat(String sFloat){
        return Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]+)?").matcher(sFloat).matches();
    }
}
