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
class Gallery(var title: String = "",
              var url: String = "",
              var id: Int = -1,
              @Transient var parser: BaseParser? = null) : Serializable {
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

    companion object {
        @JvmStatic val CACHE_DIR: String = "./cache"
    }

    init {
        title = FilenameUtils.getName(url)
    }

    fun get_path(directory_path: String, filename: String=title) =
            Paths.get(directory_path,
                    album?.title ?: "",
                    album?.gallery?.title ?: "",
                    album?.gallery?.parser?.title ?: "",
                    filename).toString()


    fun downloadImage(url: String): InputStream? {
        var file_name = Paths.get(url).fileName.toString()
        var path = get_path(CACHE_DIR, file_name)
        var stream_data: InputStream? = null
        if (Files.exists(Paths.get(path))) {
            return FileInputStream(path)
        } else {
            stream_data = album?.gallery?.parser?.downloadImage(url)
            FileUtils.copyInputStreamToFile(stream_data, File(path))
            stream_data?.reset()
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