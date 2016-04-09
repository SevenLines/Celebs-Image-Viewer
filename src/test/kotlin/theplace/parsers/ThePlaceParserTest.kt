package theplace.parsers

import org.testng.Assert
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

/**
 * Created by mk on 09.04.16.
 */
class ThePlaceParserTest: Assert() {
    var parser = ThePlaceParser()

    @BeforeSuite fun setup() {
        parser = ThePlaceParser()
    }

    @Test fun testGetGalleries() {
        assertEquals(this.parser.galleries.size, 5580)
    }

    @Test fun testGetSubGalleries() {
        var gallery = this.parser.galleries.find { it.id == 3918 }!!
        assertEquals(gallery.subGalleries.size, 1)
    }

    @Test fun testGetAlbums() {
        var gallery = this.parser.galleries.find { it.id == 3918 }!!
        var albums = gallery.subGalleries[0].albums
        assertEquals(albums.size, 1)
    }

    @Test fun testGetPages() {
        var gallery = this.parser.galleries.find { it.id == 3918 }!!
        var pages = gallery.subGalleries[0].albums[0].pages
        assertTrue(pages.size > 15)
    }

    @Test fun testGetImages() {
        var gallery = this.parser.galleries.find { it.id == 3918 }!!
        var images = gallery.subGalleries[0].albums[0].pages[15].images
        assertTrue(images.size != 0)
    }
}