package theplace.parsers

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import theplace.parsers.elements.*
import java.io.InputStream


class ThePlaceParser() : BaseParser("http://www.theplace.ru", "theplace") {
    override var isAlwaysOneAlbum = true
    override var isAlwaysOneSubGallery = true

    override fun getAlbumPages(album: GalleryAlbum): List<GalleryAlbumPage> {
        val response = Unirest.get("${url}${album.url}").asString()
        val doc = Jsoup.parse(response.body)
        val links = doc.select(".listalka.ltop a")
        val items = links.map { it.text() }.filter { "\\d+".toRegex().matches(it) }.map { it.toInt() }
        val count = when (items.size) {
            0 -> 1
            else -> items.max()
        }

        return IntRange(1, count ?: 1).map {
            var href = "/photos/gallery.php?id=${album.id}&page=$it"
            GalleryAlbumPage(url = href, album = album)
        }
    }

    private fun galleryItemForLink(link: Element): Gallery {
        val href = link.attr("href")
        val id = """mid(\d+).html""".toRegex().find(href)?.groups!![1]?.value
        return Gallery(title = link.text(), url = "/photos/$href", parser = this, id = id!!.toInt())
    }

    private fun galleryImageForLink(el: Element, albumPage: GalleryAlbumPage): GalleryImage {
        var thumb = el.attr("src")
        var url = """^(.*?)(_s)(\.\w+)$""".toRegex().replace(thumb, """$1$3""")
        return GalleryImage(
                url_thumb = thumb,
                url = url,
                page = albumPage,
                album = albumPage.album
        )
    }

    override fun downloadImage(image_url: String): InputStream? {
        return Unirest.get("${url}${image_url}").header("referer", "$url").asBinary().body
    }

    override fun getImages(albumPage: GalleryAlbumPage): List<GalleryImage> {
        var response = Unirest.get("${url}${albumPage.url}").asString()
        var doc = Jsoup.parse(response.body)
        var links = doc.select(".gallery-pics-list .pic_box a img")
        return links.map { galleryImageForLink(it, albumPage) }
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

    override fun getAlbums(subGallery: SubGallery): List<GalleryAlbum> {
        return listOf(GalleryAlbum(
                url = subGallery.url,
                title = subGallery.title,
                id = subGallery.id,
                subgallery = subGallery)
        )
    }
}