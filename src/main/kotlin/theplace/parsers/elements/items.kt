package theplace.parsers.elements

import com.mashape.unirest.http.Unirest
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import theplace.imageloaders.ImageLoaderSelector
import theplace.imageloaders.LoadedImage
import theplace.parsers.BaseParser
import java.io.FileInputStream
import java.io.Serializable
import java.nio.file.Path
import java.nio.file.Paths

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
                 var gallery: Gallery? = null,
                 @Transient var parser: BaseParser? = null) {
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
                   var subgallery: SubGallery? = null,
                   @Transient var parser: BaseParser? = null) {
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

class GalleryAlbumPage(var url: String, var album: GalleryAlbum? = null, @Transient var parser: BaseParser? = null) {
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
                   var album: GalleryAlbum? = null,
                   @Transient var parser: BaseParser? = null) {

    companion object {
        @JvmStatic var CACHE_DIR: String = "cache"
        @JvmStatic val THUMBS_PREFIX: String = "thumbs"
    }

    init {
        title = ImageLoaderSelector.getTitle(url)
    }

    protected fun _download(isThumb: Boolean = false): LoadedImage? {
        var url = if (isThumb) this.url_thumb else this.url
        var prefix = if (isThumb) THUMBS_PREFIX else ""
        var path = exists(Paths.get(CACHE_DIR), prefix)
        if (path != null) {
            return LoadedImage(url = path.toString(), body = FileInputStream(path.toFile()))
        } else {
            var loadedImage: LoadedImage?
            if (isThumb) {
                loadedImage = LoadedImage(url, Unirest.get("$url").header("referer", "$url").asBinary().body)
            } else {
                loadedImage = ImageLoaderSelector.download(url)
            }
            if (loadedImage?.body != null) {
                var newPath = getPath(CACHE_DIR, prefix, FilenameUtils.getExtension(loadedImage?.url))
                FileUtils.copyInputStreamToFile(loadedImage?.body, newPath.toFile())
                loadedImage?.body?.reset()
            }
            return loadedImage
        }
    }

    protected fun _saveToPath(path: Path, isThumb: Boolean = false): Path {
        var prefix = if (isThumb) THUMBS_PREFIX else ""
        var loadedImage = if (isThumb) downloadThumb() else download()
        var newPath = getPath(
                path.toString(), prefix = prefix, extension = FilenameUtils.getExtension(loadedImage?.url)
        )
        FileUtils.copyInputStreamToFile(loadedImage?.body, newPath.toFile())
        return newPath
    }

    fun exists(directory_path: Path, prefix: String = ""): Path? {
        return listOf("", "jpg", "jpeg", "png", "tiff", "tif", "gif").map {
            getPath(directory_path.toString(), prefix, it)
        }.firstOrNull {
            it.toFile().exists()
        }
    }

    fun thumbExists(directory_path: Path): Path? {
        return exists(directory_path, THUMBS_PREFIX)
    }

    fun safeName(title: String): String {
        var result = title.replace("""\.+$|"""".toRegex(), "").trim()
        return result
    }

    fun getPath(directory_path: String, prefix: String = "", extension: String = ""): Path {
        var ext = FilenameUtils.getExtension(title)
        if (ext.isNullOrEmpty())
            ext = extension
        return Paths.get(directory_path, prefix,
                safeName(page?.album?.subgallery?.gallery?.title ?: ""),
                safeName(page?.album?.subgallery?.gallery?.parser?.title ?: ""),
                safeName(page?.album?.subgallery?.title ?: ""),
                safeName(page?.album?.title ?: ""),
                "${FilenameUtils.getBaseName(title)}.$ext")
    }

    fun downloadThumb(): LoadedImage? {
        return _download(true)
    }

    fun download(): LoadedImage? {
        return _download()
    }

    fun saveThumbToPath(path: Path): Path {
        return _saveToPath(path, true)
    }

    fun saveToPath(path: Path): Path {
        return _saveToPath(path)
    }
}