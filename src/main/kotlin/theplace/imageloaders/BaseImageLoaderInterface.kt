package theplace.imageloaders

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.InputStream

/**
 * Created by mk on 10.04.16.
 */

data class LoadedImage (val url: String, val body: InputStream)

abstract class BaseImageLoaderInterface(var title: String, var url: String) {
    abstract fun getImageUrl(doc: Element): String
    open fun download(url: String) : LoadedImage {
        var r = Unirest.get("$url").header("referer", "$url").asString().body
        var doc = Jsoup.parse(r)
        var src = getImageUrl(doc)
        return LoadedImage(src, Unirest.get("$src").header("referer", "$src").asBinary().body)
    }
    abstract fun checkUrl(url: String): Boolean
}