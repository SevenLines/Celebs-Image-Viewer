package theplace.parsers

import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryImage
import theplace.parsers.elements.SubGallery
import java.io.InputStream

/**
 * Created by mk on 07.04.16.
 */
class SuperiorPics : BaseParser(url = "http://forums.superiorpics.com/", title = "superiorpics") {
    override fun getAlbums(subGallery: SubGallery): List<GalleryAlbum> {
        throw UnsupportedOperationException()
    }

    override fun getGalleries_internal(): List<Gallery> {
        throw UnsupportedOperationException()
    }

    override fun getImages(album: GalleryAlbum): List<GalleryImage> {
        throw UnsupportedOperationException()
    }

    override fun downloadImage(image_url: String): InputStream? {
        throw UnsupportedOperationException()
    }
}