package org.firstinspires.ftc.teamcode.dairy

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.groups.Parallel
import dev.frozenmilk.mercurial.commands.groups.Sequential
import org.firstinspires.ftc.teamcode.dairy.subsystems.Drivetrain
import org.firstinspires.ftc.teamcode.dairy.subsystems.Intake
import org.firstinspires.ftc.teamcode.dairy.subsystems.IntakeClaw
import org.firstinspires.ftc.teamcode.dairy.subsystems.Lift
import org.firstinspires.ftc.teamcode.dairy.subsystems.OuttakeClaw

@Mercurial.Attach
@Drivetrain.Attach
@Lift.Attach
@OuttakeClaw.Attach
@IntakeClaw.Attach
@Intake.Attach
@TeleOp
class DairyMain: OpMode() {
    override fun init() {
        Mercurial.gamepad1.triangle.onTrue(
            Sequential(
                Lift.pidfFalse(),
                IntakeClaw.pitchDown(), // 200 ms
                IntakeClaw.openClaw()  // 200 ms
            )
        )
        Mercurial.gamepad1.circle.onTrue(
            Sequential(
                IntakeClaw.closeClaw(), // 200 ms
                IntakeClaw.pitchUp(), // 800
                IntakeClaw.cleanYaw() ,// 200 ms
            )
        )

        Mercurial.gamepad1.cross.onTrue(
            Sequential(
                // running parallel est 300 ms saved

                Intake.pidTrue(), // 0 ms
                Intake.goTo(0), // ~300 ms
                OuttakeClaw.clawClose(), // 200 ms
                IntakeClaw.partialClaw() // 200 ms
            )
        )

        Mercurial.gamepad1.square.onTrue(
            Sequential(
                OuttakeClaw.clawClose(),
                IntakeClaw.partialClaw(),
            Parallel(
                Lift.pidfTrue(),
                Lift.goTo(3900), // ~1200 ms
                OuttakeClaw.pitchUp() // 200 ms
            ))
        )

        Mercurial.gamepad1.dpadUp.onTrue(
                Lift.goTo(2550)
        )

        Mercurial.gamepad1.dpadDown.onTrue(
            Lift.goTo(0)
        )

        Mercurial.gamepad1.dpadRight.onTrue(
            Lift.goTo(1900)
        )

        Mercurial.gamepad1.dpadLeft.onTrue(
            Lift.goTo(1200)
        )

        Mercurial.gamepad1.rightBumper.onTrue(
            Sequential(
                OuttakeClaw.clawOpen(),
                OuttakeClaw.clawClose(),
                Parallel(
                    IntakeClaw.closeClaw(),
                    OuttakeClaw.pitchDown()
                ),
                Parallel(
                    OuttakeClaw.clawOpen(),
                    Lift.goTo(0)
                )
            )
        )

        Mercurial.gamepad1.leftBumper.onTrue(
            Sequential(
                OuttakeClaw.clawClose(),
                OuttakeClaw.pitchDown(),
                OuttakeClaw.clawOpen(),
                Lift.goTo(0),
                Intake.pidFalse()
            )
        )
/*
        Mercurial.gamepad1.leftBumper.onTrue(
            Parallel(
                Lift.goTo(1200),
                OuttakeClaw.pitchUp()
            )
        )

        Mercurial.gamepad1.share.onTrue (
            Sequential(
                Lift.goTo(1300),
                OuttakeClaw.clawOpen(),
                Lift.goTo(800),
                OuttakeClaw.clawClose(),
                OuttakeClaw.pitchDown(),
                Parallel(Lift.goTo(0),OuttakeClaw.clawOpen())
            )
        )*/


    }

    override fun init_loop() {
        Lift.target = 0.0
    }

    override fun start() {
        Lift.pidfused = true
    }

    override fun loop() {

        var rT:Double = Mercurial.gamepad1.rightTrigger.state
        var lT:Double = Mercurial.gamepad1.leftTrigger.state

        if(rT>0 && !Intake.pidused) {
            Intake.intake!!.power = 0.5
        }
        else if (lT>0 && !Intake.pidused) {
            Intake.intake.power = -0.5
        }
        else if(!Intake.pidused) {
            Intake.intake!!.power = 0.0
        }

        telemetry.apply {
            addData("IP pos", IntakeClaw.pitch_pos)

            addData("intake pos", Intake.intake!!.currentPosition)
            addData("intake pid", Intake.pidused)
            addData("intake target", Intake.target)
            addData("lift target", Lift.target)
            addData("om1 pos", Lift.outtake1!!.currentPosition)
            addData("om2 pos", Lift.outtake2!!.currentPosition)

            addData("pidf", Lift.pidfused)
            addData("om1p", Lift.outtake1!!.power)
            addData("om2p", Lift.outtake2!!.power)
            addData("onTarget", Lift.atTarget())
            addData("lB", gamepad1.left_bumper)

            update()
        }
    }

}
