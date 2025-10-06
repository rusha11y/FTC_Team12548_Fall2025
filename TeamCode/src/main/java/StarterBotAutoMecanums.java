package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="12548 - AutoOp - With Mecanums", group="Autonomous")
public class StarterBotAutoMecanums extends LinearOpMode {

    private DcMotorEx frontLeft, frontRight, backLeft, backRight;
    private DcMotorEx launcher;
    private CRServo loaderLeft, loaderRight;
    private I2cDeviceSynch huskylens;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize hardware
        frontLeft = hardwareMap.get(DcMotorEx.class, "left_front_drive");
        frontRight = hardwareMap.get(DcMotorEx.class, "right_front_drive");
        backLeft = hardwareMap.get(DcMotorEx.class, "left_back_drive");
        backRight = hardwareMap.get(DcMotorEx.class, "right_back_drive");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        loaderLeft = hardwareMap.get(CRServo.class, "left_feeder");
        loaderRight = hardwareMap.get(CRServo.class, "right_feeder");

//        // Initialize HuskyLens via USB
//        huskylens = hardwareMap.get(I2cDeviceSynch.class, "huskylens");
//        huskylens.engage();
//        huskylens.setI2cAddress(I2cAddr.create7bit(0x32)); // default Huskylens I2C address

        // Set motor directions
        frontLeft.setDirection(DcMotorEx.Direction.FORWARD);
        backLeft.setDirection(DcMotorEx.Direction.FORWARD);
        frontRight.setDirection(DcMotorEx.Direction.REVERSE);
        backRight.setDirection(DcMotorEx.Direction.REVERSE);

        // Reset encoders
        frontLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        waitForStart();

        if (opModeIsActive()) {
            int tagID = readAprilTag();

            telemetry.addData("Detected Tag", tagID);
            telemetry.update();

            // Autonomous path based on tag ID
            switch(tagID) {
                case 1:
                    driveForward(24, 0.5);
                    break;
                case 2:
                    strafeRight(12, 0.5);
                    break;
                case 3:
                    strafeLeft(12, 0.5);
                    break;
                default:
                    driveForward(12, 0.5);
                    break;
            }

            // Launch rings
            launch(3);
        }
    }

    // Read Huskylens AprilTag ID via I2C
    private int readAprilTag() {
//        byte[] data = huskylens.read(0x01, 1);
//        if (data != null && data.length > 0) {
//            return data[0] & 0xFF;
//        } else {
//            return 0;
//        }
        return 0;
    }

    // Mecanum drive helpers
    private void driveForward(double inches, double power) {
        int ticks = (int)(inches * 45);
        frontLeft.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);

        while(opModeIsActive() && frontLeft.isBusy() && frontRight.isBusy() &&
                backLeft.isBusy() && backRight.isBusy()) {}

        stopMotors();
    }

    private void strafeRight(double inches, double power) {
        int ticks = (int)(inches * 45);
        frontLeft.setTargetPosition(ticks);
        backLeft.setTargetPosition(-ticks);
        frontRight.setTargetPosition(-ticks);
        backRight.setTargetPosition(ticks);

        frontLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(power);
        backRight.setPower(power);

        while(opModeIsActive() && frontLeft.isBusy() && frontRight.isBusy() &&
                backLeft.isBusy() && backRight.isBusy()) {}

        stopMotors();
    }

    private void strafeLeft(double inches, double power) {
        strafeRight(-inches, power);
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
    }

    // Launcher helper for CRServos
    private void launch(int shots) {
        for (int i = 0; i < shots; i++) {
            launcher.setPower(1.0); // spin up
            sleep(500);

            loaderLeft.setPower(1.0);   // feed rings forward
            loaderRight.setPower(1.0);
            sleep(500);

            loaderLeft.setPower(0);     // stop CRServos
            loaderRight.setPower(0);
            sleep(300);
        }
        launcher.setPower(0);
    }
}
