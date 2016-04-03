package theplace.parsers

import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum

/**
 * Created by mk on 03.04.16.
 */
open class BaseParser(var url: String = "", var title: String = "") {
    protected var _galleries: List<Gallery>? = null
    val galleries: List<Gallery>
        get() {
            _galleries = if (_galleries == null) getGalleries_internal() else _galleries
            return _galleries as? List<Gallery> ?: emptyList()
        }

    fun refreshGalleries() {
        _galleries = getGalleries_internal()
    }

    open fun getGalleries_internal(): List<Gallery> {
        throw UnsupportedOperationException("not implemented")
    }

    fun getGalleryById(id: Int): Gallery? {
        return _galleries?.find { it.id == id }
    }

    open fun getAlbums(gallery: Gallery): List<GalleryAlbum> {
        throw UnsupportedOperationException("not implemented")
    }
}