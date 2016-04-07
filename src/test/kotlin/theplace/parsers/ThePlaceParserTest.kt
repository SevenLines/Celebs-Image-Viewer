package theplace.parsers

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.hamcrest.Matcher
/**
 * Created by mk on 08.04.16.
 */
public class ThePlaceParserTest {

    var parser: SuperiorPicsParser = SuperiorPicsParser()

//    @Before fun setUp() {
//        this.parser = SuperiorPicsParser()
//    }

    @Test fun testGetGalleries() {
        assertEquals(this.parser.galleries.size, 14084)
    }

    @Test fun testGetSubGalleries() {
        var subGalleries = this.parser.galleries[454].subGalleries
        assertTrue(subGalleries.size > 37)
    }

    @Test fun testGetAlbums() {
        var albums = this.parser.galleries[454].subGalleries[0].albums
        assertEquals(albums.size, 12)
    }

}