package theplace

import theplace.parsers.ThePlaceParser
import theplace.views.MainLayout
import tornadofx.App

/**
 * Created by mk on 03.04.16.
 */

class ThePlaceApplication : App() {
    override val primaryView = MainLayout::class

    init {
//
    }
}

