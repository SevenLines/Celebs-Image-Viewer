package theplace

import com.mashape.unirest.http.Unirest
import theplace.parsers.ThePlaceParser
import java.nio.file.FileSystems
import java.nio.file.Files

/**
 * Created by mk on 03.04.16.
 */
fun main(args: Array<String>) {
    var parser = ThePlaceParser()
    var gallery = parser.getGalleryById(3918)!!
    var image = gallery.albums[1].images[0]
    image.save_to_file("/home/mk/IdeaProjects/ThePlaceKotlin/out.jpg")
}