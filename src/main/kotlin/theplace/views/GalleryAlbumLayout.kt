package theplace.views

import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import theplace.parsers.elements.GalleryAlbum
import tornadofx.Fragment
import java.io.InputStream

/**
 * Created by mk on 03.04.16.
 */
class GalleryAlbumLayout(album: GalleryAlbum) : Fragment() {
    override val root: AnchorPane by fxml()
    val flowPanel: FlowPane by fxid()
    val imageContainer: ImageView by fxid()
    val imageWrapContainer: BorderPane by fxid()

    val imageContainerShowAnimation = ParallelTransition(imageWrapContainer)

    companion object {
        @JvmStatic val imageLoading = Image(GalleryAlbumLayout::class.java.getResourceAsStream("images/loading.gif"))
        @JvmStatic val duration = Duration(300.0)
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

    fun prepareAnimation() {
        // WRAPPER ANIMATION
        val imageContainerFadeTransition = FadeTransition(duration, imageWrapContainer)
        imageContainerFadeTransition.fromValue = 0.0
        imageContainerFadeTransition.interpolator = Interpolator.EASE_BOTH
        imageContainerFadeTransition.toValue = 1.0

        val imageContainerTranslateTransition = TranslateTransition(duration, imageWrapContainer)
        imageContainerTranslateTransition.fromYProperty().bind(
                SimpleIntegerProperty(0).subtract(imageWrapContainer.heightProperty()))
        imageContainerTranslateTransition.toY = 0.0
        imageContainerTranslateTransition.interpolator = Interpolator.EASE_BOTH

        imageContainerShowAnimation.children.addAll(imageContainerFadeTransition, imageContainerTranslateTransition)
    }

    init {
        prepareAnimation()

        flowPanel.prefWidthProperty().bind(root.widthProperty())

        imageWrapContainer.isVisible = false
        imageWrapContainer.onMouseClicked = EventHandler {
            if (it.button == MouseButton.SECONDARY) {
                imageContainerShowAnimation.rate = -1.0
                imageContainerShowAnimation.play()
            }
        }
        var clipRect = Rectangle(root.width, root.height)
        clipRect.widthProperty().bind(root.widthProperty())
        clipRect.heightProperty().bind(root.heightProperty())
        root.clip = clipRect
        imageWrapContainer.layoutBoundsProperty().addListener({ obj -> setFit() })

        background {
            album.images
        } ui {
            flowPanel.children.addAll(album.images.map {
                var gall = GalleryImageLayout(it)
                gall.onImageClick = EventHandler {
                    if (it.button == MouseButton.PRIMARY) {
                        imageContainer.image = imageLoading
                        imageWrapContainer.isVisible = true
                        imageContainerShowAnimation.rate = 1.0
                        imageContainerShowAnimation.play()

                        var image_data: InputStream? = null
                        setFit(true)

                        background {
                            image_data = gall.img.download()
                        } ui {
                            if (image_data != null) {
                                setFit(false)
                                if (image_data?.markSupported() ?: false) {
                                    image_data?.reset()
                                }
                                imageContainer.image = Image(image_data)
                            }
                        }
                    }
                }
                return@map gall.root
            })
        }
    }
}