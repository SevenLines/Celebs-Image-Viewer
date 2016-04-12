package theplace.imageloaders

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.InputStream

/**
 * Created by mk on 10.04.16.
 */
class ImagebamImageLoader : BaseImageLoaderInterface(title="imagebam.com", url="http://www.imagebam.com") {
    override fun getImageUrl(doc: Element, url: String): String {
        var image = doc.select(".image-container img").first()
        return  image.attr("src")
    }

    override fun checkUrl(url: String): Boolean {
        return url.startsWith("http://www.imagebam.com/")
    }

}