package com.flask.colorpicker.builder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.R;
import com.flask.colorpicker.renderer.ColorWheelRenderer;
import com.flask.colorpicker.slider.AlphaSlider;
import com.flask.colorpicker.slider.LightnessSlider;

/**
 * Created by James on 7/21/2016.
 */

public class ColorPickerWidgetBuilder {
    private Context context;
    private LinearLayout pickerContainer;
    private ColorPickerView colorPickerView;
    private LightnessSlider lightnessSlider;
    private AlphaSlider alphaSlider;
    private EditText colorEdit;
    private LinearLayout colorPreview;

    private boolean isLightnessSliderEnabled = true;
    private boolean isAlphaSliderEnabled = true;
    private boolean isColorEditEnabled = false;
    private boolean isPreviewEnabled = false;
    private int pickerCount = 1;
    private int defaultMargin = 0;
    private Integer[] initialColor = new Integer[]{null, null, null, null, null};

    private ColorPickerWidgetBuilder(@NonNull Context context) {
        this.context = context;
        pickerContainer = new LinearLayout(context);
        pickerContainer.setOrientation(LinearLayout.VERTICAL);
        pickerContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        defaultMargin = getDimensionAsPx(context, R.dimen.default_slider_margin);

        LinearLayout.LayoutParams layoutParamsForColorPickerView = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParamsForColorPickerView.weight = 1;
        colorPickerView = new ColorPickerView(context);

        pickerContainer.addView(colorPickerView, layoutParamsForColorPickerView);
    }

    public static ColorPickerWidgetBuilder with(@NonNull Context context) {
        return new ColorPickerWidgetBuilder(context);
    }

    public ColorPickerWidgetBuilder initialColor(int initialColor) {
        this.initialColor[0] = initialColor;
        return this;
    }

    public ColorPickerWidgetBuilder initialColors(int[] initialColor) {
        for (int i = 0; i < initialColor.length && i < this.initialColor.length; i++) {
            this.initialColor[i] = initialColor[i];
        }
        return this;
    }

    public ColorPickerWidgetBuilder wheelType(ColorPickerView.WHEEL_TYPE wheelType) {
        ColorWheelRenderer renderer = ColorWheelRendererBuilder.getRenderer(wheelType);
        colorPickerView.setRenderer(renderer);
        return this;
    }

    public ColorPickerWidgetBuilder density(int density) {
        colorPickerView.setDensity(density);
        return this;
    }

    public ColorPickerWidgetBuilder setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        colorPickerView.addOnColorSelectedListener(onColorSelectedListener);
        return this;
    }

    public ColorPickerWidgetBuilder noSliders() {
        isLightnessSliderEnabled = false;
        isAlphaSliderEnabled = false;
        return this;
    }

    public ColorPickerWidgetBuilder alphaSliderOnly() {
        isLightnessSliderEnabled = false;
        isAlphaSliderEnabled = true;
        return this;
    }

    public ColorPickerWidgetBuilder lightnessSliderOnly() {
        isLightnessSliderEnabled = true;
        isAlphaSliderEnabled = false;
        return this;
    }

    public ColorPickerWidgetBuilder showAlphaSlider(boolean showAlpha) {
        isAlphaSliderEnabled = showAlpha;
        return this;
    }

    public ColorPickerWidgetBuilder showLightnessSlider(boolean showLightness) {
        isLightnessSliderEnabled = showLightness;
        return this;
    }

    public ColorPickerWidgetBuilder showColorEdit(boolean showEdit) {
        isColorEditEnabled = showEdit;
        return this;
    }

    public ColorPickerWidgetBuilder setColorEditTextColor(int argb) {
        colorPickerView.setColorEditTextColor(argb);
        return this;
    }

    public ColorPickerWidgetBuilder showColorPreview(boolean showPreview) {
        isPreviewEnabled = showPreview;
        if (!showPreview)
            pickerCount = 1;
        return this;
    }

    public ColorPickerWidgetBuilder setPickerCount(int pickerCount) throws IndexOutOfBoundsException {
        if (pickerCount < 1 || pickerCount > 5)
            throw new IndexOutOfBoundsException("Picker Can Only Support 1-5 Colors");
        this.pickerCount = pickerCount;
        if (this.pickerCount > 1)
            this.isPreviewEnabled = true;
        return this;
    }

    public View build() {
        colorPickerView.setInitialColors(initialColor, getStartOffset(initialColor));

        if (isLightnessSliderEnabled) {
            LinearLayout.LayoutParams layoutParamsForLightnessBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
            layoutParamsForLightnessBar.setMargins(defaultMargin, 0, defaultMargin, 0);
            lightnessSlider = new LightnessSlider(context);
            lightnessSlider.setLayoutParams(layoutParamsForLightnessBar);
            pickerContainer.addView(lightnessSlider);
            colorPickerView.setLightnessSlider(lightnessSlider);
            lightnessSlider.setColor(getStartColor(initialColor));
        }
        if (isAlphaSliderEnabled) {
            LinearLayout.LayoutParams layoutParamsForAlphaBar = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getDimensionAsPx(context, R.dimen.default_slider_height));
            layoutParamsForAlphaBar.setMargins(defaultMargin, 0, defaultMargin, 0);
            alphaSlider = new AlphaSlider(context);
            alphaSlider.setLayoutParams(layoutParamsForAlphaBar);
            pickerContainer.addView(alphaSlider);
            colorPickerView.setAlphaSlider(alphaSlider);
            alphaSlider.setColor(getStartColor(initialColor));
        }
        if (isColorEditEnabled) {
            LinearLayout.LayoutParams layoutParamsForColorEdit = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int padSide = getDimensionAsPx(context, R.dimen.default_padding_side);
            layoutParamsForColorEdit.leftMargin = padSide;
            layoutParamsForColorEdit.rightMargin = padSide;
            colorEdit = (EditText) View.inflate(context, R.layout.picker_edit, null);
            colorEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            colorEdit.setSingleLine();
            colorEdit.setVisibility(View.GONE);

            // limit number of characters to hexColors
            int maxLength = 9;
            colorEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

            pickerContainer.addView(colorEdit, layoutParamsForColorEdit);

            colorEdit.setText("#" + Integer.toHexString(getStartColor(initialColor)).toUpperCase());
            colorPickerView.setColorEdit(colorEdit);
        }
        if (isPreviewEnabled) {
            colorPreview = (LinearLayout) View.inflate(context, R.layout.color_preview, null);
            colorPreview.setVisibility(View.GONE);
            pickerContainer.addView(colorPreview);

            if (initialColor.length == 0) {
                ImageView colorImage = (ImageView) View.inflate(context, R.layout.color_selector, null);
                colorImage.setImageDrawable(new ColorDrawable(Color.WHITE));
            } else {
                for (int i = 0; i < initialColor.length && i < this.pickerCount; i++) {
                    if (initialColor[i] == null)
                        break;
                    LinearLayout colorLayout = (LinearLayout) View.inflate(context, R.layout.color_selector, null);
                    ImageView colorImage = (ImageView) colorLayout.findViewById(R.id.image_preview);
                    colorImage.setImageDrawable(new ColorDrawable(initialColor[i]));
                    colorPreview.addView(colorLayout);
                }
            }
            colorPreview.setVisibility(View.VISIBLE);
            colorPickerView.setColorPreview(colorPreview, getStartOffset(initialColor));
        }

        return pickerContainer;
    }

    @ColorInt
    public int getSelectedColor() {
        return colorPickerView.getSelectedColor();
    }

    public Integer[] getAllColors() {
        return colorPickerView.getAllColors();
    }

    private Integer getStartOffset(Integer[] colors) {
        Integer start = 0;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == null) {
                return start;
            }
            start = (i + 1) / 2;
        }
        return start;
    }

    private int getStartColor(Integer[] colors) {
        Integer startColor = getStartOffset(colors);
        return startColor == null ? Color.WHITE : colors[startColor];
    }

    private static int getDimensionAsPx(Context context, int rid) {
        return (int) (context.getResources().getDimension(rid) + .5f);
    }





}
