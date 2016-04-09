package theplace.parsers

import org.testng.Assert
import org.testng.annotations.BeforeSuite
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

/**
 * Created by mk on 08.04.16.
 */
class SuperiorPicsParserTest : Assert() {
    var parser: SuperiorPicsParser = SuperiorPicsParser()

    @BeforeSuite fun setUp() {
        parser = SuperiorPicsParser()
    }

    @Test fun testGetGalleries() {
        assertEquals(this.parser.galleries.size, 14084)
    }

    @Test fun testGetSubGalleries() {
        var subGalleries = this.parser.galleries[454].subGalleries
        assertNotNull(subGalleries[0].gallery)
        assertTrue(subGalleries.size > 37)
    }

    @Test fun testGetAlbums() {
        var albums = this.parser.galleries[454].subGalleries[0].albums
        assertNotNull(albums[0].subgallery)
        assertNotNull(albums[0].thumb?.album)
        assertNotNull(albums[0].subgallery?.gallery)
        assertEquals(albums.size, 12)
    }

    @Test fun testGetPages() {
        var pages = this.parser.galleries[454].subGalleries[0].albums[0].pages
        assertNotNull(pages[0].album)
        assertNotNull(pages[0].album?.subgallery)
        assertNotNull(pages[0].album?.subgallery?.gallery)
        assertEquals(pages.size, 1)
    }

    @Test fun testGetImages() {
        var images = this.parser.galleries[454].subGalleries[0].albums[10].pages[0].images
        assertNotNull(images[0].album)
        assertNotNull(images[0].page)
        assertNotNull(images[0].page?.album?.subgallery)
        assertNotNull(images[0].album?.subgallery?.gallery)
        print(images.size)
    }

}