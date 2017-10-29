package com.example.guofeng.medialoader.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.example.guofeng.medialoader.R
import com.example.guofeng.medialoader.app.GlideApp
import com.example.guofeng.medialoader.model.data.MediaStoreData
import com.example.guofeng.medialoader.model.loader.MediaStoreDataLoader
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*

/**
 * Created by guofeng on 2017/10/29.
 */
class GalleryFragment
    : Fragment(),
        LoaderManager.LoaderCallbacks<List<MediaStoreData>> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(R.id.loader_id_media_store_data, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        view.recycler_view.run {
            val manager = GridLayoutManager(activity, 1, RecyclerView.HORIZONTAL, false)
            layoutManager = manager
            setHasFixedSize(true)
        }
        return view
    }

    override fun onLoadFinished(loader: Loader<List<MediaStoreData>>?, data: List<MediaStoreData>) {
        val glideRequests = GlideApp.with(this)
        val adapter = RecyclerAdapter(activity, data, glideRequests)
        val preloader = RecyclerViewPreloader<MediaStoreData>(glideRequests, adapter, adapter, 3)
        recycler_view.addOnScrollListener(preloader)
        recycler_view.adapter = adapter
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<MediaStoreData>> {
        return MediaStoreDataLoader(activity)
    }

    override fun onLoaderReset(loader: Loader<List<MediaStoreData>>?) {
        // DO nothing
    }

}