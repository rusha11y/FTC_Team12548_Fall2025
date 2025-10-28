package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Arrays;
import java.util.List;

@Autonomous(name="StarterBotAutoMecanums 2", group="Autonomous")
public class StarterBotAutoMecanums2 extends LinearOpMode {

    // Hardware
    private DcMotorEx frontLeft, frontRight, backLeft, backRight, launcher;
    private CRServo loaderLeft, loaderRight;
    private HuskyLens huskylens;

    // Team selection
    private boolean isRedTeam = true;

    // Movement constants
    private static final double DRIVE_POWER = 0.5;
    private static final double STRAFE_POWER = 0.5;
    private static final double TURN_POWER = 0.4;

    final double LAUNCHER_TARGET_VELOCITY = 1250; //1125;

    @Override
    public void runOpMode() {
        // Initialize hardware
        frontLeft = hardwareMap.get(DcMotorEx.class, "left_front_drive");
        frontRight = hardwareMap.get(DcMotorEx.class, "right_front_drive");
        backLeft = hardwareMap.get(DcMotorEx.class, "left_back_drive");
        backRight = hardwareMap.get(DcMotorEx.class, "right_back_drive");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        loaderLeft = hardwareMap.get(CRServo.class, "left_feeder");
        loaderRight = hardwareMap.get(CRServo.class, "right_feeder");
        huskylens = hardwareMap.get(HuskyLens.class, "husky_lens");
        huskylens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        if (huskylens.knock()) {
            telemetry.addLine("HuskyLens connected successfully!");
        } else {
            telemetry.addLine("Failed to communicate with HuskyLens.");
        }
        telemetry.update();
        sleep(2000);

        // Reverse right side for mecanum
        frontRight.setDirection (DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);

        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
        loaderLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addLine("Press X for Red, B for Blue");
        telemetry.update();

        // Pre-start team selection
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.x) {
                isRedTeam = true;
                telemetry.addData("Team", "RED");
            } else if (gamepad1.b) {
                isRedTeam = false;
                telemetry.addData("Team", "BLUE");
            }
            telemetry.update();
        }

        waitForStart();

        if (opModeIsActive()) {

            // 1️⃣ Launch 3 balls at starting position
            launchRings(3);

            // 2️⃣ Move backward 6 inches
            moveDistance(-DRIVE_POWER, 800); // adjust timing for 6 inches

            // 3️⃣ Rotate 30° left to face obelisk
            if (isRedTeam) {
                turnRight(TURN_POWER, 600); // adjust timing for a 30° turn
            } else {
                turnLeft(TURN_POWER, 600); // adjust timing for a 30° turn
            }

            // 4️⃣ Strafe 30 inches based on team
            if (isRedTeam) {
                strafeRight(STRAFE_POWER, 5000); // timing approx for 30 inches, tune with encoders
            } else {
                strafeLeft(STRAFE_POWER, 5000);
            }

            // 5️⃣ Move back to position in front of obelisk
            moveDistance(-DRIVE_POWER, 5000);

//            // 6 Rotate 90° left to face obelisk
//            turnRight(TURN_POWER, 900); // adjust timing for a 90° turn
//
//            // 7 Read AprilTag from obelisk
//            int detectedTag = readAprilTag();
//            if (detectedTag != -1)
//                telemetry.addData("AprilTag Detected", detectedTag);
//            else
//                telemetry.addLine("No AprilTag detected");
//            telemetry.update();

            sleep(1000);
        }

    }

    private void turnRight(double power, int milliseconds) {
        frontLeft.setPower(-power);
        backLeft.setPower(-power);
        frontRight.setPower(power);
        backRight.setPower(power);
        sleep(milliseconds);
        stopMotors();
    }

    private void turnLeft(double power, int milliseconds) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(-power);
        backRight.setPower(-power);
        sleep(milliseconds);
        stopMotors();
    }

    // Movement helpers
    private void moveDistance(double power, int milliseconds) {
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(power);
        sleep(milliseconds);
        stopMotors();
    }

    private void strafeLeft(double power, int milliseconds) {
        frontLeft.setPower(power);
        frontRight.setPower(-power);
        backLeft.setPower(-power);
        backRight.setPower(power);
        sleep(milliseconds);
        stopMotors();
    }

    private void strafeRight(double power, int milliseconds) {
        frontLeft.setPower(-power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(-power);
        sleep(milliseconds);
        stopMotors();
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    // Launch balls
    private void launchRings(int count) {
        launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);

        for (int i = 0; i < count; i++) {
            launcher.setPower(1.0);
            sleep(500); // spin up
            loaderLeft.setPower(1.0);
            loaderRight.setPower(1.0);
            sleep(500); // feed
            loaderLeft.setPower(0);
            loaderRight.setPower(0);
            launcher.setPower(0);
        }
    }

    // Read AprilTag using HuskyLens
    private int readAprilTag() {
        int tagId = -1;
        telemetry.addLine("Initializing HuskyLens...");
        if (huskylens.knock()) {
            telemetry.addLine("HuskyLens connected!");
        } else {
            telemetry.addLine("No communication with HuskyLens!");
            telemetry.update();
            sleep(2000);
        }

        long startTime = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis() - startTime < 4000) {
            List<HuskyLens.Block> blocks = Arrays.asList(huskylens.blocks());

            telemetry.addLine("1");
            telemetry.update();

            if (!blocks.isEmpty()) {
                telemetry.addLine("2");

                telemetry.addData("Tags Detected", blocks.size());
                for (HuskyLens.Block block : blocks) {
                    telemetry.addData("Tag ID", block.id);
                    telemetry.addData("X", block.x);
                    telemetry.addData("Y", block.y);
                    telemetry.addData("Width", block.width);
                    telemetry.addData("Height", block.height);
                }
                tagId = blocks.get(0).id;
                telemetry.update();
                break;
            } else {
                telemetry.addData("Tags Detected", "None");
            }
            telemetry.update();
            sleep(100);
        }

        return tagId;
    }

}
