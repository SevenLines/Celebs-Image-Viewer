package theplace.parsers

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum


class ThePlaceParser() : BaseParser("http://www.theplace.ru", "theplace") {
    private fun galleryItemForLink(link: Element): Gallery {
        val href = link.attr("href")
        val id = "mid(\\d+).html".toRegex().find(href)?.groups!![1]?.value
        return Gallery(title = link.text(), url = "/photos/$href", parser = this, id=id!!.toInt())
    }

    override fun getGalleries_internal(): List<Gallery> {
        val response = Unirest.get("${url}/photos").queryString("s_id", "0").asString()
        val doc = Jsoup.parse(response.body)
        val links = doc.select(".td_all .main-col-content .clearfix a")
        return links.map { galleryItemForLink(it) }
    }

    override fun getAlbums(gallery: Gallery): List<GalleryAlbum> {
        val response = Unirest.get("${url}/photos/gallery.php")
                .queryString("page", "1").queryString("id", gallery.id.toString()).asString()
        val doc = Jsoup.parse(response.body)
        val links = doc.select(".listalka.ltop a")
        val items = links.map { it.text() }.filter { "\\d+".toRegex().matches(it) }.map { it.toInt() }
        val count = when(items.size) {
            0 -> 1
            else -> items.max()
        }

        return IntRange(1, count ?: 1).map {
            var href = "/photos/gallery.php?id=${gallery.id}&page=$it"
            GalleryAlbum(url=href, gallery=gallery)
        }
    }
}