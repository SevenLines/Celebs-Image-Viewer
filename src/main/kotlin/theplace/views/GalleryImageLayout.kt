package theplace.views

import javafx.event.EventHandler
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
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

    var img_data: InputStream? = null
    var iconDownload: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/download.png")))
    var iconRemove: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/Trash.png")))
    var iconLoading: ImageView = ImageView(Image(javaClass.getResourceAsStream("images/loading.gif")))
    var isLoading = false

    fun update_interface() {
        var exists = img.exists(savePath())
        if (exists) {
            root.styleClass.add("exists")
        } else {
            root.styleClass.remove("exists")
        }
    }

    fun savePath() = Preferences.userRoot().get("savepath", ".")

    init {
        update_interface()

        overlayPane.center = iconLoading

        background {
            img_data = img.download_thumb()
        } ui {
            image.image = Image(img_data)
        }

        root.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, EventHandler {
            if (isLoading) {
                return@EventHandler
            }
            overlayPane.isVisible = true
            var exists = img.exists(savePath())
            overlayPane.center = if (exists) iconRemove else iconDownload
        })

        root.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, EventHandler {
            overlayPane.isVisible = isLoading || false
        })

        listOf(iconRemove, iconDownload, iconLoading).forEach {
            it.isPreserveRatio = true
            it.isSmooth = true
        }

        root.onMouseClicked = EventHandler {
            var dir_path = savePath()
            if (it.button == MouseButton.PRIMARY) {
                overlayPane.center = iconLoading
                overlayPane.isVisible = true
                isLoading = true
                background {
                    if (img.exists(dir_path)) {
                        Files.delete(Paths.get(img.get_path(dir_path)))
                    } else {
                        img.save_to_file(dir_path)
                    }
                } ui {
                    overlayPane.isVisible = false
                    isLoading = false
                    update_interface()
                }
            }
        }
    }
}