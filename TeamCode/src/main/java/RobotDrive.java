
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "Auto Rotate Motor", group = "Test")
public class RobotDrive extends LinearOpMode {

    private DcMotor gearMotor;

    // Ticks per revolution for 312 RPM goBILDA motor
    private static final int TICKS_PER_REV = 538;

    @Override
    public void runOpMode() {
        // Initialize motor
        gearMotor = hardwareMap.get(DcMotor.class, "gearmotor");

        // Reset encoder to 0
        gearMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set target position (1 full revolution)
        gearMotor.setTargetPosition(TICKS_PER_REV);

        // Set mode to run to position
        gearMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Wait for Start button on Driver Hub
        waitForStart();

        // Start moving motor
        gearMotor.setPower(0.5);

        // Wait until motor reaches the target
        while (opModeIsActive() && gearMotor.isBusy()) {
            telemetry.addData("Motor Position", gearMotor.getCurrentPosition());
            telemetry.update();
        }

        // Stop the motor
        gearMotor.setPower(0);

        // Final telemetry
        telemetry.addData("Status", "Rotation Complete");
        telemetry.update();

        // Optionally pause so the OpMode doesn't exit immediately
        sleep(1000);
    }
}
