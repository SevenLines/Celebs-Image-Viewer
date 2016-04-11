package theplace.views

import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.beans.property.SimpleBooleanProperty
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
import theplace.imageloaders.LoadedImage
import theplace.parsers.elements.GalleryAlbumPage
import tornadofx.Fragment
import java.io.InputStream
import kotlin.properties.Delegates

/**
 * Created by mk on 03.04.16.
 */
class GalleryAlbumLayout(albumPage: GalleryAlbumPage) : Fragment() {
    override val root: AnchorPane by fxml()
    val flowPanel: FlowPane by fxid()
    val imageContainer: ImageView by fxid()
    val imageWrapContainer: BorderPane by fxid()

    val imageContainerShowAnimation = ParallelTransition(imageWrapContainer)
    val albumLoadingComplete = SimpleBooleanProperty(false)

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
            albumPage.images
        } ui {
            flowPanel.children.addAll(albumPage.images.map {
                var galleryImageLayout = GalleryImageLayout(it)
                galleryImageLayout.onImageClick = EventHandler {
                    if (it.button == MouseButton.PRIMARY) {
                        imageContainer.image = imageLoading
                        imageWrapContainer.isVisible = true
                        imageContainerShowAnimation.rate = 1.0
                        imageContainerShowAnimation.play()

                        var loadedImage: LoadedImage? = null
                        setFit(true)

                        background {
                            loadedImage = galleryImageLayout.img.download()
                        } ui {
                            if (loadedImage != null) {
                                setFit(false)
                                if (loadedImage?.body?.markSupported() ?: false) {
                                    loadedImage?.body?.reset()
                                }
                                imageContainer.image = Image(loadedImage?.body)
                            }
                        }
                    }
                }
                return@map galleryImageLayout.root
            })
            albumLoadingComplete.set(true)
        }
    }
}