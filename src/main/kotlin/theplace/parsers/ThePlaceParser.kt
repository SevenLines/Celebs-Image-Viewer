package theplace.parsers

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryImage
import java.io.InputStream


class ThePlaceParser() : BaseParser("http://www.theplace.ru", "theplace") {
    private fun galleryItemForLink(link: Element): Gallery {
        val href = link.attr("href")
        val id = """mid(\d+).html""".toRegex().find(href)?.groups!![1]?.value
        return Gallery(title = link.text(), url = "/photos/$href", parser = this, id=id!!.toInt())
    }

    private fun galleryImageForLink(el: Element, album: GalleryAlbum): GalleryImage {
        var thumb = el.attr("src")
        var url = """^(.*?)(_s)(\.\w+)$""".toRegex().replace(thumb, """$1$3""")
        return GalleryImage(
            url_thumb = thumb,
            url = url,
            album = album
        )
    }

    override fun downloadImage(image_url: String): InputStream? {
        return Unirest.get("${url}${image_url}").header("referer", "$url").asBinary().body
    }

    override fun getImages(album: GalleryAlbum): List<GalleryImage> {
        var response = Unirest.get("${url}${album.url}").asString()
        var doc = Jsoup.parse(response.body)
        var links = doc.select(".gallery-pics-list .pic_box a img")
        return links.map { galleryImageForLink(it, album) }
    }

    override fun getGalleries_internal(): List<Gallery> {
        var out = IntRange(0, 3).map {
            val response = Unirest.get("${url}/photos").queryString("s_id", it.toString()).asString()
            val doc = Jsoup.parse(response.body)
            val links = doc.select(".td_all .main-col-content .clearfix a")
            return@map links.map { galleryItemForLink(it) }
        }.reduce { list, list2 -> list + list2 }
        return out
    }

    override fun getAlbums(gallery: Gallery): List<GalleryAlbum> {
        val response = Unirest.get("${url}${gallery.url}").asString()
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