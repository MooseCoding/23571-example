package org.firstinspires.ftc.teamcode.dairy.subsystems

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.Servo
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode
import org.firstinspires.ftc.robotcore.external.Telemetry
import java.lang.annotation.Inherited
import org.firstinspires.ftc.teamcode.dairy.util.Waiter

@Config
object IntakeClaw : Subsystem {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    @Inherited
    annotation class Attach

    private val claw by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.servo.get("intakeClaw")
    }

    private val pitch by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.servo.get("intakePitch")
    }

    private val yaw by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.servo.get("intakeYaw")
    }

    override var dependency: Dependency<*> = Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    override fun postUserInitHook(opMode: Wrapper) {
        // defaultCommand = update() // set the default command to update the servos pos
    }

    override fun postUserLoopHook(opMode: Wrapper) {
        // Additional loop logic can go here
    }

    fun openClaw(): Lambda {
        val waiter:Waiter = Waiter()
        return Lambda("open claw")
            .setRequirements(claw)
            .setInit {
                claw_pos = claw_open
                claw!!.position = claw_open // Update the claw position
                waiter.start(200)
            }
            .setFinish{
                waiter.isDone

            }
    }

    fun closeClaw(): Lambda {
        val waiter:Waiter = Waiter()
        return Lambda("close claw")
            .setRequirements(claw)
            .setInit {
                claw_pos = claw_close
                claw!!.position = claw_close     // Update the claw position
                waiter.start(200)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun partialClaw(): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("close claw")
            .setRequirements(claw)
            .setInit {
                claw_pos = claw_partial
                claw!!.position = claw_partial // Update the claw position
                waiter.start(150)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun pitchUp(): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("pitch up")
            .setRequirements(pitch)
            .setInit {
                pitch_pos = pitch_up
                pitch!!.position = pitch_up // Update the pitch position
                waiter.start(900)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun pitchDown(): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("pitch down")
            .setRequirements(pitch)
            .setInit {
                pitch_pos = pitch_down
                pitch!!.position = pitch_down // Update the pitch position
                waiter.start(200)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun cleanYaw(): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("reset yaw")
            .setRequirements(yaw)
            .setInit{
                yaw_pos = yaw_home
                yaw!!.position = yaw_home // Update the yaw position
                waiter.start(200)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun wait(mS:Int): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("wait")
            .setRequirements(IntakeClaw)
            .setInit{
                waiter.start(mS)
            }
            .setFinish{
                waiter.isDone
            }
    }

    fun rotYaw90(): Lambda {
        var waiter:Waiter = Waiter()
        return Lambda("rot yaw 90")
            .setRequirements(yaw)
            .setInit {
                yaw_pos = yaw_90
                yaw!!.position = yaw_90 // Update the yaw position
                waiter.start(250)
            }
            .setFinish{
                waiter.isDone
            }
    }

    
         val claw_open: Double = 0.72 // TUNED - Dec 12
         val claw_close: Double = 0.45 // TUNED - Dec 12
         val pitch_up: Double = 0.103 // TUNED - Dec 12
         val pitch_down: Double = 0.22 // TUNED - Dec 12
         val yaw_home: Double = 0.39 // TUNED - Dec 12
         val claw_partial: Double = 0.54 // TUNED - Dec 12
        val yaw_90:Double = 0.40

        var telemetry:Telemetry? = null
    
        @JvmField
        var claw_pos: Double = claw_close  // Claw pos starts closed

        @JvmField
        var pitch_pos: Double = pitch_up // Pitch pos starts up, which is at home

        @JvmField
        var yaw_pos: Double = yaw_home // Yaw pos stays neutral

        fun update(): Lambda {
            return Lambda("update the claw")
                .setRequirements(IntakeClaw)
                .setExecute {
                }
                .setFinish {
                    false // never really finish
                }
        }
    
}
