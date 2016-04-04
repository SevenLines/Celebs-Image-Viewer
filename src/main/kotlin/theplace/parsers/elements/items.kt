package theplace.parsers.elements

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import theplace.parsers.BaseParser
import java.io.File
import java.io.InputStream
import java.nio.file.*

/**
 * Created by mk on 03.04.16.
 */
class Gallery(val title: String = "",
              val url: String = "",
              val id: Int = -1,
              var parser: BaseParser? = null) {
    protected var _albums: List<GalleryAlbum>? = null
    val albums: List<GalleryAlbum>
        get() {
            _albums = if (_albums == null) parser?.getAlbums(this) else _albums
            return _albums as? List<GalleryAlbum> ?: emptyList()
        }

    /**
     * refresh albums binded to this gallery
     */
    fun refreshAlbums() {
        _albums = parser?.getAlbums(this)
    }

    override fun toString(): String {
        return "$title"
    }
}


class GalleryAlbum(var url: String = "",
                   var title: String = "",
                   var gallery: Gallery? = null) {
    override fun toString(): String {
        return "GalleryAlbum: ${gallery?.title} [url: $url]"
    }

    protected var _images: List<GalleryImage>? = null
    val images: List<GalleryImage>
        get() {
            _images = if (_images == null) gallery?.parser?.getImages(this) else _images
            return _images as? List<GalleryImage> ?: emptyList()
        }
}

class GalleryImage(var title: String = "",
                   val url: String,
                   val url_thumb: String,
                   var album: GalleryAlbum? = null) {
    init {
        title = FilenameUtils.getName(url)
    }

    fun get_path(directory_path: String, filename: String=title) =
            Paths.get(directory_path,
                    album?.title ?: "",
                    album?.gallery?.title ?: "",
                    album?.gallery?.parser?.title ?: "",
                    filename).toString()
    fun exists(directory_path: String): Boolean = Files.exists(Paths.get(get_path(directory_path)))

    fun download(): InputStream? {
        return album?.gallery?.parser?.downloadImage(url)
    }

    fun download_thumb(): InputStream? {
        return album?.gallery?.parser?.downloadImage(url_thumb)
    }

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