import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Motor Toggle with Touch", group = "Example")
public class MotorToggleWithTouch extends LinearOpMode {

    private DcMotor gearMotor;
    private TouchSensor touchSensor;

    private boolean motorOn = false;
    private boolean lastTouchState = false;

    @Override
    public void runOpMode() {
        gearMotor = hardwareMap.get(DcMotor.class, "gearmotor");
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");

        gearMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean isPressed = touchSensor.isPressed(); // true when pressed

            // Detect rising edge: just pressed
            if (isPressed && !lastTouchState) {
                motorOn = !motorOn;

                gearMotor.setPower(motorOn ? 0.5 : 0.0);
            }

            telemetry.addData("Touch Sensor", isPressed ? "Pressed" : "Not Pressed");
            telemetry.addData("Motor State", motorOn ? "ON" : "OFF");
            telemetry.update();

            lastTouchState = isPressed;

            sleep(20);
        }

        gearMotor.setPower(0); // stop motor when opmode ends
    }
}
