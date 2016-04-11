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
        return url.startsWith("https://someimage.com")
    }

    override fun getImageUrl(doc: Element): String {
        var image = doc.select("#viewimage").first()
        return image.attr("src")
    }
}