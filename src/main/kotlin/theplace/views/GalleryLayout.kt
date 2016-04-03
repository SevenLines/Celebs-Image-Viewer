package theplace.views

import javafx.scene.control.Label
import javafx.scene.control.Pagination
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

    init {
        paginator.pageFactory = Callback { i ->  run {
            var layout = GalleryAlbumLayout(gallery.albums[i]).root
            var anchor = AnchorPane()
            AnchorPane.setLeftAnchor(layout, 10.0)
            AnchorPane.setRightAnchor(layout, 10.0)
            AnchorPane.setTopAnchor(layout, 10.0)
            AnchorPane.setBottomAnchor(layout, 10.0)
            anchor.children.add(layout)
            return@run anchor
        } }
//        paginator.pageFactory = Callback { i -> Rectangle(200.0, 200.0) }
        background {
            gallery.albums
        } ui {
            paginator.pageCount = gallery.albums.size
        }

    }
}
