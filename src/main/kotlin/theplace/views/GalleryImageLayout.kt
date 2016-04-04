package theplace.views

import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import theplace.parsers.elements.GalleryImage
import tornadofx.Fragment
import tornadofx.add
import javafx.scene.input.MouseEvent
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by mk on 03.04.16.
 */
class GalleryImageLayout(val img: GalleryImage) : Fragment() {
    override val root: AnchorPane by fxml()
    val image: ImageView by fxid()
    //    val btnDownload: Button by fxid()
    val imageContainer: AnchorPane by fxid()
    val overlayPane: BorderPane by fxid()

    var img_data: InputStream? = null
    var iconDownload: ImageView = ImageView(resources["../../images/download.png"])
    var iconRemove: ImageView = ImageView(resources["../../images/Trash.png"])
    var iconLoading: ImageView = ImageView(resources["../../images/loading.gif"])
    var isLoading = false

    var dir_path: String = "./downloads/"

    fun update_interface() {
        var exists = img.exists(dir_path)
        //        btnDownload.text = if (exists) "" else ""
        //        btnDownload.graphic = if (exists) iconRemove else iconDownload
        //        btnDownload.isDisable = false

        if (exists) {
            root.styleClass.add("exists")
            //            btnDownload.styleClass.add("exists")
        } else {
            root.styleClass.remove("exists")
            //            btnDownload.styleClass.remove("exists")
        }
    }

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
            var exists = img.exists(dir_path)
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