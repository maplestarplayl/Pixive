package dev.lifeng.pixive.infra.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
import coil.transform.Transformation
import dev.lifeng.pixive.infra.app.saveImageToGallery
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient

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

@RequiresApi(Build.VERSION_CODES.Q)
suspend fun saveImage(context: Context, imageUrl: String,
                      title:String,channel: Channel<Int>,
                      onSuccess: () -> Unit, onFailure: () -> Unit) {
    val okHttpClient = OkHttpClient.Builder()
                            .addNetworkInterceptor { chain ->
                                val originalResponse = chain.proceed(chain.request())
                                originalResponse.newBuilder()
                                    .body(ProgressResponseBody(originalResponse.body!!) {
                                        Log.d("DownloadProgress", "progress send : $it")
                                        channel.trySend(it)
                                    })
                                    .build()
                            }
    val loader = ImageLoader.Builder(context)
        .okHttpClient(okHttpClient.build())
        .build()
    val request = ImageRequest.Builder(context)
        .data(imageUrl).addHeader("Referer", "https://www.pixiv.net/")
        .build()

    when (val result = loader.execute(request)){
        is ErrorResult -> {
            channel.close(ChannelClosedException("Complete"))
            result.throwable.printStackTrace()
            onFailure()
        }
        is SuccessResult -> {
            channel.close(ChannelClosedException("Complete"))
            val bitmap = (result.drawable as BitmapDrawable).bitmap
            val flag = saveImageToGallery(context,bitmap,title)
            when(flag) {
                true -> onSuccess()
                false -> onFailure()
            }
        }
    }
}