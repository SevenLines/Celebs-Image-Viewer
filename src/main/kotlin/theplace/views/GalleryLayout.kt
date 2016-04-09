package theplace.views

import javafx.animation.TranslateTransition
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Pagination
import javafx.scene.control.ScrollPane
import javafx.scene.control.Slider
import javafx.scene.control.SplitPane
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Callback
import javafx.util.Duration
import theplace.parsers.BaseParser
import theplace.parsers.elements.Gallery
import theplace.parsers.elements.GalleryAlbum
import theplace.parsers.elements.GalleryAlbumPage
import tornadofx.Fragment
import tornadofx.add
import java.io.InputStream

/**
 * Created by mk on 03.04.16.
 */
class GalleryLayout(val gallery: Gallery) : Fragment() {
    override val root: AnchorPane by fxml()
    val paginator: Pagination by fxid()
    val vbox: VBox by fxid()
    val loadingOverlay: BorderPane by fxid()
    val sldPage: Slider by fxid()

    fun openPage(page: GalleryAlbumPage): Node {
        var albumLayout = GalleryAlbumLayout(
                page
        )
        var layout = albumLayout.root

        var anchor = AnchorPane()
        AnchorPane.setLeftAnchor(layout, 10.0)
        AnchorPane.setRightAnchor(layout, 10.0)
        AnchorPane.setTopAnchor(layout, 10.0)
        AnchorPane.setBottomAnchor(layout, 10.0)
        anchor.children.add(layout)

        return anchor
    }


    fun openAlbum(album: GalleryAlbum) {
        background {
            album.pages
            Thread.sleep(1)
        } ui {
            sldPage.min = 1.toDouble()
            sldPage.max = album.pages.size.toDouble()

            paginator.pageCount = album.pages.size
            paginator.maxPageIndicatorCountProperty().bind(paginator.pageCountProperty())
            paginator.pageFactory = Callback { i ->
                if (album.pages.size > 0) {
                    sldPage.value = i.toDouble() + 1
                    return@Callback openPage(album.pages[i])
                }
                return@Callback null
            }
            paginator.currentPageIndex = 0
        }
    }

    init {
        sldPage.valueChangingProperty().addListener({ observableValue, t, isNowChanging ->
            if (!isNowChanging) {
                paginator.currentPageIndex = sldPage.value.toInt() - 1
            }
        })

        if (gallery.parser != null
                && (gallery.parser as BaseParser).isAlwaysOneAlbum
                && (gallery.parser as BaseParser).isAlwaysOneSubGallery) {
            loadingOverlay.isVisible = false
            openAlbum(gallery.subGalleries[0].albums[0])
        } else {
            background {
                loadingOverlay.isVisible = true
                gallery.subGalleries
            } ui {
                loadingOverlay.isVisible = false
                var pagSubGalleries = Pagination(gallery.subGalleries.size)
                pagSubGalleries.prefWidthProperty().bind(vbox.widthProperty())
                pagSubGalleries.maxPageIndicatorCountProperty().bind(pagSubGalleries.pageCountProperty())
                vbox.children.add(0, pagSubGalleries)
                pagSubGalleries.pageFactory = Callback { subGalleryIndex ->
                    var scrollPane = ScrollPane()
                    var hbox = HBox()
                    hbox.spacing = 8.0
                    scrollPane.prefViewportHeight = 200.0
                    scrollPane.prefWidthProperty().bind(pagSubGalleries.widthProperty())
                    scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    scrollPane.content = hbox
                    scrollPane.onScroll = EventHandler {
                        scrollPane.hvalue -= Math.signum(it.deltaY) * 0.25
                    }

                    var subGallery = gallery.subGalleries.get(subGalleryIndex)
                    background {
                        subGallery.albums
                        Thread.sleep(1)
                    } ui {
                        pagSubGalleries.isVisible = true
                        subGallery.albums.map { it.thumb }.filterNotNull().forEach {
                            var borderPane = BorderPane()
                            borderPane.padding = Insets(10.0)
                            borderPane.style = "-fx-background-color:white"
                            borderPane.effect = DropShadow(3.0, 0.0, 0.0, Color.SILVER)

                            var imageView = ImageView(GalleryImageLayout.imageLoading)
                            imageView.fitHeightProperty().bind(scrollPane.heightProperty().subtract(22.0))
                            imageView.isPreserveRatio = true
                            imageView.isSmooth = true
                            imageView.cursor = Cursor.HAND
                            imageView.userData = it.album

                            var stream: InputStream? = null
                            background {
                                stream = it.download_thumb()
                            } ui {
                                imageView.image = Image(stream)
                            }
                            borderPane.center = imageView
                            hbox.add(borderPane)

                            imageView.onMouseClicked = EventHandler {
                                var album = imageView.userData as GalleryAlbum
                                openAlbum(album)
                            }
                        }
                    }
                    return@Callback scrollPane
                }
                pagSubGalleries.currentPageIndex = 1
            }
        }
    }
}
