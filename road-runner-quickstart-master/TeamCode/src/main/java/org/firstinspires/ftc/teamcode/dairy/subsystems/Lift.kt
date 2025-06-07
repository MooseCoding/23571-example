package org.firstinspires.ftc.teamcode.dairy.subsystems

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.dairy.control.PIDF
import java.lang.annotation.Inherited

@Config
object Lift: Subsystem {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    @Inherited
    annotation class Attach

    override var dependency: Dependency<*> =
        Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    val outtake1 by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.dcMotor.get("outtake1")
    }

    val outtake2 by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.dcMotor.get("outtake2")
    }

    override fun postUserInitHook(opMode: Wrapper) {
        outtake2.direction = DcMotorSimple.Direction.REVERSE // flip outtake 2 to reversed

        defaultCommand = update()

        pidf = PIDF(outtake1, p, i, d, f) // give the PIDF controller a value

    }

    override fun postUserLoopHook(opMode: Wrapper) {
        // Additional loop logic can go here
    }


        private var n:Int = 0
        private var nMulti:Int = 20

        @JvmField
        var pidfused: Boolean =
            true // if pidfused is true, the lift will run with the pid controller
        var pidf: PIDF? = null

        @JvmField
        var target: Double = 0.0

        @JvmField
        var p: Double = 0.01
        @JvmField
        var i: Double = 0.0001
        @JvmField
        var d: Double = 0.0003
        @JvmField
        var f: Double = 0.0001

        /*

        @JvmField var q = 0.0
        @JvmField var r = 0.0
        @JvmField var n = 0.0

        @JvmField var pkP = .004
        @JvmField var pkD = .0004
        @JvmField var pkI = 0.0

        @JvmField var vkP = 0.0
        @JvmField var vkD = 0.0
        @JvmField var vkI = 0.0

        @JvmField  var kV = 0.0
        @JvmField  var kA = 0.0
        @JvmField var kS = 0.0

         */
        @JvmField
        var tolerance = 20


    fun pidUpdate() {
        if(target == 0.0 && outtake1!!.currentPosition < tolerance) {
            outtake1!!.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            outtake2!!.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

            outtake1!!.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
            outtake2!!.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

            outtake1!!.power = 0.0
            outtake2!!.power = 0.0
        }

        else {

            pidf!!.target = target.toInt() // Set the target for FullController

            val power: Double = pidf!!.update() ?: 0.0
            outtake1!!.power = power
            outtake2!!.power = power
        }
    }

    fun update(): Lambda {
        return Lambda("update the pid")
            .addRequirements(Lift)
            .setExecute {
                if (pidfused) {
                    pidUpdate()
                }
                else {
                    outtake1!!.power = 0.0
                    outtake2!!.power = 0.0
                }
            }
            .setFinish { false }
    }

    fun goTo(to: Int): Lambda {
        return Lambda("set pid target")
            .setInit {
                target = to.toDouble()
                pidf!!.target = target.toInt()
            }
            .setExecute {

            }
            .setFinish {
                atTarget()
            }
    }

        fun pidfFalse(): Lambda {
            return Lambda("flip PID value")
                .setExecute {
                    pidfused = false // change the pid value to false
                }
                .setFinish{true}
        }

        fun pidfTrue(): Lambda {
            return Lambda("flip PID value")
                .setExecute {
                    pidfused = true // change the pid value to true
                }
                .setFinish{true}
        }

    fun atTarget(): Boolean {
        if(outtake1!!.currentPosition < target) {
            return (outtake1!!.currentPosition >= (target - tolerance))
        }

        else {
            return outtake1!!.currentPosition <= (target + tolerance)
        }

    }
}
