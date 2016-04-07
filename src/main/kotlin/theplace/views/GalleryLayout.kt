package theplace.views

import javafx.event.EventHandler
import javafx.scene.control.Pagination
import javafx.scene.control.Slider
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.util.Callback
import theplace.parsers.elements.Gallery
import tornadofx.Fragment

/**
 * Created by mk on 03.04.16.
 */
class GalleryLayout(gallery: Gallery) : Fragment() {
    override val root: AnchorPane by fxml()
    val paginator: Pagination by fxid()
    val paneLoading: BorderPane by fxid()
    val sldPage: Slider by fxid()

    init {
        background {
            gallery.subGalleries[0].albums
        } ui {
            paginator.pageCount = gallery.subGalleries[0].albums.size
            sldPage.min = 1.toDouble()
            sldPage.max = gallery.subGalleries[0].albums.size.toDouble()

            paginator.pageFactory = Callback { i ->
                if (gallery.subGalleries[0].albums.count() > 0) {
                    var albumLayout = GalleryAlbumLayout(gallery.subGalleries[0].albums[i])
                    var layout = albumLayout.root
                    paneLoading.visibleProperty().bind(albumLayout.albumLoadingComplete.not())

                    var anchor = AnchorPane()
                    AnchorPane.setLeftAnchor(layout, 10.0)
                    AnchorPane.setRightAnchor(layout, 10.0)
                    AnchorPane.setTopAnchor(layout, 10.0)
                    AnchorPane.setBottomAnchor(layout, 10.0)
                    anchor.children.add(layout)

                    sldPage.value = i.toDouble() + 1
                    return@Callback anchor
                }
                return@Callback null
            }
        }

        sldPage.valueChangingProperty().addListener({ observableValue, t, isNowChanging ->
            if (!isNowChanging) {
                paginator.currentPageIndex = sldPage.value.toInt() - 1
            }
        })

        root.onScroll = EventHandler {
            if (it.isControlDown) {
                if (it.deltaY < 0) {
                    paginator.currentPageIndex++;
                } else {
                    paginator.currentPageIndex--;
                }
            }
        }
    }
}
