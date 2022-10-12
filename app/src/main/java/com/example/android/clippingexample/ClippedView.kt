package com.example.android.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/*
* The @JvmOverloads annotation instructs the Kotlin compiler to
*  generate overloads for this function that substitute default parameter values.
*/
class ClippedView @JvmOverloads
   constructor (context: Context,atts:AttributeSet?=null,defStyAtt:Int=0)
    :View(context,atts,defStyAtt) {
     val paint=Paint().apply{
         style=Paint.Style.FILL
         isAntiAlias=true
         strokeWidth=resources.getDimension(R.dimen.strokeWidth)
         textSize=resources.getDimension(R.dimen.textSize)
     }
    val path=Path()
    //dimensions for a clipping rectangle around the whole set of shapes.
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)
    //the inset of a rectangle and the offset of a small rectangle
    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)
    //This is the circle that is drawn inside the rectangle.
    private val circleRadius = resources.getDimension(R.dimen.circleRadius)
    //An offset and a text size for text that is drawn inside the rectangle
    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)
    //Set up the coordinates for two columns.
    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight//8+8+90
    //Add the coordinates for each row, including the final row for the transformed text.
    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom//8+8+90
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    //for the rounded rectangle shape
    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )
    private val textRow = rowFour + (1.5f * clipRectBottom)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackAndUnclippedRectangle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        // drawQuickRejectExample(canvas)
    }
    private fun drawClippedRectangle(canvas: Canvas){
        /*
      set the boundaries of the clipping rectangle for the whole shape.
      Apply a clipping rectangle that constrains to drawing only the square
     */
        canvas.clipRect(clipRectLeft,clipRectTop,clipRectRight,clipRectBottom)
        canvas.drawColor(Color.WHITE)
        paint.color=Color.RED
        canvas.drawLine(clipRectLeft,clipRectTop,clipRectRight,clipRectBottom,paint)
        paint.color=Color.GREEN
        //center of circle cx,cy is 30,60 from the origin
        canvas.drawCircle(circleRadius,clipRectBottom-circleRadius,circleRadius,paint)
        paint.color=Color.BLUE

        paint.textSize=textSize
        /*
         Align the RIGHT side of the text with the origin.
        *Note: The Paint.Align property specifies which side of the text to align
        *      to the origin (not which side of the origin the text goes,
        *      or where in the region it is aligned!).
        *      Aligning the right side of the text to the origin places it on the left of the origin.
        * */
        paint.textAlign=Paint.Align.RIGHT
        canvas.drawText(context.getString(R.string.clipping),clipRectRight,textOffset,paint)

    }
    private fun drawBackAndUnclippedRectangle(canvas: Canvas){
       //THIS IS THE COLOR OF THE WHOLE VIEW NOT THE CLIPPED RECT
       canvas.drawColor(Color.GRAY)
        //canvas.save()>>I want to save the state of the current Canvas's adjustments so that I can go back to it later.
        //remember that it is one object of canvas you are dwelling with which passed in onDraw()
        canvas.save()
        canvas.translate(columnOne,rowOne)//shifting by 8,8
        drawClippedRectangle(canvas)
        //is used to remove all modifications to the matrix/clip state since the last save call
        //canvas.restore()>>is saying that I want to revert my Canvas's adjustments back to the last time I called cavas.save()
        canvas.restore()
    }
    private fun drawDifferenceClippingExample(canvas: Canvas){
        canvas.save()
        // Move the origin to the right for the next rectangle.
        canvas.translate(columnTwo,rowOne)//(106,8)
        // Use the subtraction of two clipping rectangles to create a frame.
        canvas.clipRect(//16,16,90-16=74,74
            2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .DIFFERENCE) was deprecated in API level 26. The recommended
        // alternative method is clipOutRect(float, float, float, float),
        // which is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)//26
        {
            canvas.clipRect(//32,32,90-32=58,58
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
                //The DIFFERENCE operator subtracts the second rectangle from the first one.
                Region.Op.DIFFERENCE
            )
        }else{
            canvas.clipOutRect(
                4 * rectInset,4 * rectInset,
                clipRectRight - 4 * rectInset,
                clipRectBottom - 4 * rectInset,
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawCircularClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowTwo)
        // Clears any lines and curves from the path but unlike reset(),
        // keeps the internal data structure for faster reuse.
        path.rewind()
        path.addCircle(
            circleRadius,clipRectBottom - circleRadius,
            circleRadius,Path.Direction.CCW
        )
        // The method clipPath(path, Region.Op.DIFFERENCE) was deprecated in
        // API level 26. The recommended alternative method is
        // clipOutPath(Path), which is currently available in
        // API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(path, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(path)
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawIntersectionClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowTwo)
        canvas.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight - smallRectOffset,
            clipRectBottom - smallRectOffset
        )
        // The method clipRect(float, float, float, float, Region.Op
        // .INTERSECT) was deprecated in API level 26. The recommended
        // alternative method is clipRect(float, float, float, float), which
        // is currently available in API level 26 and higher.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                clipRectRight,clipRectBottom,
                Region.Op.INTERSECT
            )
        } else {
            canvas.clipRect(
                clipRectLeft + smallRectOffset,
                clipRectTop + smallRectOffset,
                  clipRectRight,clipRectBottom
            )
        }
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawCombinedClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne, rowThree)
        path.rewind()
        path.addCircle(
            clipRectLeft + rectInset + circleRadius,
            clipRectTop + circleRadius + rectInset,
             circleRadius,Path.Direction.CCW
        )
        path.addRect(
            clipRectRight / 2 - circleRadius,
            clipRectTop + circleRadius + rectInset,
            clipRectRight / 2 + circleRadius,
            clipRectBottom - rectInset,Path.Direction.CCW //CCW>>counter clockwise
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawRoundedRectangleClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnTwo,rowThree)
        path.rewind()
        path.addRoundRect(
            //Values for the x and y values of the corner radius
            rectF,clipRectRight / 4,
            clipRectRight / 4, Path.Direction.CCW
        )
        canvas.clipPath(path)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawOutsideClippingExample(canvas: Canvas) {
        canvas.save()
        canvas.translate(columnOne,rowFour)
        //clipRect will give you the difference,if you want the intersection use clipOutRect
        canvas.clipRect(2 * rectInset,2 * rectInset,
            clipRectRight - 2 * rectInset,
            clipRectBottom - 2 * rectInset)
        drawClippedRectangle(canvas)
        canvas.restore()
    }
    private fun drawTranslatedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.GREEN
        // Align the RIGHT side of the text with the origin.
        paint.textAlign = Paint.Align.LEFT
        // Apply transformation to canvas.
        canvas.translate(columnTwo,textRow)
        // Draw text.
        canvas.drawText(context.getString(R.string.translated),
            clipRectLeft,clipRectTop,paint)
        canvas.restore()
    }
    private fun drawSkewedTextExample(canvas: Canvas) {
        canvas.save()
        paint.color = Color.YELLOW
        paint.textAlign = Paint.Align.RIGHT
        // Position text.
        canvas.translate(columnTwo, textRow)
        // Apply skew transformation.
        canvas.skew(0.2f, 0.3f)
        canvas.drawText(context.getString(R.string.skewed),
            clipRectLeft, clipRectTop, paint)
        canvas.restore()
    }
    private fun drawQuickRejectExample(canvas: Canvas) {
    }

}