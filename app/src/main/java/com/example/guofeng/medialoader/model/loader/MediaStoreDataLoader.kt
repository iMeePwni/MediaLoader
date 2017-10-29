package com.example.guofeng.medialoader.model.loader

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.AsyncTaskLoader
import com.example.guofeng.medialoader.model.data.MediaStoreData
import com.example.guofeng.medialoader.model.data.MediaType
import java.util.*

/**
 * Created by guofeng on 2017/10/16.
 * Loads metadata from the media store for images and videos.
 */
class MediaStoreDataLoader(context: Context) : AsyncTaskLoader<List<MediaStoreData>>(context) {

    companion object {
        private val IMAGE_PROJECTION = arrayOf(
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.MIME_TYPE,
                MediaStore.Images.ImageColumns.ORIENTATION
        )
        private val VIDEO_PROJECTION = arrayOf(
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATE_MODIFIED,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                "0 AS ${MediaStore.Images.ImageColumns.ORIENTATION}"
        )
    }

    private var cached: List<MediaStoreData>? = null
    private var observerRegistered = false
    private val forceLoadContentObserver = ForceLoadContentObserver()

    override fun deliverResult(data: List<MediaStoreData>?) {
        if (!isReset && isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStartLoading() {
        super.onStartLoading()
        // 如果有 缓存数据 先分发缓存数据
        if (cached != null) {
            deliverResult(cached)
        }
        // 如果内容变动 或 没有缓存 强制读取数据
        if (takeContentChanged() || cached == null) {
            forceLoad()
        }
        registerContentObserver()
    }

    override fun onStopLoading() {
        super.onStopLoading()
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
        cached = null
        unregisterContentObserver()
    }

    override fun onAbandon() {
        super.onAbandon()
        unregisterContentObserver()
    }

    override fun loadInBackground(): List<MediaStoreData> {
        val data = queryImages()
        data.addAll(queryVideos())
        Collections.sort(data) { o1, o2 ->
            o2.dateTaken.compareTo(o1.dateTaken)
        }
        return data
    }

    private fun queryImages()
            = query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
            MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
            MediaType.IMAGE)

    private fun queryVideos()
            = query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
            MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns._ID,
            MediaStore.Video.VideoColumns.DATE_TAKEN, MediaStore.Video.VideoColumns.DATE_MODIFIED,
            MediaStore.Video.VideoColumns.MIME_TYPE, MediaStore.Images.ImageColumns.ORIENTATION,
            MediaType.VIDEO)

    private fun query(contentUri: Uri, projection: Array<String>,
                      sortByCol: String, idCol: String,
                      dateTakenCol: String, dateModifiedCol: String,
                      mimeTypeCol: String, orientationCol: String,
                      type: MediaType): ArrayList<MediaStoreData> {
        val data = ArrayList<MediaStoreData>()
        context.contentResolver.query(contentUri, projection,
                null, null,
                "$sortByCol DESC").use {
            with(it) {
                val idColNum = getColumnIndexOrThrow(idCol)
                val dateTakenColNum = getColumnIndexOrThrow(dateTakenCol)
                val dateModifiedColNum = getColumnIndexOrThrow(dateModifiedCol)
                val mimeTypeColNum = getColumnIndexOrThrow(mimeTypeCol)
                val orientationNum = getColumnIndexOrThrow(orientationCol)

                while (moveToNext()) {
                    val id = getLong(idColNum)
                    val dateTaken = getLong(dateTakenColNum)
                    val mimeType = getString(mimeTypeColNum)
                    val dateModified = getLong(dateModifiedColNum)
                    val orientation = getInt(orientationNum)

                    val element = MediaStoreData(id, Uri.withAppendedPath(contentUri, "$id"),
                            mimeType, dateModified,
                            orientation, type,
                            dateTaken)
                    data.add(element)
                }
            }
        }
        return data
    }

    private fun registerContentObserver() {
        if (!observerRegistered) {
            context.contentResolver.run {
                registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, forceLoadContentObserver)
                registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, forceLoadContentObserver)
            }
            observerRegistered = true
        }
    }

    private fun unregisterContentObserver() {
        if (observerRegistered) {
            context.contentResolver.unregisterContentObserver(forceLoadContentObserver)
            observerRegistered = false
        }
    }
}