package theplace.imageloaders

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.InputStream

/**
 * Created by mk on 10.04.16.
 */
class SomeImageImageLoader : BaseImageLoaderInterface("someimage.com", "https://someimage.com/") {
    override fun checkUrl(url: String): Boolean {
        return """^https?\://someimage\.com""".toRegex().containsMatchIn(url)
    }

    override fun getImageUrl(doc: Element, url: String): String {
        var image = doc.select("#viewimage").first()
        return image.attr("src")
    }
}