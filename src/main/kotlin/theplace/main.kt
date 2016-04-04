package theplace

import javafx.stage.Stage
import theplace.parsers.ThePlaceParser
import theplace.views.MainLayout
import tornadofx.App
import tornadofx.FX
import java.util.prefs.Preferences

/**
 * Created by mk on 03.04.16.
 */

class ThePlaceApplication : App() {
    override val primaryView = MainLayout::class

    init {

    }

    override fun start(stage: Stage) {
        super.start(stage)
        val preferences = Preferences.userNodeForPackage(javaClass)
        FX.primaryStage.x = preferences.getDouble("stage.x", 0.0)
        FX.primaryStage.y = preferences.getDouble("stage.y", 0.0)
        FX.primaryStage.width = preferences.getDouble("stage.width", 800.0)
        FX.primaryStage.height = preferences.getDouble("stage.height", 600.0)
        FX.primaryStage.isMaximized = preferences.getBoolean("stage.maximized", false)
    }

    override fun stop() {
        val preferences = Preferences.userNodeForPackage(javaClass)
        preferences.putDouble("stage.x", FX.primaryStage.x)
        preferences.putDouble("stage.y", FX.primaryStage.y)
        preferences.putDouble("stage.width", FX.primaryStage.width)
        preferences.putDouble("stage.height", FX.primaryStage.height)
        preferences.putBoolean("stage.maximized", FX.primaryStage.isMaximized)
        super.stop()
    }
}

