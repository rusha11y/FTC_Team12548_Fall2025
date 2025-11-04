package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import java.util.Arrays;
import java.util.List;

@Autonomous(name="StarterBotAutoMecanums-3", group="Autonomous")
public class StarterBotAutoMecanums3 extends LinearOpMode {

    // Hardware
    private DcMotorEx frontLeft, frontRight, backLeft, backRight, launcher;
    private CRServo loaderLeft, loaderRight;
    private HuskyLens huskylens;

    // Team & zone selection
    private boolean isRedTeam = true;
    private boolean isLargeZone = true;

    // Movement constants
    private static final double DRIVE_POWER = 0.5;
    private static final double STRAFE_POWER = 0.5;
    private static final double TURN_POWER = 0.4;

    // Launcher velocities (tune these!)
    private static final double LAUNCHER_VELOCITY_LARGE = 1350;  // close to goal
    private static final double LAUNCHER_VELOCITY_SMALL = 1900;  // further from goal

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

        // Reverse right side for mecanum
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);
        loaderLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addLine("=== TEAM & ZONE SELECTION ===");
        telemetry.addLine("Press X for Red | B for Blue");
        telemetry.addLine("Press Dpad Up = Large Zone | Dpad Down = Small Zone");
        telemetry.update();

        // Selection loop before start
        while (!isStarted() && !isStopRequested()) {
            if (gamepad1.x) isRedTeam = true;
            if (gamepad1.b) isRedTeam = false;

            if (gamepad1.dpad_up) isLargeZone = true;
            if (gamepad1.dpad_down) isLargeZone = false;

            telemetry.addData("Team", isRedTeam ? "RED" : "BLUE");
            telemetry.addData("Launch Zone", isLargeZone ? "LARGE" : "SMALL");
            telemetry.update();
            sleep(100);
        }

        waitForStart();
        if (isStopRequested()) return;

        // Execute based on selection
        if (isLargeZone) {
            runLargeZoneAuto();
        } else {
            runSmallZoneAuto();
        }

        sleep(1000);
    }

    /** Large Launch Zone Routine **/
    private void runLargeZoneAuto() {
        telemetry.addLine("Running LARGE zone auto...");
        telemetry.update();

        // 1️⃣ Launch 3 artifacts
        launchRings(3, LAUNCHER_VELOCITY_LARGE);

        // 2️⃣ Move backward ~6 inches (tune)
        moveDistance(-DRIVE_POWER, 800);

        // 3️⃣ Rotate right (Red) or left (Blue)
        if (isRedTeam) {
            turnRight(TURN_POWER, 500);
        } else {
            turnLeft(TURN_POWER, 500);
        }

        // 4️⃣ Strafe right (Red) or left (Blue) to exit both launch areas
        if (isRedTeam) {
            strafeRight(STRAFE_POWER, 2000);
        } else {
            strafeLeft(STRAFE_POWER, 2000);
        }

        stopMotors();
    }

    /** Small Launch Zone Routine **/
    private void runSmallZoneAuto() {
        telemetry.addLine("Running SMALL zone auto...");
        telemetry.update();

        // 1️⃣ Move forward out of the small triangular zone (tune based on field)
        moveDistance(DRIVE_POWER, 1500);

        // 2️⃣ Rotate 45° based on team
        if (isRedTeam) {
            turnRight(TURN_POWER, 130); // approximate 45 degrees
        } else {
            turnLeft(TURN_POWER, 130);
        }

        // 3️⃣ Launch 3 artifacts toward goal
        launchRings(3, LAUNCHER_VELOCITY_SMALL);

//        // 4️⃣ Move forward again slightly to clear both launch areas
//        moveDistance(DRIVE_POWER, 1000);

        stopMotors();
    }

    /** Movement Helper Methods **/
    private void moveDistance(double power, int milliseconds) {
        frontLeft.setPower(-power);
        frontRight.setPower(-power);
        backLeft.setPower(power);
        backRight.setPower(power);
        sleep(milliseconds);
        stopMotors();
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

    /** Launch balls **/
    private void launchRings(int count, double targetVelocity) {
        telemetry.addData("Launching at velocity", targetVelocity);
        telemetry.update();

        launcher.setVelocity(targetVelocity);
        sleep(300);

        for (int i = 0; i < count; i++) {
            //launcher.setPower(1.0);
            sleep(500); // spin up
            loaderLeft.setPower(1.0);
            loaderRight.setPower(1.0);
            sleep(500); // feed one artifact
            loaderLeft.setPower(0);
            loaderRight.setPower(0);
            sleep(400);
        }

        launcher.setPower(0);
    }


    /***************************************************************************/
//    Sample calls
//    moveInches(6, 0.5);  // move forward 6 inches
//    moveInches(-6, 0.5); // move backward 6 inches


    // Move in inches using encoders instead of using time
    private static final double TICKS_PER_REV = 383.6;
    private static final double WHEEL_DIAMETER_INCHES = 3.78;
    private static final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES);

    private void moveInches(double inches, double power) {
        int moveCounts = (int)(inches * TICKS_PER_INCH);

        int newFLTarget = frontLeft.getCurrentPosition() + (int)(moveCounts);
        int newFRTarget = frontRight.getCurrentPosition() + (int)(moveCounts);
        int newBLTarget = backLeft.getCurrentPosition() + (int)(moveCounts);
        int newBRTarget = backRight.getCurrentPosition() + (int)(moveCounts);

        frontLeft.setTargetPosition(newFLTarget);
        frontRight.setTargetPosition(newFRTarget);
        backLeft.setTargetPosition(newBLTarget);
        backRight.setTargetPosition(newBRTarget);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        while (opModeIsActive() &&
                (frontLeft.isBusy() && frontRight.isBusy() && backLeft.isBusy() && backRight.isBusy())) {
            telemetry.addData("Moving", "%.1f inches", inches);
            telemetry.update();
        }

        stopMotors();

        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }
    /***************************************************************************/



}
