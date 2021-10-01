package com.example.imagetopdf.Class;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Layout;

import androidx.core.content.ContextCompat;

import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class CallStickerView {

    private static final String TAG = "==>>";
    Activity activity;
    StickerView stickerView;
    TextSticker textSticker;
    ArrayList<Sticker> arrayList;
    int counter = 0;
    public Sticker ChangeSticker;

    public CallStickerView(Activity activity, StickerView stickerView) {
        this.activity = activity;
        this.stickerView = stickerView;
        arrayList = new ArrayList<>();
    }

    public void IStickerView() {

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(activity, com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp), BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(activity, com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp), BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(activity, com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp), BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
        stickerView.setBackgroundColor(Color.TRANSPARENT);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

    }

    public void IStickerEvent() {

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerClicked(Sticker sticker) {

                if (sticker instanceof TextSticker) {
                    ChangeSticker = sticker;
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                }
            }

            @Override
            public void onStickerDeleted(Sticker sticker) {
                ChangeSticker = null;
            }

            @Override
            public void onStickerDragFinished(Sticker sticker) {
                ChangeSticker = sticker;
            }

            @Override
            public void onStickerZoomFinished(Sticker sticker) {
                ChangeSticker = sticker;

            }

            @Override
            public void onStickerFlipped(Sticker sticker) {
                ChangeSticker = sticker;

            }

            @Override
            public void onStickerDoubleTapped(Sticker sticker) {
                ChangeSticker = sticker;
            }
        });

    }

    public void AdTextViewSticker(String text, Drawable drawable) {

        textSticker = new TextSticker(activity);
        if (drawable != null) {
            textSticker.setDrawable(drawable);
        }
        textSticker.setText(text);
        textSticker.setTextColor(Color.GRAY);
        textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);

        textSticker.resizeText();
        textSticker.setId(counter);
        stickerView.addSticker(textSticker);

        ChangeSticker = textSticker;

    }

    public void UpdateStickerDetail(Sticker sticker) {

        stickerView.replace(sticker);
        stickerView.invalidate();

    }

    public Sticker GetStickerView() {

        return ChangeSticker;

    }

    public void AdImageSticker(Drawable drawable) {

        //Drawable drawable1 = ContextCompat.getDrawable(activity, R.drawable.haizewang_23);
        stickerView.addSticker(new DrawableSticker(drawable));

    }

    public void HideBorder() {
        stickerView.showIcons = false;
        stickerView.showBorder = false;

    }

    public void ShowBorder() {
        stickerView.showIcons = true;
        stickerView.showBorder = true;
    }
}
