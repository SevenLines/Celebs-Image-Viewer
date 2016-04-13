package theplace.parsers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.FileUtils
import theplace.parsers.elements.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by mk on 03.04.16.
 */
abstract class BaseParser(var url: String = "", var title: String = "") {
    protected var _galleries: List<Gallery>? = null
    open var isAlwaysOneAlbum = false
    open var isAlwaysOneSubGallery = false
    open var isAlwaysOnePage = false

    companion object {
        @JvmStatic val PARSERS_DIR = Paths.get("./parsers").toAbsolutePath().toString()
    }

    init {
        var path = Paths.get(PARSERS_DIR, "$title.json")
        if (Files.exists(path)) {
            var data = FileUtils.readFileToString(path.toFile())
            var gson = Gson()
            var typeToken = object : TypeToken<List<Gallery>>() {}.type
            _galleries = gson.fromJson(data, typeToken)
            _galleries?.forEach { it.parser = this }
        }
    }

    val galleries: List<Gallery>
        get() {
            if (_galleries == null) {
                refreshGalleries()
            }
            return _galleries?.sortedBy { it.title } as? List<Gallery> ?: emptyList()
        }

    fun refreshGalleries() {
        var path = Paths.get(PARSERS_DIR, "$title.json")
        _galleries = getGalleries_internal()

        var gson = GsonBuilder().setPrettyPrinting().create()
        var data = gson.toJson(_galleries)

        FileUtils.writeStringToFile(path.toFile(), data)
    }

    abstract fun getGalleries_internal(): List<Gallery>
    abstract fun getAlbums(subGallery: SubGallery): List<GalleryAlbum>
    abstract fun getAlbumPages(album: GalleryAlbum): List<GalleryAlbumPage>
    abstract fun getImages(albumPage: GalleryAlbumPage): List<GalleryImage>
    abstract fun downloadImage(image_url: String): InputStream?

    open fun getSubGalleries(gallery: Gallery) : List<SubGallery> {
        return listOf(SubGallery(
                title="",
                id=gallery.id,
                url=gallery.url,
                gallery=gallery))
    }

    override fun toString(): String {
        return title
    }
}