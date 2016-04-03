package theplace.views

import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import theplace.parsers.elements.GalleryImage
import tornadofx.Fragment
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by mk on 03.04.16.
 */
class GalleryImageLayout(img: GalleryImage) : Fragment() {
    override val root: AnchorPane by fxml()
    val image: ImageView by fxid()
    val chkExists: CheckBox by fxid()
    val btnDownload: Button by fxid()
    var img_data: InputStream? = null

    var dir_path: String = "/home/mk/IdeaProjects/ThePlaceKotlin/data"

    init {
        chkExists.isSelected = img.exists(dir_path)
        btnDownload.text = if (chkExists.isSelected) "remove" else "download"
        background {
            img_data = img.download_thumb()
        } ui {
            image.image = Image(img_data)
        }

        btnDownload.onAction = EventHandler {
            background {
                if (chkExists.isSelected) {
                    Files.delete(Paths.get(img.get_path(dir_path)))
                } else {
                    img.save_to_file(dir_path)
                }
            } ui {
                chkExists.isSelected = img.exists(dir_path)
                btnDownload.text = if (chkExists.isSelected) "remove" else "download"
            }
        }
    }
}