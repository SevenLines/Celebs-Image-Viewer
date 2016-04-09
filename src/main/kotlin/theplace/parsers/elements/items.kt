package theplace.parsers.elements

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import theplace.parsers.BaseParser
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.Serializable
import java.nio.file.*

/**
 * Created by mk on 03.04.16.
 */
open class Gallery(var title: String = "",
                   var url: String = "",
                   var id: Int = -1,
                   @Transient var parser: BaseParser? = null) : Serializable {

    protected var _subGalleries: List<SubGallery>? = null
    val subGalleries: List<SubGallery>
        get() {
            _subGalleries = if (_subGalleries == null) parser?.getSubGalleries(this) else _subGalleries
            return _subGalleries as? List<SubGallery> ?: emptyList()
        }

    override fun toString(): String {
        return "$title"
    }
}

class SubGallery(var title: String = "",
                 var url: String = "",
                 var id: Int = -1,
                 var gallery: Gallery? = null) {
    protected var _albums: List<GalleryAlbum>? = null
    val albums: List<GalleryAlbum>
        get() {
            _albums = if (_albums == null) gallery?.parser?.getAlbums(this) else _albums
            return _albums as? List<GalleryAlbum> ?: emptyList()
        }

    /**
     * refresh albums binded to this gallery
     */
    fun refreshAlbums() {
        _albums = gallery?.parser?.getAlbums(this)
    }

}

class GalleryAlbum(var url: String = "",
                   var title: String = "",
                   var id: Int = -1,
                   var thumb: GalleryImage? = null,
                   var subgallery: SubGallery? = null) {
    override fun toString(): String {
        return "GalleryAlbum: ${subgallery?.title} [url: $url]"
    }

    protected var _pages: List<GalleryAlbumPage>? = null
    val pages: List<GalleryAlbumPage>
        get() {
            _pages = if (_pages == null) subgallery?.gallery?.parser?.getAlbumPages(this) else _pages
            return _pages as? List<GalleryAlbumPage> ?: emptyList()
        }
}

class GalleryAlbumPage(var url: String, var album: GalleryAlbum? = null) {
    protected var _images: List<GalleryImage>? = null
    val images: List<GalleryImage>
        get() {
            _images = if (_images == null) album?.subgallery?.gallery?.parser?.getImages(this) else _images
            return _images as? List<GalleryImage> ?: emptyList()
        }
}

class GalleryImage(var title: String = "",
                   val url: String,
                   val url_thumb: String,
                   var page: GalleryAlbumPage? = null,
                   var album: GalleryAlbum? = null) {

    companion object {
        @JvmStatic val CACHE_DIR: String = "./cache"
    }

    init {
        title = FilenameUtils.getName(url)
    }

    fun get_path(directory_path: String, filename: String = title) =
            Paths.get(directory_path,
                    page?.album?.subgallery?.gallery?.title ?: "",
                    page?.album?.subgallery?.gallery?.parser?.title ?: "",
                    page?.album?.subgallery?.title ?: "",
                    page?.album?.title ?: "",
                    filename).toString()


    fun downloadImage(url: String): InputStream? {
        var file_name = Paths.get(url).fileName.toString()
        var path = get_path(CACHE_DIR, file_name)
        var stream_data: InputStream?
        if (Files.exists(Paths.get(path))) {
            return FileInputStream(path)
        } else {
            var parser = page?.album?.subgallery?.gallery?.parser ?: album?.subgallery?.gallery?.parser
            stream_data = parser?.downloadImage(url)
            if (stream_data != null) {
                FileUtils.copyInputStreamToFile(stream_data, File(path))
                stream_data.reset()
            }
            return stream_data
        }
    }

    fun exists(directory_path: String): Boolean = Files.exists(Paths.get(get_path(directory_path)))
    fun download(): InputStream? = downloadImage(url)
    fun download_thumb(): InputStream? = downloadImage(url_thumb)

    protected fun save_to_path(url: String? = null, directory_path: String) {
        var filename = if (url != null) Paths.get(url).fileName.toString() else title
        var pth = get_path(directory_path, filename)
        var data = download()
        if (data != null) {
            var file = File(pth);
            file.parentFile.mkdirs();
            FileUtils.copyInputStreamToFile(data, file)
        }
    }

    fun save_to_file(directory_path: String) {
        save_to_path(directory_path = directory_path)
    }

    fun save_thumb_to_file(directory_path: String) {
        save_to_path(url_thumb, directory_path)
    }
}