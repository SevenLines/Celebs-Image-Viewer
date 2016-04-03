package theplace.parsers

import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryImage
import java.io.InputStream

/**
 * Created by mk on 03.04.16.
 */
open abstract class BaseParser(var url: String = "", var title: String = "") {
    protected var _galleries: List<Gallery>? = null
    val galleries: List<Gallery>
        get() {
            _galleries = if (_galleries == null) getGalleries_internal() else _galleries
            return _galleries as? List<Gallery> ?: emptyList()
        }

    fun refreshGalleries() {
        _galleries = getGalleries_internal()
    }

    fun getGalleryById(id: Int): Gallery? = galleries.find { it.id == id }

    abstract fun getGalleries_internal(): List<Gallery>
    abstract fun getAlbums(gallery: Gallery): List<GalleryAlbum>
    abstract fun getImages(album: GalleryAlbum): List<GalleryImage>
    abstract fun downloadImage(image_url: String): InputStream?
}