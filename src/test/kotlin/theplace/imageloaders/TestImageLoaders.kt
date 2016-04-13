package theplace.imageloaders

import com.mashape.unirest.http.Unirest
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Factory
import org.testng.annotations.Test
import java.net.URL
import javax.imageio.ImageIO

/**
 * Created by mk on 10.04.16.
 */
class TestImageLoaders : Assert() {

    @DataProvider(parallel = true) fun data() = arrayOf(
            arrayOf("https://someimage.com/gxAPNeU", SomeImageImageLoader(), "gxAPNeU"),
            arrayOf("http://www.imagebam.com/image/6b4a9e472012133", ImagebamImageLoader(), "6b4a9e472012133"),
            arrayOf("http://img125.imagevenue.com/img.php?image=15029_septimiu29_AshleyGreene_SeventeenUSA_Dec2012_Jan20131_122_594lo.jpg",
                    ImagevenueImageLoader(),
                    "15029_septimiu29_AshleyGreene_SeventeenUSA_Dec2012_Jan20131_122_594lo.jpg"),
            arrayOf("http://www.hotflick.net/f/v/?q=2782436.th_30605_ab13485dfbad8970c_800wi_122_561lo.jpg", HotflickImageLoader(),
                    "th_30605_ab13485dfbad8970c_800wi_122_561lo.jpg"),
            arrayOf("http://imgbox.com/L0oiABWW", ImageBoxImageLoader(), "L0oiABWW")
    )

    @Test(dataProvider = "data")
    fun should_load_successfully(url: String, loader: BaseImageLoaderInterface, title: String) {
        assertTrue(loader.checkUrl(url))
        var loaderReal = ImageLoaderSelector.getLoader(url)
        assertEquals((loaderReal as Any).javaClass, loader.javaClass)
        assertEquals(loaderReal.getTitle(url), title)

        var inputStream = ImageLoaderSelector.download(url)
        var img = ImageIO.read(inputStream?.body)

        assertTrue(img.width > 0)
        assertTrue(img.height > 0)
    }
}