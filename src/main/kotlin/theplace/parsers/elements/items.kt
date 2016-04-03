package theplace.parsers.elements

import theplace.parsers.BaseParser
import java.io.InputStream
import java.nio.file.CopyOption
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
        return "Gallery: $title ($id) [url: $url]"
    }
}


class GalleryAlbum(var url: String = "",
                   var gallery: Gallery? = null) {
    override fun toString(): String {
        return "GalleryAlbum: ${gallery?.title} [url: $url]"
    }

    protected  var _images: List<GalleryImage>? = null
    val images: List<GalleryImage>
        get() {
            _images = if (_images == null) gallery?.parser?.getImages(this) else _images
            return _images as? List<GalleryImage> ?: emptyList()
        }
}

class GalleryImage(val title: String = "",
                   val url: String,
                   val url_thumb: String,
                   var album: GalleryAlbum? = null)
{
    fun download(): InputStream? {
        return album?.gallery?.parser?.downloadImage(url)
    }

    fun download_thumb(): InputStream? {
        return album?.gallery?.parser?.downloadImage(url_thumb)
    }

    fun save_to_file(path: String) {
        var data = download()
        if (data != null)
            Files.copy(data, FileSystems.getDefault().getPath(path), StandardCopyOption.REPLACE_EXISTING)
    }

    fun save_thumb_to_file(path: String) {
        var data = download_thumb()
        if (data != null)
            Files.copy(data, FileSystems.getDefault().getPath(path), StandardCopyOption.REPLACE_EXISTING)
    }
}