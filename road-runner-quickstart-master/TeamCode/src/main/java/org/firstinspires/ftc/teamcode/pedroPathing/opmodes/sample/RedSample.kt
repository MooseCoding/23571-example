package org.firstinspires.ftc.teamcode.pedroPathing.opmodes.sample

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.PathBuilder
import com.pedropathing.pathgen.PathChain
import com.pedropathing.pathgen.Point
import com.pedropathing.util.Constants
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import dev.frozenmilk.mercurial.Mercurial
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.commands.groups.Parallel
import dev.frozenmilk.mercurial.commands.groups.Sequential
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants
import org.firstinspires.ftc.teamcode.dairy.subsystems.Intake
import org.firstinspires.ftc.teamcode.dairy.subsystems.IntakeClaw
import org.firstinspires.ftc.teamcode.dairy.subsystems.Lift
import org.firstinspires.ftc.teamcode.dairy.subsystems.OuttakeClaw
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous

@Mercurial.Attach
@Intake.Attach
@Lift.Attach
@OuttakeClaw.Attach
@IntakeClaw.Attach

class RedSample: OpMode() {
    fun GeneratedPath(): PathChain {
        return PathChain()
    }

    fun intake(iT:Int, yP:Double) {
        Sequential(
            Intake.pidTrue(),
            Intake.goTo(iT),
            Lambda("yaw").setExecute{IntakeClaw.yaw_pos = yP}.setFinish{true},
            IntakeClaw.openClaw(),
            IntakeClaw.pitchDown(),
            IntakeClaw.closeClaw(),
            IntakeClaw.pitchUp(),
            IntakeClaw.cleanYaw(),
            Intake.goTo(0),
            OuttakeClaw.clawClose(),
            IntakeClaw.partialClaw(),
            Intake.pidFalse()
        ).schedule()
    }

    fun lift(lT:Int) {
        Sequential(
            Lift.pidfTrue(),
            Parallel(
                Lift.goTo(lT),
                OuttakeClaw.pitchUp()
            ),
            OuttakeClaw.clawOpen(),
            OuttakeClaw.clawClose(),
            OuttakeClaw.pitchDown(),
            Parallel(
                IntakeClaw.closeClaw(), // 200 ms
                OuttakeClaw.clawOpen(), // 200 ms // ~600 ms
            ),
        ).schedule()
    }

    private lateinit var follower: Follower
    private var pathTimer: Timer? = null
    private var opmodeTimer: Timer? = null

    private var pathState = 0

    private lateinit var path:PathChain

    override fun init() {
        pathTimer = Timer()
        opmodeTimer = Timer()

        opmodeTimer!!.resetTimer()

        Constants.setConstants(FConstants::class.java, LConstants::class.java)
        follower = Follower(hardwareMap)
        follower!!.setStartingPose(Pose(144-9.3, 144-83.0, Math.PI))
        path = GeneratedPath()
    }

    fun setPathState(pState: Int) {
        pathState = pState
        pathTimer!!.resetTimer()
    }

    lateinit var target: Pose
    var x:Boolean = false
    var y:Boolean = false

    fun isClose(): Boolean {
        target = Pose(path.getPath(pathState-1).getPoint(1.0).x, path.getPath(pathState-1).getPoint(1.0).y)
        var x:Boolean = false
        var y:Boolean = false

        x = if(follower.pose.x < target.x) {
            follower.pose.x > target.x - 0.2
        } else {
            follower.pose.x < target.x + 0.2
        }

        y = if(follower.pose.y < target.y) {
            follower.pose.y > target.y - 0.2
        } else {
            follower.pose.y < target.y + 0.2
        }

        return x && y
    }

    fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {
                Mercurial.runCatching {
                    OuttakeClaw.pitchUp().schedule()
                }
                follower.followPath(path.getPath(0), true)
                setPathState(1)
            }

            1 -> {
                if(isClose()) {
                    Mercurial.runCatching {
                        Sequential(
                            Lift.goTo(2280),
                            OuttakeClaw.clawOpen(),
                            OuttakeClaw.clawClose(),
                            OuttakeClaw.pitchDown(),
                            OuttakeClaw.clawOpen(),
                            Intake.goTo(500),
                            IntakeClaw.openClaw(),
                            IntakeClaw.pitchDown(),
                            IntakeClaw.closeClaw(),
                            IntakeClaw.pitchUp(),
                            IntakeClaw.cleanYaw(),
                            Intake.goTo(0),
                            OuttakeClaw.clawClose(),
                            IntakeClaw.partialClaw(),
                        ).schedule()
                    }

                    if(Intake.target.toInt() == 0 && OuttakeClaw.pitch_pos == OuttakeClaw.pitch_down) {
                        follower.followPath(path.getPath(1), true)
                        setPathState(2)
                    }
                }

            }

            2 ->   {
                if(isClose()) {
                    lift(3900)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                        follower.followPath(path.getPath(2), true)
                        setPathState(3)
                    }
                }

            }

            3 -> {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                ).schedule()

                if(isClose()) {
                    intake(1500, IntakeClaw.yaw_home)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        follower.followPath(path.getPath(3), true)
                        setPathState(4)
                    }
                }
            }

            4 -> {
                if(isClose()) {
                    lift(4000)

                    if(!Lift.pidfused) {
                        follower.followPath(path.getPath(4), true)
                        setPathState(5)
                    }
                }
            }

            5 -> {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                ).schedule()

                if(isClose()) {
                    intake(1500,IntakeClaw.yaw_home)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        follower.followPath(path.getPath(5), true)
                        setPathState(6)
                    }
                }
            }

            6 -> {
                if(isClose()) {
                    lift(4000)

                    if(!Lift.pidfused) {
                        follower.followPath(path.getPath(6), true)
                        setPathState(7)
                    }
                }
            }

            7 ->  {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                ).schedule()

                if(isClose()) {
                    intake(1500, IntakeClaw.yaw_90)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        follower.followPath(path.getPath(7), true)
                        setPathState(8)
                    }
                }
            }

            8 -> {
                if(isClose()) {
                    lift(4000)

                    if(!Lift.pidfused) {
                        follower.followPath(path.getPath(6), true)
                        setPathState(9)
                    }
                }
            }

            9 -> {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                ).schedule()

                if(isClose()) {
                    intake(1500, IntakeClaw.yaw_home)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        follower.followPath(path.getPath(7), true)
                        setPathState(10)
                    }
                }
            }

            10 -> {
                if(isClose()) {
                    lift(4000)

                    if(!Lift.pidfused) {
                        follower.followPath(path.getPath(8), true)
                        setPathState(11)
                    }
                }
            }

            11 -> {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                )

                if(isClose()) {
                    Mercurial.runCatching {
                        Sequential(
                            Lift.goTo(2000),
                            OuttakeClaw.pitchUp()
                        ).schedule()
                    }

                    setPathState(-1)
                }
            }
        }
    }

    override fun start() {
        Mercurial.runCatching {
            Sequential(
                OuttakeClaw.clawClose(),
                Lift.goTo(1300),
                IntakeClaw.closeClaw(),
            ).schedule()
        }
    }

    override fun loop() {
        follower.update()
        autonomousPathUpdate()

        telemetry.addData("path state", pathState)
        telemetry.addData("x", follower!!.pose.x)
        telemetry.addData("y", follower!!.pose.y)
        telemetry.addData("heading", follower!!.pose.heading)
        telemetry.addData("target x", path.getPath(pathState-1).getPoint(1.0).x)
        telemetry.addData("target y", path.getPath(pathState-1).getPoint(1.0).y)
        telemetry.addData("isClose", isClose())
        telemetry.addData("x", x)
        telemetry.addData("y",y)

        telemetry.update()
    }
}