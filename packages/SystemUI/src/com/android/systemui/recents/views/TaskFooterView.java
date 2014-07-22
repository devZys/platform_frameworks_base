/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.recents.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.recents.RecentsConfiguration;


/** The task footer view */
public class TaskFooterView extends FrameLayout {

    interface TaskFooterViewCallbacks {
        public void onTaskFooterHeightChanged(int height, int maxHeight);
    }

    RecentsConfiguration mConfig;

    TaskFooterViewCallbacks mCb;
    int mFooterHeight;
    int mMaxFooterHeight;
    ObjectAnimator mFooterAnimator;

    public TaskFooterView(Context context) {
        this(context, null);
    }

    public TaskFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TaskFooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mConfig = RecentsConfiguration.getInstance();
        mMaxFooterHeight = mConfig.taskViewLockToAppButtonHeight;
        setFooterHeight(getFooterHeight());
    }

    /** Sets the callbacks for when the footer height changes. */
    void setCallbacks(TaskFooterViewCallbacks cb) {
        mCb = cb;
        mCb.onTaskFooterHeightChanged(mFooterHeight, mMaxFooterHeight);
    }

    /** Sets the footer height. */
    public void setFooterHeight(int footerHeight) {
        if (footerHeight != mFooterHeight) {
            mFooterHeight = footerHeight;
            mCb.onTaskFooterHeightChanged(footerHeight, mMaxFooterHeight);
        }
    }

    /** Gets the footer height. */
    public int getFooterHeight() {
        return mFooterHeight;
    }

    /** Animates the footer into and out of view. */
    void animateFooterVisibility(final boolean visible, int duration) {
        // Return early if there is no footer
        if (mMaxFooterHeight <= 0) return;
        // Return early if we are already in the final state
        if (!visible && getVisibility() != View.VISIBLE) return;
        if (visible && getVisibility() == View.VISIBLE) return;

        // Cancel the previous animation
        if (mFooterAnimator != null) {
            mFooterAnimator.removeAllListeners();
            mFooterAnimator.cancel();
        }
        int finalHeight = visible ? mMaxFooterHeight : 0;
        if (duration > 0) {
            // Make the view visible for the animation
            if (visible && getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);
            }
            mFooterAnimator = ObjectAnimator.ofInt(this, "footerHeight", finalHeight);
            mFooterAnimator.setDuration(duration);
            mFooterAnimator.setInterpolator(mConfig.fastOutSlowInInterpolator);
            mFooterAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!visible) {
                        setVisibility(View.INVISIBLE);
                    }
                }
            });
            mFooterAnimator.start();
        } else {
            setFooterHeight(finalHeight);
            setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
