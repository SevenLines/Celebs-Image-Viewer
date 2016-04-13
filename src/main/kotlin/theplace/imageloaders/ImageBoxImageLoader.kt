package theplace.imageloaders

import org.jsoup.nodes.Element

/**
 * Created by mk on 13.04.16.
 */
class ImageBoxImageLoader : BaseImageLoaderInterface(title = "imagebox.com", url="http://imgbox.com/") {
    override fun getImageUrl(doc: Element, url: String): String {
        return doc.select("#img").first().attr("src")
    }

    override fun checkUrl(url: String): Boolean {
        return "^http://.*?imgbox.com".toRegex().containsMatchIn(url)
    }
}