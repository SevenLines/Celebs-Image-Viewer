package theplace.views

import javafx.beans.value.ChangeListener
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
    override val root: VBox by fxml()
    val lblTitle: Label by fxid()
    val paginator: Pagination by fxid()
//    val txtCurrentPage: TextField by fxid()
    val sldPage: Slider by fxid()
    val contextMenu: ContextMenu = ContextMenu()

    init {
        paginator.contextMenu = contextMenu

        sldPage.valueChangingProperty().addListener({ observableValue, t, isNowChanging ->
            if (! isNowChanging) {
                paginator.currentPageIndex = sldPage.value.toInt()
            }
        })

        paginator.pageFactory = Callback { i ->  run {
            var layout = GalleryAlbumLayout(gallery.albums[i]).root
            var anchor = AnchorPane()
            AnchorPane.setLeftAnchor(layout, 10.0)
            AnchorPane.setRightAnchor(layout, 10.0)
            AnchorPane.setTopAnchor(layout, 10.0)
            AnchorPane.setBottomAnchor(layout, 10.0)
            anchor.children.add(layout)

//            txtCurrentPage.text = i.toString()
            sldPage.value = i.toDouble()

            return@run anchor
        } }

        background {
            gallery.albums
        } ui {
            paginator.pageCount = gallery.albums.size
            sldPage.min = 1.toDouble()
            sldPage.max = gallery.albums.size.toDouble()
            IntRange(1, gallery.albums.size).forEach {
                contextMenu.items.add(MenuItem(it.toString()))
            }
        }

    }
}
