package com.example.guofeng.medialoader.view

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.view.*
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.signature.MediaStoreSignature
import com.example.guofeng.medialoader.R
import com.example.guofeng.medialoader.app.GlideRequests
import com.example.guofeng.medialoader.model.data.MediaStoreData
import kotlinx.android.synthetic.main.recycler_item.view.*
import java.util.*


/**
 * Created by guofeng on 2017/10/29.
 */
class RecyclerAdapter(private val context: Context,
                      val data: List<MediaStoreData>,
                      private val glideRequests: GlideRequests)
    : RecyclerView.Adapter<RecyclerAdapter.ListViewHolder>(),
        ListPreloader.PreloadSizeProvider<MediaStoreData>,
        ListPreloader.PreloadModelProvider<MediaStoreData> {

    private var actualDimensions: IntArray? = null
    private val screenWidth by lazy {
        getScreenWidth(context)
    }
    private val requestBuilder by lazy {
        glideRequests.asDrawable().fitCenter()
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false)
        view.layoutParams.width = screenWidth

        if (actualDimensions == null) {
            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (actualDimensions == null) {
                        actualDimensions = intArrayOf(view.width, view.height)
                    }
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            })
        }

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(data[position])
    }
    // 因为item使用固定Id,所以需要复写这一方法
    override fun getItemId(position: Int): Long {
        return data[position].rowId
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getPreloadSize(item: MediaStoreData?, adapterPosition: Int, perItemPosition: Int): IntArray? {
        return actualDimensions
    }

    override fun getPreloadRequestBuilder(item: MediaStoreData): RequestBuilder<*> {
        with(item) {
            val signature = MediaStoreSignature(mimeType, dateModified, orientation)
            return requestBuilder
                    .clone()
                    .signature(signature)
                    .load(uri)
        }
    }

    override fun getPreloadItems(position: Int): MutableList<MediaStoreData> {
       return Collections.singletonList(data[position])
    }

    private fun getScreenWidth(context: Context): Int {
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point.x
    }

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(mediaStoreData: MediaStoreData) {

            with(mediaStoreData) {
                val signature = MediaStoreSignature(mimeType, dateModified, orientation)
                requestBuilder
                        .clone()
                        .signature(signature)
                        .load(uri)
                        .into(itemView.image_view)
            }
        }
    }
}