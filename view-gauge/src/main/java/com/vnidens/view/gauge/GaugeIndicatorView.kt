package com.vnidens.view.gauge

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.use
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * VehicleIndicators
 *
 * Created by Victor Nidens
 * Date: 14.11.2020
 */
class GaugeIndicatorView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val logger = LoggerFactory.getLogger(GaugeIndicatorView::class.java)

    private val gaugeStartAngle = GAUGE_INDICATOR_START_ANGLE
    private val gaugeEndAngle = -ANGLE_180 - GAUGE_INDICATOR_START_ANGLE
    private val gaugeSector = abs(gaugeEndAngle - gaugeStartAngle)

    private var handColor: Int = HandParams.COLOR_DEFAULT
    private var scalesPrimaryStrokeColor: Int = ScalesPrimaryParams.COLOR_DEFAULT
    private var scalesPrimaryDangerStrokeColor: Int = ScalesPrimaryParams.DANGER_COLOR_DEFAULT
    private var scalesSecondaryStrokeColor: Int = ScalesSecondaryParams.COLOR_DEFAULT
    private var scalesSecondaryDangerColor: Int = ScalesSecondaryParams.DANGER_COLOR_DEFAULT
    private var strokeTextPadding: Float =
        STROKE_TEXT_PADDING_DEFAULT_DP * context.resources.displayMetrics.density
    private var textTypeface: Typeface = LabelParams.TYPEFACE_DEFAULT

    private val outlinePaint = Paint()
    private val handPaint = Paint()
    private val scalesPrimaryPaint = Paint()
    private val scalesPrimaryDangerPaint = Paint()
    private val scalesSecondaryPaint = Paint()
    private val scalesSecondaryDangerPaint = Paint()
    private val textPaint = Paint()

    private val valueSector: Float
        get() = gaugeSector / maxValue

    private val primaryScaleSector: Float
        get() = gaugeSector / (primaryScalesCount + primaryDangerousScalesCount)

    private val primaryScalesCount: Int
        get() = dangerousValue / primaryScalesStep

    private val primaryDangerousScalesCount: Int
        get() = (maxValue - dangerousValue) / primaryScalesStep

    private val secondaryScaleSector: Float
        get() = gaugeSector / (secondaryScalesCount + secondaryScalesDangerousCount)

    private val secondaryScalesCount: Int
        get() = dangerousValue / secondaryScalesStep

    private val secondaryScalesDangerousCount: Int
        get() = (maxValue - dangerousValue) / secondaryScalesStep

    private var gaugeViewParams: GaugeViewParams? = null

    private val currentValueAnimator = CurrentValueAnimator()

    private var currentAnimatedValue = 0f
        set(value) {
            field = value
            invalidate()
        }

    var currentValue = 0f
        set(value) {

            field = when {
                value < 0 -> 0f
                value > maxValue -> maxValue.toFloat()
                else -> value
            }

            currentValueAnimator.restartAnimation(field)
        }

    var maxValue = MAX_VALUE_DEFAULT

    var dangerousValue = maxValue

    var primaryScalesStep = PRIMARY_SCALES_STEP_DEFAULT

    var primaryScalesLabelStep = PRIMARY_SCALES_LABEL_STEP_DEFAULT

    var secondaryScalesStep = SECONDARY_SCALES_STEP_DEFAULT

    init {
        initStyleProperties(context, attrs, defStyleAttr, defStyleRes)
        initPaints()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        initGaugeParams(w.toFloat(), h.toFloat())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val drawRect = getDrawAreaRectF(widthSize.toFloat(), heightSize.toFloat())

        setMeasuredDimension(
            getDefaultSize(drawRect.width().toInt(), widthMeasureSpec),
            getDefaultSize(drawRect.height().toInt(), heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return

        gaugeViewParams?.also { viewParams ->
            canvas.draw(viewParams)
        }
    }

    private fun initStyleProperties(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GaugeIndicatorView,
            defStyleAttr,
            defStyleRes
        )
            .use { array ->

                currentValue = array.getFloat(
                    R.styleable.GaugeIndicatorView_giv_currentValue,
                    0f
                )

                maxValue = array.getInteger(
                    R.styleable.GaugeIndicatorView_giv_maxValue,
                    MAX_VALUE_DEFAULT
                )

                dangerousValue = array.getInteger(
                    R.styleable.GaugeIndicatorView_giv_dangerousValue,
                    maxValue
                )

                primaryScalesStep = array.getInteger(
                    R.styleable.GaugeIndicatorView_giv_primaryScalesStep,
                    PRIMARY_SCALES_STEP_DEFAULT
                )

                primaryScalesLabelStep = array.getInteger(
                    R.styleable.GaugeIndicatorView_giv_primaryScalesLabelStep,
                    PRIMARY_SCALES_LABEL_STEP_DEFAULT
                )

                secondaryScalesStep = array.getInteger(
                    R.styleable.GaugeIndicatorView_giv_secondaryScalesStep,
                    SECONDARY_SCALES_STEP_DEFAULT
                )

                handColor = array.getColor(
                    R.styleable.GaugeIndicatorView_giv_handColor,
                    HandParams.COLOR_DEFAULT
                )

                scalesSecondaryStrokeColor = array.getColor(
                    R.styleable.GaugeIndicatorView_giv_strokeColor,
                    ScalesSecondaryParams.COLOR_DEFAULT
                )

                strokeTextPadding = array.getDimension(
                    R.styleable.GaugeIndicatorView_giv_strokeTextPadding,
                    STROKE_TEXT_PADDING_DEFAULT_DP * context.resources.displayMetrics.density
                )

                textTypeface = array.getString(R.styleable.GaugeIndicatorView_giv_font)
                    ?.let { path ->
                        try {
                            if (path.startsWith("/")) {
                                Typeface.createFromFile(path)
                            } else {
                                Typeface.createFromAsset(context.assets, path)
                            }
                        } catch (e: Exception) {
                            logger.warn("Cannot access referenced font resources.\n", e)
                            LabelParams.TYPEFACE_DEFAULT
                        }
                    }
                    ?: LabelParams.TYPEFACE_DEFAULT
            }
    }

    private fun initPaints() {
        initOutlinePaint()
        initScalesPrimaryPaint()
        initScalesPrimaryDangerPaint()
        initScalesSecondaryPaint()
        initScalesSecondaryDangerPaint()
        initHandPaint()
        initTextPaint()
    }

    private fun initOutlinePaint() {
        with(outlinePaint) {
            style = Paint.Style.STROKE
            color = Color.BLUE
            strokeWidth = OutlineParams.WIDTH
            isAntiAlias = true
        }
    }

    private fun initScalesPrimaryPaint() {
        with(scalesPrimaryPaint) {
            style = Paint.Style.STROKE
            color = scalesPrimaryStrokeColor
            strokeWidth = ScalesPrimaryParams.STROKE_LENGTH
            isAntiAlias = true
        }
    }

    private fun initScalesPrimaryDangerPaint() {
        with(scalesPrimaryDangerPaint) {
            style = Paint.Style.STROKE
            color = scalesPrimaryDangerStrokeColor
            strokeWidth = ScalesPrimaryParams.STROKE_LENGTH
            isAntiAlias = true
        }
    }

    private fun initScalesSecondaryPaint() {
        with(scalesSecondaryPaint) {
            style = Paint.Style.STROKE
            color = scalesSecondaryStrokeColor
            strokeWidth = ScalesSecondaryParams.STROKE_LENGTH
            isAntiAlias = true
        }
    }

    private fun initScalesSecondaryDangerPaint() {
        with(scalesSecondaryDangerPaint) {
            style = Paint.Style.STROKE
            color = scalesSecondaryDangerColor
            strokeWidth = ScalesSecondaryParams.STROKE_LENGTH
            isAntiAlias = true
        }
    }

    private fun initHandPaint() {
        with(handPaint) {
            style = Paint.Style.FILL
            color = handColor
            isAntiAlias = true
        }
    }

    private fun initTextPaint() {
        with(textPaint) {
            color = Color.WHITE
            typeface = textTypeface
            textSize = LabelParams.TEXT_SIZE
            isAntiAlias = true
        }
    }

    private fun initGaugeParams(width: Float, height: Float) {
        val area = getDrawAreaRectF(width, height)
        val maxPadding =
            arrayOf(paddingBottom, paddingTop, paddingStart, paddingEnd).maxOfOrNull { it } ?: 0

        val outlineRect = RectF(area).apply {
            val sizeDiff = outlinePaint.strokeWidth / 2

            set(
                left + sizeDiff + maxPadding,
                top + sizeDiff + maxPadding,
                right - sizeDiff - maxPadding,
                bottom - sizeDiff - maxPadding
            )
        }

        val outlineParams = OutlineParams(
            Path().apply {
                addArc(outlineRect, gaugeEndAngle, gaugeSector)
            },
            outlinePaint
        )

        val scalesPrimaryRect = RectF(outlineRect).apply {
            val sizeDiff = outlineParams.paint.strokeWidth / 2 + scalesPrimaryPaint.strokeWidth / 2
            set(
                left + sizeDiff,
                top + sizeDiff,
                right - sizeDiff,
                bottom - sizeDiff
            )
        }

        val scalesPrimaryLabelsRect = RectF(scalesPrimaryRect).apply {
            set(
                left + strokeTextPadding,
                top + strokeTextPadding,
                right - strokeTextPadding,
                bottom - strokeTextPadding
            )
        }

        val scalesSecondaryRect = RectF(outlineRect).apply {
            val sizeDiff =
                outlineParams.paint.strokeWidth / 2 + scalesSecondaryPaint.strokeWidth / 2
            set(
                left + sizeDiff,
                top + sizeDiff,
                right - sizeDiff,
                bottom - sizeDiff
            )
        }

        val scalesPrimaryParams = ScalesPrimaryParams(
            scalesPrimaryRect.createPrimaryScalesPath(),
            scalesPrimaryPaint
        )

        val scalesPrimaryDangerousParams = ScalesPrimaryParams(
            scalesPrimaryRect.createPrimaryDangerousScalesPath(),
            scalesPrimaryDangerPaint
        )

        val scalesSecondaryParams = ScalesSecondaryParams(
            scalesSecondaryRect.createSecondaryScalesPath(),
            scalesSecondaryPaint
        )

        val scalesSecondaryDangerousParams = ScalesSecondaryParams(
            scalesSecondaryRect.createSecondaryDangerousScalesPath(),
            scalesSecondaryDangerPaint
        )

        val labelsParams = scalesPrimaryLabelsRect.getLabelsParams()

        val handArea = RectF(scalesPrimaryRect).apply {
            val sizeDiff = scalesSecondaryPaint.strokeWidth / 2
            set(
                left + sizeDiff,
                top + sizeDiff,
                right - sizeDiff,
                bottom - sizeDiff
            )
        }

        val handParams = HandParams(
            handArea.createHandPath(),
            handPaint
        )

        gaugeViewParams = GaugeViewParams(
            PointF(area.centerX(), area.centerY()),
            outlineParams,
            scalesPrimaryParams,
            scalesPrimaryDangerousParams,
            scalesSecondaryParams,
            scalesSecondaryDangerousParams,
            labelsParams,
            handParams
        )
    }

    private fun getDrawAreaRectF(viewWidth: Float, viewHeight: Float): RectF {
        val center = PointF(viewWidth / 2, viewHeight / 2)
        val size = min(viewWidth, viewHeight)
        val halfSize = size / 2

        return RectF(
            center.x - halfSize,
            center.y - halfSize,
            center.x + halfSize,
            center.y + halfSize
        )
    }

    private fun RectF.createHandPath(): Path = Path().apply {
        val centerX = centerX()
        val centerY = centerY()

        val handLength = width() / 2

        moveTo(centerX, centerY)
        lineTo(centerX, centerY - (HandParams.WIDTH / 2))
        lineTo(centerX + handLength, centerY)
        lineTo(centerX, centerY + (HandParams.WIDTH / 2))
        lineTo(centerX, centerY)
        addCircle(centerX, centerY, HandParams.CENTER_RADIUS, Path.Direction.CW)
    }

    private fun RectF.createPrimaryScalesPath(): Path = Path().apply {
        val sector = primaryScaleSector

        generateSequence(gaugeEndAngle) { it + sector }
            .take(primaryScalesCount)
            .forEach {
                addArc(this@createPrimaryScalesPath, it, SCALE_STROKE_WIDTH)
            }
    }

    private fun RectF.createPrimaryDangerousScalesPath(): Path = Path().apply {
        val sector = primaryScaleSector

        generateSequence(gaugeEndAngle + sector * primaryScalesCount) { it + sector }
            .take(primaryDangerousScalesCount + 1)
            .forEach {
                addArc(this@createPrimaryDangerousScalesPath, it, SCALE_STROKE_WIDTH)
            }
    }

    private fun RectF.createSecondaryScalesPath(): Path = Path().apply {
        val sector = secondaryScaleSector

        generateSequence(gaugeEndAngle) { it + sector }
            .take(secondaryScalesCount)
            .forEach {
                addArc(this@createSecondaryScalesPath, it, SCALE_STROKE_WIDTH)
            }
    }

    private fun RectF.createSecondaryDangerousScalesPath(): Path = Path().apply {
        val sector = secondaryScaleSector

        generateSequence(gaugeEndAngle + sector * secondaryScalesCount) { it + sector }
            .take(secondaryScalesDangerousCount + 1)
            .forEach {
                addArc(this@createSecondaryDangerousScalesPath, it, SCALE_STROKE_WIDTH)
            }
    }

    private fun RectF.getLabelsParams(): List<LabelParams> {
        val sector = primaryScaleSector.toDouble()
        val centerX = centerX()
        val centerY = centerY()

        val areaRadius = min(width() / 2, height() / 2)

        val textRect = Rect()

        val labels = Array(primaryScalesCount + primaryDangerousScalesCount + 1) { index ->
            val x = centerX + areaRadius * cos(
                Math.toRadians(index * sector + ANGLE_90 + gaugeStartAngle).toFloat()
            )
            val y = centerY + areaRadius * sin(
                Math.toRadians(index * sector + ANGLE_90 + gaugeStartAngle).toFloat()
            )

            val labelText = (index * primaryScalesStep).toString()

            textPaint.getTextBounds(labelText, 0, labelText.length, textRect)

            LabelParams(
                labelText,
                x - textRect.width() / 2,
                y + textRect.height() / 2,
                textPaint
            )
        }

        return labels.toList()
    }

    private fun Canvas.draw(viewParams: GaugeViewParams) {
        drawPath(viewParams.outline.path, viewParams.outline.paint)

        drawPath(viewParams.scalesSecondary.path, viewParams.scalesSecondary.paint)
        drawPath(
            viewParams.scalesSecondaryDangerous.path,
            viewParams.scalesSecondaryDangerous.paint
        )

        drawPath(viewParams.scalesPrimary.path, viewParams.scalesPrimary.paint)
        drawPath(viewParams.scalesPrimaryDangerous.path, viewParams.scalesPrimaryDangerous.paint)

        viewParams.labels.forEach { label ->
            drawText(label.text, label.x, label.y, label.paint)
        }

        save()

        rotate(
            ANGLE_180 - gaugeStartAngle + valueSector * currentAnimatedValue,
            viewParams.center.x,
            viewParams.center.y
        )

        drawPath(viewParams.hand.path, viewParams.hand.paint)

        restore()
    }

    private class GaugeViewParams(
        val center: PointF,
        val outline: OutlineParams,
        val scalesPrimary: ScalesPrimaryParams,
        val scalesPrimaryDangerous: ScalesPrimaryParams,
        val scalesSecondary: ScalesSecondaryParams,
        val scalesSecondaryDangerous: ScalesSecondaryParams,
        val labels: List<LabelParams>,
        val hand: HandParams
    )

    private class OutlineParams(
        val path: Path,
        val paint: Paint
    ) {
        companion object {
            const val WIDTH = 32f
        }
    }

    private class ScalesPrimaryParams(
        val path: Path,
        val paint: Paint
    ) {
        companion object {
            const val STROKE_LENGTH = 60f

            const val COLOR_DEFAULT = Color.WHITE
            const val DANGER_COLOR_DEFAULT = Color.RED
        }
    }

    private class ScalesSecondaryParams(
        val path: Path,
        val paint: Paint
    ) {
        companion object {
            const val STROKE_LENGTH = 15f

            const val COLOR_DEFAULT = Color.WHITE
            const val DANGER_COLOR_DEFAULT = Color.RED
        }
    }

    private class LabelParams(
        val text: String,
        val x: Float,
        val y: Float,
        val paint: Paint
    ) {
        companion object {
            const val TEXT_SIZE = 50f

            val TYPEFACE_DEFAULT: Typeface = Typeface.DEFAULT
        }
    }

    private class HandParams(
        val path: Path,
        val paint: Paint
    ) {
        companion object {
            const val COLOR_DEFAULT = Color.GREEN
            const val WIDTH = 32f
            const val CENTER_RADIUS = 32f
            const val MOVEMENT_DURATION = 100L
        }
    }

    private inner class CurrentValueAnimator : ValueAnimator() {

        init {
            interpolator = LinearInterpolator()
            duration = HandParams.MOVEMENT_DURATION
            addUpdateListener { animator ->
                currentAnimatedValue = animator.animatedValue as Float
            }
        }

        fun restartAnimation(toValue: Float) {
            cancel()
            setFloatValues(currentAnimatedValue, toValue)
            start()
        }
    }

    companion object {

        private const val SCALE_STROKE_WIDTH = 1f

        private const val ANGLE_45 = 45f

        private const val ANGLE_90 = 90f

        private const val ANGLE_180 = 180f

        private const val GAUGE_INDICATOR_START_ANGLE = ANGLE_45

        const val MAX_VALUE_DEFAULT = 200

        const val PRIMARY_SCALES_STEP_DEFAULT = 20

        const val PRIMARY_SCALES_LABEL_STEP_DEFAULT = 40

        const val SECONDARY_SCALES_STEP_DEFAULT = 5

        const val STROKE_TEXT_PADDING_DEFAULT_DP = 32f
    }

}