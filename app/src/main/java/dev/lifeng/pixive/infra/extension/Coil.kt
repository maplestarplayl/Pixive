package dev.lifeng.pixive.infra.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import coil.size.Size
import coil.transform.Transformation

class CustomRoundedCornersTransformation(
    private val topLeft: Float,
    private val topRight: Float,
    private val bottomLeft: Float,
    private val bottomRight: Float
) : Transformation {
    override val cacheKey: String = "CustomRoundedCornersTransformation-$topLeft-$topRight-$bottomLeft-$bottomRight"
    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val width = input.width
        val height = input.height

        val output = Bitmap.createBitmap(width, height, input.config)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = null
        canvas.drawBitmap(input, 0f, 0f, paint)

        val path = Path()
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())

        // Define path with rounded corners
        path.addRoundRect(
            rectF,
            floatArrayOf(
                topLeft, topLeft,     // Top left corner
                topRight, topRight,   // Top right corner
                bottomRight, bottomRight, // Bottom right corner
                bottomLeft, bottomLeft // Bottom left corner
            ),
            Path.Direction.CW
        )

        // Clip the canvas to the path with rounded corners
        canvas.clipPath(path)
        canvas.drawBitmap(input, 0f, 0f, paint)

        input.recycle()

        return output
    }
}