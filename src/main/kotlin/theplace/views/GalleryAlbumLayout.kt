package theplace.views

import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import theplace.parsers.elements.GalleryAlbum
import tornadofx.Fragment
import tornadofx.View
import tornadofx.add
import java.io.InputStream

/**
 * Created by mk on 03.04.16.
 */
class GalleryAlbumLayout(album: GalleryAlbum) : Fragment() {
    override val root: AnchorPane by fxml()
    val flowPanel: FlowPane by fxid()
    val imageContainer: ImageView by fxid()
    val imageWrapContainer: BorderPane by fxid()

    companion object {
        @JvmField
        val imageLoading = Image(GalleryAlbumLayout::class.java.getResourceAsStream("images/loading.gif"))
    }

    fun setFit(isReal: Boolean=false) {
        if (isReal) {
            imageContainer.fitWidth = 0.0
            imageContainer.fitHeight = 0.0
        } else {
            imageContainer.fitWidth = root.width
            imageContainer.fitHeight = root.height
        }
    }

    init {
        flowPanel.prefWidthProperty().bind(root.widthProperty())

        imageWrapContainer.isVisible = false
        imageWrapContainer.onMouseClicked = EventHandler {
            if (it.button == MouseButton.SECONDARY) {
                imageWrapContainer.isVisible = false
            }
        }
        imageWrapContainer.layoutBoundsProperty().addListener({ obj -> setFit() })

        background {
            album.images
        } ui {
            flowPanel.children.addAll(album.images.map {
                var gall = GalleryImageLayout(it)
                gall.onImageClick = EventHandler {
                    imageContainer.image = imageLoading
                    imageWrapContainer.isVisible = true
                    var image_data: InputStream? = null
                    setFit(true)

                    background {
                        image_data = gall.img.download()
                    } ui {
                        if (image_data != null) {
                            setFit(false)
                            imageContainer.image = Image(image_data)
                        }
                    }
                }
                return@map gall.root
            })
        }
    }
}