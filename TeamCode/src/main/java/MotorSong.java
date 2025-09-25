
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "Hot Cross Buns Song", group = "Fun")
public class MotorSong extends LinearOpMode {


    private DcMotor motor;

    // Frequencies for notes (Hz) in the Hot Cross Buns melody
    private final double E4 = 330;
    private final double D4 = 294;
    private final double C4 = 262;

    // Hot Cross Buns melody notes
    private final double[] melody = {
            E4, D4, C4,
            E4, D4, C4,
            C4, C4, C4, C4,
            D4, D4, D4, D4,
            E4, D4, C4
    };

    // Duration of each note in milliseconds
    private final int[] durations = {
            400, 400, 800,
            400, 400, 800,
            300, 300, 300, 300,
            300, 300, 300, 300,
            800, 400, 800
    };

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotor.class, "gearmotor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Ready to play Hot Cross Buns");
        telemetry.update();

        waitForStart();

        for (int i = 0; i < melody.length && opModeIsActive(); i++) {
            playTone(melody[i], durations[i]);
            sleep(50); // short pause between notes
        }

        motor.setPower(0);

        telemetry.addData("Status", "Finished playing Hot Cross Buns");
        telemetry.update();
    }

    // Simulates a tone by pulsing the motor at the given frequency
    private void playTone(double frequency, int durationMs) {
        if (frequency == 0) {
            // Rest note (if any)
            sleep(durationMs);
            return;
        }

        long periodUs = (long) (1_000_000 / frequency);
        long endTime = System.nanoTime() + durationMs * 1_000_000L;

        while (System.nanoTime() < endTime && opModeIsActive()) {
            motor.setPower(0.5);
            sleepMicros(periodUs / 2);
            motor.setPower(0);
            sleepMicros(periodUs / 2);
        }
    }

    // Busy wait helper method to sleep for microseconds
    private void sleepMicros(long micros) {
        long start = System.nanoTime();
        long waitTime = micros * 1000;
        while (System.nanoTime() - start < waitTime) {
            // Busy wait
        }
    }
}
