package theplace.parsers.elemets

import org.apache.commons.io.FileUtils
import org.testng.Assert
import org.testng.annotations.*
import theplace.parsers.elements.*
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.properties.Delegates

/**
 * Created by mk on 11.04.16.
 */
class TestItems : Assert() {
    var image: GalleryImage by Delegates.notNull()
    var gallery = Gallery(title = "gallery")
    var subgallery = SubGallery(title = "subgallery", gallery = gallery)
    var album = GalleryAlbum(title = "album", subgallery = subgallery)
    var albumPage = GalleryAlbumPage(url = "", album = album)
    var testDataPath = Paths.get("testData", "out")

    init {
        GalleryImage.CACHE_DIR = Paths.get(testDataPath.toString(), "cache").toString()
    }


    @BeforeTest fun setUp() {
        image = GalleryImage(
                title = "image",
                url = "http://www.imagebam.com/image/b3c704464276987",
                url_thumb = "http://thumbnails105.imagebam.com/46428/b3c704464276987.jpg",
                album = album,
                page = albumPage
        )
    }

    @AfterTest fun cleanTestDirs() {
        FileUtils.deleteDirectory(testDataPath.toFile())
    }

    @Test fun testDownloadThumb() {
        var info = image.downloadThumb()!!
        assertEquals(info.url, image.url_thumb)
        var img = ImageIO.read(info.body)
        assertTrue(img.width > 0)
        assertTrue(img.height > 0)
    }

    @Test fun testDownloadImage() {
        var info = image.download()!!
        assertEquals(info.url, "http://105.imagebam.com/download/Ff2aHFRqmel5xGMJ9w51-g/46428/464276987/ezNj6oN.jpg")
        var img = ImageIO.read(info.body)
        assertTrue(img.width > 0)
        assertTrue(img.height > 0)
    }

    @DataProvider fun data() = arrayOf(
            arrayOf(""),
            arrayOf(GalleryImage.THUMBS_PREFIX)
    )

    @Test(dataProvider = "data") fun testSaveToPath(prefix: String) {
        var expectedPath = Paths.get(testDataPath.toString(), prefix,
                gallery.title, subgallery.title, album.title, "b3c704464276987.jpg")
        var cachePath = Paths.get(GalleryImage.CACHE_DIR, prefix,
                gallery.title, subgallery.title, album.title, "b3c704464276987.jpg")

        var path: Path? = if (prefix==GalleryImage.THUMBS_PREFIX)
            image.saveThumbToPath(testDataPath)
        else image.saveToPath(testDataPath)

        // check that file saved to cache
        assertEquals(path, expectedPath)
        assertTrue(cachePath.toFile().exists())
        assertTrue(expectedPath.toFile().exists())

        path = if (prefix==GalleryImage.THUMBS_PREFIX) image.thumbExists(testDataPath)
        else image.exists(testDataPath)
        assertNotNull(path)
        assertEquals(path, expectedPath)
    }
}