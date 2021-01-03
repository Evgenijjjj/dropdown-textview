package ru.evgenymotorin.dropdown_textview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd

class DropDownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {
    private var isExpanded = false
    private var isInitialHeightSet = false
    private var initialHeight = 0
    private var duration = DEFAULT_DURATION
    private val onClick = OnClickListener { toggleIsExpanded() }
    private var animator: ValueAnimator? = null
    private var animate = true

    init {
        if (attrs != null) {
            var ta: TypedArray? = null
            try {
                ta = context.obtainStyledAttributes(attrs, R.styleable.DropDownTextView)
                animate = ta?.getBoolean(R.styleable.DropDownTextView_ddtv_animate, DEFAULT_ANIMATE)
                    ?: DEFAULT_ANIMATE
                duration = ta?.getInt(
                    R.styleable.DropDownTextView_ddtv_animationDuration,
                    DEFAULT_DURATION.toInt()
                )?.toLong() ?: DEFAULT_DURATION
            } finally {
                ta?.recycle()
            }
        }

        setOnClickListener(onClick)
        post {
            if (isExpanded) {
                showAllContent(animate = false)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isInitialHeightSet) {
            isInitialHeightSet = true
            initialHeight = measuredHeight
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val state = SavedState(super.onSaveInstanceState())
        state.isExpanded = this.isExpanded
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            this.isExpanded = state.isExpanded
        }
    }

    private fun showAllContent(animate: Boolean = true) {
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        measure(widthMeasureSpec, heightMeasureSpec)
        if (animate) {
            animate(from = height, to = measuredHeight)
        } else {
            layoutParams.height = measuredHeight
            requestLayout()
        }
    }

    private fun showMeasuredContent(animate: Boolean = true) {
        if (animate) {
            animate(from = height, to = initialHeight)
        } else {
            layoutParams.height = initialHeight
            requestLayout()
        }
    }

    private fun animate(from: Int, to: Int) {
        animator = ValueAnimator.ofInt(from, to).apply {
            duration = this@DropDownTextView.duration
            interpolator = LinearInterpolator()
            addUpdateListener {
                layoutParams.height = it.animatedValue as Int
                requestLayout()
            }
            doOnEnd {
                animator = null
            }
        }
        animator?.start()
    }

    private fun toggleIsExpanded() {
        if (animator != null) return
        if (isExpanded) {
            showMeasuredContent(animate = this.animate)
        } else {
            showAllContent(animate = this.animate)
        }
        isExpanded = !isExpanded
    }

    private class SavedState : BaseSavedState, Parcelable {
        var isExpanded = false

        constructor(parcel: Parcel?) : super(parcel) {
            if (parcel == null) return
            isExpanded = parcel.readInt() != 0
        }

        constructor(superState: Parcelable?) : super(superState)

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (isExpanded) 1 else 0)
        }
    }

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val DEFAULT_ANIMATE = true
    }
}