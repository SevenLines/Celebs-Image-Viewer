package theplace.views

import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.util.Callback
import theplace.parsers.elements.Gallery
import tornadofx.Fragment

/**
 * Created by mk on 03.04.16.
 */
class GalleryLayout(gallery: Gallery) : Fragment() {
    override val root: AnchorPane by fxml()
    val paginator: Pagination by fxid()
    val sldPage: Slider by fxid()

    init {
        sldPage.valueChangingProperty().addListener({ observableValue, t, isNowChanging ->
            if (!isNowChanging) {
                paginator.currentPageIndex = sldPage.value.toInt() - 1
            }
        })

        paginator.pageFactory = Callback { i ->
            run {
                var layout = GalleryAlbumLayout(gallery.albums[i]).root
                var anchor = AnchorPane()
                AnchorPane.setLeftAnchor(layout, 10.0)
                AnchorPane.setRightAnchor(layout, 10.0)
                AnchorPane.setTopAnchor(layout, 10.0)
                AnchorPane.setBottomAnchor(layout, 10.0)
                anchor.children.add(layout)

                sldPage.value = i.toDouble() + 1
                return@run anchor
            }
        }

        root.onScroll = EventHandler {
            if (it.isControlDown) {
                if (it.deltaY < 0) {
                    paginator.currentPageIndex++;
                } else {
                    paginator.currentPageIndex--;
                }
            }
        }

        background {
            gallery.albums
        } ui {
            paginator.pageCount = gallery.albums.size
            sldPage.min = 1.toDouble()
            sldPage.max = gallery.albums.size.toDouble()
        }

    }
}
