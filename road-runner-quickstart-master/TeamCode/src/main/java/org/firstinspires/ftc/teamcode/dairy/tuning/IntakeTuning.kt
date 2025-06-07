package org.firstinspires.ftc.teamcode.dairy.tuning

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor.RunMode
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.commands.groups.Sequential
import org.firstinspires.ftc.teamcode.dairy.subsystems.Intake
import org.firstinspires.ftc.teamcode.dairy.subsystems.Intake.intake
import org.firstinspires.ftc.teamcode.dairy.subsystems.Intake.pid

import org.firstinspires.ftc.teamcode.dairy.subsystems.IntakeClaw

@Mercurial.Attach
@IntakeClaw.Attach
@Intake.Attach
@TeleOp
class IntakeTuning: OpMode() {
    override fun init() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)
    }

    override fun loop() {
        Mercurial.gamepad1.triangle.onTrue(
            Intake.goTo(0)
        )

        Mercurial.gamepad1.cross.onTrue(
            Sequential(
            Intake.goTo(1000),
            Intake.pidTrue())
        )

        Mercurial.gamepad1.leftStickButton.onTrue(
            Lambda("reset encoder")
                .setInit{Intake.intake!!.mode=RunMode.STOP_AND_RESET_ENCODER}
                .setFinish{true}
        )

        telemetry.addData("pos", intake!!.currentPosition)
        telemetry.addData("power", intake!!.power)
        telemetry.addData("pid", pid!!.update())
        telemetry.addData("target", Intake.target)
        telemetry.addData("pidused", Intake.pidused)

        telemetry.update()
    }
}