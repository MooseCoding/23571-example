package org.firstinspires.ftc.teamcode.dairy.control

import com.acmerobotics.dashboard.config.Config
import com.arcrobotics.ftclib.controller.PIDController
import com.qualcomm.robotcore.hardware.DcMotor

@Config
class PID(
    var motor: DcMotor,
    p:Double,
    i:Double,
    d:Double,
) {

    // creation of the PID object
    var controller = PIDController(p, i, d)

    var target:Int = 0

    fun update(): Double {
        return controller.calculate(motor.currentPosition.toDouble(), target.toDouble())
    }
}