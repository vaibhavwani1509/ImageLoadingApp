package com.example.imageloadingapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val LOAD_MORE_COUNT = 20

class MainActivity : AppCompatActivity() {

    private val imagesData = ArrayList<ImageData>()
    private var imageCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.images_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create the data for the recycler view
        createImageUrls()

        val adapter = ImagesAdapter()

        // Set up click listener for the load more action
        val loadMoreClickListener = createLoadMoreClickListener(adapter)

        adapter.setData(imagesData)
        adapter.setLoadMoreClickListener(loadMoreClickListener)

        recyclerView.adapter = adapter
    }

    private fun createLoadMoreClickListener(adapter: ImagesAdapter) = View.OnClickListener {
        val positionStart = imagesData.size

        // Create the new data
        createImageUrls()

        // Update the new data to the recyclerView
        adapter.setData(imagesData)

        // Notify the change in the data
        adapter.notifyItemRangeInserted(positionStart, LOAD_MORE_COUNT)
    }

    private fun createImageUrls() {
        (0 until LOAD_MORE_COUNT).forEach { _ ->
            imagesData.add(ImageData("https://picsum.photos/id/${imageCount}/600/300"))
            imageCount += 1
        }
    }
}