package theplace.views

import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.FXCollections.observableList
import javafx.collections.transformation.FilteredList
import javafx.event.EventHandler
import javafx.scene.control.ListView
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TextField
import javafx.scene.layout.*
import theplace.controllers.MainLayoutController
import theplace.parsers.elements.Gallery
import tornadofx.View
import tornadofx.asyncItems
import tornadofx.selectedItem

/**
 * Created by mk on 03.04.16.
 */
class MainLayout : View() {
    override val root: VBox by fxml()
    val queryText: TextField by fxid()
    val galleriesList: ListView<Gallery> by fxid()
    val tabPane: TabPane by fxid()
    val controller: MainLayoutController by inject()

    var galleries: FilteredList<Gallery>? = null
    var tabMap: MutableMap<Gallery, Tab> = mutableMapOf()

    init {
        background {
            galleries = FilteredList(observableList(controller.listGalleries()))
        } ui {
            galleriesList.items = galleries
        }

        queryText.textProperty().addListener(ChangeListener {
            value, old, new ->
            run {
                galleries?.setPredicate { it.title.contains(new, true) }
            }
        })

        galleriesList.getSelectionModel().selectedItemProperty().addListener(ChangeListener {
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
