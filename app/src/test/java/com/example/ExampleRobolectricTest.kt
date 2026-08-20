package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ParkingCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ParkGo", appName)
  }

  @Test
  fun `verify parking rate calculation rules`() {
    // 0 - 1 hour: Ksh 50
    assertEquals(50, ParkingCalculator.calculateFee(30))
    assertEquals(50, ParkingCalculator.calculateFee(60))

    // 1 - 2 hours: Ksh 100
    assertEquals(100, ParkingCalculator.calculateFee(85)) // 1h 25m
    assertEquals(100, ParkingCalculator.calculateFee(120)) // 2h

    // 2 - 3 hours: Ksh 200
    assertEquals(200, ParkingCalculator.calculateFee(150))
    assertEquals(200, ParkingCalculator.calculateFee(180)) // 3h

    // 3 - 4 hours: Ksh 350
    assertEquals(350, ParkingCalculator.calculateFee(190)) // 3h 10m
    assertEquals(350, ParkingCalculator.calculateFee(240)) // 4h

    // Above 4 hours: Ksh 500 flat (up to 5h)
    assertEquals(500, ParkingCalculator.calculateFee(270))
    assertEquals(500, ParkingCalculator.calculateFee(300)) // 5h

    // Above 5 hours: + Ksh 100 for every hour after 5 hours
    assertEquals(600, ParkingCalculator.calculateFee(360)) // 6h = 500 + 100
    assertEquals(700, ParkingCalculator.calculateFee(420)) // 7h = 500 + 200
  }
}

