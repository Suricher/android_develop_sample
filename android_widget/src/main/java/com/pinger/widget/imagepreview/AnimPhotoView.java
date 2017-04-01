package com.pinger.widget.imagepreview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import uk.co.senab.photoview.PhotoView;

public class AnimPhotoView extends PhotoView {
    private static final int ANIM_DURATION = 300;

    private BitmapTransform bitmapTransform;
    private OnEnterAnimEndListener onEnterAnimEndListener;
    private OnExitAnimEndListener onExitAnimEndListener;

    private boolean isPlayingEnterAnim = false;

    private Point globalOffset;
    private RectF clipBounds;

    public AnimPhotoView(Context context) {
        super(context);
    }

    public AnimPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public AnimPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        bitmapTransform = new BitmapTransform();
        globalOffset = new Point();
    }

    @Override
    public void draw(Canvas canvas) {
        if (clipBounds != null) {
            canvas.clipRect(clipBounds);
            clipBounds = null;
        }
        super.draw(canvas);
    }

    public void playEnterAnim(final Rect from, final View container, @Nullable final OnEnterAnimEndListener l) {
        this.onEnterAnimEndListener = l;
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                playEnterAnimInternal(from,container);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void playEnterAnimInternal(final Rect from, View container) {
        if (isPlayingEnterAnim || from == null) return;

        final Rect tFrom = new Rect(from);
        final Rect to = new Rect();

        getGlobalVisibleRect(to, globalOffset);

        tFrom.offset(-globalOffset.x, -globalOffset.y);
        to.offset(-globalOffset.x, -globalOffset.y);

        float[] scaleRatios = calculateRatios(tFrom, to);

        setPivotX(tFrom.centerX() / to.width());
        setPivotY(tFrom.centerY() / to.height());

        final AnimatorSet enterSet = new AnimatorSet();
        enterSet.play(ObjectAnimator.ofFloat(this, View.X, tFrom.left, to.left))
                .with(ObjectAnimator.ofFloat(this, View.Y, tFrom.top, to.top))
                .with(ObjectAnimator.ofFloat(this, View.SCALE_X, scaleRatios[0], 1f))
                .with(ObjectAnimator.ofFloat(this, View.SCALE_Y, scaleRatios[1], 1f));

        enterSet.setDuration(ANIM_DURATION);

        Animation animation = new AlphaAnimation(0, 1.0f);
        animation.setDuration(ANIM_DURATION);
        container.setAnimation(animation);

        enterSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isPlayingEnterAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isPlayingEnterAnim = false;
                if (onEnterAnimEndListener != null) {
                    onEnterAnimEndListener.onEnterAnimEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isPlayingEnterAnim = false;

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isPlayingEnterAnim = true;

            }
        });
        enterSet.start();
    }


    public void playExitAnim(Rect to, @Nullable View alphaView, @Nullable final OnExitAnimEndListener listener) {
        this.onExitAnimEndListener = listener;
        playExitAnimInternal(to, alphaView);
    }

    private void playExitAnimInternal(final Rect to, @Nullable View alphaView) {
        if (isPlayingEnterAnim || to == null || mAttacher == null) {
            if (onExitAnimEndListener != null) {
                onExitAnimEndListener.onExitAnimEnd();
            }
            return;
        }

        final float currentScale = getScale();
        if (currentScale > 1.0f) setScale(1.0f);

        final Rect from = getViewBounds();
        final Rect drawableBounds = getDrawableBounds(getDrawable());
        final Rect target = new Rect(to);
        from.offset(-globalOffset.x, -globalOffset.y);
        target.offset(-globalOffset.x, -globalOffset.y);
        if (drawableBounds == null) {
            if (onExitAnimEndListener != null) {
                onExitAnimEndListener.onExitAnimEnd();
            }
            return;
        }

        //bitmap位移
        bitmapTransform.animTranslate(from.centerX(), target.centerX(), from.centerY(), target.centerY());

        float scale = calculateScaleByCenterCrop(from, drawableBounds, target);

        // 等比缩放
        bitmapTransform.animScale(getScale(), scale, from.centerX(), from.centerY());

        if (alphaView != null) {
            bitmapTransform.animAlpha(alphaView, 1.0f, 0);
        }

        if (target.width() < from.width() || target.height() < from.height()) {
            bitmapTransform.animClip(from, target);
        }


        bitmapTransform.start(new OnAllFinishListener() {
            @Override
            public void onAllFinish() {
                if (onExitAnimEndListener != null) {
                    onExitAnimEndListener.onExitAnimEnd();
                }
            }
        });


    }

    private float calculateScaleByCenterCrop(Rect from, Rect drawableBounds, Rect target) {
        int viewWidth = from.width();
        int viewHeight = from.height();
        int imageHeight = drawableBounds.height();
        int imageWidth = drawableBounds.width();

        float result;


        /**
         * 假设原始图片高h，宽w ，Imageview的高y，宽x
         * 判断高宽比例，如果目标高宽比例大于原图，则原图高度不变，宽度为(w1 = (h * x) / y)拉伸
         * 画布宽高(w1,h),在原图的((w - w1) / 2, 0)位置进行切割
         */
        int t = (viewHeight / viewWidth) - (imageHeight / imageWidth);

        if (t > 0) {
            int w1 = (imageHeight * viewWidth) / viewHeight;
            result = Math.min(target.width() * 1.0f / w1 * 1.0f, target.height() * 1.0f / imageHeight * 1.0f);

        } else {
            int h1 = (viewHeight * imageWidth) / viewWidth;
            result = Math.max(target.width() * 1.0f / imageWidth * 1.0f, target.height() * 1.0f / h1 * 1.0f);
        }

        return result;
    }

    /**
     * 获取view的大小
     *
     * @return
     */
    public Rect getViewBounds() {
        Rect result = new Rect();
        getGlobalVisibleRect(result, globalOffset);
        return result;
    }

    private Rect rectF2rect(RectF rectf) {
        Rect rect = new Rect();
        if (rectf == null) {
            rect.setEmpty();
        } else {
            rect.left = (int) rectf.left;
            rect.top = (int) rectf.top;
            rect.right = (int) rectf.right;
            rect.bottom = (int) rectf.bottom;
        }
        return rect;
    }

    private Rect getDrawableBounds(Drawable d) {
        if (d == null) return null;
        Rect result = new Rect();
        Rect tDrawableRect = d.getBounds();
        Matrix drawableMatrix = getImageMatrix();

        float[] values = new float[9];
        drawableMatrix.getValues(values);

        result.left = (int) values[Matrix.MTRANS_X];
        result.top = (int) values[Matrix.MTRANS_Y];
        result.right = (int) (result.left + tDrawableRect.width() * values[Matrix.MSCALE_X]);
        result.bottom = (int) (result.top + tDrawableRect.height() * values[Matrix.MSCALE_Y]);

        return result;
    }


    private float[] calculateRatios(Rect startBounds, Rect endBounds) {
        float[] result = new float[2];
        float widthRatio = startBounds.width() * 1.0f / endBounds.width() * 1.0f;
        float heightRatio = startBounds.height() * 1.0f / endBounds.height() * 1.0f;
        result[0] = widthRatio;
        result[1] = heightRatio;
        return result;
    }


    public interface OnEnterAnimEndListener {
        void onEnterAnimEnd();
    }

    public interface OnExitAnimEndListener {
        void onExitAnimEnd();
    }

    interface OnAllFinishListener {
        void onAllFinish();
    }

    private class BitmapTransform implements Runnable {

        static final float PRECISION = 10000f;

        View targetView;

        volatile boolean isRunning;

        Scroller translateScroller;
        Scroller scaleScroller;
        Scroller alphaScroller;
        Scroller clipScroller;

        Interpolator defaultInterpolator = new DecelerateInterpolator();


        int scaleCenterX;
        int scaleCenterY;

        float scaleX;
        float scaleY;

        float alpha;

        int dx;
        int dy;

        int preTranslateX;
        int preTranslateY;

        RectF mClipRect;
        RectF clipTo;
        RectF clipFrom;
        Matrix tempMatrix;

        OnAllFinishListener onAllFinishListener;


        BitmapTransform() {
            isRunning = false;
            translateScroller = new Scroller(getContext(), defaultInterpolator);
            scaleScroller = new Scroller(getContext(), defaultInterpolator);
            alphaScroller = new Scroller(getContext(), defaultInterpolator);
            clipScroller = new Scroller(getContext(), defaultInterpolator);
            mClipRect = new RectF();
            tempMatrix = new Matrix();
        }

        void animScale(float fromX, float toX, float fromY, float toY, int centerX, int centerY) {
            this.scaleCenterX = centerX;
            this.scaleCenterY = centerY;
            scaleScroller.startScroll((int) (fromX * PRECISION), (int) (fromY * PRECISION), (int) ((toX - fromX) * PRECISION), (int) ((toY - fromY) * PRECISION), ANIM_DURATION);
        }

        void animScale(float from, float to, int centerX, int centerY) {
            animScale(from, to, from, to, centerX, centerY);
        }

        void animTranslate(int fromX, int toX, int fromY, int toY) {
            preTranslateX = 0;
            preTranslateY = 0;
            translateScroller.startScroll(0, 0, toX - fromX, toY - fromY, ANIM_DURATION);
        }

        void animAlpha(View target, float fromAlpha, float toAlpha) {
            this.targetView = target;
            alphaScroller.startScroll((int) (fromAlpha * PRECISION), 0, (int) ((toAlpha - fromAlpha) * PRECISION), 0, ANIM_DURATION);
        }

        void animClip(Rect clipFrom, Rect clipTo) {
            this.clipFrom = new RectF(clipFrom);
            this.clipTo = new RectF(clipTo);

            if (!clipFrom.isEmpty() && !clipTo.isEmpty()) {
                //算出裁剪比率
                float dx = Math.min(1.0f, clipTo.width() * 1.0f / clipFrom.width() * 1.0f);
                float dy = Math.min(1.0f, clipTo.height() * 1.0f / clipFrom.height() * 1.0f);
                //因为scroller是对起始值和终点值之间的数值影响，所以减去1，如果为0，意味着不裁剪，因为目标值比开始值大，而画布无法再扩大了，所以忽略
                dx = dx - 1;
                dy = dy - 1;
                //从1开始,乘以1w保证精度
                clipScroller.startScroll((int) (0 * PRECISION), (int) (0 * PRECISION), (int) (dx * PRECISION), (int) (dy * PRECISION), ANIM_DURATION);
            }
        }

        @Override
        public void run() {

            boolean isAllFinish = true;

            if (scaleScroller.computeScrollOffset()) {
                scaleX = (float) scaleScroller.getCurrX() / PRECISION;
                scaleY = (float) scaleScroller.getCurrY() / PRECISION;

                isAllFinish = false;
            }

            if (translateScroller.computeScrollOffset()) {
                int curX = translateScroller.getCurrX();
                int curY = translateScroller.getCurrY();

                dx += curX - preTranslateX;
                dy += curY - preTranslateY;


                preTranslateX = curX;
                preTranslateY = curY;

                isAllFinish = false;
            }

            if (alphaScroller.computeScrollOffset()) {
                alpha = (float) alphaScroller.getCurrX() / PRECISION;
                isAllFinish = false;
            }

            if (clipScroller.computeScrollOffset()) {

                float curX = Math.abs((float) clipScroller.getCurrX() / PRECISION);
                float curY = Math.abs((float) clipScroller.getCurrY() / PRECISION);


                //算出当前的移动像素的综合
                float dx = clipFrom.width() * curX;
                float dy = clipFrom.height() * curY;


                /*
                 裁剪动画算法是一个。。。初中的简单方程题

                 设裁剪过程中，左边缘移动dl个像素，右边缘移动dr个像素
                 由上面dx算出dl+dr=dx;
                 因为d和dr的比率可知，因此可以知道dLeft和dRight的相对速率
                 比如左边裁剪1像素，而右边宽度是左边的3倍，则右边应该裁剪3像素才追得上左边
                 联立方程：
                 1：dl+dr=dx;
                 2：dl/dr=a;
                 则由2可得
                 dl=a*dr;
                 代入1
                 dr*(a+1)=dx;
                 可以算得出dr
                 再次代入1可知
                 dl=dx-dr
                 */

                float ratiofLeftAndRight = Math.abs(clipFrom.left - clipTo.left) / Math.abs(clipFrom.right - clipTo.right);
                float ratiofTopAndBottom = Math.abs(clipFrom.top - clipTo.top) / Math.abs(clipFrom.bottom - clipTo.bottom);

                float dClipRight = dx / (ratiofLeftAndRight + 1);
                float dClipLeft = dx - dClipRight;
                float dClipBottom = dy / (ratiofTopAndBottom + 1);
                float dClipTop = dy - dClipBottom;


                mClipRect.left = clipFrom.left + dClipLeft;
                mClipRect.right = clipFrom.right - dClipRight;

                mClipRect.top = clipFrom.top + dClipTop;
                mClipRect.bottom = clipFrom.bottom - dClipBottom;

                if (!mClipRect.isEmpty()) {
                    clipBounds = mClipRect;
                }

                isAllFinish = false;

            }

            if (!isAllFinish) {
                setMatrixValue();
                postExecuteSelf();
            } else {
                isRunning = false;
                reset();
                if (onAllFinishListener != null) {
                    onAllFinishListener.onAllFinish();
                }
            }

        }

        private void setMatrixValue() {
            if (mAttacher == null) return;
            resetSuppMatrix();
            postMatrixScale(scaleX, scaleY, scaleCenterX, scaleCenterY);
            postMatrixTranslate(dx, dy);
            if (targetView != null) targetView.setAlpha(alpha);
            applyMatrix();
        }

        private void postExecuteSelf() {
            if (isRunning) post(this);
        }

        private void reset() {
            scaleCenterX = 0;
            scaleCenterY = 0;

            scaleX = 0;
            scaleY = 0;

            dx = 0;
            dy = 0;

            preTranslateX = 0;
            preTranslateY = 0;

            alpha = 0;
        }

        void stop(boolean reset) {
            removeCallbacks(this);
            scaleScroller.abortAnimation();
            translateScroller.abortAnimation();
            alphaScroller.abortAnimation();
            clipScroller.abortAnimation();

            isRunning = false;
            onAllFinishListener = null;
            if (reset) reset();
        }

        void start(@Nullable OnAllFinishListener onAllFinishListener) {
            if (isRunning) stop(false);
            this.onAllFinishListener = onAllFinishListener;
            isRunning = true;
            postExecuteSelf();
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        bitmapTransform.stop(true);
        super.onDetachedFromWindow();
    }


    /**
     * TODO 触摸时做透明和缩放动画
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}