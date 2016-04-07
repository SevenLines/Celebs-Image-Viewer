package theplace.parsers

import com.mashape.unirest.http.Unirest
import org.jsoup.Jsoup
import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryImage
import theplace.parsers.elements.SubGallery
import java.io.InputStream

/**
 * Created by mk on 07.04.16.
 */
class SuperiorPicsParser : BaseParser(url = "http://forums.superiorpics.com/", title = "superiorpics") {
    override fun getAlbums(subGallery: SubGallery): List<GalleryAlbum> {
        var r = Unirest.get(subGallery.url).asString()
        var doc = Jsoup.parse(r.body)
        var boxes = doc.select(".content-left .box135.box-shadow-full")

        return boxes.map {
            var href = it.select("a").map { it.attr("href") }.filter {
                it.startsWith("http://forums.superiorpics.com")
            }.distinct().first()
            var title = it.select(".forum-box-news-title-small a").first().text()
            var thumb_url = it.select(".box135-thumb-wrapper img").first().attr("src")

            var album = GalleryAlbum(
                    url = href,
                    title = title,
                    subgallery = subGallery
            )
            album.thumb = GalleryImage(url = thumb_url, url_thumb = thumb_url, album = album)
            return@map album
        }
    }

    override fun getGalleries_internal(): List<Gallery> {
        throw UnsupportedOperationException()
    }

    override fun getImages(album: GalleryAlbum): List<GalleryImage> {
        var r = Unirest.get(album.url).asString()
        var doc = Jsoup.parse(r.body)
        var links = doc.select(".post-content .post_inner a")
        return links.filter {
            !(it.parent().hasClass("fr-item-thumb-box") or
                    it.parents().hasClass("signature"))
        }.map {
            var url = it.attr("href")
            var url_thumb = it.select("img").first()?.attr("src")
            if (url_thumb != null) {
                GalleryImage(album = album, url = url, url_thumb = url_thumb)
            } else {
                return@map null
            }
        }.filterNotNull()
    }

    override fun downloadImage(image_url: String): InputStream? {
        return Unirest.get("$image_url").header("referer", "$url").asBinary().body
    }

    override fun getSubGalleries(gallery: Gallery): List<SubGallery> {
        var r = Unirest.get(gallery.url).asString()
        var doc = Jsoup.parse(r.body)
        var links = doc.select(".alphaGender-font-paging-box-center a")

        var max = links.map { it.text() }.filter { "\\d+".toRegex().matches(it) }.map { it.toInt() }.max()
        return IntRange(1, max ?: 1).map {
            var newUrl = if (it != 1) "${gallery.url}/index$it.html" else gallery.url
            SubGallery(url = newUrl, gallery = gallery)
        }
    }
}