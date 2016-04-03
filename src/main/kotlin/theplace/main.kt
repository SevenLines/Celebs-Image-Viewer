package theplace

import theplace.parsers.ThePlaceParser

/**
 * Created by mk on 03.04.16.
 */
fun main(args: Array<String>) {
    var parser = ThePlaceParser()
    println(parser.galleries)
    var album = parser.getGalleryById(3918)
    println(album?.albums)
}