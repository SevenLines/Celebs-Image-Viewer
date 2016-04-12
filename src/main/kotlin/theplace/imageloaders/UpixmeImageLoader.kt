package theplace.imageloaders;

import org.jsoup.nodes.Element

/**
 * Created by mk on 12.04.16.
 */

class UpixmeImageLoader : BaseImageLoaderInterface("upix.me", "http://upix.me/") {
    override fun getImageUrl(doc: Element, url: String): String {
        return doc.select("#b1 a").first().attr("href")
    }

    override fun checkUrl(url: String): Boolean {
        return url.startsWith(this.url)
    }

}