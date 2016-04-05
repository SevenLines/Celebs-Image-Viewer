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
    var iconDownload: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/download.png")))
    var iconRemove: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/Trash.png")))
    var iconLoading: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/loading.gif")))
    var isDownloading = false
    var opTransition = TranslateTransition(Duration(300.0), overlayPane)

    fun update_interface(force: Boolean = false, check_exists: Boolean = false) {
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
                overlayPane.isVisible = false
                return
            } else {
                overlayPane.isVisible = true
                opTransition.play()
            }
        }
    }

    companion object {
        @JvmField
        val CURRENT_IMAGE = SimpleObjectProperty<Fragment>()
    }

    fun savePath() = Preferences.userRoot().get("savepath", ".")

    init {
        var clipRect = Rectangle(root.width, root.height)
        clipRect.heightProperty().bind(root.heightProperty())
        clipRect.widthProperty().bind(root.widthProperty())
        root.clip = clipRect

        overlayPane.center = iconLoading
        opTransition.interpolator = Interpolator.EASE_OUT
        opTransition.fromYProperty().bind(root.heightProperty())
        opTransition.toYProperty().bind(root.heightProperty().subtract(overlayPane.heightProperty()))

        background {
            img_data = img.download_thumb()
        } ui {
            image.image = Image(img_data)
            overlayPane.layoutX = root.height
        }

        CURRENT_IMAGE.addListener({ obs ->
            update_interface(true)
        })

        root.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandler {
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
                    update_interface(true, true)
                }
            }
        }
        update_interface(true, true)
    }
}