package org.firstinspires.ftc.teamcode.dairy.tuning

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import com.qualcomm.robotcore.hardware.DcMotorEx
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Lambda
import org.firstinspires.ftc.teamcode.dairy.control.FullController
import org.firstinspires.ftc.teamcode.dairy.subsystems.Lift
import org.firstinspires.ftc.teamcode.dairy.subsystems.OuttakeClaw

@Mercurial.Attach
@OuttakeClaw.Attach
@Lift.Attach
@TeleOp

class OuttakeTuning: OpMode() {
    override fun init() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
        //Mercurial.gamepad1.leftBumper.onTrue(Lambda("false pid").setExecute{Lift.pidfused=false})
        //Mercurial.gamepad1.rightBumper.onTrue(Lambda("true pid").setExecute{Lift.pidfused=true})
            //Mercurial.gamepad1.triangle.onTrue(Parallel(Lambda("reset motor").setExecute{ outtake1!!.mode = RunMode.STOP_AND_RESET_ENCODER}, Lambda("reset motor").setExecute{ outtake2!!.mode = RunMode.STOP_AND_RESET_ENCODER}))
    }

    override fun loop() {
        /*if (gamepad1.a) {
            Lift.target = 3000.0
            Lift.pidf!!.target = Lift.target.toInt()
        }

        if (gamepad1.b) {
            Lift.target = 0.0
            Lift.pidf!!.target = Lift.target.toInt()
        }
*/

        telemetry.addData("target", Lift.pidf!!.target)
        telemetry.addData("L target", Lift.target)

        telemetry.addData("atTarget", Lift.atTarget())
        telemetry.addData("atT & t==0", Lift.atTarget() && Lift.target == 0.0 && Lift.outtake1!!.currentPosition != 0)
        telemetry.addData("pid", Lift.pidf!!.update())
        telemetry.addData("pidf", Lift.pidfused)

        telemetry.update()
    }
}