package theplace.views

import javafx.animation.Animation
import javafx.animation.Interpolator
import javafx.animation.TranslateTransition
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import theplace.parsers.elements.GalleryImage
import tornadofx.Fragment
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.prefs.Preferences

/**
 * Created by mk on 03.04.16.
 */
class GalleryImageLayout(val img: GalleryImage) : Fragment() {
    override val root: AnchorPane by fxml()
    val image: ImageView by fxid()
    val overlayPane: BorderPane by fxid()
    val imageContainer: AnchorPane by fxid()

    var img_data: InputStream? = null
    var iconDownload: ImageView = ImageView(imageDownload)
    var iconRemove: ImageView = ImageView(imageRemove)
    var iconLoading: ImageView = ImageView(imageLoading)
    var isDownloading = false
    var slideTransition = TranslateTransition(Duration(300.0), overlayPane)
//    var slideTransitionRev = TranslateTransition(Duration(300.0), overlayPane)
    var onImageClick: EventHandler<MouseEvent>? = null

    companion object {
        @JvmStatic val CURRENT_IMAGE = SimpleObjectProperty<Fragment>()
        @JvmStatic val imageDownload = Image(GalleryImageLayout::class.java.getResourceAsStream("images/download.png"))
        @JvmStatic val imageRemove = Image(GalleryImageLayout::class.java.getResourceAsStream("images/Trash.png"))
        @JvmStatic val imageLoading = Image(GalleryImageLayout::class.java.getResourceAsStream("images/loading.gif"))
        @JvmStatic val CACHE_DIR = "./"
    }

    fun update_interface(check_exists: Boolean = false) {
        if (check_exists) {
            var exists = img.exists(savePath())
            if (exists) {
                root.styleClass.clear()
                root.styleClass.add("exists")
            } else {
                root.styleClass.clear()
            }
            overlayPane.center = if (exists) iconRemove else iconDownload
        }

        if (!isDownloading) {
            if (CURRENT_IMAGE.value != this) {
                slideTransition.rate = -1.0
                slideTransition.setOnFinished { overlayPane.isVisible = false }
                slideTransition.play()
                return
            } else {
                overlayPane.isVisible = true
                slideTransition.setOnFinished {}
                slideTransition.rate = 1.0
                slideTransition.play()
            }
        }
    }

    fun savePath() = Preferences.userRoot().get("savepath", ".")

    init {
        var clipRect = Rectangle(root.width, root.height)
        clipRect.heightProperty().bind(root.heightProperty())
        clipRect.widthProperty().bind(root.widthProperty())
        root.clip = clipRect

        overlayPane.center = iconLoading
        slideTransition.interpolator = Interpolator.EASE_OUT
        slideTransition.fromYProperty().bind(root.heightProperty())
        slideTransition.toYProperty().bind(root.heightProperty().subtract(overlayPane.heightProperty()).subtract(3))

        background {
            img_data = img.download_thumb()
        } ui {
            image.image = Image(img_data)
            overlayPane.layoutX = root.height
        }

        CURRENT_IMAGE.addListener({ obs ->
            update_interface()
        })

        root.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, {
            CURRENT_IMAGE.set(this)
        })

        listOf(iconRemove, iconDownload, iconLoading).forEach {
            it.isPreserveRatio = true
            it.isSmooth = true
        }

        overlayPane.onMouseClicked = EventHandler {
            var dir_path = savePath()
            if (it.button == MouseButton.PRIMARY) {
                overlayPane.center = iconLoading
                isDownloading = true
                background {
                    if (img.exists(dir_path)) {
                        Files.delete(Paths.get(img.get_path(dir_path)))
                    } else {
                        img.save_to_file(dir_path)
                    }
                } ui {
                    isDownloading = false
                    update_interface(true)
                }
            }
        }
        image.onMouseClicked = EventHandler { onImageClick?.handle(it) }
        update_interface(true)
    }
}