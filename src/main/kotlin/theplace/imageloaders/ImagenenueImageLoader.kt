package theplace.imageloaders

import org.jsoup.nodes.Element
import java.net.URL

/**
 * Created by mk on 12.04.16.
 */
class ImagenenueImageLoader : BaseImageLoaderInterface("imagevenue.com", "http://imagevenue.com") {
    override fun getImageUrl(doc: Element, url: String): String {
        var baseUrl = URL(url)
        var src =  doc.select("#thepic").first().attr("src")
        return "${baseUrl.protocol}://${baseUrl.host}/$src"
    }

    override fun checkUrl(url: String): Boolean {
        return """http://img(\d+).imagevenue\.com/""".toRegex().containsMatchIn(url)
    }
}