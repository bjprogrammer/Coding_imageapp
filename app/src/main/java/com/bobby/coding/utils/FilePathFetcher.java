package com.bobby.coding.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.webkit.MimeTypeMap;


public class FilePathFetcher {
    public  static final String UNDEFINED_PDF="UNDEFINED_PDF";

    public static String getMimeType(String path){
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if(extension==null){
            extension = extension.substring(extension.lastIndexOf(".")+1);
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getPathFromUri(Context context, Uri uri) {

        if (isExternalStorageDocument(uri)) {
            if(Build.VERSION.SDK_INT>=19) {
                final String doc_id = DocumentsContract.getDocumentId(uri);
                final String split[] = doc_id.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        }


        if (isDownloadsDocument(uri)) {
            if(Build.VERSION.SDK_INT>=21) {
                final String doc_id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(doc_id));
                return getDataColumn(context, contentUri, null, null);
            }
        }

        return UNDEFINED_PDF;
    }


    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionargs){
        Cursor cursor = null;
        String column = "_data";
        final String[] projection ={column};

        try{
            cursor = context.getContentResolver().query(uri,projection,selection,selectionargs,null);
            if(cursor!=null &&  cursor.moveToFirst()){
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }finally {
            if(cursor!=null) {
                cursor.close();
            }

        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri){
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
