package rs.healthylife.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import rs.healthylife.app.ui.HealthyLifeApp
import rs.healthylife.app.ui.theme.HealthyLifeTema

/** Ulazna tacka aplikacije: prikazuje Compose ekrane preko celog ekrana telefona. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Svetla tema: tamne ikonice u statusnoj traci i na traci za navigaciju
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            HealthyLifeTema {
                HealthyLifeApp()
            }
        }
    }
}
