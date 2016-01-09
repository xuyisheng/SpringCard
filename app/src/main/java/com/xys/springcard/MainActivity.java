package com.xys.springcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.io.File;

//http://m.blog.csdn.net/blog/Jing_Unique_Da/47254993
public class MainActivity extends Activity {

    private IWXAPI wxApi;
    private String TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private ImageView imageView;
    private Button mShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.springcard);
        //实例化
        wxApi = WXAPIFactory.createWXAPI(this, "wxe84825b94f4432ed");
        wxApi.registerApp("wxe84825b94f4432ed");

        EditText editText = (EditText) findViewById(R.id.word);
        editText.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/test.ttf"));

        imageView = (ImageView) findViewById(R.id.photo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhoto();
            }
        });

        mShare = (Button) findViewById(R.id.share);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatShare(1);
//                imageView.setImageBitmap(generateSpringCard());
                mShare.setVisibility(View.VISIBLE);
            }
        });
    }

    private void wechatShare(int flag) {
        WXWebpageObject webpage = new WXWebpageObject();
//        webpage.webpageUrl = "http://blog.csdn.net/eclipsexys";
        WXMediaMessage msg = new WXMediaMessage(webpage);
//        msg.title = "这里填写标题";
//        msg.description = "这里填写内容";
//        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        msg.setThumbImage(thumb);
        msg.mediaObject = new WXImageObject(generateSpringCard());

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private void getPhoto() {
        String[] customItems = new String[]{"本地图册", "相机拍照"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(customItems, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    // 本地相册
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, 100);
                } else if (1 == which) {
                    // 系统相机
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, 200);
                }
            }
        });
        builder.create().show();
    }

    /**
     * 调用图库相机回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100 && data != null) {
                imageView.setImageURI(data.getData());
            } else if (requestCode == 200) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(TEMP_IMAGE_PATH));
            }
        }
    }

    private Bitmap generateSpringCard() {
        mShare.setVisibility(View.INVISIBLE);
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
}
