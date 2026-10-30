package vn.edu.hust.tictactoe.ui.game

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.google.android.material.color.MaterialColors
import vn.edu.hust.tictactoe.R
import vn.edu.hust.tictactoe.domain.Player
import kotlin.math.min

/** Một ô cờ tự vẽ: quân được vẽ dần, có vầng sáng; ô thắng nhấp nháy. */
// [KT Ch3.2] kế thừa View: ô cờ tự vẽ
class CellView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var mark: Player? = null
        private set

    private var progress = 1f      // 0..1: quân đã vẽ được bao nhiêu
    private var glowLevel = 0f     // 0..1: độ sáng khi nhấp nháy
    private var highlighted = false
    private var drawAnimator: ValueAnimator? = null
    private var pulseAnimator: ValueAnimator? = null

    private val markPaint = newPaint()
    private val glowPaint = newPaint()
    private val oval = RectF()

    private val xColor = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, 0)
    private val oColor = MaterialColors.getColor(context, com.google.android.material.R.attr.colorTertiary, 0)

    init {
        // BlurMaskFilter (vầng sáng) chỉ chạy với layer phần mềm
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        contentDescription = context.getString(R.string.empty_cell)
    }

    /** Đặt quân cho ô. [animate] = true thì vẽ dần (dùng khi vừa đánh xong). */
    fun setMark(player: Player?, animate: Boolean) {
        drawAnimator?.cancel()
        mark = player
        contentDescription = player?.name ?: context.getString(R.string.empty_cell)
        if (player != null && animate) {
            // [KT Android] ValueAnimator tăng progress 0 → 1, mỗi khung gọi invalidate() để vẽ lại
            drawAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = DRAW_MS
                interpolator = DecelerateInterpolator()
                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            progress = 1f
            invalidate()
        }
    }

    /** Bật/tắt nhấp nháy cho ô thuộc đường thắng. */
    fun setHighlighted(on: Boolean) {
        if (on == highlighted) return
        highlighted = on
        pulseAnimator?.cancel()
        pulseAnimator = null
        if (on) {
            pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = PULSE_MS
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    glowLevel = it.animatedValue as Float
                    val scale = 1f + PULSE_SCALE * glowLevel
                    scaleX = scale
                    scaleY = scale
                    invalidate()
                }
                start()
            }
        } else {
            glowLevel = 0f
            scaleX = 1f
            scaleY = 1f
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val stroke = min(w, h) * STROKE_RATIO
        markPaint.strokeWidth = stroke
        glowPaint.strokeWidth = stroke * 1.6f
        glowPaint.maskFilter = BlurMaskFilter(stroke.coerceAtLeast(1f), BlurMaskFilter.Blur.NORMAL)
    }

    // [KT Android] onDraw: vẽ quân bằng Canvas (drawLine cho X, drawArc cho O)
    override fun onDraw(canvas: Canvas) {
        // [KT Ch1] Elvis + return: ô trống thì không vẽ gì
        val player = mark ?: return
        val color = if (player == Player.X) xColor else oColor
        glowPaint.color = color
        glowPaint.alpha = 70 + (110 * glowLevel).toInt()
        markPaint.color = color
        drawShape(canvas, player, glowPaint)
        drawShape(canvas, player, markPaint)
    }

    private fun drawShape(canvas: Canvas, player: Player, paint: Paint) {
        val size = min(width, height).toFloat()
        val inset = size * INSET_RATIO
        val end = size - inset
        if (player == Player.X) {
            // nét thứ nhất vẽ trong nửa đầu, nét thứ hai trong nửa sau
            val t1 = (progress * 2f).coerceAtMost(1f)
            val t2 = ((progress - 0.5f) * 2f).coerceIn(0f, 1f)
            canvas.drawLine(inset, inset, inset + (end - inset) * t1, inset + (end - inset) * t1, paint)
            if (t2 > 0f) {
                canvas.drawLine(end, inset, end - (end - inset) * t2, inset + (end - inset) * t2, paint)
            }
        } else {
            oval.set(inset, inset, end, end)
            canvas.drawArc(oval, -90f, 360f * progress, false, paint)
        }
    }

    override fun onDetachedFromWindow() {
        drawAnimator?.cancel()
        pulseAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun newPaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    // [KT Ch3.4] companion object: hằng số thời gian và tỉ lệ
    companion object {
        private const val DRAW_MS = 260L
        private const val PULSE_MS = 450L
        private const val PULSE_SCALE = 0.08f
        private const val STROKE_RATIO = 0.11f
        private const val INSET_RATIO = 0.26f
    }
}
