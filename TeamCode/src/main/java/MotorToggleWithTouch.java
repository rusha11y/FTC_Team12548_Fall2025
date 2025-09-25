import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Touch Sensor Debug", group = "Test")
public class TouchSensorDebug extends LinearOpMode {
    private TouchSensor touchSensor;

    @Override
    public void runOpMode() {
        touchSensor = hardwareMap.get(TouchSensor.class, "touchSensor");

        telemetry.addData("Status", "Waiting for start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Touch Pressed", touchSensor.isPressed());
            telemetry.update();
            sleep(100);
        }
    }
}
