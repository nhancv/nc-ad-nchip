package com.nhancv.nchip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class NChip<O> extends ViewGroup implements View.OnClickListener {

    public static int MAX_CHARACTER_COUNT = 20;
    private final List<List<View>> mLines = new ArrayList<>();
    private final List<Integer> mLineHeights = new ArrayList<>();
    private final List<Integer> mLineMargins = new ArrayList<>();
    private List<View> lineViews = new ArrayList<>();

    private int mGravity = (isIcs() ? Gravity.START : Gravity.LEFT) | Gravity.TOP;
    private float textSize, chipTextPadding, chipPadding, chipPaddingLeft, chipPaddingRight,
            chipPaddingTop, chipPaddingBottom, chipTextPaddingLeft, chipTextPaddingRight,
            chipTextPaddingTop, chipTextPaddingBottom, chipEditPadding, chipEditPaddingTop,
            chipEditPaddingBottom, chipEditPaddingLeft, chipEditPaddingRight, chipDropdownWidth, chipDropdownTopOffset, chipDropdownLeftOffset;
    private NChip nchip;
    private Context context;
    private boolean showDeleteButton, showKeyboardInFocus, autoSplitInActionKey, initFocus, chipEnableEdit;
    private int labelPosition, editTextColor, chipColor, chipTextColor, chipHintColor;
    private int dropDownWidth = -1;
    private ArrayAdapter<O> adapter;
    private String chipInitHint, chipSplitFlag;
    private Bitmap deleteIcon_ = null;
    private Drawable deleteIcon, chipDrawable, chipLayoutDrawable;
    private View.OnClickListener onClickListener;
    private View.OnFocusChangeListener onFocusChangeListener;
    private AdapterView.OnItemClickListener onItemClickListener;
    private ChipItemChangeListener<O> chipItemChangeListener;
    private TextWatcher focusedTextWatcher;
    private DisplayMetrics displayMetrics;
    private List<TextWatcher> listTextWatcher = new ArrayList<>();

    public NChip(Context context) {
        this(context, null);
    }

    public NChip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NChip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a_ = context.getTheme().obtainStyledAttributes(attrs, R.styleable.nchip_layout, defStyle, 0);

        editTextColor = a_.getColor(R.styleable.nchip_layout_editTextColor_, Color.parseColor("#000000"));
        chipTextColor = a_.getColor(R.styleable.nchip_layout_chipTextColor_, Color.parseColor("#ffffff"));
        chipHintColor = a_.getColor(R.styleable.nchip_layout_chipHintColor_, Color.parseColor("#9b9b9b"));

        chipColor = a_.getColor(R.styleable.nchip_layout_chipColor_, Color.parseColor("#00FFFFFF"));
        chipDrawable = a_.getDrawable(R.styleable.nchip_layout_chipDrawable_);
        deleteIcon = a_.getDrawable(R.styleable.nchip_layout_deleteIcon_);
        showDeleteButton = a_.getBoolean(R.styleable.nchip_layout_showDeleteButton_, true);
        initFocus = a_.getBoolean(R.styleable.nchip_layout_chipInitFocus_, false);
        chipEnableEdit = a_.getBoolean(R.styleable.nchip_layout_chipEnableEdit_, true);
        chipInitHint = a_.getString(R.styleable.nchip_layout_chipInitHint_);
        chipSplitFlag = a_.getString(R.styleable.nchip_layout_chipSplitFlag_);
        labelPosition = a_.getInt(R.styleable.nchip_layout_labelPosition_, 0);
        chipLayoutDrawable = a_.getDrawable(R.styleable.nchip_layout_chipLayoutDrawable_);
        textSize = a_.getDimensionPixelSize(R.styleable.nchip_layout_textSize_, 14);
        chipTextPadding = a_.getDimension(R.styleable.nchip_layout_chipTextPadding_, 0);
        chipTextPaddingLeft = a_.getDimension(R.styleable.nchip_layout_chipTextPaddingLeft_, chipTextPadding);
        chipTextPaddingRight = a_.getDimension(R.styleable.nchip_layout_chipTextPaddingRight_, chipTextPadding);
        chipTextPaddingTop = a_.getDimension(R.styleable.nchip_layout_chipTextPaddingTop_, chipTextPadding);
        chipTextPaddingBottom = a_.getDimension(R.styleable.nchip_layout_chipTextPaddingBottom_, chipTextPadding);
        chipPadding = a_.getDimension(R.styleable.nchip_layout_chipPadding_, 0);
        chipPaddingLeft = a_.getDimension(R.styleable.nchip_layout_chipPaddingLeft_, 0);
        chipPaddingRight = a_.getDimension(R.styleable.nchip_layout_chipPaddingRight_, 0);
        chipPaddingTop = a_.getDimension(R.styleable.nchip_layout_chipPaddingTop_, 0);
        chipPaddingBottom = a_.getDimension(R.styleable.nchip_layout_chipPaddingBottom_, 0);

        chipEditPadding = a_.getDimension(R.styleable.nchip_layout_chipEditPadding_, 10);
        chipEditPaddingLeft = a_.getDimension(R.styleable.nchip_layout_chipEditPaddingLeft_, chipEditPadding);
        chipEditPaddingTop = a_.getDimension(R.styleable.nchip_layout_chipEditPaddingTop_, chipEditPadding);
        chipEditPaddingRight = a_.getDimension(R.styleable.nchip_layout_chipEditPaddingRight_, chipEditPadding);
        chipEditPaddingBottom = a_.getDimension(R.styleable.nchip_layout_chipEditPaddingBottom_, chipEditPadding);

        chipDropdownWidth = a_.getDimension(R.styleable.nchip_layout_chipDropdownWidth_, 0);
        chipDropdownTopOffset = a_.getDimension(R.styleable.nchip_layout_chipDropdownTopOffset_, chipTextPaddingBottom);
        chipDropdownLeftOffset = a_.getDimension(R.styleable.nchip_layout_chipDropdownLeftOffset_, 0);

        displayMetrics = getResources().getDisplayMetrics();
        if (chipDropdownWidth < 0) chipDropdownWidth /= displayMetrics.density;
        if (chipDropdownWidth == -3) chipDropdownWidth = displayMetrics.widthPixels;

        if (deleteIcon != null) {
            deleteIcon_ = ((BitmapDrawable) deleteIcon).getBitmap();
        }


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.nchipLayout, defStyle, 0);

        try {
            int index = a.getInt(R.styleable.nchipLayout_android_gravity, -1);
            if (index > 0) {
                setGravity(index);
            }
        } finally {
            a.recycle();
        }
        this.context = context;
        nchip = this;
        if (chipLayoutDrawable != null) {
            setLayoutBackground(chipLayoutDrawable);
        }
        if (chipDrawable == null) {
            chipDrawable = ContextCompat.getDrawable(getContext(), R.drawable.nchip_round_corner_drawable);
        }

        if (chipSplitFlag == null) chipSplitFlag = ",";
        showKeyboardInFocus = true;
        autoSplitInActionKey = true;

        createNewChipLayout(null);
        setOnClickListener();

    }

    private static boolean isIcs() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public void setOnChipItemChangeListener(ChipItemChangeListener l) {
        this.chipItemChangeListener = l;
    }

    /**
     * Ask all children to measure themselves and compute the measurement of this
     * layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0;
        int height = getPaddingTop() + getPaddingBottom();

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {

            View child = getChildAt(i);
            boolean lastChild = i == childCount - 1;

            if (child.getVisibility() == View.GONE) {

                if (lastChild) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }

                continue;
            }

            measureChildWithMargins(child, widthMeasureSpec, lineWidth, heightMeasureSpec, height);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidthMode = MeasureSpec.AT_MOST;
            int childWidthSize = sizeWidth;

            int childHeightMode = MeasureSpec.AT_MOST;
            int childHeightSize = sizeHeight;

            if (lp.width == LayoutParams.MATCH_PARENT) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidthSize -= lp.leftMargin + lp.rightMargin;
            } else if (lp.width >= 0) {
                childWidthMode = MeasureSpec.EXACTLY;
                childWidthSize = lp.width;
            }

            if (lp.height >= 0) {
                childHeightMode = MeasureSpec.EXACTLY;
                childHeightSize = lp.height;
            } else if (modeHeight == MeasureSpec.UNSPECIFIED) {
                childHeightMode = MeasureSpec.UNSPECIFIED;
                childHeightSize = 0;
            }

            child.measure(
                    MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
                    MeasureSpec.makeMeasureSpec(childHeightSize, childHeightMode)
            );

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            if (lineWidth + childWidth > sizeWidth) {

                width = Math.max(width, lineWidth);
                lineWidth = childWidth;

                height += lineHeight;
                lineHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            }

            if (lastChild) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }

        }

        width += getPaddingLeft() + getPaddingRight();

        setMeasuredDimension(
                (modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width,
                (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }


    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mLines.clear();
        mLineHeights.clear();
        mLineMargins.clear();

        int width = getWidth();
        int height = getHeight();

        int linesSum = getPaddingTop();

        int lineWidth = 0;
        int lineHeight = 0;

        lineViews.clear();
        float horizontalGravityFactor;
        switch ((mGravity & Gravity.HORIZONTAL_GRAVITY_MASK)) {
            case Gravity.LEFT:
            default:
                horizontalGravityFactor = 0;
                break;
            case Gravity.CENTER_HORIZONTAL:
                horizontalGravityFactor = .5f;
                break;
            case Gravity.RIGHT:
                horizontalGravityFactor = 1;
                break;
        }

        for (int i = 0; i < getChildCount(); i++) {

            ViewGroup child = (ViewGroup) getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;

            if (lineWidth + childWidth > width) {
                mLineHeights.add(lineHeight);
                mLines.add(lineViews);
                mLineMargins.add((int) ((width - lineWidth) * horizontalGravityFactor) + getPaddingLeft());

                linesSum += lineHeight;

                lineHeight = 0;
                lineWidth = 0;
                lineViews.clear();
            }

            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }

        mLineHeights.add(lineHeight);
        mLines.add(lineViews);
        mLineMargins.add((int) ((width - lineWidth) * horizontalGravityFactor) + getPaddingLeft());

        linesSum += lineHeight;

        int verticalGravityMargin = 0;
        switch ((mGravity & Gravity.VERTICAL_GRAVITY_MASK)) {
            case Gravity.TOP:
            default:
                break;
            case Gravity.CENTER_VERTICAL:
                verticalGravityMargin = (height - linesSum) / 2;
                break;
            case Gravity.BOTTOM:
                verticalGravityMargin = height - linesSum;
                break;
        }

        int numLines = mLines.size();

        int left;
        int top = getPaddingTop();

        for (int i = 0; i < numLines; i++) {

            lineHeight = mLineHeights.get(i);
            lineViews = mLines.get(i);
            left = mLineMargins.get(i);

            int children = lineViews.size();

            for (int j = 0; j < children; j++) {

                View child = lineViews.get(j);

                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (lp.height == LayoutParams.MATCH_PARENT) {
                    int childWidthMode = MeasureSpec.AT_MOST;
                    int childWidthSize = lineWidth;

                    if (lp.width == LayoutParams.MATCH_PARENT) {
                        childWidthMode = MeasureSpec.EXACTLY;
                    } else if (lp.width >= 0) {
                        childWidthMode = MeasureSpec.EXACTLY;
                        childWidthSize = lp.width;
                    }

                    child.measure(
                            MeasureSpec.makeMeasureSpec(childWidthSize, childWidthMode),
                            MeasureSpec.makeMeasureSpec(lineHeight - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY)
                    );
                }

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                int gravityMargin = 0;

                if (Gravity.isVertical(lp.gravity)) {
                    switch (lp.gravity) {
                        case Gravity.TOP:
                        default:
                            break;
                        case Gravity.CENTER_VERTICAL:
                        case Gravity.CENTER:
                            gravityMargin = (lineHeight - childHeight - lp.topMargin - lp.bottomMargin) / 2;
                            break;
                        case Gravity.BOTTOM:
                            gravityMargin = lineHeight - childHeight - lp.topMargin - lp.bottomMargin;
                            break;
                    }
                }

                child.layout(left + lp.leftMargin,
                        top + lp.topMargin + gravityMargin + verticalGravityMargin,
                        left + childWidth + lp.leftMargin,
                        top + childHeight + lp.topMargin + gravityMargin + verticalGravityMargin);

                left += childWidth + lp.leftMargin + lp.rightMargin;

            }

            top += lineHeight;
        }

    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    public int getGravity() {
        return mGravity;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
                gravity |= isIcs() ? Gravity.START : Gravity.LEFT;
            }

            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
                gravity |= Gravity.TOP;
            }

            mGravity = gravity;
            requestLayout();
        }
    }

    private AutoCompleteTextView createAutoCompleteTextView(Context context) {
        final LayoutParams lparamsTextView = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lparamsTextView.setMargins(0, 0, 10, 0);
        lparamsTextView.gravity = Gravity.CENTER;
        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(context);
        autoCompleteTextView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        autoCompleteTextView.setLayoutParams(lparamsTextView);
        autoCompleteTextView.setHint(" ");
        autoCompleteTextView.setHintTextColor(chipHintColor);
        autoCompleteTextView.setPadding((int) chipEditPaddingLeft, (int) chipEditPaddingTop, (int) chipEditPaddingRight, (int) chipEditPaddingBottom);
        autoCompleteTextView.setSingleLine(true);
        autoCompleteTextView.setTextColor(editTextColor);
        autoCompleteTextView.setCursorVisible(true);

        return autoCompleteTextView;
    }

    private ImageButton createImageButton(Context context) {
        final LayoutParams lparamsImageButton = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        lparamsImageButton.setMargins(0, 0, 0, 0);
        lparamsImageButton.gravity = Gravity.CENTER;
        final ImageButton imageButton = new ImageButton(context);
        imageButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        if (deleteIcon != null) {
            imageButton.setImageBitmap(deleteIcon_);
        } else {
            imageButton.setImageResource(R.drawable.nchip_ic_remove);
        }
        imageButton.setPadding(5, 0, 10, 0);
        imageButton.setLayoutParams(lparamsImageButton);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                View chip = (View) view.getParent();
                int pos = nchip.indexOfChild(chip);
                removeChipAt(pos);
            }
        });
        imageButton.setVisibility(View.GONE);
        return imageButton;
    }

    private LinearLayout createLinearLayout(Context context) {

        final LayoutParams lparamsLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lparamsLayout.setMargins((int) chipPadding + (int) chipPaddingLeft + 2, (int) chipPadding + (int) chipPaddingTop + 2,
                (int) chipPadding + (int) chipPaddingRight + 2, (int) chipPadding + (int) chipPaddingBottom + 2);
        lparamsLayout.gravity = Gravity.CENTER;
        final LinearLayout layout = new LinearLayout(context);
        layout.setPadding((int) chipTextPadding + (int) chipTextPaddingLeft, (int) chipTextPadding + (int) chipTextPaddingTop,
                (int) chipTextPadding + (int) chipTextPaddingRight, (int) chipTextPadding + (int) chipTextPaddingBottom);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(lparamsLayout);
        layout.setFocusable(true);

        return layout;
    }

    private ViewGroup createChips(Context context, O val, boolean setText) {

        final LinearLayout layout = createLinearLayout(context);
        final AutoCompleteTextView autoCompleteTextView = createAutoCompleteTextView(context);
        final ImageButton imageButton = createImageButton(context);

        if (labelPosition == 0) {
            layout.addView(autoCompleteTextView);
            layout.addView(imageButton);
        } else {
            layout.addView(imageButton);
            layout.addView(autoCompleteTextView);
        }

        Drawable newDrawable = null;
        if (chipDrawable != null) {
            newDrawable = chipDrawable.getConstantState().newDrawable();
        }

        TextWatcher textWatcher = new ChipTextWatcher(layout, context, this, chipTextColor, chipColor, newDrawable,
                showDeleteButton, labelPosition, setText, chipSplitFlag);
        focusedTextWatcher = textWatcher;
        if (textSize > 0) {
            autoCompleteTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            ((ChipTextWatcher) textWatcher).setTextSize(textSize);
        }
        autoCompleteTextView.addTextChangedListener(textWatcher);
        for (TextWatcher tw : listTextWatcher) {
            autoCompleteTextView.addTextChangedListener(tw);
        }

        OnFocusChangeListener focusChangeListener = new ChipOnFocusChangeListener(this, chipLayoutDrawable, onFocusChangeListener);
        autoCompleteTextView.setOnFocusChangeListener(focusChangeListener);

        if (initFocus || getChildCount() > 0) {
            autoCompleteTextView.requestFocus();
        }

        if (autoSplitInActionKey) {
            autoCompleteTextView.setOnEditorActionListener(new ChipEditorActionListener(autoCompleteTextView, chipSplitFlag));
        }
        autoCompleteTextView.setAdapter(adapter);
        if (chipDropdownWidth == 0) {
            switch (displayMetrics.densityDpi) {
                case DisplayMetrics.DENSITY_LOW:
                    break;
                case DisplayMetrics.DENSITY_MEDIUM:
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    dropDownWidth = 280;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    dropDownWidth = 300;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    dropDownWidth = 320;
                    break;
                case DisplayMetrics.DENSITY_560:
                    dropDownWidth = 360;
                    break;
            }
        } else {
            autoCompleteTextView.setDropDownAnchor(nchip.getId());
            dropDownWidth = (int) chipDropdownWidth;
        }

        autoCompleteTextView.setDropDownWidth(dropDownWidth);
        autoCompleteTextView.setDropDownVerticalOffset((int) chipDropdownTopOffset);
        autoCompleteTextView.setDropDownHorizontalOffset((int) chipDropdownLeftOffset);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    autoCompleteTextView.setTag(arg0.getAdapter().getItem(arg2));
                    autoCompleteTextView.setText(autoCompleteTextView.getText().toString() + chipSplitFlag);
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(arg0, arg1, arg2, arg3);
                    }
                } catch (Exception ignored) {
                }
            }
        });


        if (val != null) {
            autoCompleteTextView.setText(val.toString() + chipSplitFlag);
        } else if (chipInitHint != null && getChildCount() == 0) {
            autoCompleteTextView.setHint(chipInitHint);
        }

        if (!chipEnableEdit) {
            autoCompleteTextView.setText(null);
            autoCompleteTextView.setEnabled(false);
            autoCompleteTextView.setFocusable(false);
            autoCompleteTextView.setFocusableInTouchMode(false);

        }
        return layout;
    }

    void createNewChipLayout(O val) {
        this.addView(createChips(context, val, false));
    }

    void createNewChipLayout(O val, boolean setText) {
        this.addView(createChips(context, val, setText));
    }

    void chipCreated(ViewGroup vg) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) vg.getChildAt(labelPosition);
        int pos = nchip.indexOfChild(vg);

        if (chipItemChangeListener != null) {
            if (autoCompleteTextView.getText() != null && autoCompleteTextView.getText().toString().length() > 0) {
                chipItemChangeListener.onChipAdded(pos, (O) autoCompleteTextView.getTag());
            } else {
                chipItemChangeListener.onChipAdded(pos, null);
            }
        }
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener f) {
        onFocusChangeListener = f;
        if (this.getChildCount() > 0) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(this.getChildCount() - 1)).getChildAt(labelPosition);
            OnFocusChangeListener focusChangeListener = new ChipOnFocusChangeListener(this, chipLayoutDrawable, onFocusChangeListener);
            autoCompleteTextView.setOnFocusChangeListener(focusChangeListener);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    private void setOnClickListener() {
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onLayoutClick();
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    private void onLayoutClick() {
        if (chipEnableEdit) {
            int totalChips = this.getChildCount() - 1;
            if (totalChips < 0) {
                createNewChipLayout(null);
            } else {
                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(totalChips)).getChildAt(labelPosition);
                if (autoCompleteTextView.isFocusable()) {
                    autoCompleteTextView.requestFocus();
                    toogleSoftInputKeyboard(autoCompleteTextView);
                } else {
                    createNewChipLayout(null);
                }
            }
        }
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.onItemClickListener = l;
    }

    public void addLayoutTextChangedListener(TextWatcher textWatcher) {
        listTextWatcher.add(textWatcher);
        if (this.getChildCount() > 0) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(this.getChildCount() - 1)).getChildAt(labelPosition);
            autoCompleteTextView.addTextChangedListener(textWatcher);
        }
    }

    public void removeLayoutTextChangedListener(TextWatcher textWatcher) {
        listTextWatcher.remove(textWatcher);
        if (this.getChildCount() > 0) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(this.getChildCount() - 1)).getChildAt(labelPosition);
            autoCompleteTextView.removeTextChangedListener(textWatcher);
        }
    }

    public int getEditTextColor() {
        return this.editTextColor;
    }

    public void setEditTextColor(int editTextColor) {
        this.editTextColor = editTextColor;
        for (int i = 0; i < this.getChildCount(); i++) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(i)).getChildAt(labelPosition);
            autoCompleteTextView.setTextColor(editTextColor);
            ((ChipTextWatcher) focusedTextWatcher).setChipTextColor(chipTextColor);

        }
    }

    public int getChipColor() {
        return this.chipColor;
    }

    public void setChipColor(int bgColor) {
        this.chipColor = bgColor;
        this.chipDrawable = null;
        for (int i = 0; i < this.getChildCount(); i++) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(i)).getChildAt(labelPosition);
            if (!autoCompleteTextView.isFocusable()) {
                View v = this.getChildAt(i);
                v.setBackgroundColor(chipColor);
            } else {
                ((ChipTextWatcher) focusedTextWatcher).setChipColor(chipColor);
                ((ChipTextWatcher) focusedTextWatcher).setChipDrawable(null);
            }
        }
    }

    public void highlightChipAt(int pos, int bgColor, int textColor) {
        View v = this.getChildAt(pos);
        v.setBackgroundColor(bgColor);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) v).getChildAt(labelPosition);
        autoCompleteTextView.setTextColor(textColor);

    }

    public void highlightChipAt(int pos, Drawable bgDrawable, int textColor) {
        View v = this.getChildAt(pos);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(bgDrawable);
        } else {
            v.setBackgroundDrawable(bgDrawable);
        }
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) v).getChildAt(labelPosition);
        autoCompleteTextView.setTextColor(textColor);
    }

    public Drawable getChipDrawable() {
        return this.chipDrawable;
    }

    public void setChipDrawable(Drawable bgDrawable) {
        this.chipDrawable = bgDrawable;
        for (int i = 0; i < this.getChildCount(); i++) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(i)).getChildAt(labelPosition);
            if (!autoCompleteTextView.isFocusable()) {
                View v = this.getChildAt(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(chipDrawable);
                } else {
                    v.setBackgroundDrawable(chipDrawable);
                }
            } else {
                ((ChipTextWatcher) focusedTextWatcher).setChipDrawable(chipDrawable);
            }

        }
    }

    public List<O> getObjList() {
        List<O> objList = new ArrayList<>();
        for (int i = 0; i < this.getChildCount(); i++) {
            try {
                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(i)).getChildAt(labelPosition);
                if (autoCompleteTextView.getTag() != null) {
                    String txt = autoCompleteTextView.getTag().toString();
                    if (txt.lastIndexOf(chipSplitFlag) == -1) {
                        objList.add((O) autoCompleteTextView.getTag());
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return objList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setObjList(List<O> vals) {
        this.removeAllViews();
        for (O str : vals) {
            try {
                createNewChipLayout(str, true);
            } catch (Exception ignored) {
            }
        }
    }

    public void removeChipAt(int pos) {
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(pos)).getChildAt(labelPosition);
        this.removeViewAt(pos);
        if (chipItemChangeListener != null) {
            if (autoCompleteTextView.getText() != null && autoCompleteTextView.getText().toString().length() > 0) {
                chipItemChangeListener.onChipRemoved(pos, (O) autoCompleteTextView.getTag());
            } else {
                chipItemChangeListener.onChipRemoved(pos, null);
            }
        }
        updateHint();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setLayoutBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(drawable);
        } else {
            this.setBackgroundDrawable(drawable);
        }
    }

    public ArrayAdapter<O> getAdapter() {
        return this.adapter;
    }

    public void setAdapter(ArrayAdapter<O> adapter) {
        this.adapter = adapter;
        if (this.getChildCount() > 0) {
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(this.getChildCount() - 1)).getChildAt(labelPosition);
            autoCompleteTextView.setAdapter(adapter);
        }
    }

    public void addObj(O obj) {
        int totalChips = this.getChildCount() - 1;
        if (totalChips < 0) return;
        getAdapter().add(obj);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(totalChips)).getChildAt(labelPosition);
        autoCompleteTextView.setTag(obj);
        autoCompleteTextView.setText(obj.toString() + chipSplitFlag);
        if (autoCompleteTextView.isFocusable()) {
            autoCompleteTextView.requestFocus();
            toogleSoftInputKeyboard(autoCompleteTextView);
        }

    }

    public String getCurrentText() {
        int totalChips = this.getChildCount() - 1;
        if (totalChips < 0) return null;
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(totalChips)).getChildAt(labelPosition);
        return autoCompleteTextView.getText().toString();
    }

    public void updateHint() {
        int totalChips = this.getChildCount() - 1;
        if (totalChips != 0) return;
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(totalChips)).getChildAt(labelPosition);
        autoCompleteTextView.setHint(chipInitHint);
    }

    /**
     * Check if editable exist chipSplitFlag
     *
     * @param editable
     * @return
     */
    public boolean hasChanged(String editable) {
        if (editable == null) return false;
        if (editable.lastIndexOf(chipSplitFlag) != -1) {
            return true;
        }
        return false;
    }

    /**
     * Check if current text exist chipSplitFlag
     *
     * @return
     */
    public boolean hasChanged() {
        return hasChanged(getCurrentText());
    }

    public O getCurrentObj() {
        try {
            int totalChips = this.getChildCount() - 1;
            if (totalChips < 0) return null;
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ((ViewGroup) this.getChildAt(totalChips)).getChildAt(labelPosition);
            return (O) autoCompleteTextView.getTag();
        } catch (Exception e) {
            return null;
        }
    }

    private void toogleSoftInputKeyboard(AutoCompleteTextView autoCompleteTextView) {
        if (showKeyboardInFocus) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(autoCompleteTextView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public boolean isAutoSplitInActionKey() {
        return autoSplitInActionKey;
    }

    public void setAutoSplitInActionKey(boolean autoSplitInActionKey) {
        this.autoSplitInActionKey = autoSplitInActionKey;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public boolean isShowKeyboardInFocus() {
        return showKeyboardInFocus;
    }

    public void setShowKeyboardInFocus(boolean showKeyboardInFocus) {
        this.showKeyboardInFocus = showKeyboardInFocus;
    }

    public boolean isInitFocus() {
        return initFocus;
    }

    public void setInitFocus(boolean initFocus) {
        this.initFocus = initFocus;
    }

    public int getChipTextColor() {
        return chipTextColor;
    }

    public void setChipTextColor(int chipTextColor) {
        this.chipTextColor = chipTextColor;
    }

    public int getChipHintColor() {
        return chipHintColor;
    }

    public void setChipHintColor(int chipHintColor) {
        this.chipHintColor = chipHintColor;
    }

    public Drawable getDeleteIcon() {
        return deleteIcon;
    }

    public void setDeleteIcon(Drawable deleteIcon) {
        this.deleteIcon = deleteIcon;
    }

    public interface ChipItemChangeListener<O> {
        void onChipAdded(int pos, O data);

        void onChipRemoved(int pos, O data);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.nchipLayout_Layout);

            try {
                gravity = a.getInt(R.styleable.nchipLayout_Layout_android_layout_gravity, -1);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }

}