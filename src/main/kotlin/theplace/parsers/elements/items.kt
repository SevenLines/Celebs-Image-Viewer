package theplace.parsers.elements

import theplace.parsers.BaseParser

/**
 * Created by mk on 03.04.16.
 */
class Gallery(val title: String = "",
              val url: String = "",
              val id: Int = -1,
              var parser: BaseParser? = null) {
    var _albums: List<GalleryAlbum>? = null
    val albums: List<GalleryAlbum>
        get() {
            _albums = if (_albums == null) parser?.getAlbums(this) else _albums
            return _albums as? List<GalleryAlbum> ?: emptyList()
        }

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
}

class GalleryImage(val title: String = "",
                   val url: String,
                   val url_thumb: String,
                   var album: GalleryAlbum? = null)