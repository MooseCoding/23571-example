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

class RedTurn: OpMode() {
    fun GeneratedPath(): PathChain {
        val builder: PathBuilder = PathBuilder()

        builder
            .addPath( // Line 1
                BezierLine(
                    Point(135.000, 64.000, Point.CARTESIAN),
                    Point(105.684, 64.000, Point.CARTESIAN) // change to 105
                )
            )
            .setConstantHeadingInterpolation(Math.PI)
            .setZeroPowerAccelerationMultiplier(2.0)
            .addPath( // Line 2
                BezierCurve(
                    Point(105.684, 64.000, Point.CARTESIAN), // change to 105
                    Point(116.421, 32.421, Point.CARTESIAN),
                    Point(127.2, 19.421, Point.CARTESIAN)
                )
            ).setZeroPowerAccelerationMultiplier(1.7)
            .setTangentHeadingInterpolation()
            .addPath( // Line 3
                BezierPoint(
                    Point(127.2, 19.421, Point.CARTESIAN)
                )
            )
            //.setConstantHeadingInterpolation(follower.pose.heading - Math.PI/5)
            .setTangentHeadingInterpolation()
            .addPath( // Line 4
                BezierCurve(
                    Point(124.421, 23.789, Point.CARTESIAN),
                    Point(127.368, 14.526, Point.CARTESIAN),
                    Point(129.263, 16.421, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 5
                BezierLine(
                    Point(129.263, 16.421, Point.CARTESIAN),
                    Point(119.579, 14.737, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 6
                BezierCurve(
                    Point(119.579, 14.737, Point.CARTESIAN),
                    Point(127.368, 19.579, Point.CARTESIAN),
                    Point(129.684, 16.842, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 7
                BezierLine(
                    Point(129.684, 16.842, Point.CARTESIAN),
                    Point(118.526, 11.579, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 8
                BezierCurve(
                    Point(118.526, 11.579, Point.CARTESIAN),
                    Point(130.526, 22.947, Point.CARTESIAN),
                    Point(129.474, 16.842, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 9
                BezierCurve(
                    Point(129.474, 16.842, Point.CARTESIAN),
                    Point(83.158, 20.632, Point.CARTESIAN),
                    Point(83.789, 34.526, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 10
                BezierCurve(
                    Point(83.789, 34.526, Point.CARTESIAN),
                    Point(123.158, 30.316, Point.CARTESIAN),
                    Point(129.474, 16.632, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()
            .addPath( // Line 11
                BezierCurve(
                    Point(129.474, 16.632, Point.CARTESIAN),
                    Point(83.579, 17.684, Point.CARTESIAN),
                    Point(82.105, 46.947, Point.CARTESIAN)
                )
            )
            .setTangentHeadingInterpolation()

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
                                ),
                                Sequential(
                                    IntakeClaw.pitchDown(),
                                    Wait.wait(300),
                                    IntakeClaw.closeClaw(),
                                    IntakeClaw.pitchUp(),
                                    IntakeClaw.cleanYaw(),
                                    Intake.goTo(0),
                                )
                            ),
                            OuttakeClaw.clawClose(),
                            IntakeClaw.partialClaw(),
                        ).schedule() // Scores specimen and grabs sample 0
                        first = true
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial && Intake.intake!!.currentPosition < 100) {
                        follower.followPath(path.getPath(1), true)
                        liftNoDrop(3900)
                        setPathState(2)
                        first = false
                    }
                }

            } // Specimen

            2 ->   {
                if(isClose()) {
                    if(!first) {
                        first = true
                        dropAndDown() // score sample 0
                    }

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_close) {
                        follower.followPath(path.getPath(2), true)
                        turnFrom(Math.PI)
                        intake(1200, IntakeClaw.yaw_home) // intake sample 1
                        setPathState(3)
                    }
                }

            } // First intake && 0 drop

            3 -> {
                if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                    turnFrom(-Math.PI)
                    follower.followPath(path.getPath(3), true)
                    liftNoDrop(4000)
                    setPathState(11)
                    first = false
                }
            } // First drop off

            4 -> {
                    if(OuttakeClaw.claw_pos == OuttakeClaw.claw_open) {
                        Sequential(
                            Lift.goTo(0)
                        ).schedule()

                        first = false
                        turnFrom(Math.PI) // second sample intake
                        intake(1200, IntakeClaw.yaw_home)
                        follower.followPath(path.getPath(4), true)
                        setPathState(5)
                    }

            } // Second intake

            5 -> {
                //second sample score
                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        turnFrom(-Math.PI)
                        follower.followPath(path.getPath(5), true)
                        setPathState(6)
                        lift(4000)
                    }

            }

            6 -> { // second sample score && third intake
                if(OuttakeClaw.claw_pos == OuttakeClaw.claw_open) {
                    Sequential(
                        Lift.goTo(0)
                    ).schedule()

                    first = false
                    turnFrom(Math.PI) // second sample intake
                    intake(1200, IntakeClaw.yaw_home)
                    follower.followPath(path.getPath(4), true)
                    setPathState(7)
                }
            } // third dropoff && second intake

            7 ->  { // third sample score
                if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                    turnFrom(-Math.PI)
                    follower.followPath(path.getPath(7), true)
                    lift(4000)
                    setPathState(8)
                    first = false
                }

            }
                /*
            8 -> {
                    lift(4000)

                    if(!Lift.pidfused) {
                        turnFrom(-2*Math.PI/3)
                        intake(1200, IntakeClaw.yaw_home)
                        //follower.followPath(path.getPath(6), true)
                        setPathState(9)
                    }

            }

            9 -> {
                Sequential(
                    Lift.goTo(0),
                    Lift.pidfFalse()
                ).schedule()

                intake(1200, IntakeClaw.yaw_home)

                    if(IntakeClaw.claw_pos == IntakeClaw.claw_partial) {
                        follower.followPath(path.getPath(9)120, true)
                        setPathState(10)
                    }

            }

            10 -> {

                lift(4000)

                if (!Lift.pidfused) {
                    intake(1200, IntakeClaw.yaw_home)
                    follower.followPath(path.getPath(10), true)
                    setPathState(11)
                }
            }

                 */

            11 -> {
                if(OuttakeClaw.claw_pos == OuttakeClaw.claw_open) {
                    Sequential(
                        Lift.goTo(0),
                        Lift.pidfFalse()
                    ).schedule()
                }

                if(isClose()) {

                        Sequential(
                            OuttakeClaw.clawClose(),
                            Lift.goTo(2000),
                            OuttakeClaw.pitchUp()
                        ).schedule()


                    setPathState(-1)
                }
            }

            -1 -> {
                if(!first) {
                    dropAndDown()
                }

                if(IntakeClaw.claw_pos == IntakeClaw.claw_pos) {

                }
            }
        }
    }

    override fun start() {
        Sequential(
            OuttakeClaw.pitchUp(),
            OuttakeClaw.clawClose(),
            Lift.goTo(1300),
            Intake.pidTrue(),
            Intake.goTo(400),
            IntakeClaw.closeClaw()
        ).schedule()
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
        telemetry.addData("pid", Intake.pidused)
        telemetry.addData("intake claw partial", IntakeClaw.claw_pos == IntakeClaw.claw_partial)
        telemetry.addData("intake claw close", IntakeClaw.claw_pos == IntakeClaw.claw_close)
        telemetry.addData("intake claw claw", IntakeClaw.claw_pos)

        telemetry.update()
    }
}