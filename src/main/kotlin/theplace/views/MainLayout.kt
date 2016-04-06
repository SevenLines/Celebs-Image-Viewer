package theplace.views

import javafx.collections.FXCollections.observableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import theplace.controllers.MainLayoutController
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

    val btnSavePathSelector: Button by fxid()
    val btnRefresh: Button by fxid()
    val txtQuery: TextField by fxid()
    val txtSavePath: TextField by fxid()
    val tabPane: TabPane by fxid()
    val controller: MainLayoutController by inject()

    var galleries: FilteredList<Gallery>? = null
    var tabMap: MutableMap<Gallery, Tab> = mutableMapOf()

    init {
        txtSavePath.text = Preferences.userRoot().get("savepath", ".")
        txtQuery.text = Preferences.userRoot().get("query", "")

        background {
            galleries = FilteredList(observableList(controller.listGalleries()), {
                it.title.contains(txtQuery.text, true)
            })
        } ui {
            galleriesList.items = galleries
        }

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

        btnRefresh.onAction = EventHandler {
            background {
                galleries = FilteredList(observableList(controller.refreshGalleries()), {
                    it.title.contains(txtQuery.text, true)
                })
            } ui {
                galleriesList.items = galleries
            }
        }

        galleriesList.getSelectionModel().selectedItemProperty().addListener({
            observableValue, old, newGallery ->
            run {
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

                        tab.onClosed = EventHandler { tabMap.remove(newGallery) }

                        tabMap.set(newGallery, tab)
                    }
                }
            }
        })
    }


}
