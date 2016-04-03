package theplace.views

import javafx.beans.value.ChangeListener
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import theplace.parsers.elements.GalleryAlbum
import tornadofx.Fragment
import tornadofx.View

/**
 * Created by mk on 03.04.16.
 */
class GalleryAlbumLayout(album: GalleryAlbum) : Fragment() {
    override val root: AnchorPane by fxml()
    val flowPanel: FlowPane by fxid()

    init {
        root.widthProperty().addListener(ChangeListener { observableValue, old, new ->
            flowPanel.setPrefWidth(new.toDouble())
        })

        background {
            album.images
        } ui {
            flowPanel.children.addAll(album.images.map { GalleryImageLayout(it).root })
        }
    }
}