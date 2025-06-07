package org.firstinspires.ftc.teamcode.pedroPathing.opmodes.sample

import com.pedropathing.follower.Follower
import com.pedropathing.localization.Pose
import com.pedropathing.pathgen.BezierCurve
import com.pedropathing.pathgen.BezierLine
import com.pedropathing.pathgen.BezierPoint
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
import org.firstinspires.ftc.teamcode.dairy.subsystems.Wait
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants

@Autonomous

@Mercurial.Attach
@Intake.Attach
@Lift.Attach
@OuttakeClaw.Attach
@IntakeClaw.Attach

class SampleSide: OpMode() {
    fun GeneratedPath(): PathChain {
        val builder: PathBuilder = PathBuilder()

        builder
            .addPath( // Line 1
                BezierLine(
                    Point(135.000, 64.000, Point.CARTESIAN),
                    Point(105.684, 64.000, Point.CARTESIAN) // change to 105
                )
            )
            .setZeroPowerAccelerationMultiplier(2.0)
            .addPath( // Line 2
                BezierCurve(
                    Point(105.684, 64.000, Point.CARTESIAN), // change to 105
                    Point(116.421, 32.421, Point.CARTESIAN),
                    Point(127.2, 19.421, Point.CARTESIAN)
                )
            ).setZeroPowerAccelerationMultiplier(1.7)

        return builder.build()
    }

    fun intake(iT:Int, yP:Double) {
        Sequential(
            Intake.pidTrue(),
            Intake.goTo(iT),
            Lambda("yaw").setExecute{IntakeClaw.yaw_pos = yP}.setFinish{true},
            IntakeClaw.openClaw(),
            IntakeClaw.pitchDown(),
            IntakeClaw.wait(300),
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
            Parallel(
                OuttakeClaw.pitchDown(),
                IntakeClaw.closeClaw(), // 200 ms // 200 ms // ~600 ms
            ),
            OuttakeClaw.clawOpen(),
        ).schedule()
    }

    fun liftNoDrop(lT:Int) {
        Sequential(
            Lift.pidfTrue(),
            Parallel(
                Lift.goTo(lT),
                OuttakeClaw.pitchUp()
            )
        ).schedule()
    }

    fun dropAndDown() {
        IntakeClaw.closeClaw().schedule()
        Sequential(
            OuttakeClaw.clawOpen(),
            OuttakeClaw.clawClose(),
            OuttakeClaw.pitchDown(),
            OuttakeClaw.clawOpen(), // 200 ms // ~600 ms
            Lift.goTo(0)
        ).schedule()
    }

    fun down() {
        IntakeClaw.closeClaw().schedule()
        Sequential(
            Lift.goTo(0)
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
        follower!!.setStartingPose(Pose(135.0, 74.0, Math.PI))
        path = GeneratedPath()
    }

    override fun init_loop() {
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
            follower.pose.x > target.x - 0.5
        } else {
            follower.pose.x < target.x + 0.5
        }

        y = if(follower.pose.y < target.y) {
            follower.pose.y > target.y - 0.5
        } else {
            follower.pose.y < target.y + 0.5
        }

        return x && y
    }

    var aTarget:Double = 0.0

    fun turn(radians:Double) {
        var temp: Pose = Pose(follower.pose.x, follower.pose.y, radians)
        follower.holdPoint(temp)
    }

    fun turnFrom(radians:Double) {
        var originalHeading: Double = follower.pose.heading + radians
        var temp: Pose = Pose(follower.pose.x, follower.pose.y, follower.pose.heading + radians)
        follower.holdPoint(temp)

        if(originalHeading - radians < originalHeading) {
            while(follower.pose.heading < originalHeading - Math.PI/3) {
                //follower.holdPoint(temp)
                follower.update()
            }
        }

        if(originalHeading - radians > originalHeading) {
            while(follower.pose.heading > originalHeading + Math.PI/3) {
                //follower.holdPoint(temp)
                follower.update()
            }
        }
    }

    var first:Boolean = false

    fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {
                follower.followPath(path.getPath(0), true)
                setPathState(1)
            }

            1 -> { // Specimen
                if(isClose()) {
                    if(!first) {
                        Sequential(
                            Parallel(
                                Sequential(
                                    Lift.goTo(2280),
                                    OuttakeClaw.clawOpen(),
                                    Wait.wait(300),
                                    OuttakeClaw.clawClose(),
                                    OuttakeClaw.pitchDown(),
                                    OuttakeClaw.clawOpen(),
                                    Lift.goTo(0),
                                )
                            ),
                            OuttakeClaw.clawClose(),
                            IntakeClaw.partialClaw(),
                        ).schedule() // Scores specimen and grabs sample 0
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial && Intake.intake!!.currentPosition < 100) {
                        follower.followPath(path.getPath(1), true)
                        //liftNoDrop(3900)
                        setPathState(2)
                        first = false
                    }
                }

            } // Specimen

            2 -> {
                if(isClose()) {
                    if(!first) {
                        turnFrom(-Math.PI)
                        intake(1400, IntakeClaw.yaw_home)
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        lift(4000)
                        turnFrom(Math.PI)
                        setPathState(3)
                        first = false
                    }
                }
            }

            3 -> {
                    if(!first && IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                        turnFrom(-Math.PI)
                        down()
                        intake(1400, IntakeClaw.yaw_home)
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        lift(4000)
                        turnFrom(Math.PI)
                        setPathState(3)
                                first = false

                    }

            }

            4 -> {
                    if(!first && IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                        turnFrom(-2/3 * Math.PI)
                        down()
                        intake(1400, IntakeClaw.yaw_home)
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        lift(4000)
                        turnFrom(2/3 * Math.PI)
                        setPathState(3)
                        first = false

                    }
                }

            5 -> {
                    if(!first && IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                        turnFrom(-2/3 * Math.PI)
                        down()
                        intake(1400, IntakeClaw.yaw_home)
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        lift(4000)
                        turnFrom(2/3 * Math.PI)
                        setPathState(3)
                        first = false
                    }
            }

            6 -> {
                if(!first && IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                    turnFrom(-2/3 * Math.PI)
                    down()
                    intake(1400, IntakeClaw.yaw_home)
                    first = true
                }

            }

        }
    }

    override fun start() {

        Sequential(
            OuttakeClaw.pitchUp(),
            OuttakeClaw.clawClose(),
            Lift.goTo(1300),
            IntakeClaw.closeClaw(),
        ).schedule()

        first = false
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
        telemetry.addData("intake", Intake.intake!!.currentPosition)
        telemetry.addData("ou1", Lift.outtake1!!.currentPosition)
        telemetry.addData("first", first)

        telemetry.addData("pid", Intake.pidused)
        telemetry.addData("intake claw partial", IntakeClaw.claw_pos == IntakeClaw.claw_partial)
        telemetry.addData("intake claw close", IntakeClaw.claw_pos == IntakeClaw.claw_close)
        telemetry.addData("intake claw claw", IntakeClaw.claw_pos)

        telemetry.update()
    }
}