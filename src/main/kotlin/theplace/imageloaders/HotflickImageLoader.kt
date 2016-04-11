package theplace.imageloaders

import org.jsoup.nodes.Element
import java.io.InputStream

/**
 * Created by mk on 10.04.16.
 */
class HotflickImageLoader : BaseImageLoaderInterface(title = "hotflick", url = "http://www.hotflick.net") {
    override fun getImageUrl(doc: Element): String {
        var img = doc.select("#img").first()
        return img.attr("src")
    }

    override fun checkUrl(url: String): Boolean {
        return url.startsWith(this.url)
    }


}