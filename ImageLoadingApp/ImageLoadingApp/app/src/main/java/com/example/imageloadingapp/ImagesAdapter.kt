package com.example.imageloadingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

private const val NUMBER_OF_LIBS = 3
private const val TAG = "ImagesAdapter"

class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    private lateinit var loadMoreClickListener: View.OnClickListener
    private lateinit var images: List<ImageData>

    private var glide = 0
    private var picasso = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == images.size) {
            R.layout.load_more_layout
        } else {
            R.layout.list_item_layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ImageViewHolder(item)
    }

    override fun getItemCount(): Int {
        // Add 1 to the image list size for the load more button
        return images.size + 1
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        if (position == images.size) {
            // Attach the click listener for the load more action
            val button = holder.itemView.findViewById<AppCompatButton>(R.id.load_more_button)
            button.setOnClickListener(loadMoreClickListener)
            return
        }

        val url = images[position].url

        // Load the image view with image from above URL
        holder.imageView?.run {
            when (position) {
                glide -> {
                    // Load image using 'Glide'
                    loadImageWithGlide(context, url)

                    glide += NUMBER_OF_LIBS
                    Log.d(TAG, "$position => ${url}: This image was loaded with Glide")
                }

                picasso -> {
                    // Load image using 'Picasso'
                    loadImageWithPicasso(url)

                    picasso += NUMBER_OF_LIBS
                    Log.d(TAG, "$position => ${url}: This image was loaded with Picasso")
                }

                else -> {
                    // Load image using 'Volley'
                    loadImageWithVolley(context, url)

                    Log.d(TAG, "$position => ${url}: This image was loaded with Volley")
                }
            }
        }
    }

    /**
     * Method to load images using 'Glide'
     */
    private fun ImageView.loadImageWithGlide(
        context: Context,
        url: String
    ) {
        Glide.with(context)
            .load(url)
            .override(300, 150)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .transition(withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(this)
    }

    /**
     * Method to load images using 'Picasso'
     */
    private fun ImageView.loadImageWithPicasso(
        url: String
    ) {
        Picasso.get()
            .load(url)
            .resize(300, 150)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .noFade()
            .into(this, object : Callback {
                override fun onSuccess() {
                    this@loadImageWithPicasso.alpha = 0f

                    // Create a fade transition
                    this@loadImageWithPicasso.animate().setDuration(150).alpha(1f).start()
                }

                override fun onError(e: Exception?) = Unit
            })
    }

    /**
     * Method to load images using 'Volley'
     */
    private fun ImageView.loadImageWithVolley(
        context: Context,
        url: String
    ) {
        // Create an imageRequest for the URL
        val imageRequest = ImageRequest(
            url,
            { bitmap ->
                setImageBitmap(bitmap)
            },
            300,
            150,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.ARGB_8888,
            {
                setBackgroundColor(Color.RED)
                Log.e(TAG, "Error loading the image", it)
            }
        )

        // Create the request queue
        val queue = Volley.newRequestQueue(context)

        // Add the image request to the queue
        queue.add(imageRequest)
    }

    fun setLoadMoreClickListener(loadMoreClickListener: View.OnClickListener) {
        this.loadMoreClickListener = loadMoreClickListener
    }

    fun setData(images: List<ImageData>) {
        this.images = images
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView?

        init {
            imageView = itemView.findViewById(R.id.image)
        }
    }
}