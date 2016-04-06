package theplace.parsers

import org.apache.commons.io.FileUtils
import org.yaml.snakeyaml.Yaml
import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryImage
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by mk on 03.04.16.
 */
abstract class BaseParser(var url: String = "", var title: String = "") {
    protected var _galleries: List<Gallery>? = null

    companion object {
        @JvmStatic val PARSERS_DIR = "./parsers"
    }

    init {
        var path = Paths.get(PARSERS_DIR, title)
        if (Files.exists(path)) {
            var fs = FileInputStream(path.toFile())
            var os = ObjectInputStream(fs)
            _galleries = os.readObject() as List<Gallery>
            _galleries?.forEach { it.parser = this }
            os.close()
        }
    }

    val galleries: List<Gallery>
        get() {
            if (_galleries == null) {
                refreshGalleries()
            }
            return _galleries as? List<Gallery> ?: emptyList()
        }

    fun refreshGalleries() {
        var path = Paths.get(PARSERS_DIR, title)
        _galleries = getGalleries_internal()
        var fs = FileOutputStream(path.toFile())
        var os = ObjectOutputStream(fs)
        os.writeObject(_galleries)
        os.close()
    }

    fun getGalleryById(id: Int): Gallery? = galleries.find { it.id == id }

    abstract fun getGalleries_internal(): List<Gallery>
    abstract fun getAlbums(gallery: Gallery): List<GalleryAlbum>
    abstract fun getImages(album: GalleryAlbum): List<GalleryImage>
    abstract fun downloadImage(image_url: String): InputStream?
}