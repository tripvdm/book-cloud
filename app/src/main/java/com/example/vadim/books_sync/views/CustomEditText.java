package com.example.vadim.books_sync.views;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.vadim.books_sync.R;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    //The image we are going to use for the Clear button
    private Drawable imgCloseButton;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    void init() {
        imgCloseButton =
                getResources().getDrawable(R.mipmap.ic_clear_text_foreground);
        // Set bounds of the Clear button so it will look ok
        imgCloseButton.setBounds(0,
                        0,
                imgCloseButton.getIntrinsicWidth() / 3,
                imgCloseButton.getIntrinsicHeight() / 3);

        // There may be initial text in the field, so we may need to display the  button
        handleClearButton();
//        addTextChangedListener
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                CustomEditText.this.handleClearButton();
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//        });

        setOnTouchListener((v, event) -> {
            if (getCompoundDrawables()[2] == null)
                return false;
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if ((event.getX() > getWidth()  -
                    getPaddingRight() - imgCloseButton.getIntrinsicWidth() / imgCloseButton.getMinimumWidth()) &&
                    (event.getY() < getPaddingTop() + (imgCloseButton.getMinimumHeight())) &&
                    (event.getY() > getHeight() - getPaddingBottom() - (imgCloseButton.getMinimumHeight()))) {
                setText("");
                CustomEditText.this.handleClearButton();
            }
            return false;
        });

    }

    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(
                    Typeface.createFromAsset(getContext().getAssets(),
                            "fonts/Vegur-B 0.602.otf"));
        } else {
            super.setTypeface(
                    Typeface.createFromAsset(getContext().getAssets(),
                            "fonts/Vegur-R 0.602.otf"));
        }
    }

    public void handleClearButton() {
        if (this.getText().toString().equals("")) {
            setVisibleCloseButton(false);
        } else {
            setVisibleCloseButton(true);
        }
    }

    public void setVisibleCloseButton(boolean visible) {
        if (visible) {
            this.setCompoundDrawables(
                    this.getCompoundDrawables()[0],
                    this.getCompoundDrawables()[1],
                    imgCloseButton, this.getCompoundDrawables()[3]);
            this.setBackgroundColor(1);
        } else {
            this.setCompoundDrawables(
                    this.getCompoundDrawables()[0],
                    this.getCompoundDrawables()[1],
                    null, this.getCompoundDrawables()[3]);
        }
    }

}
