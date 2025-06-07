package org.firstinspires.ftc.teamcode.dairy.control

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.controller.PIDFController
import com.qualcomm.robotcore.hardware.DcMotor

@Config
class PIDF(
    var motor: DcMotor,
    p:Double,
    i:Double,
    d:Double,
    f:Double
) {
    // creation of the PID object
    var controller = PIDFController(p, i, d, f)

    var target:Int = 0

    fun update(): Double {
        return controller.calculate( motor.currentPosition.toDouble(), target.toDouble());
    }
}