package theplace.views

import javafx.collections.FXCollections.observableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import theplace.controllers.MainLayoutController
import theplace.parsers.BaseParser
import theplace.parsers.SuperiorPicsParser
import theplace.parsers.ThePlaceParser
import theplace.parsers.elements.Gallery
import tornadofx.View
import java.io.File
import java.util.prefs.Preferences

/**
 * Created by mk on 03.04.16.
 */
class MainLayout : View() {
    override val root: VBox by fxml()
    val galleriesList: ListView<Gallery> by fxid()

    val cmbParsers: ComboBox<BaseParser> by fxid()
    val btnSavePathSelector: Button by fxid()
    val btnRefresh: Button by fxid()
    val txtQuery: TextField by fxid()
    val txtSavePath: TextField by fxid()
    val tabPane: TabPane by fxid()
    val controller: MainLayoutController by inject()

    var galleries: FilteredList<Gallery>? = null
    var tabMap: MutableMap<Gallery, Tab> = mutableMapOf()

    private var parsers = observableList(listOf(
            ThePlaceParser(),
            SuperiorPicsParser()
    ))

    init {
        cmbParsers.selectionModel.selectedItemProperty().addListener({ obs, o, newParser ->
            background {
                galleries = FilteredList(observableList(newParser.galleries), {
                    it.title.contains(txtQuery.text, true)
                })
            } ui {
                Preferences.userRoot().put("parser", newParser.title)
                galleriesList.items = galleries
            }
        })

        txtQuery.textProperty().addListener({
            value, old, new ->
            run {
                galleries?.setPredicate { it.title.contains(new, true) }
                Preferences.userRoot().put("query", new)
            }
        })

        btnSavePathSelector.onAction = EventHandler {
            var chooser = DirectoryChooser()
            chooser.initialDirectory = File(if (!txtSavePath.text.isNullOrEmpty()) txtSavePath.text else ".")
            var file = chooser.showDialog(primaryStage)

            if (file != null) {
                txtSavePath.text = file.absolutePath
                Preferences.userRoot().put("savepath", file.absolutePath)
            }
        }

        root.onKeyPressed = EventHandler {
            if (it.isControlDown && it.code == KeyCode.W) {
                if (tabPane.selectionModel.selectedItem != null) {
                    tabMap.remove(tabPane.selectionModel.selectedItem.userData)
                    tabPane.tabs.remove(tabPane.selectionModel.selectedItem)
                }
            }
        }

//        btnRefresh.onAction = EventHandler {
//            background {
//                galleries = FilteredList(observableList(controller.refreshGalleries()), {
//                    it.title.contains(txtQuery.text, true)
//                })
//            } ui {
//                galleriesList.items = galleries
//            }
//        }

        galleriesList.selectionModel.selectedItemProperty().addListener({
            observableValue, old, newGallery ->
            if (newGallery != null) {
                if (tabMap.containsKey(newGallery)) {
                    var tab = tabMap.get(newGallery)
                    tabPane.selectionModel.select(tab)
                } else {
                    var tab = Tab()
                    tab.text = newGallery.title
                    tab.content = GalleryLayout(newGallery).root
                    tabPane.tabs.add(0, tab)
                    tabPane.selectionModel.select(0)

                    tab.onClosed = EventHandler {
                        System.out.println("closed")
                        tabMap.remove(newGallery)
                    }
                    tab.userData = newGallery

                    tabMap.set(newGallery, tab)
                }
            }
        })

        txtSavePath.text = Preferences.userRoot().get("savepath", ".")
        txtQuery.text = Preferences.userRoot().get("query", "")

        cmbParsers.items = parsers
        var galleryItem = cmbParsers.items.find { it.title == Preferences.userRoot().get("parser", "") }
        if (galleryItem != null)
            cmbParsers.selectionModel.select(galleryItem)
        else
            cmbParsers.selectionModel.select(cmbParsers.items[0])
    }


}
