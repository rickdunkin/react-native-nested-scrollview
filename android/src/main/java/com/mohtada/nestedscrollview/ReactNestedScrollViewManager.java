/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.mohtada.nestedscrollview;

import javax.annotation.Nullable;

import java.util.Map;

import android.graphics.Color;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.views.scroll.FpsListener;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.views.scroll.ScrollEventType;
import com.facebook.react.uimanager.ReactClippingViewGroupHelper;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.facebook.react.views.scroll.ReactScrollViewCommandHelper;

/**
 * Forked from https://github.com/facebook/react-native/blob/v0.42.0/ReactAndroid/src/main/java/com/facebook/react/views/scroll/ReactScrollViewManager.java
 *
 * View manager for {@link ReactScrollView} components.
 *
 * <p>Note that {@link ReactScrollView} and {@link ReactHorizontalScrollView} are exposed to JS
 * as a single ScrollView component, configured via the {@code horizontal} boolean property.
 */
public class ReactNestedScrollViewManager
    extends ViewGroupManager<ReactNestedScrollView>
    implements ReactScrollViewCommandHelper.ScrollCommandHandler<ReactNestedScrollView> {

    private static final String REACT_CLASS = "RCTNestedScrollView";
    private @Nullable FpsListener mFpsListener = null;

    public ReactNestedScrollViewManager() {
        this(null);
    }

    public ReactNestedScrollViewManager(@Nullable FpsListener fpsListener) {
        mFpsListener = fpsListener;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public ReactNestedScrollView createViewInstance(ThemedReactContext context) {
        return new ReactNestedScrollView(context, mFpsListener);
    }

    @Override
    public void flashScrollIndicators(ReactNestedScrollView scrollView) {
        // @TODO
        // scrollView.flashScrollIndicators();
    }

    @ReactProp(name = "scrollEnabled", defaultBoolean = true)
    public void setScrollEnabled(ReactNestedScrollView view, boolean value) {
        view.setScrollEnabled(value);
    }

    @ReactProp(name = "showsVerticalScrollIndicator")
    public void setShowsVerticalScrollIndicator(ReactNestedScrollView view, boolean value) {
        view.setVerticalScrollBarEnabled(value);
    }

    @ReactProp(name = ReactClippingViewGroupHelper.PROP_REMOVE_CLIPPED_SUBVIEWS)
    public void setRemoveClippedSubviews(ReactNestedScrollView view, boolean removeClippedSubviews) {
        view.setRemoveClippedSubviews(removeClippedSubviews);
    }

    /**
     * Computing momentum events is potentially expensive since we post a runnable on the UI thread
     * to see when it is done.  We only do that if {@param sendMomentumEvents} is set to true.  This
     * is handled automatically in js by checking if there is a listener on the momentum events.
     *
     * @param view
     * @param sendMomentumEvents
     */
    @ReactProp(name = "sendMomentumEvents")
    public void setSendMomentumEvents(ReactNestedScrollView view, boolean sendMomentumEvents) {
        view.setSendMomentumEvents(sendMomentumEvents);
    }

    /**
     * Tag used for logging scroll performance on this scroll view. Will force momentum events to be
     * turned on (see setSendMomentumEvents).
     *
     * @param view
     * @param scrollPerfTag
     */
    @ReactProp(name = "scrollPerfTag")
    public void setScrollPerfTag(ReactNestedScrollView view, String scrollPerfTag) {
        view.setScrollPerfTag(scrollPerfTag);
    }

    /**
     * When set, fills the rest of the scrollview with a color to avoid setting a background and
     * creating unnecessary overdraw.
     * @param view
     * @param color
     */
    @ReactProp(name = "endFillColor", defaultInt = Color.TRANSPARENT, customType = "Color")
    public void setBottomFillColor(ReactNestedScrollView view, int color) {
        view.setEndFillColor(color);
    }

    /**
     * Controls overScroll behaviour
     */
    @ReactProp(name = "overScrollMode")
    public void setOverScrollMode(ReactNestedScrollView view, String value) {
        view.setOverScrollMode(ReactScrollViewHelper.parseOverScrollMode(value));
    }

    @Override
    public @Nullable Map<String, Integer> getCommandsMap() {
        return ReactScrollViewCommandHelper.getCommandsMap();
    }

    @Override
    public void receiveCommand(
        ReactNestedScrollView scrollView,
        int commandId,
        @Nullable ReadableArray args) {
        ReactScrollViewCommandHelper.receiveCommand(this, scrollView, commandId, args);
    }

    @Override
    public void scrollTo(
        ReactNestedScrollView scrollView,
        ReactScrollViewCommandHelper.ScrollToCommandData data) {
        if (data.mAnimated) {
            scrollView.smoothScrollTo(data.mDestX, data.mDestY);
        } else {
            scrollView.scrollTo(data.mDestX, data.mDestY);
        }
    }

    @Override
    public void scrollToEnd(
        ReactNestedScrollView scrollView,
        ReactScrollViewCommandHelper.ScrollToEndCommandData data) {
      // ScrollView always has one child - the scrollable area
      int bottom =
        scrollView.getChildAt(0).getHeight() + scrollView.getPaddingBottom();
      if (data.mAnimated) {
        scrollView.smoothScrollTo(scrollView.getScrollX(), bottom);
      } else {
        scrollView.scrollTo(scrollView.getScrollX(), bottom);
      }
    }

    @Override
    public @Nullable Map getExportedCustomDirectEventTypeConstants() {
        return createExportedCustomDirectEventTypeConstants();
    }

    public static Map createExportedCustomDirectEventTypeConstants() {
        return MapBuilder.builder()
            .put(ScrollEventType.getJSEventName(ScrollEventType.SCROLL), MapBuilder.of("registrationName", "onScroll"))
            .put(ScrollEventType.getJSEventName(ScrollEventType.BEGIN_DRAG), MapBuilder.of("registrationName", "onScrollBeginDrag"))
            .put(ScrollEventType.getJSEventName(ScrollEventType.END_DRAG), MapBuilder.of("registrationName", "onScrollEndDrag"))
            .put(ScrollEventType.getJSEventName(ScrollEventType.MOMENTUM_BEGIN), MapBuilder.of("registrationName", "onMomentumScrollBegin"))
            .put(ScrollEventType.getJSEventName(ScrollEventType.MOMENTUM_END), MapBuilder.of("registrationName", "onMomentumScrollEnd"))
            .build();
    }
}
